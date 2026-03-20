package medilabo.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import medilabo.back.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
