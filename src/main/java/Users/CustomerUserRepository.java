package Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerUserRepository extends JpaRepository<CustomerUser, Long> {


    @Query("SELECT u FROM User u WHERE TYPE(u) = CustomerUser AND u.login = :login")
    Optional<CustomerUser> findByLogin(@Param("login") String login);
    @Query("SELECT u FROM User u WHERE TYPE(u) = CustomerUser AND u.id = :id")
    Optional<CustomerUser> findById(@Param("id") Long id);
     @Query("SELECT u FROM User u WHERE TYPE(u) = CustomerUser")
     List<CustomerUser> findAllCustomers();
     @Query("SELECT u FROM User u WHERE TYPE(u) = CustomerUser AND u.firstName = :firstName")
    List<CustomerUser> findByFirstName(@Param("firstName") String firstName);



    @Query("SELECT u FROM User u WHERE TYPE(u) = CustomerUser AND u.lastName = :lastName")
    List<CustomerUser> findByLastName(@Param("lastName") String lastName);
}
