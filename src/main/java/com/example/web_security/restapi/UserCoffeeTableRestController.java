package com.example.web_security.restapi;

import com.example.web_security.Repo.CoffeRepository;
import com.example.web_security.model.Coffee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/home/tables")
public class UserCoffeeTableRestController {

    @Autowired
    private CoffeRepository coffeRepository;

    // Lấy danh sách tất cả bàn có trạng thái "Chưa đặt"
    @GetMapping("/all")
    public ResponseEntity<List<Coffee>> getAllTablesForUser() {
        return ResponseEntity.ok(coffeRepository.findAll());
    }

    // Đặt bàn
    @PutMapping("/{id}/reserve")
    public ResponseEntity<String> reserveTable(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        Coffee coffee = coffeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với id: " + id));

        // Nếu bàn đã được đặt
        if ("Đã đặt".equalsIgnoreCase(coffee.getStatus())) {
            return ResponseEntity.status(400).body("Bàn này đã được đặt.");
        }

        // Lấy notes mới từ request body
        String newNote = requestBody.get("notes");

        // Cập nhật status và notes
        coffee.setStatus("Đã đặt");
        if (newNote != null) {
            coffee.setNotes(newNote);
        }

        coffeRepository.save(coffee);
        return ResponseEntity.ok("Bàn đã được đặt thành công và ghi chú đã được cập nhật!");
    }

}
