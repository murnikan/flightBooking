package Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerUserService {

    @Autowired
    private CustomerUserRepository repository;

    public CustomerUser saveCustomer(CustomerUser customer) {
        return repository.save(customer);
    }
    public List<CustomerUser> getAllCustomers() {
        return repository.findAll();
    }

    public Optional<CustomerUser> getCustomerById(Long id) {
        return repository.findById(id);
    }
    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }


    public Optional<CustomerUser> getCustomerByLogin(String login) {
        return repository.findByLogin(login);
    }
    public List<CustomerUser> getCustomersByFirstName(String firstName) {
        return repository.findByFirstName(firstName);
    }
    public List<CustomerUser> getCustomersByLastName(String lastName) {
        return repository.findByLastName(lastName);
    }
}
