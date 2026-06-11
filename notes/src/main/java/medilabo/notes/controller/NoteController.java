package medilabo.notes.controller;

import medilabo.notes.model.Note;
import medilabo.notes.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST du microservice notes.
 * Expose les endpoints CRUD sur la ressource {@code /api/notes}.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * {@code GET /api/notes/patient/{patId}}
     * Retourne toutes les notes d'un patient.
     *
     * @param patId identifiant du patient
     * @return liste de {@link Note} (200 OK)
     */
    @GetMapping("/patient/{patId}")
    public List<Note> getByPatient(@PathVariable Long patId) {
        return noteService.findByPatId(patId);
    }

    /**
     * {@code GET /api/notes/{id}}
     * Retourne une note par son identifiant MongoDB.
     *
     * @param id identifiant MongoDB de la note
     * @return la {@link Note} correspondante (200 OK)
     */
    @GetMapping("/{id}")
    public Note getById(@PathVariable String id) {
        return noteService.findById(id);
    }

    /**
     * {@code POST /api/notes}
     * Crée une nouvelle note.
     *
     * @param note données de la note à créer
     * @return la {@link Note} créée (201 Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note create(@RequestBody Note note) {
        return noteService.create(note);
    }

    /**
     * {@code PUT /api/notes/{id}}
     * Met à jour le contenu d'une note existante.
     *
     * @param id   identifiant MongoDB de la note à modifier
     * @param note nouvelles données de la note
     * @return la {@link Note} mise à jour (200 OK)
     */
    @PutMapping("/{id}")
    public Note update(@PathVariable String id, @RequestBody Note note) {
        return noteService.update(id, note);
    }

    /**
     * {@code DELETE /api/notes/{id}}
     * Supprime une note par son identifiant MongoDB.
     *
     * @param id identifiant MongoDB de la note à supprimer
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        noteService.delete(id);
    }
}
