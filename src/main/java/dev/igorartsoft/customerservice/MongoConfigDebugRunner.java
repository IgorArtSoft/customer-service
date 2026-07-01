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
        System.out.println("ACTIVE spring.mongodb.uri = "
                + mask(environment.getProperty("spring.mongodb.uri")));

        System.out.println("ACTIVE spring.data.mongodb.uri = "
                + mask(environment.getProperty("spring.data.mongodb.uri")));

        System.out.println("ACTIVE MONGODB_URI = "
                + environment.getProperty("MONGODB_URI"));
    }

    private String mask(String uri) {
        return uri == null ? null : uri.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");
    }
}