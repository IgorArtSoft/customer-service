package dev.igorartsoft.customerservice.service;


import dev.igorartsoft.customerservice.dto.*;
import dev.igorartsoft.customerservice.exception.CustomerAlreadyExistsException;
import dev.igorartsoft.customerservice.exception.CustomerNotFoundException;
import dev.igorartsoft.customerservice.model.Address;
import dev.igorartsoft.customerservice.model.Customer;
import dev.igorartsoft.customerservice.model.CustomerStatus;
import dev.igorartsoft.customerservice.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

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
                now,
                now
        );

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public CustomerResponse getCustomer(String customerId) {
        Customer customer = findCustomerByCustomerId(customerId);
        return toResponse(customer);
    }

    public Page<CustomerResponse> getCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public CustomerResponse updateCustomer(String customerId, CustomerUpdateRequest request) {
        Customer customer = findCustomerByCustomerId(customerId);

        CustomerStatus status = parseStatus(request.status());

        customer.setEmail(request.email());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phone());
        customer.setStatus(status);
        customer.setAddress(toAddress(request.address()));
        customer.setUpdatedAt(Instant.now());

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public CustomerResponse patchCustomer(String customerId, CustomerPatchRequest request) {
        Customer customer = findCustomerByCustomerId(customerId);

        if (request.email() != null) {
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
            customer.setStatus(parseStatus(request.status()));
        }

        if (request.address() != null) {
            customer.setAddress(toAddress(request.address()));
        }

        customer.setUpdatedAt(Instant.now());

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    public void deleteCustomer(String customerId) {
        Customer customer = findCustomerByCustomerId(customerId);
        customerRepository.delete(customer);
    }

    private Customer findCustomerByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    private CustomerStatus parseStatus(String status) {
        try {
            return CustomerStatus.valueOf(status.toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Invalid customer status. Allowed values: ACTIVE, INACTIVE"
            );
        }
    }

    private Address toAddress(AddressRequest request) {
        if (request == null) {
            return null;
        }

        return new Address(
                request.line1(),
                request.line2(),
                request.city(),
                request.province(),
                request.postalCode(),
                request.country()
        );
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.getStatus().name(),
                toAddressResponse(customer.getAddress()),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    private AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressResponse(
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getProvince(),
                address.getPostalCode(),
                address.getCountry()
        );
    }
}