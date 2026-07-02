package dev.igorartsoft.customerservice.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.igorartsoft.customerservice.dto.CustomerCreateRequest;
import dev.igorartsoft.customerservice.dto.CustomerPatchRequest;
import dev.igorartsoft.customerservice.dto.CustomerResponse;
import dev.igorartsoft.customerservice.dto.CustomerSelfPatchRequest;
import dev.igorartsoft.customerservice.dto.CustomerSelfUpdateRequest;
import dev.igorartsoft.customerservice.dto.CustomerUpdateRequest;
import dev.igorartsoft.customerservice.dto.PagedResponse;
import dev.igorartsoft.customerservice.dto.PostalAddressDto;
import dev.igorartsoft.customerservice.dto.PostalAddressPatchRequest;
import dev.igorartsoft.customerservice.exception.CustomerAlreadyExistsException;
import dev.igorartsoft.customerservice.exception.CustomerNotFoundException;
import dev.igorartsoft.customerservice.model.Address;
import dev.igorartsoft.customerservice.model.Customer;
import dev.igorartsoft.customerservice.model.CustomerStatus;
import dev.igorartsoft.customerservice.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ---------------------------------------------------------------------
    // ADMIN operations
    // ---------------------------------------------------------------------

    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        if (customerRepository.existsByCustomerId(request.customerId())) {
            throw new CustomerAlreadyExistsException(
                    "Customer with customerId already exists: " + request.customerId()
            );
        }

        if (customerRepository.existsByEmail(request.email())) {
            throw new CustomerAlreadyExistsException(
                    "Customer with email already exists: " + request.email()
            );
        }

        Instant now = Instant.now();

        Customer customer = new Customer(
                request.customerId(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                CustomerStatus.ACTIVE,
                toAddress(request.address()),
                now, now, 
                null, null
        );

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public CustomerResponse getCustomer(String customerId) {
        Customer customer = findCustomerByCustomerId(customerId);
        return toResponse(customer);
    }

    public PagedResponse<CustomerResponse> getCustomers(Pageable pageable) {
        Page<CustomerResponse> customersPage = customerRepository.findAll(pageable)
                .map(this::toResponse);

        return new PagedResponse<>(
                customersPage.getContent(),
                customersPage.getNumber(),
                customersPage.getSize(),
                customersPage.getTotalElements(),
                customersPage.getTotalPages(),
                customersPage.isFirst(),
                customersPage.isLast()
        );
    }
    
    
    
    public CustomerResponse updateCustomer(String customerId, CustomerUpdateRequest request) {
        Customer customer = findCustomerByCustomerId(customerId);

        validateEmailCanBeUsedByThisCustomer(request.email(), customer);

        customer.setEmail(request.email());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phone());
        customer.setStatus(request.status());
        customer.setAddress(toAddress(request.address()));
        customer.setUpdatedAt(Instant.now());

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public CustomerResponse patchCustomer(String customerId, CustomerPatchRequest request) {
        Customer customer = findCustomerByCustomerId(customerId);

        if (request.email() != null) {
            validateEmailCanBeUsedByThisCustomer(request.email(), customer);
            customer.setEmail(request.email());
        }

        if (request.firstName() != null) {
            customer.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            customer.setLastName(request.lastName());
        }

        if (request.phone() != null) {
            customer.setPhone(request.phone());
        }

        if (request.status() != null) {
            customer.setStatus(request.status());
        }
        
        if (request.address() != null) {
            customer.setAddress(patchAddress(customer.getAddress(), request.address()));
        }

        customer.setUpdatedAt(Instant.now());

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public void deleteCustomer(String customerId) {
        Customer customer = findCustomerByCustomerId(customerId);
        customerRepository.delete(customer);
    }

    // ---------------------------------------------------------------------
    // CUSTOMER own profile operations
    // ---------------------------------------------------------------------

    public CustomerResponse getByOidcIdentity(String oidcIssuer, String oidcSubject) {
        Customer customer = findCustomerByOidcIdentity(oidcIssuer, oidcSubject);
        return toResponse(customer);
    }

    public CustomerResponse updateMyProfile(
            String oidcIssuer,
            String oidcSubject,
            CustomerSelfUpdateRequest request
    ) {
        Customer customer = findCustomerByOidcIdentity(oidcIssuer, oidcSubject);

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phone());
        customer.setAddress(toAddress(request.address()));
        customer.setUpdatedAt(Instant.now());

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public CustomerResponse patchMyProfile(
            String oidcIssuer,
            String oidcSubject,
            CustomerSelfPatchRequest request
    ) {
        Customer customer = findCustomerByOidcIdentity(oidcIssuer, oidcSubject);

        if (request.firstName() != null) {
            customer.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            customer.setLastName(request.lastName());
        }

        if (request.phone() != null) {
            customer.setPhone(request.phone());
        }

        if (request.address() != null) {
            customer.setAddress(patchAddress(customer.getAddress(), request.address()));
        }

        customer.setUpdatedAt(Instant.now());

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    // ---------------------------------------------------------------------
    // Private helper methods
    // ---------------------------------------------------------------------

    private Customer findCustomerByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    private Customer findCustomerByOidcIdentity(String oidcIssuer, String oidcSubject) {
        return customerRepository.findByOidcIssuerAndOidcSubject(oidcIssuer, oidcSubject)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer profile not found for authenticated user"
                ));
    }

    private void validateEmailCanBeUsedByThisCustomer(String email, Customer currentCustomer) {
        if (email == null) {
            return;
        }

        if (email.equalsIgnoreCase(currentCustomer.getEmail())) {
            return;
        }

        if (customerRepository.existsByEmail(email)) {
            throw new CustomerAlreadyExistsException(
                    "Customer with email already exists: " + email
            );
        }
    }

    private Address toAddress(PostalAddressDto dto) {
        if (dto == null) {
            return null;
        }

        return new Address(
                dto.line1(),
                dto.line2(),
                dto.city(),
                dto.region(),
                dto.postalCode(),
                dto.countryCode()
        );
    }

    private Address patchAddress(Address currentAddress, PostalAddressPatchRequest request) {
        if (request == null) {
            return currentAddress;
        }

        Address address = currentAddress != null
                ? currentAddress
                : new Address(null, null, null, null, null, null);

        if (request.line1() != null) {
            address.setLine1(request.line1());
        }

        if (request.line2() != null) {
            address.setLine2(request.line2());
        }

        if (request.city() != null) {
            address.setCity(request.city());
        }

        if (request.region() != null) {
            address.setRegion(request.region());
        }

        if (request.postalCode() != null) {
            address.setPostalCode(request.postalCode());
        }

        if (request.countryCode() != null) {
            address.setCountryCode(request.countryCode());
        }

        return address;
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.getStatus().name(),
                toPostalAddressDto(customer.getAddress()),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    private PostalAddressDto toPostalAddressDto(Address address) {
        if (address == null) {
            return null;
        }

        return new PostalAddressDto(
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getRegion(),
                address.getPostalCode(),
                address.getCountryCode()
        );
    }
}