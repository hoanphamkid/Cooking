package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import fpoly.ph62768.cooking.auth.SessionManager;
import fpoly.ph62768.cooking.data.remote.AuthRepository;
import fpoly.ph62768.cooking.data.remote.dto.AuthResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiaoDienChinhActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_chinh);

        TextInputEditText emailInput = findViewById(R.id.email_input);
        TextInputEditText passwordInput = findViewById(R.id.password_input);
        MaterialButton loginButton = findViewById(R.id.login_button);

        AuthRepository authRepository = new AuthRepository();
        SessionManager sessionManager = new SessionManager(this);

        TextView registerText = findViewById(R.id.register_text);
        String prompt = getString(R.string.login_register_prompt);
        SpannableString spannable = new SpannableString(prompt);
        int start = prompt.indexOf("Đăng ký");
        if (start >= 0) {
            int end = start + "Đăng ký ngay".length();
            spannable.setSpan(new ForegroundColorSpan(0xFFFF7A33), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        registerText.setText(spannable);
        registerText.setOnClickListener(v -> {
            startActivity(new Intent(this, GiaoDienDangKyActivity.class));
        });

        TextView forgotPasswordText = findViewById(R.id.forgot_password_text);
        forgotPasswordText.setTextColor(0xFFFF7A33);
        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, GiaoDienQuenMatKhauActivity.class));
        });

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

            boolean hasError = false;
            if (TextUtils.isEmpty(email)) {
                emailInput.setError(getString(R.string.error_email_required));
                hasError = true;
            } else {
                emailInput.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError(getString(R.string.error_password_required));
                hasError = true;
            } else {
                passwordInput.setError(null);
            }

            if (hasError) {
                return;
            }

            setLoadingState(loginButton, true);
            authRepository.login(email, password).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    setLoadingState(loginButton, false);
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse body = response.body();
                        AuthResponse.AuthUser user = body.getUser();
                        if (user == null || TextUtils.isEmpty(body.getToken())) {
                            showError(response, getString(R.string.login_account_not_found));
                            return;
                        }
                        sessionManager.saveSession(
                                body.getToken(),
                                user.getName() != null ? user.getName() : "",
                                user.getEmail() != null ? user.getEmail() : email,
                                user.getRole()
                        );
                        Intent destination = sessionManager.isAdmin()
                                ? new Intent(GiaoDienChinhActivity.this, ManHinhQuanTriActivity.class)
                                : new Intent(GiaoDienChinhActivity.this, GiaoDienTrangChuActivity.class);
                        destination.putExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL, sessionManager.getEmail());
                        destination.putExtra(GiaoDienTrangChuActivity.EXTRA_USER_NAME, sessionManager.getName());
                        startActivity(destination);
                        Toast.makeText(GiaoDienChinhActivity.this, R.string.login_success_message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError(response, null);
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    setLoadingState(loginButton, false);
                    Toast.makeText(GiaoDienChinhActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setLoadingState(MaterialButton button, boolean loading) {
        button.setEnabled(!loading);
        button.setText(loading ? getString(R.string.loading) : getString(R.string.login_button_text));
    }

    private void showError(Response<?> response, String fallback) {
        String message = fallback;
        if (message == null && response != null && response.errorBody() != null) {
            try {
                message = response.errorBody().string();
            } catch (Exception ignored) {
            }
        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.login_account_not_found);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

