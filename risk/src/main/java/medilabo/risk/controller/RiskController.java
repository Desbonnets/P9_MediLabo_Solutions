package medilabo.risk.controller;

import medilabo.risk.dto.RiskResponseDto;
import medilabo.risk.service.RiskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST du microservice risk.
 * Expose l'endpoint d'évaluation du risque de diabète sur {@code /api/assess}.
 */
@RestController
@RequestMapping("/api/assess")
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    /**
     * {@code GET /api/assess/{patId}}
     * Évalue le niveau de risque de diabète d'un patient.
     *
     * @param patId identifiant du patient
     * @return {@link RiskResponseDto} contenant le patId et le niveau de risque (200 OK)
     */
    @GetMapping("/{patId}")
    public RiskResponseDto assess(@PathVariable Long patId) {
        String risk = riskService.assessRisk(patId);
        return new RiskResponseDto(patId, risk);
    }
}
