package medilabo.notes.repository;

import medilabo.notes.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByPatId(Long patId);
}
