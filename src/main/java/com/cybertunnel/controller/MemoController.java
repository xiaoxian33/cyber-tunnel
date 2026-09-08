package com.cybertunnel.controller;

import com.cybertunnel.model.Memo;
import com.cybertunnel.service.MemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    private final MemoService service;

    public MemoController(MemoService service) {
        this.service = service;
    }

    /** GET /api/memos?userId=1 — 获取所有备忘录 */
    @GetMapping
    public ResponseEntity<List<Memo>> listByUser(@RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    /** POST /api/memos — 新增备忘录 */
    @PostMapping
    public ResponseEntity<Memo> create(@RequestBody CreateRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Memo memo = service.create(
                request.userId() != null ? request.userId() : 1L,
                request.content().trim()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(memo);
    }

    /** DELETE /api/memos/{id} — 删除单条备忘录 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/memos?userId=1 — 清空用户所有备忘录 */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestParam(defaultValue = "1") Long userId) {
        service.deleteAllByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(Long userId, String content) {}
}
