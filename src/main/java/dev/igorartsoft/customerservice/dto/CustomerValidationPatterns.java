package dev.igorartsoft.customerservice.dto;

public final class CustomerValidationPatterns {
	
    private CustomerValidationPatterns() {}

    public static final String PHONE = "^[+0-9()\\-\\s]{7,30}$";
    public static final String CUSTOMER_ID = "^(?!me$)[A-Za-z0-9_-]+$";
}