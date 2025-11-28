package fpoly.ph62768.cooking.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class AuthResponse {

    private AuthUser user;
    private String token;
    private String message;

    @Nullable
    public AuthUser getUser() {
        return user;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public static class AuthUser {
        @SerializedName("id")
        private String id;
        @SerializedName("_id")
        private String legacyId;
        private String name;
        private String email;
        private String role;

        @Nullable
        public String getId() {
            return id != null ? id : legacyId;
        }

        @Nullable
        public String getName() {
            return name;
        }

        @Nullable
        public String getEmail() {
            return email;
        }

        @Nullable
        public String getRole() {
            return role;
        }

        public boolean isAdmin() {
            return "admin".equalsIgnoreCase(role);
        }
    }
}


