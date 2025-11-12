package fpoly.ph62768.cooking.model;

import androidx.annotation.NonNull;

public class RecipeFeedback {

    private final String userName;
    private final float rating;
    private final String comment;
    private final String createdAt;

    public RecipeFeedback(@NonNull String userName,
                          float rating,
                          @NonNull String comment,
                          @NonNull String createdAt) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return rating;
    }

    @NonNull
    public String getComment() {
        return comment;
    }

    @NonNull
    public String getCreatedAt() {
        return createdAt;
    }
}

