package com.cybertunnel.controller;

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

    /** GET /api/medications?userId=1 — 获取用户所有服药记录 */
    @GetMapping
    public ResponseEntity<List<MedicationRecord>> listByUser(@RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    /** GET /api/medications/public — 获取所有公开记录 */
    @GetMapping("/public")
    public ResponseEntity<List<MedicationRecord>> listPublic() {
        return ResponseEntity.ok(service.findPublic());
    }

    /** GET /api/medications/stats/risk?userId=1 — 剂量风险统计 */
    @GetMapping("/stats/risk")
    public ResponseEntity<?> riskStats(@RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(service.riskStats(userId));
    }

    /** POST /api/medications — 新增服药记录 */
    @PostMapping
    public ResponseEntity<MedicationRecord> create(@RequestBody CreateRequest request) {
        if (request.medicineName() == null || request.medicineName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        MedicationRecord record = service.create(
                request.userId() != null ? request.userId() : 1L,
                request.medicineName().trim(),
                request.dosage() != null ? request.dosage() : 1,
                request.takenAt() != null ? request.takenAt() : LocalDateTime.now(),
                request.isCommon(),
                request.isNormalDose(),
                request.privacy()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    /** PATCH /api/medications/{id}/thoughts — 更新心得 */
    @PatchMapping("/{id}/thoughts")
    public ResponseEntity<MedicationRecord> updateThoughts(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        String thoughts = body.getOrDefault("thoughts", "");
        return service.updateThoughts(id, thoughts)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/medications/archive?userId=1 — 归档所有未归档记录 */
    @PostMapping("/archive")
    public ResponseEntity<Map<String, Integer>> archiveAll(@RequestParam(defaultValue = "1") Long userId) {
        int count = service.archiveAll(userId);
        return ResponseEntity.ok(Map.of("archivedCount", count));
    }

    /** DELETE /api/medications/{id} — 删除记录 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(Long userId, String medicineName, Integer dosage,
                                 LocalDateTime takenAt, Boolean isCommon,
                                 Boolean isNormalDose,
                                 MedicationRecord.Privacy privacy) {}
}


