package com.example.customer.controller;

import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.service.CustomerService;
import com.example.shared.util.JwtPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for managing customer-related operations.
 * Provides endpoints for retrieving customer details and paginated lists of customers.
 */
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Operations related to customer management.")
@SecurityRequirement(name = "BearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Constructs a new CustomerController with the given CustomerService.
     * @param customerService The service for customer-related operations.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Retrieves the details of a specific customer by their UUID.
     * Requires administrator privileges.
     * @param uuid UUID of the customer to retrieve.
     * @return ResponseEntity containing the CustomerResponse if found, or a 404 if not.
     */
    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get a customer by UUID",
            description = "Retrieves the details of a specific customer by their UUID. Requires administrator privileges.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer found.",
                            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Customer not found.")
            }
    )
    public ResponseEntity<CustomerResponse> getCustomerByUUID(
            @Parameter(description = "UUID of the customer to retrieve.", required = true)
            @PathVariable UUID uuid) {
        CustomerResponse customer = customerService.getCustomerByUUID(uuid);
        customer.add(linkTo(methodOn(CustomerController.class).getCustomerByUUID(uuid)).withSelfRel());
        customer.add(linkTo(CustomerController.class).withRel("customers"));
        return ResponseEntity.ok(customer);
    }

    /**
     * Retrieves the details of the currently authenticated user.
     * Requires user privileges.
     * @return ResponseEntity containing the CustomerResponse of the current user.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get current customer details",
            description = "Retrieves the details of the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The current customer's details.",
                            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
                    )
            }
    )
    public ResponseEntity<CustomerResponse> getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(JwtPrincipal.getId(authentication));
        CustomerResponse customer = customerService.getCustomerByUUID(userId);
        customer.add(linkTo(methodOn(CustomerController.class).getCurrentCustomer()).withSelfRel());
        customer.add(linkTo(methodOn(CustomerController.class).getCustomerByUUID(customer.getId())).withRel("canonical"));
        customer.add(linkTo(CustomerController.class).withRel("customers"));
        return ResponseEntity.ok(customer);
    }

    /**
     * Retrieves a paginated list of all customers.
     * Requires administrator privileges.
     * @param page The page index (0-based).
     * @param size The number of customers per page.
     * @param assembler PagedResourcesAssembler for creating HATEOAS links.
     * @return ResponseEntity containing a PagedModel of CustomerResponse.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get a paginated list of customers",
            description = "Retrieves a paginated list of all customers. Requires administrator privileges.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A paginated list of customers.",
                            content = @Content(schema = @Schema(implementation = PagedModel.class))
                    )
            }
    )
    public ResponseEntity<PagedModel<EntityModel<CustomerResponse>>> pagination(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(hidden = true) PagedResourcesAssembler<CustomerResponse> assembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CustomerResponse> customers = customerService.getAllCustomers(pageRequest);
        return ResponseEntity.ok(assembler.toModel(customers, customer ->
                EntityModel.of(customer,
                        linkTo(methodOn(CustomerController.class).getCustomerByUUID(customer.getId())).withSelfRel())));
    }
}
