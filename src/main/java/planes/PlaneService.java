package planes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaneService {

    @Autowired
    private PlaneRepository repository;
    public Plane createPlane(Plane plane) {
        return repository.save(plane);
    }
    public List<Plane> getAllPlanes() {
        return repository.findAll();
    }
    public Optional<Plane> getPlaneByRegistrationNumber(String registrationNumber) {
        return repository.findByRegistrationNumber(registrationNumber);
    }
    public void deletePlane(String registrationNumber) {
        repository.deleteById(registrationNumber);
    }
}
