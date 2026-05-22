package medilabo.risk.dto;

public class NoteDto {

    private String id;
    private Long patId;
    private String content;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getPatId() { return patId; }
    public void setPatId(Long patId) { this.patId = patId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
