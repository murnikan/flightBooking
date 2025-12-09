package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f " +
            "WHERE LOWER(f.departureAirportCode) = LOWER(:from) " +
            "AND LOWER(f.arrivalAirportCode) = LOWER(:to) " +
            "AND f.departureTime BETWEEN :start AND :end")
    List<Flight> findFlightsByCityAndDate(@Param("from") String from,
                                          @Param("to") String to,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);


    @Query("SELECT f FROM Flight f " +
            "WHERE LOWER(f.departureAirportCode) = LOWER(:from) " +
            "AND LOWER(f.arrivalAirportCode) = LOWER(:to)")
    List<Flight> findFlightsByCity(@Param("from") String from,
                                   @Param("to") String to);
}