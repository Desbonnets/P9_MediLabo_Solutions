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
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(new User(
                "Test", "TestNone",
                LocalDate.of(1966, 12, 31),
                "F",
                "1 Brookside St",
                "100-222-3333"
        ));

        userRepository.save(new User(
                "Test", "TestBorderline",
                LocalDate.of(1945, 6, 24),
                "M",
                "2 High St",
                "200-333-4444"
        ));

        userRepository.save(new User(
                "Test", "TestInDanger",
                LocalDate.of(2004, 6, 18),
                "M",
                "3 Club Road",
                "300-444-5555"
        ));

        userRepository.save(new User(
                "Test", "TestEarlyOnset",
                LocalDate.of(2002, 6, 28),
                "F",
                "4 Valley Dr",
                "400-555-6666"
        ));

        System.out.println("4 patients de test chargés.");
    }
}
