package Users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

@Tag(name = "manageruser", description = "операции с менеджерами")
@RestController
@RequestMapping("/api/managers")
public class ManagerUserController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerUserController.class);

    @Autowired
    private ManagerUserService managerService;

    @Operation(summary = "создать менеджера")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerUser> createManager(@RequestBody ManagerUserDTO dto) {
        if (dto.getLogin() == null || dto.getPassword() == null) {
            logger.warn("[managerusercontroller] неверные данные для создания менеджера");
            return ResponseEntity.badRequest().build();
        }

        ManagerUser manager = new ManagerUser(dto.getLogin(), dto.getPassword());
        ManagerUser saved = managerService.save(manager);

        logger.info("[managerusercontroller] менеджер создан: id={}, login={}",
                saved.getId(), saved.getLogin());

        return ResponseEntity
                .created(URI.create("/api/managers/" + saved.getId()))
                .body(saved);
    }

    @Operation(summary = "обновить менеджера")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #id == principal.id)")
    public ResponseEntity<ManagerUser> updateManager(
            @PathVariable Long id,
            @RequestBody ManagerUserDTO dto
    ) {
        return managerService.getById(id)
                .map(existing -> {
                    if (dto.getLogin() != null) {
                        existing.setLogin(dto.getLogin());
                    }
                    if (dto.getPassword() != null) {
                        existing.setPassword(dto.getPassword());
                    }

                    ManagerUser updated = managerService.save(existing);

                    logger.info("[managerusercontroller] менеджер обновлён: id={}", id);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> {
                    logger.warn("[managerusercontroller] менеджер id={} не найден", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "получить всех менеджеров")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ManagerUser>> getAllManagers() {
        List<ManagerUser> managers = managerService.getAll();
        logger.info("[managerusercontroller] получено {} менеджеров", managers.size());
        return ResponseEntity.ok(managers);
    }

    @Operation(summary = "получить менеджера по id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #id == principal.id)")
    public ResponseEntity<ManagerUser> getManagerById(@PathVariable Long id) {
        return managerService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "удалить менеджера")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        managerService.deleteById(id);
        logger.info("[managerusercontroller] менеджер удалён: id={}", id);
        return ResponseEntity.noContent().build();
    }

    public static class ManagerUserDTO {
        private String login;
        private String password;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
