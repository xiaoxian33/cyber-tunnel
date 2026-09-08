package com.cybertunnel.controller;

import com.cybertunnel.model.MedPlan;
import com.cybertunnel.service.MedPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/plans")
public class MedPlanController {

    private final MedPlanService service;

    public MedPlanController(MedPlanService service) {
        this.service = service;
    }

    /** GET /api/plans?userId=1 — 获取用户的用药方案 */
    @GetMapping
    public ResponseEntity<List<MedPlan>> list(@RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    /**
     * POST /api/plans — 新建用药方案
     * 请求体: { "userId":1, "planName":"感冒套餐", "note":"...", "items":[["维生素B1","1"],["感冒灵","2"]] }
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequest request) {
        Optional<MedPlan> plan = service.create(
                request.userId() != null ? request.userId() : 1L,
                request.planName(),
                request.note(),
                request.items()
        );
        return plan.<ResponseEntity<?>>map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p))
                .orElseGet(() -> ResponseEntity.badRequest().body(java.util.Map.of("error", "方案创建失败：请填写名称并至少添加一种药。")));
    }

    /** DELETE /api/plans/{id} — 删除方案 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(Long userId, String planName, String note, List<String[]> items) {}
}
