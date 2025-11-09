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

    public Recipe(String id,
                  String name,
                  String duration,
                  double rating,
                  RecipeCategory category,
                  String imageUrl,
                  String description,
                  List<RecipeStep> steps) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.rating = rating;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
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
}
