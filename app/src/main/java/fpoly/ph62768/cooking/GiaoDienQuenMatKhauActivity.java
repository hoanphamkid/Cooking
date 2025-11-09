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

import fpoly.ph62768.cooking.auth.UserAccountManager;

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

        sendButton.setOnClickListener(v -> {
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            if (TextUtils.isEmpty(email)) {
                emailInput.setError(getString(R.string.error_email_required));
                return;
            }
            UserAccountManager manager = new UserAccountManager(this);
            manager.resetPassword(email);
            Toast.makeText(this, getString(R.string.forgot_success_message, UserAccountManager.DEFAULT_PASSWORD), Toast.LENGTH_LONG).show();
            finish();
        });
    }
}

