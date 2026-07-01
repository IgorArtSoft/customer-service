package dev.igorartsoft.customerservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import dev.igorartsoft.customerservice.model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);

    boolean existsByEmail(String email);

    void deleteByCustomerId(String customerId);
    
    boolean existsByCustomerIdAndOidcIssuerAndOidcSubject(
            String customerId,
            String oidcIssuer,
            String oidcSubject
    );

	Optional<Customer> findByOidcIssuerAndOidcSubject(String oidcIssuer, String oidcSubject);
	
}