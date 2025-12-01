package planes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaneRepository extends JpaRepository<Plane, String> { // String вместо Long
    Optional<Plane> findByRegistrationNumber(String registrationNumber);
}
