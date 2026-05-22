package medilabo.risk.controller;

import medilabo.risk.dto.RiskResponseDto;
import medilabo.risk.service.RiskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assess")
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping("/{patId}")
    public RiskResponseDto assess(@PathVariable Long patId) {
        String risk = riskService.assessRisk(patId);
        return new RiskResponseDto(patId, risk);
    }
}
