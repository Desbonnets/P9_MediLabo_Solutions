package medilabo.risk.dto;

public class RiskResponseDto {

    private Long patientId;
    private String risk;

    public RiskResponseDto(Long patientId, String risk) {
        this.patientId = patientId;
        this.risk = risk;
    }

    public Long getPatientId() { return patientId; }
    public String getRisk() { return risk; }
}
