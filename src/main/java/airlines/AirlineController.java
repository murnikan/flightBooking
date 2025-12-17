package airlines;

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
@RequestMapping("/api/airlines")
@Tag(name = "Авиакомпании")
public class AirlineController {

    private static final Logger logger = LoggerFactory.getLogger(AirlineController.class);

    @Autowired
    private AirlineService airlineService;

    @Operation(summary = "создать авиакомпанию", description = "уровень доступа: админ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "успех!", content = @Content(schema = @Schema(implementation = Airline.class))),
            @ApiResponse(responseCode = "400", description = "ошибка! неправильные данные"),
            @ApiResponse(responseCode = "403", description = "нет прав")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Airline> createAirline(@RequestBody AirlineDTO dto) {
        logger.info("[AirlineController] попытка создания авиакомпании: {}", dto.getName());

        Airline airline = new Airline();
        airline.setIataCode(dto.getIataCode().toUpperCase());
        airline.setName(dto.getName());
        airline.setCountry(dto.getCountry());
        airline.setFoundedYear(dto.getFoundedYear());
        airline.setInfo(dto.getInfo());
        airline.setWebsite(dto.getWebsite());
        airline.setFreeBaggageKg(dto.getFreeBaggageKg());
        airline.setExtraKgPrice(dto.getExtraKgPrice());

        Airline created = airlineService.saveAirline(airline);
        logger.info("[AirlineController] создана авиакомпания: {} ({})", created.getName(), created.getIataCode());
        return ResponseEntity.created(URI.create("/api/airlines/" + created.getIataCode())).body(created);
    }

    @Operation(summary = "список всех авиакомпаний")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "список получен")
    })
    @GetMapping
    public ResponseEntity<List<Airline>> getAllAirlines() {
        logger.info("[AirlineController] запрос списка всех авиакомпаний");
        List<Airline> airlines = airlineService.getAllAirlines();
        logger.info("[AirlineController] найдено {} авиакомпаний", airlines.size());
        return ResponseEntity.ok(airlines);
    }

    @Operation(summary = "авиакомпания по IATA-коду")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "авиакомпания найдена", content = @Content(schema = @Schema(implementation = Airline.class))),
            @ApiResponse(responseCode = "404", description = "авиакомпания не найдена")
    })
    @GetMapping("/{iataCode}")
    public ResponseEntity<Airline> getAirlineByIataCode(@PathVariable String iataCode) {
        logger.info("[AirlineController] Запрос авиакомпании по IATA: {}", iataCode);
        Optional<Airline> airline = airlineService.getAirlineByIataCode(iataCode);
        return airline.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "обновить данные авиакомпании")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "авиакомпания обновлена"),
            @ApiResponse(responseCode = "404", description = "авиакомпания не найдена"),
            @ApiResponse(responseCode = "403", description = "нет прав!")
    })
    @PutMapping("/{iataCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Airline> updateAirline(@PathVariable String iataCode, @RequestBody AirlineDTO dto) {
        logger.info("[AirlineController] попытка обновления авиакомпании с IATA={}", iataCode);

        Optional<Airline> existing = airlineService.getAirlineByIataCode(iataCode);
        if (existing.isEmpty()) {
            logger.warn("[AirlineController] авиакомпания IATA={} не найдена", iataCode);
            return ResponseEntity.notFound().build();
        }

        Airline airline = existing.get();
        airline.setName(dto.getName());
        airline.setCountry(dto.getCountry());
        airline.setFoundedYear(dto.getFoundedYear());
        airline.setInfo(dto.getInfo());
        airline.setWebsite(dto.getWebsite());
        airline.setFreeBaggageKg(dto.getFreeBaggageKg());
        airline.setExtraKgPrice(dto.getExtraKgPrice());

        Airline updated = airlineService.saveAirline(airline);
        logger.info("[AirlineController] авиакомпания обновлена: {} ({})", updated.getName(), updated.getIataCode());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "удалить авиакомпанию")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "удаление авиакомпании успешно"),
            @ApiResponse(responseCode = "403", description = "нет прав")
    })
    @DeleteMapping("/{iataCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAirline(@PathVariable String iataCode) {
        logger.info("[AirlineController] попытка удаления авиакомпании с IATA={}", iataCode);
        airlineService.deleteAirline(iataCode);
        logger.info("[AirlineController] авиакомпания IATA={} удалена", iataCode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск авиакомпании по названию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "авиакомпания найдена"),
            @ApiResponse(responseCode = "404", description = "авиакомпания не найдена")
    })
    @GetMapping("/search/name")
    public ResponseEntity<Airline> getAirlineByName(@RequestParam String name) {
        logger.info("[AirlineController] поиск авиакомпании по имени: {}", name);
        Optional<Airline> airline = airlineService.getAirlineByName(name);
        return airline.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "поиск авиакомпаний по стране")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "список авиакомпаний получен")
    })
    @GetMapping("/search/country")
    public ResponseEntity<List<Airline>> getAirlinesByCountry(@RequestParam String country) {
        logger.info("[AirlineController] поиск авиакомпаний по стране: {}", country);
        List<Airline> airlines = airlineService.getAirlinesByCountry(country);
        logger.info("[AirlineController] найдено {} авиакомпаний в стране {}", airlines.size(), country);
        return ResponseEntity.ok(airlines);
    }
}
