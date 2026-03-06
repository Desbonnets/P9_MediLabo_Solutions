package medilabo.back.service;

import org.springframework.stereotype.Service;
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

    public User findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User create(User user) {
        return repository.save(user);
    }

    public User update(String id, User user) {
        User existing = findById(id);

        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setBirthDate(user.getBirthDate());
        existing.setGender(user.getGender());
        existing.setAddress(user.getAddress());
        existing.setPhone(user.getPhone());

        return repository.save(existing);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
