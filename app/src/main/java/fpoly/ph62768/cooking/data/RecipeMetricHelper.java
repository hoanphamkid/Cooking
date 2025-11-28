package fpoly.ph62768.cooking.data;

import androidx.annotation.NonNull;

import fpoly.ph62768.cooking.model.Recipe;

public final class RecipeMetricHelper {

    public enum Metric {
        VIEWS,
        FAVORITES,
        FEEDBACKS
    }

    private RecipeMetricHelper() {
    }

    public static int calculateBaseMetric(@NonNull Recipe recipe, @NonNull Metric metric) {
        int seed = Math.abs(recipe.getId().hashCode());
        double ratingWeight = recipe.getRating() * 40;
        switch (metric) {
            case FAVORITES:
                return (int) (50 + ratingWeight / 2 + (seed % 150));
            case FEEDBACKS:
                return (int) (20 + ratingWeight / 3 + (seed % 60));
            case VIEWS:
            default:
                return (int) (200 + ratingWeight + (seed % 600));
        }
    }

    public static int calculateTotalMetric(@NonNull Recipe recipe,
                                           @NonNull Metric metric,
                                           @NonNull RecipeStatsStore statsStore) {
        int base = calculateBaseMetric(recipe, metric);
        switch (metric) {
            case FAVORITES:
                return Math.max(0, base + statsStore.getFavoriteDelta(recipe.getId()));
            case FEEDBACKS:
                return Math.max(0, base + statsStore.getFeedbackDelta(recipe.getId()));
            case VIEWS:
            default:
                return statsStore.getViewTotal(recipe.getId(), base);
        }
    }

    public static int calculateTotalViews(@NonNull Recipe recipe,
                                          @NonNull RecipeStatsStore statsStore) {
        return calculateTotalMetric(recipe, Metric.VIEWS, statsStore);
    }
}

