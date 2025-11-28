package fpoly.ph62768.cooking.data.remote.dto;

import androidx.annotation.Nullable;

public class ForgotPasswordResponse {
    private String message;
    private String temporaryPassword;

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public String getTemporaryPassword() {
        return temporaryPassword;
    }
}

