package medilabo.risk.service;

import medilabo.risk.dto.NoteDto;
import medilabo.risk.dto.PatientDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier du microservice risk.
 * Évalue le niveau de risque de diabète d'un patient en combinant ses données
 * démographiques (âge, genre) et les termes déclencheurs présents dans ses notes médicales.
 *
 * <p>L'algorithme repose sur 11 groupes de déclencheurs cliniques. Chaque groupe compte
 * pour 1 déclencheur si au moins un de ses termes apparaît dans l'ensemble des notes
 * (recherche insensible à la casse, par sous-chaîne). Le niveau de risque final est
 * déterminé selon la table suivante :</p>
 *
 * <pre>
 *   Âge &lt; 30, homme  : ≥ 5 → EarlyOnset | ≥ 3 → InDanger
 *   Âge &lt; 30, femme  : ≥ 7 → EarlyOnset | ≥ 4 → InDanger
 *   Âge ≥ 30         : ≥ 8 → EarlyOnset | ≥ 6 → InDanger | ≥ 2 → Borderline | sinon None
 * </pre>
 *
 * <p>Ce service interroge les microservices {@code back} et {@code notes} via {@link RestClient}.</p>
 */
@Service
public class RiskService {

    /**
     * Chaque tableau est un groupe : compte pour 1 déclencheur si au moins un
     * de ses termes apparaît dans les notes (recherche insensible à la casse).
     *
     * "vertige" (sans s) permet de matcher à la fois "Vertige" et "Vertiges".
     * "anormal" matche "anormale", "anormaux", etc. (sous-chaîne).
     * "fumeur"/"fumeuse" sont regroupés car ils représentent le même fait clinique.
     */
    private static final List<String[]> TRIGGER_GROUPS = List.of(
        new String[]{"hémoglobine a1c"},
        new String[]{"microalbumine"},
        new String[]{"taille"},
        new String[]{"poids"},
        new String[]{"fumeur", "fumeuse"},
        new String[]{"anormal"},
        new String[]{"cholestérol"},
        new String[]{"vertige"},
        new String[]{"rechute"},
        new String[]{"réaction"},
        new String[]{"anticorps"}
    );

    private final RestClient restClient;

    @Value("${services.patient.url}")
    private String patientServiceUrl;

    @Value("${services.notes.url}")
    private String notesServiceUrl;

    public RiskService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Évalue le risque de diabète d'un patient.
     *
     * <ol>
     *   <li>Récupère les données démographiques du patient via le microservice back.</li>
     *   <li>Récupère toutes ses notes via le microservice notes.</li>
     *   <li>Calcule l'âge du patient à la date du jour.</li>
     *   <li>Compte les groupes de déclencheurs présents dans la concaténation des notes.</li>
     *   <li>Retourne le niveau de risque calculé par {@link #determineRisk}.</li>
     * </ol>
     *
     * @param patId identifiant du patient
     * @return niveau de risque parmi : {@code "None"}, {@code "Borderline"}, {@code "InDanger"}, {@code "EarlyOnset"}
     * @throws ResponseStatusException 404 si le patient est introuvable dans le microservice back
     */
    public String assessRisk(Long patId) {
        PatientDto patient = fetchPatient(patId);
        List<NoteDto> notes = fetchNotes(patId);

        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();

        String allNotes = notes.stream()
                .map(NoteDto::getContent)
                .collect(Collectors.joining(" "))
                .toLowerCase();

        long triggerCount = TRIGGER_GROUPS.stream()
                .filter(group -> Arrays.stream(group).anyMatch(allNotes::contains))
                .count();

        return determineRisk(age, patient.getGender(), triggerCount);
    }

    /**
     * Détermine le niveau de risque selon l'âge, le genre et le nombre de déclencheurs.
     * Visibilité package pour permettre les tests unitaires sans appels réseau.
     *
     * @param age      âge du patient en années
     * @param gender   genre du patient ({@code "M"} pour masculin, autre valeur pour féminin)
     * @param triggers nombre de groupes de déclencheurs détectés dans les notes
     * @return niveau de risque : {@code "EarlyOnset"}, {@code "InDanger"}, {@code "Borderline"} ou {@code "None"}
     */
    // Package-visible pour les tests unitaires
    String determineRisk(int age, String gender, long triggers) {
        boolean male = "M".equalsIgnoreCase(gender);
        if (age < 30) {
            if (male) {
                if (triggers >= 5) return "EarlyOnset";
                if (triggers >= 3) return "InDanger";
            } else {
                if (triggers >= 7) return "EarlyOnset";
                if (triggers >= 4) return "InDanger";
            }
        } else {
            if (triggers >= 8) return "EarlyOnset";
            if (triggers >= 6) return "InDanger";
            if (triggers >= 2) return "Borderline";
        }
        return "None";
    }

    /**
     * Appelle le microservice back pour récupérer les données d'un patient.
     *
     * @param patId identifiant du patient
     * @return {@link PatientDto} avec les données démographiques
     * @throws ResponseStatusException 404 si le patient est introuvable
     */
    private PatientDto fetchPatient(Long patId) {
        try {
            return restClient.get()
                    .uri(patientServiceUrl + "/api/users/" + patId)
                    .retrieve()
                    .body(PatientDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable : " + patId);
            }
            throw e;
        }
    }

    /**
     * Appelle le microservice notes pour récupérer toutes les notes d'un patient.
     *
     * @param patId identifiant du patient
     * @return liste de {@link NoteDto} (peut être vide si le patient n'a pas de notes)
     */
    private List<NoteDto> fetchNotes(Long patId) {
        return restClient.get()
                .uri(notesServiceUrl + "/api/notes/patient/" + patId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoteDto>>() {});
    }
}
