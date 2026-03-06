package medilabo.back;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import medilabo.back.model.User;
import medilabo.back.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        System.out.println("URI Mongo utilisée: " + System.getenv("SPRING_DATA_MONGODB_URI"));
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {

//        if (userRepository.findByUsername("admin").isEmpty()) {

            User user = new User(
                    "admin",
                    "admin",
                    LocalDate.of(2020, 1, 8),
                    "admin",
                    "admin",
                    "admin"
            );

            userRepository.save(user);

            System.out.println("Utilisateur admin créé !");
//        }
    }
}