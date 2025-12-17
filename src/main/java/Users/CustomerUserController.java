package Users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

@Tag(name = "customeruser")
@RestController
@RequestMapping("/api/customers")
public class CustomerUserController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerUserController.class);

    @Autowired
    private CustomerUserService service;

    @Operation(summary = "создать пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "клиент создан"),
            @ApiResponse(responseCode = "403", description = "нет прав!")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerUser> createCustomer(@RequestBody CustomerUserDto dto) {
        CustomerUser customer = new CustomerUser(
                dto.getLogin(),
                dto.getPassword(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPassportNumber()
        );
        customer.setRole(UserRole.CUSTOMER); // роль всегда CUSTOMER
        CustomerUser created = service.saveCustomer(customer);
        logger.info("[CustomerUserController] создан пользователь: id={}, login={}", created.getId(), created.getLogin());
        return ResponseEntity.created(URI.create("/api/customers/" + created.getId())).body(created);
    }

    @Operation(summary = "обновить данные пользователя")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<CustomerUser> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerUserDto dto
    ) {
        return service.getCustomerById(id)
                .map(existing -> {
                    existing.setLogin(dto.getLogin());
                    existing.setPassword(dto.getPassword());
                    existing.setFirstName(dto.getFirstName());
                    existing.setLastName(dto.getLastName());
                    existing.setPassportNumber(dto.getPassportNumber());
                    existing.setRole(UserRole.CUSTOMER); // роль фиксированная
                    CustomerUser saved = service.saveCustomer(existing);
                    logger.info("[CustomerUserController] пользователь с id={} обновлен", id);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> {
                    logger.warn("[CustomerUserController] пользователь с id={} не найден", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerUser>> getAllCustomers() {
        List<CustomerUser> customers = service.getAllCustomers();
        logger.info("[CustomerUserController] найдено {} пользователей", customers.size());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<CustomerUser> getCustomerById(@PathVariable Long id) {
        return service.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(customer))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/login")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerUser> getCustomerByLogin(@RequestParam String login) {
        return service.getCustomerByLogin(login)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/firstName")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerUser>> getCustomersByFirstName(@RequestParam String firstName) {
        return ResponseEntity.ok(service.getCustomersByFirstName(firstName));
    }

    @GetMapping("/search/lastName")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerUser>> getCustomersByLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(service.getCustomersByLastName(lastName));
    }
}
