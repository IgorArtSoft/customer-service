package dev.igorartsoft.customerservice.dto;

import dev.igorartsoft.customerservice.model.CustomerStatus;
import dev.igorartsoft.customerservice.validation.CustomerValidationRules;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerPatchRequest(
		
        @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,

        @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,

        @Email(message = "Email must be valid")
        @Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters")
        String email,
        
        @Pattern(
                regexp = CustomerValidationRules.PHONE,
                message = "Phone must contain 7-15 digits, optionally starting with + country code, for example +14165551234 or 4165551234"
        )
        String phone,
                
        CustomerStatus status,

        @Valid
        PostalAddressPatchRequest address
        
       
) {
	@AssertTrue(message = "At least one field must be provided")
	public boolean hasAtLeastOneField() {
	    return firstName != null
	            || lastName != null
	            || phone != null
	            || status != null
	            || address != null;
	}
}