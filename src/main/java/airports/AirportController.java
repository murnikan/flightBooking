package airports;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airports")
@Tag(name = "аэропорты")
public class AirportController {

    private static final Logger logger = LoggerFactory.getLogger(AirportController.class);

    @Autowired
    private AirportService service;

    @Operation(summary = "получить список аэропортов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "список аэропортов получен")
    })
    @GetMapping
    public ResponseEntity<List<Airport>> getAllAirports() {
        logger.info("[airportcontroller] запрос списка всех аэропортов");
        List<Airport> airports = service.getAllAirports();
        logger.info("[airportcontroller] найдено {} аэропортов", airports.size());
        return ResponseEntity.ok(airports);
    }

    @Operation(summary = "получить аэропорт по коду")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "аэропорт найден",
                    content = @Content(schema = @Schema(implementation = Airport.class))),
            @ApiResponse(responseCode = "404", description = "аэропорт не найден")
    })
    @GetMapping("/{code}")
    public ResponseEntity<Airport> getAirportByCode(
            @Parameter(description = "код аэропорта", example = "SVO")
            @PathVariable String code
    ) {
        logger.info("[airportcontroller] запрос аэропорта по коду: {}", code);
        Optional<Airport> airport = service.getAirportByCode(code);
        if (airport.isPresent()) {
            logger.info("[airportcontroller] аэропорт найден: {} ({})", airport.get().getName(), code);
            return ResponseEntity.ok(airport.get());
        } else {
            logger.warn("[airportcontroller] аэропорт с кодом {} не найден", code);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "создать аэропорт")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "аэропорт успешно создан",
                    content = @Content(schema = @Schema(implementation = Airport.class))),
            @ApiResponse(responseCode = "400", description = "некорректные данные"),
            @ApiResponse(responseCode = "403", description = "нет прав!")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Airport> createAirport(
            @RequestBody AirportDTO dto
    ) {
        logger.info("[airportcontroller] попытка создать аэропорт: {}", dto.getName());


        Airport airport = new Airport();
        airport.setCode(dto.getCode().toUpperCase());
        airport.setName(dto.getName());
        airport.setCity(dto.getCity());
        airport.setAvailableFlight(dto.isAvailableFlight());

        Airport created = service.createAirport(airport);
        logger.info("[airportcontroller] аэропорт создан: {} ({})", created.getName(), created.getCode());
        return ResponseEntity.created(URI.create("/api/airports/" + created.getCode())).body(created);
    }


    @Operation(summary = "обновить аэропорт")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "аэропорт обновлён"),
            @ApiResponse(responseCode = "404", description = "аэропорт не найден"),
            @ApiResponse(responseCode = "403", description = "нет прав!")
    })
    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Airport> updateAirport(
            @Parameter(description = "код аэропорта")
            @PathVariable String code,
            @RequestBody Airport airport
    ) {
        logger.info("[airportcontroller] попытка обновить аэропорт с кодом={}", code);
        Optional<Airport> existing = service.getAirportByCode(code);
        if (existing.isEmpty()) {
            logger.warn("[airportcontroller] аэропорт с  кодом={} не найден для обновления", code);
            return ResponseEntity.notFound().build();
        }
        airport.setCode(existing.get().getCode());
        Airport updated = service.updateAirport(airport);
        logger.info("[airportcontroller] аэропорт обновлен: {} ({})", updated.getName(), updated.getCode());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "удалить аэропорт")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "аэропорт удалён"),
            @ApiResponse(responseCode = "403", description = "нет прав!"),
            @ApiResponse(responseCode = "404", description = "не найден")
    })
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAirport(
            @Parameter(description = "код аэропорта")
            @PathVariable String code
    ) {
        logger.info("[airportcontroller] попытка удалить аэропорт с кодом={}", code);
        service.deleteAirport(code);
        logger.info("[airportcontroller] аэропорт с кодом={} удален", code);
        return ResponseEntity.noContent().build();
    }
}
