package medilabo.back.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import medilabo.back.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}
