package com.example.web_security.restapi;

import com.example.web_security.Repo.CoffeRepository;
import com.example.web_security.model.Coffee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/home/tables")
public class AdminCoffeeTableRestController {

    @Autowired
    private CoffeRepository coffeRepository;

    // Kiểm tra quyền truy cập của admin
    private boolean isAdmin(Authentication authentication) {
        return authentication != null &&
                AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_ADMIN");
    }

    // Tạo bàn mới
    @PostMapping("/create")
    public ResponseEntity<String> createTable(@RequestBody Coffee coffee, Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Access Denied: You do not have admin privileges.");
        }

        // Kiểm tra xem bàn đã tồn tại với số bàn chưa
        if (coffeRepository.existsByNumber(coffee.getNumber())) {
            return ResponseEntity.badRequest().body("Bàn với số này đã tồn tại.");
        }
        coffee.setStatus("Chưa đặt");
        coffeRepository.save(coffee);
        return ResponseEntity.ok("Bàn đã được tạo thành công!");
    }

    // Hiển thị tất cả bàn
    @GetMapping("/all")
    public ResponseEntity<List<Coffee>> getAllTables() {
        return ResponseEntity.ok(coffeRepository.findAll());
    }

    // Lấy thông tin bàn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getTableById(@PathVariable Long id) {
        Coffee coffee = coffeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với id: " + id));
        return ResponseEntity.ok(coffee);
    }

    // Cập nhật thông tin bàn
    @PutMapping("/{id}/update")
    public ResponseEntity<String> updateTable(@PathVariable Long id, @RequestBody Coffee updatedTable,
            Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Access Denied: You do not have admin privileges.");
        }

        Coffee existing = coffeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với id: " + id));
        existing.setNumber(updatedTable.getNumber());
        existing.setNotes(updatedTable.getNotes()); // Đổi từ description thành notes
        existing.setStatus(updatedTable.getStatus()); // status giờ là String
        coffeRepository.save(existing);
        return ResponseEntity.ok("Cập nhật thông tin bàn thành công!");
    }

    // Xóa bàn
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteTable(@PathVariable Long id, Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Access Denied: You do not have admin privileges.");
        }

        if (!coffeRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Không tìm thấy bàn để xóa.");
        }

        coffeRepository.deleteById(id);
        return ResponseEntity.ok("Bàn đã được xóa thành công!");
    }
}
