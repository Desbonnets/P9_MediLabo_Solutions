package medilabo.back.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import medilabo.back.service.UserService;
import medilabo.back.model.User;

import java.util.List;

/**
 * Contrôleur REST du microservice back (patients).
 * Expose les endpoints CRUD sur la ressource {@code /api/users}.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * {@code GET /api/users}
     * Retourne la liste de tous les patients.
     *
     * @return liste de {@link User} (200 OK)
     */
    @GetMapping
    public List<User> getAll() {
        return service.findAll();
    }

    /**
     * {@code GET /api/users/{id}}
     * Retourne un patient par son identifiant.
     *
     * @param id identifiant du patient
     * @return le {@link User} correspondant (200 OK)
     */
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return service.findById(id);
    }

    /**
     * {@code POST /api/users}
     * Crée un nouveau patient.
     *
     * @param user données validées du patient à créer
     * @return le {@link User} créé (201 Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return service.create(user);
    }

    /**
     * {@code PUT /api/users/{id}}
     * Met à jour un patient existant.
     *
     * @param id   identifiant du patient à modifier
     * @param user nouvelles données validées du patient
     * @return le {@link User} mis à jour (200 OK)
     */
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User user) {
        return service.update(id, user);
    }

    /**
     * {@code DELETE /api/users/{id}}
     * Supprime un patient par son identifiant.
     *
     * @param id identifiant du patient à supprimer
     * @return réponse vide (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
