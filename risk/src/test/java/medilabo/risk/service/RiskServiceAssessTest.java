package medilabo.risk.service;

import medilabo.risk.dto.NoteDto;
import medilabo.risk.dto.PatientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskServiceAssessTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @InjectMocks
    private RiskService riskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(riskService, "patientServiceUrl", "http://back");
        ReflectionTestUtils.setField(riskService, "notesServiceUrl", "http://notes");
    }

    private PatientDto patient(int birthYear, String gender) {
        PatientDto p = new PatientDto();
        p.setBirthDate(LocalDate.of(birthYear, 1, 1));
        p.setGender(gender);
        return p;
    }

    private NoteDto note(String content) {
        NoteDto n = new NoteDto();
        n.setContent(content);
        return n;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mockServices(PatientDto patient, List<NoteDto> notes) {
        when(restClient.get().uri(anyString()).retrieve().body(PatientDto.class))
                .thenReturn(patient);
        when(restClient.get().uri(anyString()).retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenAnswer(inv -> notes);
    }

    @Test
    void assessRisk_noNotes_returnsNone() {
        mockServices(patient(1970, "M"), List.of());

        assertThat(riskService.assessRisk(1L)).isEqualTo("None");
    }

    @Test
    void assessRisk_keywordsDetected_caseInsensitive() {
        // Patient > 30 ans, "Cholestérol" + "Fumeur" en majuscules → 2 déclencheurs → Borderline
        mockServices(patient(1970, "M"), List.of(
                note("Taux de Cholestérol élevé, patient Fumeur")));

        assertThat(riskService.assessRisk(1L)).isEqualTo("Borderline");
    }

    @Test
    void assessRisk_fumeuseCounted_asOneTriggerWithFumeur() {
        // "fumeuse" et "fumeur" appartiennent au même groupe → 1 seul déclencheur
        // Patient > 30 ans, 1 déclencheur → None
        mockServices(patient(1970, "F"), List.of(
                note("Patiente fumeuse"),
                note("Anciennement fumeur")));

        assertThat(riskService.assessRisk(1L)).isEqualTo("None");
    }

    @Test
    void assessRisk_triggersSpreadAcrossMultipleNotes() {
        // Déclencheurs répartis sur plusieurs notes : cholestérol + poids + anormal → 3 triggers
        // Patient > 30 ans, 3 déclencheurs → Borderline
        mockServices(patient(1970, "M"), List.of(
                note("Taux de cholestérol élevé"),
                note("Poids insuffisant"),
                note("Résultat anormal")));

        assertThat(riskService.assessRisk(1L)).isEqualTo("Borderline");
    }

    @Test
    void assessRisk_patientNotFound_throws404() {
        when(restClient.get().uri(anyString()).retrieve().body(PatientDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> riskService.assessRisk(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }
}
