package medilabo.notes.service;

import medilabo.notes.model.Note;
import medilabo.notes.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service métier du microservice notes.
 * Fournit les opérations CRUD sur les entités {@link Note} stockées dans MongoDB.
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Retourne toutes les notes associées à un patient.
     *
     * @param patId identifiant du patient (clé étrangère logique vers le microservice back)
     * @return liste (éventuellement vide) des notes du patient
     */
    public List<Note> findByPatId(Long patId) {
        return noteRepository.findByPatId(patId);
    }

    /**
     * Recherche une note par son identifiant MongoDB.
     *
     * @param id identifiant MongoDB de la note
     * @return la {@link Note} correspondante
     * @throws ResponseStatusException 404 si aucune note ne correspond à {@code id}
     */
    public Note findById(String id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note introuvable : " + id));
    }

    /**
     * Persiste une nouvelle note dans MongoDB.
     *
     * @param note données de la note à créer (id ignoré, généré par MongoDB)
     * @return la {@link Note} sauvegardée avec son id affecté
     */
    public Note create(Note note) {
        return noteRepository.save(note);
    }

    /**
     * Met à jour le contenu d'une note existante.
     * Seul le champ {@code content} est modifié ; {@code patId} reste inchangé.
     *
     * @param id      identifiant MongoDB de la note à modifier
     * @param updated note portant le nouveau contenu
     * @return la {@link Note} mise à jour et sauvegardée
     * @throws ResponseStatusException 404 si la note est introuvable
     */
    public Note update(String id, Note updated) {
        Note existing = findById(id);
        existing.setContent(updated.getContent());
        return noteRepository.save(existing);
    }

    /**
     * Supprime une note par son identifiant MongoDB.
     * La vérification d'existence est effectuée avant la suppression.
     *
     * @param id identifiant MongoDB de la note à supprimer
     * @throws ResponseStatusException 404 si la note est introuvable
     */
    public void delete(String id) {
        findById(id);
        noteRepository.deleteById(id);
    }
}
