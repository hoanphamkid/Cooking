package fpoly.ph62768.cooking.data.remote;

import java.util.List;

import fpoly.ph62768.cooking.data.remote.dto.UserResponse;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface UserApiService {

    @GET("auth/users")
    Call<List<UserResponse>> getUsers();

    @DELETE("auth/users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String authHeader, @Path("id") String userId);
}

