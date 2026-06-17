package dev.igorartsoft.customerservice.controller;

import dev.igorartsoft.customerservice.dto.CustomerCreateRequest;
import dev.igorartsoft.customerservice.dto.CustomerPatchRequest;
import dev.igorartsoft.customerservice.dto.CustomerResponse;
import dev.igorartsoft.customerservice.dto.CustomerUpdateRequest;
import dev.igorartsoft.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request
    ) {
        CustomerResponse response = customerService.createCustomer(request);

        return ResponseEntity
                .created(URI.create("/customers/" + response.customerId()))
                .body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable String customerId
    ) {
        CustomerResponse response = customerService.getCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getCustomers(
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size
    ) {
        int safeSize = Math.min(size, MAX_SIZE);
        Pageable pageable = PageRequest.of(page, safeSize);

        Page<CustomerResponse> response = customerService.getCustomers(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        CustomerResponse response = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> patchCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerPatchRequest request
    ) {
        CustomerResponse response = customerService.patchCustomer(customerId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable String customerId
    ) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}