package Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerUserRepository extends JpaRepository<ManagerUser, Long> {
    Optional<ManagerUser> findByLogin(String login);
    List<ManagerUser> findAll();
}
