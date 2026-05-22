package medilabo.notes.controller;

import medilabo.notes.model.Note;
import medilabo.notes.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/patient/{patId}")
    public List<Note> getByPatient(@PathVariable Long patId) {
        return noteService.findByPatId(patId);
    }

    @GetMapping("/{id}")
    public Note getById(@PathVariable String id) {
        return noteService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note create(@RequestBody Note note) {
        return noteService.create(note);
    }

    @PutMapping("/{id}")
    public Note update(@PathVariable String id, @RequestBody Note note) {
        return noteService.update(id, note);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        noteService.delete(id);
    }
}
