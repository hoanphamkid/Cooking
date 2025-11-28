package fpoly.ph62768.cooking.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeResponse {

    @SerializedName("_id")
    private String id;
    private String name;
    private String duration;
    private double rating;
    private String description;
    private String imageUrl;
    private String category;
    private String authorEmail;
    private String authorName;
    private List<RecipeStepDto> steps;
    private CategoryPayload categoryid;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAuthorName() {
        return authorName;
    }

    public List<RecipeStepDto> getSteps() {
        return steps;
    }

    public CategoryPayload getCategoryPayload() {
        return categoryid;
    }

    public static class CategoryPayload {
        @SerializedName("_id")
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}



