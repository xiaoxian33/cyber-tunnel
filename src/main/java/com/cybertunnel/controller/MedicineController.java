package com.cybertunnel.controller;

import com.cybertunnel.config.CurrentUser;
import com.cybertunnel.model.Medicine;
import com.cybertunnel.service.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    /** GET /api/medicines — 获取当前登录用户的所有药品 */
    @GetMapping
    public ResponseEntity<List<Medicine>> listMine() {
        return ResponseEntity.ok(medicineService.findByUserId(CurrentUser.id()));
    }

    /** GET /api/medicines/{id} — 按 ID 获取单个药品（仅限本人） */
    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getById(@PathVariable Long id) {
        return medicineService.findByIdAndUserId(id, CurrentUser.id())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/medicines — 新增药品（归属自动取当前登录用户） */
    @PostMapping
    public ResponseEntity<Medicine> create(@RequestBody MedicineRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Medicine> result = medicineService.createMedicine(
                CurrentUser.id(),
                request.name().trim(),
                request.stock() != null ? request.stock() : 0,
                request.isCommon() != null && request.isCommon()
        );

        return result.map(medicine ->
                        ResponseEntity.status(HttpStatus.CREATED).body(medicine))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    /**
     * PATCH /api/medicines/{id}/stock — 调整库存（仅限本人）
     * 请求体: { "delta": -1 }
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Medicine> adjustStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        int delta = body.getOrDefault("delta", 0);
        return medicineService.adjustStock(id, CurrentUser.id(), delta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/medicines/{id} — 删除药品（仅限本人） */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicineService.deleteById(id, CurrentUser.id());
        return ResponseEntity.noContent().build();
    }

    public record MedicineRequest(String name, Integer stock, Boolean isCommon) {
    }
}

