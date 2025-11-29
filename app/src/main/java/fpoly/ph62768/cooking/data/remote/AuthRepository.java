package fpoly.ph62768.cooking.data.remote;

import androidx.annotation.NonNull;

import fpoly.ph62768.cooking.data.remote.dto.AuthResponse;
import fpoly.ph62768.cooking.data.remote.dto.ForgotPasswordRequest;
import fpoly.ph62768.cooking.data.remote.dto.ForgotPasswordResponse;
import fpoly.ph62768.cooking.data.remote.dto.LoginRequest;
import fpoly.ph62768.cooking.data.remote.dto.RegisterRequest;
import fpoly.ph62768.cooking.network.ApiClient;
import retrofit2.Call;

public class AuthRepository {

    private final AuthApiService apiService;

    public AuthRepository() {
        this(ApiClient.getInstance().create(AuthApiService.class));
    }

    public AuthRepository(@NonNull AuthApiService apiService) {
        this.apiService = apiService;
    }

    public Call<AuthResponse> register(String name, String email, String password) {
        return apiService.register(new RegisterRequest(name, email, password));
    }

    public Call<AuthResponse> login(String email, String password) {
        return apiService.login(new LoginRequest(email, password));
    }

    public Call<ForgotPasswordResponse> forgotPassword(String email) {
        return apiService.forgotPassword(new ForgotPasswordRequest(email));
    }
}

