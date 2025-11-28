package fpoly.ph62768.cooking.data.remote;

import fpoly.ph62768.cooking.data.remote.dto.RecipeListResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeApiService {

    @GET("asm/recipes")
    Call<RecipeListResponse> getRecipes();
}



