package airlines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, String> {
    Optional<Airline> findByIataCode(String iataCode);
    Optional<Airline> findByName(String name);
    List<Airline> findByCountryIgnoreCase(String country);
}
