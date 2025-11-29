package fpoly.ph62768.cooking.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RecipeStatsStore {

    private static final String PREF_NAME = "recipe_stats_store";
    private static final String KEY_VIEWS = "key_views";
    private static final String KEY_FAVORITES = "key_favorites";
    private static final String KEY_FEEDBACKS = "key_feedbacks";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();

    public RecipeStatsStore(@NonNull Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void incrementView(@NonNull String recipeId, int baseValue) {
        int current = ensureViewBase(recipeId, baseValue);
        setValue(KEY_VIEWS, recipeId, current + 1);
    }

    public int getViewTotal(@NonNull String recipeId, int baseValue) {
        return ensureViewBase(recipeId, baseValue);
    }

    public void setFavoriteState(@NonNull String recipeId, boolean favorite) {
        setValue(KEY_FAVORITES, recipeId, favorite ? 1 : 0);
    }

    public int getFavoriteDelta(@NonNull String recipeId) {
        return getValue(KEY_FAVORITES, recipeId);
    }

    public void incrementFeedback(@NonNull String recipeId) {
        increment(KEY_FEEDBACKS, recipeId, 1);
    }

    public int getFeedbackDelta(@NonNull String recipeId) {
        return getValue(KEY_FEEDBACKS, recipeId);
    }

    private void increment(String key, String recipeId, int delta) {
        Map<String, Integer> map = loadMap(key);
        int current = map.getOrDefault(recipeId, 0);
        map.put(recipeId, Math.max(0, current + delta));
        saveMap(key, map);
    }

    private void setValue(String key, String recipeId, int value) {
        Map<String, Integer> map = loadMap(key);
        map.put(recipeId, Math.max(0, value));
        saveMap(key, map);
    }

    private int getValue(String key, String recipeId) {
        Map<String, Integer> map = loadMap(key);
        return map.getOrDefault(recipeId, 0);
    }

    private Map<String, Integer> loadMap(String key) {
        String raw = prefs.getString(key, null);
        if (raw == null || raw.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Integer> map = gson.fromJson(raw, mapType);
        if (map == null) {
            return new HashMap<>();
        }
        return new HashMap<>(map);
    }

    private void saveMap(String key, Map<String, Integer> map) {
        prefs.edit().putString(key, gson.toJson(map)).apply();
    }

    private int ensureViewBase(@NonNull String recipeId, int baseValue) {
        Map<String, Integer> map = loadMap(KEY_VIEWS);
        int current = map.getOrDefault(recipeId, 0);
        int target = Math.max(baseValue, 0);
        if (current < target) {
            current = target;
            map.put(recipeId, current);
            saveMap(KEY_VIEWS, map);
        }
        return current;
    }
}

