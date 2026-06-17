package dev.igorartsoft.customerservice.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String customerId) {
        super("Customer was not found: " + customerId);
    }
}