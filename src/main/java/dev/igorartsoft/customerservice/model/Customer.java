package dev.igorartsoft.customerservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "customers")
public class Customer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String customerId;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private String phone;
    private CustomerStatus status;
    private Address address;

    private Instant createdAt;
    private Instant updatedAt;

    private String oidcIssuer;
    private String oidcSubject;
    
    public Customer() {
    }

    public Customer(
            String customerId,
            String email,
            String firstName,
            String lastName,
            String phone,
            CustomerStatus status,
            Address address,
            Instant createdAt,
            Instant updatedAt,
            String oidcIssuer,
            String oidcSubject
    ) {
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.status = status;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.oidcIssuer = oidcIssuer;
        this.oidcSubject = oidcSubject;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Address getAddress() {
        return address;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getOidcIssuer() {
        return oidcIssuer;
    }

    public void setOidcIssuer(String oidcIssuer) {
        this.oidcIssuer = oidcIssuer;
    }

    public String getOidcSubject() {
        return oidcSubject;
    }

    public void setOidcSubject(String oidcSubject) {
        this.oidcSubject = oidcSubject;
    }
    
}