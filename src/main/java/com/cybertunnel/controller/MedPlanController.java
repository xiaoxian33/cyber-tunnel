package com.cybertunnel.controller;

import com.cybertunnel.config.CurrentUser;
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

    /** GET /api/plans — 获取当前登录用户的用药方案 */
    @GetMapping
    public ResponseEntity<List<MedPlan>> listMine() {
        return ResponseEntity.ok(service.findByUserId(CurrentUser.id()));
    }

    /** POST /api/plans — 新建用药方案（归属自动取当前登录用户） */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequest request) {
        Optional<MedPlan> plan = service.create(
                CurrentUser.id(),
                request.planName(),
                request.note(),
                request.items()
        );
        return plan.<ResponseEntity<?>>map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p))
                .orElseGet(() -> ResponseEntity.badRequest().body(java.util.Map.of("error", "方案创建失败：请填写名称并至少添加一种药。")));
    }

    /** DELETE /api/plans/{id} — 删除方案（仅限本人） */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(CurrentUser.id(), id);
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(String planName, String note, List<String[]> items) {}
}

