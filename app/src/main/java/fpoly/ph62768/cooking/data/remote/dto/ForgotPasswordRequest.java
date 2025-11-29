package fpoly.ph62768.cooking.data.remote.dto;

import androidx.annotation.NonNull;

public class ForgotPasswordRequest {
    private final String email;

    public ForgotPasswordRequest(@NonNull String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}


