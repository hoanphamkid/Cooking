package fpoly.ph62768.cooking.data.remote;

import androidx.annotation.Nullable;

import java.util.List;

import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeDecisionRequest;
import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeRequest;
import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeResponse;
import fpoly.ph62768.cooking.network.ApiClient;
import retrofit2.Call;

public class PendingRecipeRepository {

    private final PendingRecipeApiService apiService;

    public PendingRecipeRepository() {
        apiService = ApiClient.getInstance().create(PendingRecipeApiService.class);
    }

    private String buildAuthHeader(@Nullable String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return "Bearer " + token;
    }

    public Call<PendingRecipeResponse> submitRecipe(String token, PendingRecipeRequest request) {
        return apiService.submitPendingRecipe(buildAuthHeader(token), request);
    }

    public Call<List<PendingRecipeResponse>> getPendingRecipes(String token,
                                                               @Nullable String status,
                                                               @Nullable String authorEmail) {
        return apiService.getPendingRecipes(buildAuthHeader(token), status, authorEmail);
    }

    public Call<PendingRecipeResponse> approve(String token, String recipeId) {
        return apiService.decidePendingRecipe(
                buildAuthHeader(token),
                recipeId,
                PendingRecipeDecisionRequest.approve()
        );
    }

    public Call<PendingRecipeResponse> reject(String token, String recipeId, @Nullable String reason) {
        return apiService.decidePendingRecipe(
                buildAuthHeader(token),
                recipeId,
                PendingRecipeDecisionRequest.reject(reason)
        );
    }

    public Call<Void> delete(String token, String recipeId) {
        return apiService.deletePendingRecipe(buildAuthHeader(token), recipeId);
    }
}

