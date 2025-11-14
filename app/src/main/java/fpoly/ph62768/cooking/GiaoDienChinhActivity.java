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

import java.util.Locale;

import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;

public class GiaoDienChinhActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_chinh);

        TextInputEditText emailInput = findViewById(R.id.email_input);
        TextInputEditText passwordInput = findViewById(R.id.password_input);
        MaterialButton loginButton = findViewById(R.id.login_button);

        UserAccountManager accountManager = new UserAccountManager(this);
        accountManager.ensureAccount("Người dùng mẫu", "demo@candycancook.com", UserAccountManager.DEFAULT_PASSWORD);
        accountManager.ensureAccount(
                getString(R.string.admin_default_name),
                getString(R.string.admin_default_email),
                UserAccountManager.DEFAULT_PASSWORD
        );

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

            UserAccount account = accountManager.getAccount(email);
            if (account == null) {
                Toast.makeText(this, R.string.login_account_not_found, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!account.getPassword().equals(password)) {
                Toast.makeText(this, R.string.login_wrong_password, Toast.LENGTH_SHORT).show();
                return;
            }

            accountManager.setCurrentUser(this, email);

            String normalizedEmail = email.trim().toLowerCase(Locale.getDefault());
            String adminEmail = getString(R.string.admin_default_email).trim().toLowerCase(Locale.getDefault());

            Intent destination;
            if (normalizedEmail.equals(adminEmail)) {
                destination = new Intent(this, ManHinhQuanTriActivity.class);
            } else {
                destination = new Intent(this, GiaoDienTrangChuActivity.class);
            }
            destination.putExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL, email);
            destination.putExtra(GiaoDienTrangChuActivity.EXTRA_USER_NAME, account.getName());
            startActivity(destination);
            Toast.makeText(this, R.string.login_success_message, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

