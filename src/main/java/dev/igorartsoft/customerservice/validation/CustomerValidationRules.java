package dev.igorartsoft.customerservice.validation;

public final class CustomerValidationRules {

    private CustomerValidationRules() {
    }

    public static final int CUSTOMER_ID_MIN = 3;
    public static final int CUSTOMER_ID_MAX = 64;

    public static final int NAME_MAX = 100;
    public static final int EMAIL_MAX = 255;

    public static final int ADDRESS_LINE_MAX = 255;
    public static final int CITY_MAX = 100;
    public static final int REGION_MAX = 100;
    public static final int POSTAL_CODE_MAX = 20;

    public static final String CUSTOMER_ID = "^(?!(?i:me)$)[A-Za-z0-9_-]+$";

    public static final String PHONE = "^[+0-9()\\-\\s]{7,30}$";

    public static final String COUNTRY_CODE = "^[A-Z]{2}$";

    public static final String NOT_BLANK_IF_PRESENT = ".*\\S.*";
}