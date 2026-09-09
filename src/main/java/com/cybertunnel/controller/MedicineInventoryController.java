package com.cybertunnel.controller;

import com.cybertunnel.config.CurrentUser;
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

    /** GET /api/inventory — 获取当前登录用户的所有库存 */
    @GetMapping
    public ResponseEntity<List<MedicineInventory>> listMine() {
        return ResponseEntity.ok(service.findByUserId(CurrentUser.id()));
    }

    /** GET /api/inventory/{medicineName} — 查询当前用户某个药品库存 */
    @GetMapping("/{medicineName}")
    public ResponseEntity<MedicineInventory> getByName(@PathVariable String medicineName) {
        return service.findByUserIdAndMedicineName(CurrentUser.id(), medicineName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** PUT /api/inventory/{medicineName} — 设置当前用户某药品的库存 */
    @PutMapping("/{medicineName}")
    public ResponseEntity<MedicineInventory> setStock(
            @PathVariable String medicineName,
            @RequestBody Map<String, Object> body) {
        Integer stock = body.get("stock") != null ? Integer.valueOf(body.get("stock").toString()) : 0;
        return ResponseEntity.ok(service.setStock(CurrentUser.id(), medicineName, stock));
    }

    /** PATCH /api/inventory/{medicineName}/take?count=1 — 服用扣减（当前用户） */
    @PatchMapping("/{medicineName}/take")
    public ResponseEntity<MedicineInventory> take(@PathVariable String medicineName,
                                                   @RequestParam(defaultValue = "1") Integer count) {
        return service.take(CurrentUser.id(), medicineName, count)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** PATCH /api/inventory/{medicineName}/add?count=1 — 补货增加（当前用户） */
    @PatchMapping("/{medicineName}/add")
    public ResponseEntity<MedicineInventory> add(@PathVariable String medicineName,
                                                  @RequestParam(defaultValue = "1") Integer count) {
        return service.add(CurrentUser.id(), medicineName, count)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
