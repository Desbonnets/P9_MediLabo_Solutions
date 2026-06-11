package medilabo.risk.controller;

import medilabo.risk.service.RiskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskController.class)
class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskService riskService;

    @Test
    void assess_existingPatient_returns200WithRiskDto() throws Exception {
        when(riskService.assessRisk(1L)).thenReturn("Borderline");

        mockMvc.perform(get("/api/assess/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.risk").value("Borderline"));
    }

    @Test
    void assess_allRiskLevels_returnedCorrectly() throws Exception {
        for (String level : new String[]{"None", "Borderline", "InDanger", "EarlyOnset"}) {
            when(riskService.assessRisk(1L)).thenReturn(level);
            mockMvc.perform(get("/api/assess/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.risk").value(level));
        }
    }

    @Test
    void assess_unknownPatient_returns404() throws Exception {
        when(riskService.assessRisk(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/assess/99"))
                .andExpect(status().isNotFound());
    }
}
