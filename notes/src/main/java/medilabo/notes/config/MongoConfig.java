package medilabo.notes.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient(
            @Value("${MONGODB_HOST:localhost}") String host,
            @Value("${MONGODB_PORT:27017}") int port) {
        return MongoClients.create("mongodb://" + host + ":" + port);
    }

    @Bean
    public MongoTemplate mongoTemplate(
            MongoClient mongoClient,
            @Value("${MONGODB_DATABASE:medilabo_notes}") String database) {
        return new MongoTemplate(mongoClient, database);
    }
}
