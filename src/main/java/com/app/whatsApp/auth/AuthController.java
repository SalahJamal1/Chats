package com.app.whatsApp.auth;

import com.app.whatsApp.user.User;
import com.app.whatsApp.user.UserDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody AuthRegister authRegister, HttpServletResponse response) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.register(authRegister, response));

    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLogin authLogin, HttpServletResponse response) {
        try {


            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(service.login(authLogin, response));
        } catch (Exception exc) {
            throw new RuntimeException("the user or password is wrong");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "you are logged out successfully");
        service.logout(response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(map);
    }

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrent(@AuthenticationPrincipal User user) {
        if (user != null) {
            UserDto userDto = UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .imgUrl(user.getImgUrl())
                    .build();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(userDto);
        } else {
            throw new RuntimeException("you are not authenticated");

        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<Map<String, Object>> forgetPassword(@RequestParam String phone) {

        return ResponseEntity.ok().body(service.forgetPassword(phone));
    }

    @PatchMapping("/restPassword")
    public ResponseEntity<AuthResponse> restPassword(@Valid @RequestParam String token, @RequestBody AuthRegister authRegister, HttpServletResponse response) {

        return ResponseEntity.ok().body(service.restPassword(token, authRegister, response));
    }
}
