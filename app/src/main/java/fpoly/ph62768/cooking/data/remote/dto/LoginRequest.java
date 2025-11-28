package fpoly.ph62768.cooking.data.remote.dto;

import androidx.annotation.NonNull;

public class LoginRequest {
    private final String email;
    private final String password;

    public LoginRequest(@NonNull String email, @NonNull String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}


