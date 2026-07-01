package dev.igorartsoft.customerservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MongoConfigDebugRunner implements CommandLineRunner {

    private final Environment environment;

    public MongoConfigDebugRunner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        String mongoUri = environment.getProperty("spring.data.mongodb.uri");

        String maskedMongoUri = mongoUri == null
                ? null
                : mongoUri.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");

        System.out.println("ACTIVE spring.data.mongodb.uri = " + maskedMongoUri);
        System.out.println("ACTIVE MONGODB_URI = " + environment.getProperty("MONGODB_URI"));
    }
}