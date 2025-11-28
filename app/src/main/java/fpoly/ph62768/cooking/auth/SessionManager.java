package fpoly.ph62768.cooking.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

public class SessionManager {

    private static final String PREFS_NAME = "remote_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public SessionManager(@NonNull Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String name, String email, String role) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email != null ? email.trim().toLowerCase() : "")
                .putString(KEY_ROLE, role)
                .apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getToken());
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(getRole());
    }
}


