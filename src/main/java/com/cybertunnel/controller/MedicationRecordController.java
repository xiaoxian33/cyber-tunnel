package com.cybertunnel.controller;

import com.cybertunnel.config.CurrentUser;
import com.cybertunnel.model.MedicationRecord;
import com.cybertunnel.service.MedicationRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medications")
public class MedicationRecordController {

    private final MedicationRecordService service;

    public MedicationRecordController(MedicationRecordService service) {
        this.service = service;
    }

    /** GET /api/medications — 获取当前登录用户的所有服药记录（身份来自 token，不再信前端参数） */
    @GetMapping
    public ResponseEntity<List<MedicationRecord>> listMine() {
        return ResponseEntity.ok(service.findByUserId(CurrentUser.id()));
    }

    /** GET /api/medications/public — 获取所有公开记录 */
    @GetMapping("/public")
    public ResponseEntity<List<MedicationRecord>> listPublic() {
        return ResponseEntity.ok(service.findPublic());
    }

    /** GET /api/medications/stats/risk — 当前用户的剂量风险统计 */
    @GetMapping("/stats/risk")
    public ResponseEntity<?> riskStats() {
        return ResponseEntity.ok(service.riskStats(CurrentUser.id()));
    }

    /** POST /api/medications — 新增服药记录（归属自动取当前登录用户） */
    @PostMapping
    public ResponseEntity<MedicationRecord> create(@RequestBody CreateRequest request) {
        if (request.medicineName() == null || request.medicineName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        MedicationRecord record = service.create(
                CurrentUser.id(),
                request.medicineName().trim(),
                request.dosage() != null ? request.dosage() : 1,
                request.takenAt() != null ? request.takenAt() : LocalDateTime.now(),
                request.isCommon(),
                request.isNormalDose(),
                request.privacy()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    /** PATCH /api/medications/{id}/thoughts — 更新心得（仅限本人） */
    @PatchMapping("/{id}/thoughts")
    public ResponseEntity<MedicationRecord> updateThoughts(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        String thoughts = body.getOrDefault("thoughts", "");
        return service.updateThoughts(CurrentUser.id(), id, thoughts)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/medications/archive — 归档当前用户所有未归档记录 */
    @PostMapping("/archive")
    public ResponseEntity<Map<String, Integer>> archiveAll() {
        int count = service.archiveAll(CurrentUser.id());
        return ResponseEntity.ok(Map.of("archivedCount", count));
    }

    /** DELETE /api/medications/{id} — 删除记录（仅限本人） */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(CurrentUser.id(), id);
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(String medicineName, Integer dosage,
                                 LocalDateTime takenAt, Boolean isCommon,
                                 Boolean isNormalDose,
                                 MedicationRecord.Privacy privacy) {}
}


