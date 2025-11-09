package fpoly.ph62768.cooking.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UserAccountManager {

    private static final String PREFS_NAME = "user_accounts";
    public static final String DEFAULT_PASSWORD = "123456";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public UserAccountManager(@NonNull Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void resetPassword(@NonNull String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return;
        }
        UserAccount account = getAccount(normalizedEmail);
        if (account == null) {
            account = new UserAccount("", DEFAULT_PASSWORD);
        } else {
            account.setPassword(DEFAULT_PASSWORD);
        }
        saveAccountInternal(normalizedEmail, account);
    }

    public void saveAccount(@NonNull String name, @NonNull String email, @NonNull String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return;
        }
        UserAccount account = new UserAccount(name, password);
        saveAccountInternal(normalizedEmail, account);
    }

    public UserAccount getAccount(@NonNull String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return null;
        }
        String raw = sharedPreferences.getString(normalizedEmail, null);
        if (raw == null) {
            return null;
        }
        try {
            UserAccount account = gson.fromJson(raw, UserAccount.class);
            if (account == null) {
                return null;
            }
            if (account.getName() == null) {
                account.setName("");
            }
            return account;
        } catch (JsonSyntaxException ex) {
            // legacy format: password stored as plain string
            return new UserAccount("", raw);
        }
    }

    public String getPassword(@NonNull String email) {
        UserAccount account = getAccount(email);
        return account != null ? account.getPassword() : null;
    }

    public void ensureAccount(@NonNull String name, @NonNull String email, @NonNull String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return;
        }
        if (!sharedPreferences.contains(normalizedEmail)) {
            saveAccount(name, email, password);
        }
    }

    private void saveAccountInternal(String normalizedEmail, UserAccount account) {
        sharedPreferences.edit()
                .putString(normalizedEmail, gson.toJson(account))
                .apply();
    }

    public void setCurrentUser(@NonNull Context context, @NonNull String email) {
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
                .edit()
                .putString("current_user_email", normalizeEmail(email))
                .apply();
    }

    public void clearCurrentUser(@NonNull Context context) {
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
                .edit()
                .remove("current_user_email")
                .apply();
    }

    public String getCurrentUserEmail(@NonNull Context context) {
        return context.getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("current_user_email", "");
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}

