package fpoly.ph62768.cooking.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

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
            account = new UserAccount("", DEFAULT_PASSWORD, System.currentTimeMillis());
        } else {
            account.setPassword(DEFAULT_PASSWORD);
            if (account.getCreatedAt() == 0L) {
                account.setCreatedAt(System.currentTimeMillis());
            }
        }
        saveAccountInternal(normalizedEmail, account);
    }

    public void saveAccount(@NonNull String name, @NonNull String email, @NonNull String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return;
        }
        UserAccount account = new UserAccount(name, password, System.currentTimeMillis());
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
            ensureDefaults(account);
            return account;
        } catch (JsonSyntaxException ex) {
            // legacy format: password stored as plain string
            return new UserAccount("", raw, System.currentTimeMillis());
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

    public Map<String, UserAccount> getAllAccounts() {
        Map<String, ?> all = sharedPreferences.getAll();
        Map<String, UserAccount> result = new HashMap<>();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof String)) {
                continue;
            }
            String raw = (String) value;
            try {
                UserAccount account = gson.fromJson(raw, UserAccount.class);
                if (account != null) {
                    ensureDefaults(account);
                    result.put(entry.getKey(), account);
                }
            } catch (JsonSyntaxException ignore) {
                UserAccount legacy = new UserAccount();
                legacy.setName("");
                legacy.setPassword(raw);
                legacy.setCreatedAt(System.currentTimeMillis());
                result.put(entry.getKey(), legacy);
            }
        }
        return result;
    }

    public void removeAccount(@NonNull String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return;
        }
        sharedPreferences.edit().remove(normalizedEmail).apply();
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

    public boolean updateAccount(UserAccount account) {
        // Logic cập nhật account
        return false;
    }

    private void ensureDefaults(UserAccount account) {
        if (account.getName() == null) {
            account.setName("");
        }
        if (account.getPassword() == null) {
            account.setPassword(DEFAULT_PASSWORD);
        }
        if (account.getCreatedAt() == 0L) {
            account.setCreatedAt(System.currentTimeMillis());
        }
    }

    public String getCurrentUserName(@NonNull Context context) {
        return context.getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("current_user_name", "");
    }
}

