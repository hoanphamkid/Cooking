package fpoly.ph62768.cooking.data.remote.dto;

import androidx.annotation.NonNull;

public class RegisterRequest {
    private final String name;
    private final String email;
    private final String password;

    public RegisterRequest(@NonNull String name, @NonNull String email, @NonNull String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}


