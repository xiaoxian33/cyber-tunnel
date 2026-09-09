package com.cybertunnel.controller;

import com.cybertunnel.config.CurrentUser;
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

    /** GET /api/memos — 获取当前登录用户的所有备忘录（身份来自 token） */
    @GetMapping
    public ResponseEntity<List<Memo>> listMine() {
        return ResponseEntity.ok(service.findByUserId(CurrentUser.id()));
    }

    /** POST /api/memos — 新增备忘录（归属自动取当前登录用户） */
    @PostMapping
    public ResponseEntity<Memo> create(@RequestBody CreateRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Memo memo = service.create(CurrentUser.id(), request.content().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(memo);
    }

    /** DELETE /api/memos/{id} — 删除单条备忘录（仅限本人） */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(CurrentUser.id(), id);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/memos — 清空当前登录用户的所有备忘录 */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        service.deleteAllByUserId(CurrentUser.id());
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(String content) {}
}
