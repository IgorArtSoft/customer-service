package dev.igorartsoft.customerservice.security;

import java.net.URL;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import dev.igorartsoft.customerservice.repository.CustomerRepository;

@Component("customerSecurity")
public class CustomerSecurity {

    private final CustomerRepository customerRepository;

    public CustomerSecurity(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public boolean canAccessCustomer(String customerId, Authentication authentication) {
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            return false;
        }

        URL issuerUrl = jwtAuthentication.getToken().getIssuer();
        String subject = jwtAuthentication.getToken().getSubject();

        if (issuerUrl == null || subject == null) {
            return false;
        }

        String issuer = issuerUrl.toString();

        return customerRepository.existsByCustomerIdAndOidcIssuerAndOidcSubject(
                customerId,
                issuer,
                subject
        );
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}