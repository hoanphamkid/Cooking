package fpoly.ph62768.cooking.data.remote;

import fpoly.ph62768.cooking.data.remote.dto.CategoryListResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApiService {

    @GET("asm/get-all-danhmuc")
    Call<CategoryListResponse> getCategories();
}



