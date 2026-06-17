package dev.igorartsoft.customerservice.exception;

public record ApiFieldError(
        String field,
        String message
) {
}