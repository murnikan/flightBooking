package airlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirlineService {
// ключ - iata код
    @Autowired
    private AirlineRepository repository;
    public Airline saveAirline(Airline airline) {
        return repository.save(airline);
    }
        public List<Airline> getAllAirlines() {
        return repository.findAll();
    }
    public Optional<Airline> getAirlineByIataCode(String iataCode) {
        return repository.findById(iataCode);
    }

    public void deleteAirline(String iataCode) {
        repository.deleteById(iataCode);
    }
    public Optional<Airline> getAirlineByName(String name) {
        return repository.findByName(name);
    }
    public List<Airline> getAirlinesByCountry(String country) {
        return repository.findByCountryIgnoreCase(country);
    }
}
