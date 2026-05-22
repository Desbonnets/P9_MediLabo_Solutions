package medilabo.risk.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class RiskServiceTest {

    private RiskService riskService;

    @BeforeEach
    void setUp() {
        riskService = new RiskService(RestClient.create());
    }

    // --- Cas patients de test (données Sprint 2) ---

    @Test
    void patient1_femme59ans_1trigger_noneRisk() {
        // F, 59 ans, 1 déclencheur (Poids) → None (Borderline exige ≥2)
        assertThat(riskService.determineRisk(59, "F", 1)).isEqualTo("None");
    }

    @Test
    void patient2_homme80ans_2triggers_borderlineRisk() {
        // M, 80 ans, 2 déclencheurs (anormal, réaction) → Borderline
        assertThat(riskService.determineRisk(80, "M", 2)).isEqualTo("Borderline");
    }

    @Test
    void patient3_homme21ans_3triggers_inDangerRisk() {
        // M, 21 ans, 3 déclencheurs (fumeur, anormal, cholestérol) → InDanger
        assertThat(riskService.determineRisk(21, "M", 3)).isEqualTo("InDanger");
    }

    @Test
    void patient4_femme23ans_7triggers_earlyOnsetRisk() {
        // F, 23 ans, 7 déclencheurs (anticorps, réaction, hémoglobine A1C,
        //   taille, poids, cholestérol, vertige) → EarlyOnset
        assertThat(riskService.determineRisk(23, "F", 7)).isEqualTo("EarlyOnset");
    }

    // --- Cas limites par règle ---

    @Test
    void none_aucunDeclencheur() {
        assertThat(riskService.determineRisk(50, "M", 0)).isEqualTo("None");
    }

    @Test
    void none_homme_moins30ans_moinsDe3triggers() {
        assertThat(riskService.determineRisk(25, "M", 2)).isEqualTo("None");
    }

    @Test
    void none_femme_moins30ans_moinsDe4triggers() {
        assertThat(riskService.determineRisk(25, "F", 3)).isEqualTo("None");
    }

    @Test
    void borderline_plus30ans_exactement2triggers() {
        assertThat(riskService.determineRisk(45, "F", 2)).isEqualTo("Borderline");
    }

    @Test
    void borderline_plus30ans_5triggers() {
        assertThat(riskService.determineRisk(45, "M", 5)).isEqualTo("Borderline");
    }

    @Test
    void inDanger_homme_moins30ans_4triggers() {
        assertThat(riskService.determineRisk(28, "M", 4)).isEqualTo("InDanger");
    }

    @Test
    void inDanger_femme_moins30ans_4triggers() {
        assertThat(riskService.determineRisk(25, "F", 4)).isEqualTo("InDanger");
    }

    @Test
    void inDanger_femme_moins30ans_6triggers() {
        assertThat(riskService.determineRisk(25, "F", 6)).isEqualTo("InDanger");
    }

    @Test
    void inDanger_plus30ans_6triggers() {
        assertThat(riskService.determineRisk(50, "M", 6)).isEqualTo("InDanger");
    }

    @Test
    void inDanger_plus30ans_7triggers() {
        assertThat(riskService.determineRisk(50, "F", 7)).isEqualTo("InDanger");
    }

    @Test
    void earlyOnset_homme_moins30ans_exactement5triggers() {
        assertThat(riskService.determineRisk(20, "M", 5)).isEqualTo("EarlyOnset");
    }

    @Test
    void earlyOnset_femme_moins30ans_exactement7triggers() {
        assertThat(riskService.determineRisk(20, "F", 7)).isEqualTo("EarlyOnset");
    }

    @Test
    void earlyOnset_plus30ans_8triggers() {
        assertThat(riskService.determineRisk(55, "M", 8)).isEqualTo("EarlyOnset");
    }

    @Test
    void earlyOnset_plus30ans_11triggers_maximum() {
        assertThat(riskService.determineRisk(60, "F", 11)).isEqualTo("EarlyOnset");
    }
}
