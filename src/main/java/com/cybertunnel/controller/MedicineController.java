package com.cybertunnel.controller;

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

    /** GET /api/medicines?userId=1 — 获取某个用户的所有药品 */
    @GetMapping
    public ResponseEntity<List<Medicine>> listByUser(@RequestParam Long userId) {
        List<Medicine> medicines = medicineService.findByUserId(userId);
        return ResponseEntity.ok(medicines);
    }

    /** GET /api/medicines/{id} — 按 ID 获取单个药品 */
    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getById(@PathVariable Long id) {
        return medicineService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/medicines — 新增药品
     * 请求体: { "userId": 1, "name": "愈美片", "stock": 0, "isCommon": false }
     */
    @PostMapping
    public ResponseEntity<Medicine> create(@RequestBody MedicineRequest request) {
        if (request.userId() == null || request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Medicine> result = medicineService.createMedicine(
                request.userId(),
                request.name().trim(),
                request.stock() != null ? request.stock() : 0,
                request.isCommon() != null && request.isCommon()
        );

        return result.map(medicine ->
                        ResponseEntity.status(HttpStatus.CREATED).body(medicine))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    /**
     * PATCH /api/medicines/{id}/stock — 调整库存
     * 请求体: { "delta": -1 }
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Medicine> adjustStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        int delta = body.getOrDefault("delta", 0);
        return medicineService.adjustStock(id, delta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/medicines/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record MedicineRequest(Long userId, String name, Integer stock, Boolean isCommon) {
    }
}

