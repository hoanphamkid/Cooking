package fpoly.ph62768.cooking;

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

import fpoly.ph62768.cooking.data.remote.AuthRepository;
import fpoly.ph62768.cooking.data.remote.dto.ForgotPasswordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiaoDienQuenMatKhauActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_quen_mat_khau);

        TextInputEditText emailInput = findViewById(R.id.forgot_email_input);
        MaterialButton sendButton = findViewById(R.id.forgot_send_button);
        TextView backToRegister = findViewById(R.id.back_to_register_text);

        String backPrompt = getString(R.string.forgot_back_to_register);
        SpannableString spannable = new SpannableString(backPrompt);
        int start = backPrompt.indexOf("Đăng ký");
        if (start >= 0) {
            int end = start + "Đăng ký".length();
            spannable.setSpan(new ForegroundColorSpan(0xFFFF7A33), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        backToRegister.setText(spannable);
        backToRegister.setOnClickListener(v -> finish());

        AuthRepository authRepository = new AuthRepository();

        sendButton.setOnClickListener(v -> {
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            if (TextUtils.isEmpty(email)) {
                emailInput.setError(getString(R.string.error_email_required));
                return;
            }
            setLoading(sendButton, true);
            authRepository.forgotPassword(email).enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                    setLoading(sendButton, false);
                    if (response.isSuccessful() && response.body() != null) {
                        ForgotPasswordResponse body = response.body();
                        String temp = body.getTemporaryPassword();
                        Toast.makeText(
                                GiaoDienQuenMatKhauActivity.this,
                                getString(R.string.forgot_success_message, temp != null ? temp : ""),
                                Toast.LENGTH_LONG
                        ).show();
                        finish();
                    } else {
                        showError(response, null);
                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    setLoading(sendButton, false);
                    Toast.makeText(GiaoDienQuenMatKhauActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setLoading(MaterialButton button, boolean loading) {
        button.setEnabled(!loading);
        button.setText(loading ? getString(R.string.loading) : getString(R.string.forgot_send_request));
    }

    private void showError(Response<?> response, String fallback) {
        String message = fallback;
        if (response != null && response.errorBody() != null) {
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

