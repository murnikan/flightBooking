package org.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "Flights",
        description = "операции с рейсами: создание, просмотр, удаление и поиск")
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    @Autowired
    private FlightService flightService;

    @Operation(summary = "создание рейса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "рейс успешно создан",
                    content = @Content(schema = @Schema(implementation = Flight.class))),
            @ApiResponse(responseCode = "403", description = "нет прав"),
            @ApiResponse(responseCode = "400", description = "некорректные данные")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flight> createFlight(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные рейса",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Flight.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"departureAirportCode\": \"GOJ\",\n" +
                                            "  \"arrivalAirportCode\": \"LED\",\n" +
                                            "  \"departureTime\": \"2025-12-20T10:00:00\",\n" +
                                            "  \"arrivalTime\": \"2025-12-20T12:00:00\",\n" +
                                            "  \"airlineCode\": \"SU\",\n" +
                                            "  \"planeRegistration\": \"TP-001\",\n" +
                                            "  \"availableSeats\": 2\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Flight flight
    ) {
        logger.info("[FlightController] ADMIN создает рейс: {} -> {}, departure={}, arrival={}",
                flight.getDepartureAirportCode(),
                flight.getArrivalAirportCode(),
                flight.getDepartureTime(),
                flight.getArrivalTime());

        Flight created = flightService.saveFlight(flight);

        logger.info("[FlightController] рейс создан: ID={}, seats={}",
                created.getFlightId(),
                created.getAvailableSeats());
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "получить все рейсы"    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "список рейсов получен"),
            @ApiResponse(responseCode = "401", description = "пользователь не аутентифицирован")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Flight>> getAllFlights() {
        logger.info("[FlightController] пользователь запрашивает все рейсы");
        List<Flight> flights = flightService.getAllFlights();
        logger.info("[FlightController] найдено {} рейсов", flights.size());
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "получить рейс по ID"  )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "рейс найден",
                    content = @Content(schema = @Schema(implementation = Flight.class))),
            @ApiResponse(responseCode = "404", description = "рейс не найден")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Flight> getFlightById(
            @Parameter(description = "ID рейса", example = "1")
            @PathVariable Long id
    ) {
        logger.info("[FlightController] пользователь запрашивает рейс ID={}", id);
        Flight flight = flightService.getFlightById(id);
        if (flight == null) {
            logger.warn("[FlightController] рейс ID={} не найден", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("[FlightController] рейс ID={} возвращен", id);
        return ResponseEntity.ok(flight);
    }

    @Operation(
            summary = "удалить рейс по ID",
            description = "доступно только ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "рейс удален"),
            @ApiResponse(responseCode = "403", description = "недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "рейс не найден")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlight(
            @Parameter(description = "ID рейса", example = "1")
            @PathVariable Long id
    ) {
        logger.info("[FlightController] ADMIN пытается удалить рейс ID={}", id);
        flightService.deleteFlight(id);
        logger.info("[FlightController] рейс ID={} успешно удален", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "поиски рейсов",
            description = "поиск рейсов по пунктам отправления, назначения и дате"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "рейсы найдены"),
            @ApiResponse(responseCode = "400", description = "некорректные параметры")
    })
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Flight>> searchFlights(
            @Parameter(description = "пункт отправления", example = "GOJ")
            @RequestParam String from,

            @Parameter(description = "пункт назначения", example = "LED")
            @RequestParam String to,

            @Parameter(description = "дата рейса (YYYY-MM-DD)", example = "2025-06-01")
            @RequestParam(required = false) String date
    ) {
        logger.info("[FlightController] поиск рейсов: from={}, to={}, date={}", from, to, date);

        List<Flight> flights;
        if (date != null) {
            LocalDate localDate = LocalDate.parse(date);
            flights = flightService.searchFlights(from, to, localDate);
        } else {
            flights = flightService.searchFlightsByCities(from, to);
        }

        logger.info("[FlightController] найдено {} рейсов по запросу", flights.size());
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "поиск рейсов с пересадкой" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "маршруты найдены"),
            @ApiResponse(responseCode = "400", description = "некорректная дата")
    })
    @GetMapping("/search/connection")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<List<Flight>>> searchFlightsWithConnection(
            @Parameter(description = "пункт отправления", example = "LED")
            @RequestParam String from,

            @Parameter(description = "пункт назначения", example = "SVO")
            @RequestParam String to,

            @Parameter(description = "дата рейса (YYYY-MM-DD)", example = "2025-06-01")
            @RequestParam String date
    ) {
        logger.info("[FlightController] поиск рейсов с пересадкой: from={}, to={}, date={}", from, to, date);
        LocalDate localDate = LocalDate.parse(date);

        List<List<Flight>> result = flightService.searchFlightsWithConnection(from, to, localDate);
        logger.info("[FlightController] найдено {} маршрутов с пересадкой", result.size());
        return ResponseEntity.ok(result);
    }
}
