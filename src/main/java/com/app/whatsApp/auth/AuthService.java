package com.app.whatsApp.auth;

import com.app.whatsApp.config.JwtService;
import com.app.whatsApp.user.User;
import com.app.whatsApp.user.UserDto;
import com.app.whatsApp.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    public AuthResponse register(AuthRegister authRegister, HttpServletResponse response) {
        User user = User.builder()
                .phone(authRegister.getPhone())
                .name(authRegister.getName())
                .imgUrl(authRegister.getImgUrl())
                .password(passwordEncoder.encode(authRegister.getPassword()))
                .roles(authRegister.getRoles())
                .build();
        userRepository.save(user);

        String msg = """
                Hi %s,
                
                Welcome, we're glad to have you 🎉🙏
                
                We're all a big familiy here, so make sure to upload your user photo so we get to know you a bit better!
                """.formatted(authRegister.getName());
        sendEmail(authRegister.getName().split(" ")[0].concat("@abufarha.com"), "Welcome, we are glad to have you", msg);

        String jwt = jwtService.generateToken(user);
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .imgUrl(authRegister.getImgUrl())
                .build();
        setCookie(response, jwt);
        return AuthResponse.builder().token(jwt).user(userDto).build();
    }

    public AuthResponse login(AuthLogin authLogin, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authLogin.getPhone(),
                        authLogin.getPassword()
                )
        );
        User user = userRepository.findUserByPhone(authLogin.getPhone()).orElseThrow();
        String jwt = jwtService.generateToken(user);

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .imgUrl(user.getImgUrl())
                .build();
        setCookie(response, jwt);
        return AuthResponse.builder().token(jwt).user(userDto).build();
    }

    public Map<String, Object> forgetPassword(String phone) {
        Map<String, Object> map = new LinkedHashMap<>();
        User user = userRepository.findUserByPhone("+" + phone).orElseThrow(() -> new UsernameNotFoundException("user is not exist"));
        String token = UUID.randomUUID().toString();
        user.setRestToken(token);

        user.setLocalDateToken(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        String url = "http://localhost:8080/api/v1/auth/restPassword?token=" + token;
        String msg = """
                Hi %s,
                
                Forgot your password? 
                
                Submit a PATCH request with your new password and passwordConfirm to:
                
                %s                
                """.formatted(user.getName(), url);
        sendEmail(user.getName().split(" ")[0].concat("@abufarha.com"), "forgot password will expier after 10m", msg);
        map.put("status", "success");
        map.put("message", "email sent");
        return map;
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setAttribute("SameSite", "None");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public AuthResponse restPassword(String token, AuthRegister authRegister, HttpServletResponse response) {
        User user = userRepository.findUserByRestToken(token);
        if (user == null || user.getLocalDateToken().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("the url is expired");
        }
        user.setPassword(passwordEncoder.encode(authRegister.getPassword()));
        user.setRestToken(null);
        user.setLocalDateToken(null);
        userRepository.save(user);
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .imgUrl(user.getImgUrl())
                .build();
        String jwt = jwtService.generateToken(user);
        setCookie(response, jwt);
        return AuthResponse.builder().user(userDto).token(jwt).build();
    }

    public void setCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setAttribute("SameSite", "None");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(90 * 24 * 60 * 60);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Gm@abufarha.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);

        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }


    }
}
