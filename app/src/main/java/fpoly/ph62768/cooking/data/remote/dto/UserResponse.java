package fpoly.ph62768.cooking.data.remote.dto;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UserResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("_id")
    private String legacyId;
    private String name;
    private String email;
    private String role;
    @SerializedName("createdAt")
    private String createdAtIso;
    @SerializedName("updatedAt")
    private String updatedAtIso;

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

    public long getCreatedAtMillis() {
        return parseIsoToMillis(createdAtIso);
    }

    public long getUpdatedAtMillis() {
        return parseIsoToMillis(updatedAtIso);
    }

    @Nullable
    public String getCreatedAtRaw() {
        return createdAtIso;
    }

    @Nullable
    public String getUpdatedAtRaw() {
        return updatedAtIso;
    }

    private static final String[] ISO_PATTERNS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
    };

    private long parseIsoToMillis(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return 0L;
        }
        try {
            if (isNumeric(value)) {
                return Long.parseLong(value);
            }
            for (String pattern : ISO_PATTERNS) {
                try {
                    SimpleDateFormat isoFormat = new SimpleDateFormat(pattern, Locale.US);
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = isoFormat.parse(value);
                    if (date != null) {
                        return date.getTime();
                    }
                } catch (ParseException ignore) {
                    // Try next pattern
                }
            }
        } catch (NumberFormatException ignored) {
            // Fall through to default
        }
        return 0L;
    }

    private boolean isNumeric(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}

