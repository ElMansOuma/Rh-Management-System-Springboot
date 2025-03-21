package com.example.personnel_management.controller;




import com.example.personnel_management.DTO.AuthAdminResponse;
import com.example.personnel_management.DTO.LoginAdminRequest;
import com.example.personnel_management.DTO.RegisterAdminRequest;
import com.example.personnel_management.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AdminService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthAdminResponse> register(@Valid @RequestBody RegisterAdminRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthAdminResponse> login(@Valid @RequestBody LoginAdminRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}