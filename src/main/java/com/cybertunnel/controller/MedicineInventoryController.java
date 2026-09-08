package com.cybertunnel.controller;

import com.cybertunnel.model.MedicineInventory;
import com.cybertunnel.service.MedicineInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class MedicineInventoryController {

    private final MedicineInventoryService service;

    public MedicineInventoryController(MedicineInventoryService service) {
        this.service = service;
    }

    /** GET /api/inventory?userId=1 — 获取用户所有库存 */
    @GetMapping
    public ResponseEntity<List<MedicineInventory>> listByUser(@RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    /** GET /api/inventory/{medicineName} — 查询某个药品库存 */
    @GetMapping("/{medicineName}")
    public ResponseEntity<MedicineInventory> getByName(@PathVariable String medicineName) {
        return service.findByMedicineName(medicineName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** PUT /api/inventory/{medicineName} — 设置库存 */
    @PutMapping("/{medicineName}")
    public ResponseEntity<MedicineInventory> setStock(
            @PathVariable String medicineName,
            @RequestBody Map<String, Object> body) {
        Long userId = body.get("userId") != null ? Long.valueOf(body.get("userId").toString()) : 1L;
        Integer stock = body.get("stock") != null ? Integer.valueOf(body.get("stock").toString()) : 0;
        return ResponseEntity.ok(service.setStock(userId, medicineName, stock));
    }

    /** PATCH /api/inventory/{medicineName}/take?count=1 — 服用扣减 */
    @PatchMapping("/{medicineName}/take")
    public ResponseEntity<MedicineInventory> take(@PathVariable String medicineName,
                                                   @RequestParam(defaultValue = "1") Integer count) {
        return service.take(medicineName, count)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** PATCH /api/inventory/{medicineName}/add?count=1 — 补货增加 */
    @PatchMapping("/{medicineName}/add")
    public ResponseEntity<MedicineInventory> add(@PathVariable String medicineName,
                                                  @RequestParam(defaultValue = "1") Integer count) {
        return service.add(medicineName, count)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
