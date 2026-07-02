package dev.igorartsoft.customerservice.controller;

import java.net.URI;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.igorartsoft.customerservice.dto.CustomerCreateRequest;
import dev.igorartsoft.customerservice.dto.CustomerPatchRequest;
import dev.igorartsoft.customerservice.dto.CustomerResponse;
import dev.igorartsoft.customerservice.dto.CustomerSelfPatchRequest;
import dev.igorartsoft.customerservice.dto.CustomerSelfUpdateRequest;
import dev.igorartsoft.customerservice.dto.CustomerUpdateRequest;
import dev.igorartsoft.customerservice.dto.PagedResponse;
import dev.igorartsoft.customerservice.service.CustomerService;
import dev.igorartsoft.customerservice.validation.annotation.RequiredCustomerId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // CUSTOMER own profile operations

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> getMyCustomerProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        CustomerResponse response = customerService.getByOidcIdentity(
                jwt.getIssuer().toString(),
                jwt.getSubject()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> updateMyCustomerProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CustomerSelfUpdateRequest request
    ) {
        CustomerResponse response = customerService.updateMyProfile(
                jwt.getIssuer().toString(),
                jwt.getSubject(),
                request
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> patchMyCustomerProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CustomerSelfPatchRequest request
    ) {
        CustomerResponse response = customerService.patchMyProfile(
                jwt.getIssuer().toString(),
                jwt.getSubject(),
                request
        );

        return ResponseEntity.ok(response);
    }

    // ADMIN operations

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        CustomerResponse response = customerService.createCustomer(request);

        /*
         * It uses a URI template variable instead of manual string concatenation. 
         * Spring’s ServletUriComponentsBuilder.fromCurrentRequestUri() builds from the current request URI without copying the query string, 
         * and UriComponentsBuilder.build(...) expands URI variables into a URI.
         */
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{customerId}")
                .build(response.customerId());

        return ResponseEntity
                .created(location)
                .body(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<CustomerResponse>> getCustomers(
            @RequestParam(defaultValue = "" + DEFAULT_PAGE)
            @Min(value = 0, message = "{pagination.page.min}")       
            int page,

            @RequestParam(defaultValue = "" + DEFAULT_SIZE)
            @Min(value = 1, message = "{pagination.page.min}")             
            @Max(value = MAX_SIZE, message = "{pagination.size.max}")
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        PagedResponse<CustomerResponse> response = customerService.getCustomers(pageable);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @customerSecurity.canAccessCustomer(#customerId, authentication)")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable @RequiredCustomerId String customerId
    ) {
        CustomerResponse response = customerService.getCustomer(customerId);
        return ResponseEntity.ok(response);
    }
    
    
    
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> updateCustomer(
    		@PathVariable @RequiredCustomerId String customerId,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        CustomerResponse response = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> patchCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerPatchRequest request
    ) {
        CustomerResponse response = customerService.patchCustomer(customerId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(
    		@PathVariable @RequiredCustomerId String customerId
    ) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}