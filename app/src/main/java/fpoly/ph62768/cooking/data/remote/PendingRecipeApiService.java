package fpoly.ph62768.cooking.data.remote;

import java.util.List;

import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeDecisionRequest;
import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeRequest;
import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PendingRecipeApiService {

    @POST("recipes/pending")
    Call<PendingRecipeResponse> submitPendingRecipe(
            @Header("Authorization") String authHeader,
            @Body PendingRecipeRequest request
    );

    @GET("recipes/pending")
    Call<List<PendingRecipeResponse>> getPendingRecipes(
            @Header("Authorization") String authHeader,
            @Query("status") String status,
            @Query("authorEmail") String authorEmail
    );

    @POST("recipes/pending/{id}/decision")
    Call<PendingRecipeResponse> decidePendingRecipe(
            @Header("Authorization") String authHeader,
            @Path("id") String recipeId,
            @Body PendingRecipeDecisionRequest request
    );

    @DELETE("recipes/pending/{id}")
    Call<Void> deletePendingRecipe(
            @Header("Authorization") String authHeader,
            @Path("id") String recipeId
    );
}

