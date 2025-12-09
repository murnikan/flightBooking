package Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerUserController {

    @Autowired
    private CustomerUserService service;
    @PostMapping
    public ResponseEntity<CustomerUser> createCustomer(@RequestBody CustomerUser customer) {
        CustomerUser created = service.saveCustomer(customer);
        return ResponseEntity.created(URI.create("/api/customers/" + created.getId()))
                .body(created);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CustomerUser> updateCustomer(@PathVariable Long id,
                                                       @RequestBody CustomerUser customer) {
        return service.getCustomerById(id)
                .map(existing -> {
                    customer.setId(existing.getId());
                    CustomerUser updated = service.saveCustomer(customer);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<CustomerUser>> getAllCustomers() {
        return ResponseEntity.ok(service.getAllCustomers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CustomerUser> getCustomerById(@PathVariable Long id) {
        return service.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search/login")
    public ResponseEntity<CustomerUser> getCustomerByLogin(@RequestParam String login) {
        return service.getCustomerByLogin(login)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/search/firstName")
    public ResponseEntity<List<CustomerUser>> getCustomersByFirstName(@RequestParam String firstName) {
        return ResponseEntity.ok(service.getCustomersByFirstName(firstName));
    }
    @GetMapping("/search/lastName")
    public ResponseEntity<List<CustomerUser>> getCustomersByLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(service.getCustomersByLastName(lastName));
    }
}
