package airports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> { // PK — String
    Optional<Airport> findByCode(String code);
    Optional<Airport> findByCityIgnoreCase(String city);
}
