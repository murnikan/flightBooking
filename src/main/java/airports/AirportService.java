package airports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    @Autowired
    private AirportRepository repository;

    public List<Airport> getAllAirports() {
        return repository.findAll();
    }

    public Optional<Airport> getAirportByCode(String code) {
        return repository.findByCode(code);
    }

    public Airport createAirport(Airport airport) {
        return repository.save(airport);
    }

    public Airport updateAirport(Airport airport) {
        return repository.save(airport);
    }

    public void deleteAirport(String code) {
        repository.deleteById(code);
    }
}
