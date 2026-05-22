package medilabo.back.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import medilabo.back.repository.UserRepository;
import medilabo.back.model.User;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable : " + id));
    }

    public User create(User user) {
        return repository.save(user);
    }

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

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}
