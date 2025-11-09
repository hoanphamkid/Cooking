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

public class GiaoDienDangKyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_dang_ky);

        TextInputEditText nameInput = findViewById(R.id.name_input);
        TextInputEditText emailInput = findViewById(R.id.register_email_input);
        TextInputEditText passwordInput = findViewById(R.id.register_password_input);
        MaterialButton registerButton = findViewById(R.id.register_button);
        TextView loginLink = findViewById(R.id.login_link_text);

        String prompt = getString(R.string.register_have_account);
        SpannableString spannable = new SpannableString(prompt);
        int start = prompt.indexOf("Đăng nhập");
        if (start >= 0) {
            int end = start + "Đăng nhập".length();
            spannable.setSpan(new ForegroundColorSpan(0xFFFF7A33), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        loginLink.setText(spannable);
        loginLink.setOnClickListener(v -> finish());

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

            boolean hasError = false;
            if (TextUtils.isEmpty(name)) {
                nameInput.setError(getString(R.string.error_name_required));
                hasError = true;
            } else {
                nameInput.setError(null);
            }

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

            UserAccountManager accountManager = new UserAccountManager(this);
            if (accountManager.getAccount(email) != null) {
                emailInput.setError(getString(R.string.error_email_exists));
                Toast.makeText(this, R.string.register_email_exists_message, Toast.LENGTH_SHORT).show();
                return;
            }
            accountManager.saveAccount(name, email, password);
            Toast.makeText(this, getString(R.string.register_success_message, email), Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

