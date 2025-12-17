package Users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

@Tag(name = "adminuser", description = "пперации с админами")
@RestController
@RequestMapping("/api/admins")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private AdminUserService adminService;

    @Operation(summary = "создать администратора")// админ может сделать админа ТОЛЬКО для примера отработки
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "администратор создан",
                    content = @Content(schema = @Schema(implementation = AdminUser.class))),
            @ApiResponse(responseCode = "400", description = "некорректные данные")
    })
    @PostMapping
    public ResponseEntity<AdminUser> createAdmin(@RequestBody AdminUserDTO dto) {
        if (dto.getLogin() == null || dto.getPassword() == null) {
            logger.warn("[AdminUserController] неверные данные для создания администратора!");
            return ResponseEntity.badRequest().build();
        }

        AdminUser admin = new AdminUser(dto.getLogin(), dto.getPassword());


        AdminUser saved = adminService.save(admin);
        logger.info("[AdminUserController] администратор создан: login={}", saved.getLogin());
        return ResponseEntity.created(URI.create("/api/admins/" + saved.getId())).body(saved);
    }
    @Operation(summary = "обновить администратора")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "администратор обновлён",
                    content = @Content(schema = @Schema(implementation = AdminUser.class))),
            @ApiResponse(responseCode = "404", description = "администратор не найден"),
            @ApiResponse(responseCode = "400", description = "некорректные данные")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdminUser> updateAdmin(@PathVariable Long id, @RequestBody AdminUserDTO dto) {
        return adminService.getById(id)
                .map(existing -> {
                    if (dto.getLogin() != null) existing.setLogin(dto.getLogin());
                    if (dto.getPassword() != null) existing.setPassword(dto.getPassword());

                    AdminUser updated = adminService.save(existing);
                    logger.info("[AdminUserController] администратор обновлён: id={}", id);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> {
                    logger.warn("[AdminUserController] администратор id={} не найден!", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public ResponseEntity<List<AdminUser>> getAllAdmins() {
        List<AdminUser> admins = adminService.getAll();
        logger.info("[AdminUserController] получено {} администраторов", admins.size());
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUser> getAdminById(@PathVariable Long id) {
        return adminService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")//админ может удалить админа ТОЛЬКО для примера работы апишки
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteById(id);
        logger.info("[AdminUserController] администратор удалён: id={}", id);
        return ResponseEntity.noContent().build();
    }


}
