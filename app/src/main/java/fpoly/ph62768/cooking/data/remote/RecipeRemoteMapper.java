package fpoly.ph62768.cooking.data.remote;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fpoly.ph62768.cooking.data.remote.dto.RecipeResponse;
import fpoly.ph62768.cooking.data.remote.dto.RecipeStepDto;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.model.RecipeCategory;
import fpoly.ph62768.cooking.model.RecipeStep;

public final class RecipeRemoteMapper {

    private RecipeRemoteMapper() {
    }

    public static List<Recipe> toRecipes(List<RecipeResponse> remoteRecipes) {
        List<Recipe> results = new ArrayList<>();
        if (remoteRecipes == null) {
            return results;
        }
        for (RecipeResponse response : remoteRecipes) {
            Recipe recipe = toRecipe(response);
            if (recipe != null) {
                results.add(recipe);
            }
        }
        return results;
    }

    public static Recipe toRecipe(RecipeResponse response) {
        if (response == null) {
            return null;
        }
        String id = response.getId() != null ? response.getId() : "";
        String name = safeText(response.getName(), "Công thức mới");
        String duration = safeText(response.getDuration(), "20 phút");
        double rating = response.getRating();
        RecipeCategory category = mapCategory(response);
        String backendCategoryName = extractCategoryName(response);
        String imageUrl = safeText(response.getImageUrl(), "");
        String description = safeText(response.getDescription(), "");
        List<RecipeStep> steps = mapSteps(response.getSteps());
        String authorEmail = safeText(response.getAuthorEmail(), "");
        String authorName = safeText(response.getAuthorName(), "");
        return new Recipe(id, name, duration, rating, category, imageUrl, description, steps, authorEmail, authorName,
                backendCategoryName);
    }

    private static List<RecipeStep> mapSteps(List<RecipeStepDto> stepDtos) {
        List<RecipeStep> steps = new ArrayList<>();
        if (stepDtos == null || stepDtos.isEmpty()) {
            steps.add(new RecipeStep("Bước 1", "Đang cập nhật...", ""));
            return steps;
        }
        for (RecipeStepDto dto : stepDtos) {
            if (dto == null) continue;
            String title = safeText(dto.getTitle(), "Bước");
            String description = safeText(dto.getDescription(), "");
            String imageUrl = safeText(dto.getImageUrl(), "");
            steps.add(new RecipeStep(title, description, imageUrl));
        }
        return steps;
    }

    private static RecipeCategory mapCategory(RecipeResponse response) {
        String value = extractCategoryName(response);
        if (value == null) {
            return RecipeCategory.ALL;
        }
        String normalized = normalize(value);
        switch (normalized) {
            case "tat ca":
            case "all":
                return RecipeCategory.ALL;
            case "it calo":
            case "low cal":
            case "lowcal":
            case "eat clean":
                return RecipeCategory.LOW_CAL;
            case "healthy":
            case "lanh manh":
                return RecipeCategory.HEALTHY;
            case "nhanh":
            case "quick":
                return RecipeCategory.QUICK;
            case "truyen thong":
            case "traditional":
                return RecipeCategory.TRADITIONAL;
            case "trang mieng":
            case "dessert":
                return RecipeCategory.DESSERT;
            case "do uong":
            case "drink":
            case "thuc uong":
                return RecipeCategory.DRINK;
            default:
                return RecipeCategory.ALL;
        }
    }

    private static String extractCategoryName(RecipeResponse response) {
        if (response == null) {
            return "";
        }
        String value = response.getCategory();
        if ((value == null || value.trim().isEmpty()) && response.getCategoryPayload() != null) {
            value = response.getCategoryPayload().getName();
        }
        return value == null ? "" : value.trim();
    }

    private static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.trim().toLowerCase(Locale.US);
    }

    private static String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}


