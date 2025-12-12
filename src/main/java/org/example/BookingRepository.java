package org.example;

import Users.CustomerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassenger(CustomerUser passenger);
    List<Booking> findByFlight(Flight flight);
    boolean existsByPassengerAndFlight(CustomerUser passenger, Flight flight);
}
