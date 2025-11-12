package fpoly.ph62768.cooking.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipePreferenceStore {

    private static final String PREF_NAME = "recipe_preferences";
    private static final String KEY_SAVED = "saved_recipes";
    private static final String KEY_FAVORITES = "favorite_recipes";
    private static final String KEY_HISTORY = "history_recipes";
    private static final int HISTORY_LIMIT = 30;

    private final SharedPreferences sharedPreferences;

    public RecipePreferenceStore(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isSaved(@NonNull String recipeId) {
        return getStringSet(KEY_SAVED).contains(recipeId);
    }

    public boolean isFavorite(@NonNull String recipeId) {
        return getStringSet(KEY_FAVORITES).contains(recipeId);
    }

    public boolean toggleSaved(@NonNull String recipeId) {
        return toggleValue(KEY_SAVED, recipeId);
    }

    public boolean toggleFavorite(@NonNull String recipeId) {
        return toggleValue(KEY_FAVORITES, recipeId);
    }

    public List<String> getSavedRecipeIds() {
        return new ArrayList<>(getStringSet(KEY_SAVED));
    }

    public List<String> getFavoriteRecipeIds() {
        return new ArrayList<>(getStringSet(KEY_FAVORITES));
    }

    public List<String> getHistoryRecipeIds() {
        return new ArrayList<>(getHistoryIdsInternal());
    }

    public void addToHistory(@NonNull String recipeId) {
        List<String> history = new ArrayList<>(getHistoryIdsInternal());
        history.remove(recipeId);
        history.add(0, recipeId);
        if (history.size() > HISTORY_LIMIT) {
            history = history.subList(0, HISTORY_LIMIT);
        }
        sharedPreferences.edit()
                .putString(KEY_HISTORY, serializeList(history))
                .apply();
    }

    public void clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply();
    }

    private boolean toggleValue(String key, String recipeId) {
        Set<String> current = new HashSet<>(getStringSet(key));
        boolean added;
        if (current.contains(recipeId)) {
            current.remove(recipeId);
            added = false;
        } else {
            current.add(recipeId);
            added = true;
        }
        sharedPreferences.edit().putStringSet(key, current).apply();
        return added;
    }

    private Set<String> getStringSet(String key) {
        Set<String> value = sharedPreferences.getStringSet(key, null);
        if (value == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(value);
    }

    private List<String> getHistoryIdsInternal() {
        String raw = sharedPreferences.getString(KEY_HISTORY, "");
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] parts = raw.split(",");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && !result.contains(trimmed)) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private String serializeList(List<String> values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(values.get(i));
        }
        return builder.toString();
    }
}

