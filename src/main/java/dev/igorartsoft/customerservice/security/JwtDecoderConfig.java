package dev.igorartsoft.customerservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
public class JwtDecoderConfig {

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${OIDC_ISSUER_URI:http://localhost:8180/realms/customer-microservice}")
            String issuerUri
    ) {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }
}