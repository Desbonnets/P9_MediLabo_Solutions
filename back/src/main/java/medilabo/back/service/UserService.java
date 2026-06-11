package medilabo.back.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import medilabo.back.repository.UserRepository;
import medilabo.back.model.User;

import java.util.List;

/**
 * Service métier du microservice back (patients).
 * Fournit les opérations CRUD sur les entités {@link User}.
 */
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Retourne la liste de tous les patients enregistrés.
     *
     * @return liste (éventuellement vide) de tous les {@link User}
     */
    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * Recherche un patient par son identifiant.
     *
     * @param id identifiant du patient
     * @return le {@link User} correspondant
     * @throws ResponseStatusException 404 si aucun patient ne correspond à {@code id}
     */
    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable : " + id));
    }

    /**
     * Persiste un nouveau patient.
     *
     * @param user données du patient à créer (id ignoré, généré par la base)
     * @return le {@link User} sauvegardé avec son id affecté
     */
    public User create(User user) {
        return repository.save(user);
    }

    /**
     * Met à jour les informations d'un patient existant.
     * Seuls les champs métier sont modifiés ; l'id reste inchangé.
     *
     * @param id   identifiant du patient à mettre à jour
     * @param user nouvelles valeurs (firstName, lastName, birthDate, gender, address, phone)
     * @return le {@link User} mis à jour et sauvegardé
     * @throws ResponseStatusException 404 si le patient est introuvable
     */
    public User update(Long id, User user) {
        User existing = findById(id);

        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setBirthDate(user.getBirthDate());
        existing.setGender(user.getGender());
        existing.setAddress(user.getAddress());
        existing.setPhone(user.getPhone());

        return repository.save(existing);
    }

    /**
     * Supprime un patient par son identifiant.
     * La vérification d'existence est effectuée avant la suppression.
     *
     * @param id identifiant du patient à supprimer
     * @throws ResponseStatusException 404 si le patient est introuvable
     */
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}
