package fpoly.ph62768.cooking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recipe {
    private final String id;
    private final String name;
    private final String duration;
    private final double rating;
    private final RecipeCategory category;
    private final String imageUrl;
    private final String description;
    private final List<RecipeStep> steps;
    private final String authorEmail;
    private final String authorName;
    private final String backendCategoryName;

    public Recipe(String id,
                  String name,
                  String duration,
                  double rating,
                  RecipeCategory category,
                  String imageUrl,
                  String description,
                  List<RecipeStep> steps) {
        this(id, name, duration, rating, category, imageUrl, description, steps, null, null);
    }

    public Recipe(String id,
                  String name,
                  String duration,
                  double rating,
                  RecipeCategory category,
                  String imageUrl,
                  String description,
                  List<RecipeStep> steps,
                  String authorEmail,
                  String authorName) {
        this(id, name, duration, rating, category, imageUrl, description, steps, authorEmail, authorName,
                category != null ? category.getDisplayName() : "");
    }

    public Recipe(String id,
                  String name,
                  String duration,
                  double rating,
                  RecipeCategory category,
                  String imageUrl,
                  String description,
                  List<RecipeStep> steps,
                  String authorEmail,
                  String authorName,
                  String backendCategoryName) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.rating = rating;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
        this.authorEmail = authorEmail == null ? "" : authorEmail.trim().toLowerCase();
        this.authorName = authorName == null ? "" : authorName.trim();
        this.backendCategoryName = backendCategoryName == null ? "" : backendCategoryName.trim();
    }

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

    public RecipeCategory getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public List<RecipeStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getBackendCategoryName() {
        if (!backendCategoryName.isEmpty()) {
            return backendCategoryName;
        }
        return category != null ? category.getDisplayName() : "";
    }
}
