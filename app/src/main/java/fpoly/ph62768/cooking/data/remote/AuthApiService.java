package fpoly.ph62768.cooking.data.remote;

import fpoly.ph62768.cooking.data.remote.dto.AuthResponse;
import fpoly.ph62768.cooking.data.remote.dto.ForgotPasswordRequest;
import fpoly.ph62768.cooking.data.remote.dto.ForgotPasswordResponse;
import fpoly.ph62768.cooking.data.remote.dto.LoginRequest;
import fpoly.ph62768.cooking.data.remote.dto.RegisterRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);
}


