package com.app.whatsApp.auth;

import com.app.whatsApp.user.Roles;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegister {
    private String name;
    private String phone;
    private String password;
    private String passwordConfirm;
    private String imgUrl;
    private Roles roles = Roles.ROLE_USER;

    @AssertTrue(message = "password is not match")
    public boolean isPasswordConfirmMatch() {
        if (password == null && passwordConfirm == null) return false;
        return password.equals(passwordConfirm);
    }
}
