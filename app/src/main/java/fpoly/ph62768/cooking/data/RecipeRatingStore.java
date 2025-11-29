package fpoly.ph62768.cooking.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RecipeRatingStore {

    private static final String PREF_NAME = "recipe_ratings";
    private static final String KEY_PREFIX = "rating_";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Float>>() {}.getType();

    public RecipeRatingStore(@NonNull Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public float getUserRating(@NonNull String recipeId, @NonNull String email) {
        Map<String, Float> ratings = loadMap(recipeId);
        Float value = ratings.get(normalizeEmail(email));
        return value != null ? value : 0f;
    }

    public void setUserRating(@NonNull String recipeId,
                              @NonNull String email,
                              float rating) {
        Map<String, Float> ratings = loadMap(recipeId);
        ratings.put(normalizeEmail(email), rating);
        saveMap(recipeId, ratings);
    }

    public float getAverageRating(@NonNull String recipeId, float fallback) {
        Map<String, Float> ratings = loadMap(recipeId);
        if (ratings.isEmpty()) {
            return fallback;
        }
        float sum = 0f;
        for (float value : ratings.values()) {
            sum += value;
        }
        float userAverage = sum / ratings.size();
        return (fallback + userAverage) / 2f;
    }

    public int getRatingCount(@NonNull String recipeId) {
        return loadMap(recipeId).size();
    }

    private Map<String, Float> loadMap(String recipeId) {
        String raw = prefs.getString(buildKey(recipeId), null);
        if (raw == null || raw.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Float> map = gson.fromJson(raw, mapType);
        if (map == null) {
            return new HashMap<>();
        }
        return new HashMap<>(map);
    }

    private void saveMap(String recipeId, Map<String, Float> map) {
        prefs.edit()
                .putString(buildKey(recipeId), gson.toJson(map))
                .apply();
    }

    @NonNull
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String buildKey(String recipeId) {
        return KEY_PREFIX + recipeId;
    }
}

