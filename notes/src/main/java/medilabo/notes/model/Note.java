package medilabo.notes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notes")
public class Note {

    @Id
    private String id;

    @Indexed
    private Long patId;

    private String content;

    public Note() {}

    public Note(Long patId, String content) {
        this.patId = patId;
        this.content = content;
    }

    public String getId() { return id; }

    public Long getPatId() { return patId; }
    public void setPatId(Long patId) { this.patId = patId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
