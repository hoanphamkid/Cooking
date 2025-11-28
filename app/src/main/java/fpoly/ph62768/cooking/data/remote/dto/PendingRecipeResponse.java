package fpoly.ph62768.cooking.data.remote.dto;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PendingRecipeResponse {

    @SerializedName("id")
    private String id;
    private String name;
    private String description;
    private String duration;
    private String category;
    private String imageUrl;
    private List<RecipeStepDto> steps;
    private String status;
    private String authorEmail;
    private String authorName;
    private String reviewerName;
    private String reviewerEmail;
    private String rejectionReason;
    @SerializedName("createdAt")
    private String createdAtIso;
    @SerializedName("updatedAt")
    private String updatedAtIso;

    private static final String[] ISO_PATTERNS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<RecipeStepDto> getSteps() {
        return steps;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Nullable
    public String getReviewerName() {
        return reviewerName;
    }

    @Nullable
    public String getReviewerEmail() {
        return reviewerEmail;
    }

    @Nullable
    public String getRejectionReason() {
        return rejectionReason;
    }

    public long getCreatedAtMillis() {
        return parseIso(createdAtIso);
    }

    public long getUpdatedAtMillis() {
        return parseIso(updatedAtIso);
    }

    private long parseIso(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return 0L;
        }
        for (String pattern : ISO_PATTERNS) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = format.parse(value);
                if (date != null) {
                    return date.getTime();
                }
            } catch (ParseException ignored) {
            }
        }
        return 0L;
    }
}

