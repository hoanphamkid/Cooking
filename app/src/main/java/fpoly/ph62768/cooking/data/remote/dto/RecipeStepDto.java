package fpoly.ph62768.cooking.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class RecipeStepDto {

    @SerializedName("title")
    private final String title;
    @SerializedName("description")
    private final String description;
    @SerializedName("imageUrl")
    private final String imageUrl;

    public RecipeStepDto(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

