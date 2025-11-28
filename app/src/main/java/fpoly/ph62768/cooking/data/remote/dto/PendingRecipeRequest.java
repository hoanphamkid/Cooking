package fpoly.ph62768.cooking.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PendingRecipeRequest {

    @SerializedName("name")
    private final String name;
    @SerializedName("description")
    private final String description;
    @SerializedName("duration")
    private final String duration;
    @SerializedName("category")
    private final String category;
    @SerializedName("imageUrl")
    private final String imageUrl;
    @SerializedName("steps")
    private final List<RecipeStepDto> steps;

    public PendingRecipeRequest(String name,
                                String description,
                                String duration,
                                String category,
                                String imageUrl,
                                List<RecipeStepDto> steps) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.imageUrl = imageUrl;
        this.steps = steps;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public List<RecipeStepDto> getSteps() {
        return steps;
    }
}

