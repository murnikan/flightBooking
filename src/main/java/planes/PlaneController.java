package planes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.net.URI;
import java.util.List;

@Tag(name = "planes")
@RestController
@RequestMapping("/api/planes")
public class PlaneController {

    private static final Logger logger = LoggerFactory.getLogger(PlaneController.class);

    @Autowired
    private PlaneService service;

    @Operation(summary = "получить все самолеты")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "список самолетов получен"),
            @ApiResponse(responseCode = "401", description = "пользователь не вошел в аккаунт")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Plane>> getAllPlanes() {
        logger.info("[planecontroller] пользователь запрашивает все самолеты");
        List<Plane> planes = service.getAllPlanes();
        logger.info("[planecontroller] найдено {} самолетов", planes.size());
        return ResponseEntity.ok(planes);
    }

    @Operation(summary = "получить самолет по рег номеру")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "самолет найден",
                    content = @Content(schema = @Schema(implementation = Plane.class))),
            @ApiResponse(responseCode = "404", description = "самолет не найден")
    })
    @GetMapping("/{registrationNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Plane> getPlaneByRegistrationNumber(
            @Parameter(description = "рег самолета", example = "RA-89001")
            @PathVariable String registrationNumber
    ) {
        logger.info("[planecontroller] пользователь запрашивает самолет с registrationNumber={}", registrationNumber);
        return service.getPlaneByRegistrationNumber(registrationNumber)
                .map(plane -> {
                    logger.info("[planecontroller] найден самолет: registration={}, model={}",
                            plane.getRegistrationNumber(), plane.getModel());
                    return ResponseEntity.ok(plane);
                })
                .orElseGet(() -> {
                    logger.warn("[planecontroller] самолет с registrationNumber={} не найден", registrationNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "создать самолет")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "самолет создан",
                    content = @Content(schema = @Schema(implementation = Plane.class))),
            @ApiResponse(responseCode = "403", description = "нет прав!"),
            @ApiResponse(responseCode = "400", description = "некорректные данные")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Plane> createPlane(@RequestBody PlaneDTO dto) {
        logger.info("[planecontroller] admin создает самолет: model={}, registration={}, capacity={}",
                dto.getModel(), dto.getRegistrationNumber(), dto.getCapacity());

        Plane plane = new Plane();
        plane.setRegistrationNumber(dto.getRegistrationNumber().toUpperCase());
        plane.setModel(dto.getModel());
        plane.setType(dto.getType());
        plane.setCapacity(dto.getCapacity());
        plane.setMaxWeightKg(dto.getMaxWeightKg());
        plane.setMaxDistanceKm(dto.getMaxDistanceKm());
        plane.setProductionYear(dto.getProductionYear());

        Plane created = service.createPlane(plane);
        logger.info("[planecontroller] самолет создан: registration={}", created.getRegistrationNumber());

        return ResponseEntity.created(URI.create("/api/planes/" + created.getRegistrationNumber()))
                .body(created);
    }


    @Operation(summary = "удалить самолет")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "самолет удален"),
            @ApiResponse(responseCode = "403", description = "нет прав"),
            @ApiResponse(responseCode = "404", description = "самолет не найден")
    })
    @DeleteMapping("/{registrationNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlane(
            @Parameter(description = "регистрационный номер самолета", example = "RA-89001")
            @PathVariable String registrationNumber
    ) {
        logger.info("[planecontroller] admin удаляет самолет с registrationNumber={}", registrationNumber);
        service.deletePlane(registrationNumber);
        logger.info("[planecontroller] самолет с registrationNumber={} успешно удален", registrationNumber);
        return ResponseEntity.noContent().build();
    }
}
