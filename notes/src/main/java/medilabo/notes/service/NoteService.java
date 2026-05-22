package medilabo.notes.service;

import medilabo.notes.model.Note;
import medilabo.notes.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> findByPatId(Long patId) {
        return noteRepository.findByPatId(patId);
    }

    public Note findById(String id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note introuvable : " + id));
    }

    public Note create(Note note) {
        return noteRepository.save(note);
    }

    public Note update(String id, Note updated) {
        Note existing = findById(id);
        existing.setContent(updated.getContent());
        return noteRepository.save(existing);
    }

    public void delete(String id) {
        findById(id);
        noteRepository.deleteById(id);
    }
}
