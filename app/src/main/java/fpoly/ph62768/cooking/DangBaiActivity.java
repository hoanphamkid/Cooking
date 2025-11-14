package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.model.BaiChoDuyet;

public class DangBaiActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "extra_user_email";

    private BaiChoDuyetStore baiChoDuyetStore;
    private String currentUserEmail = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_bai);

        baiChoDuyetStore = new BaiChoDuyetStore(this);

        currentUserEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            Toast.makeText(this, R.string.create_recipe_no_user, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageButton backButton = findViewById(R.id.dang_bai_quay_lai);
        TextInputLayout nameLayout = findViewById(R.id.dang_bai_ten_mon_layout);
        TextInputLayout durationLayout = findViewById(R.id.dang_bai_thoi_gian_layout);
        TextInputLayout descriptionLayout = findViewById(R.id.dang_bai_mo_ta_layout);
        TextInputEditText nameInput = findViewById(R.id.dang_bai_ten_mon_input);
        TextInputEditText durationInput = findViewById(R.id.dang_bai_thoi_gian_input);
        TextInputEditText descriptionInput = findViewById(R.id.dang_bai_mo_ta_input);
        TextInputLayout imageLayout = findViewById(R.id.dang_bai_anh_layout);
        TextInputEditText imageInput = findViewById(R.id.dang_bai_anh_input);
        TextInputLayout stepsLayout = findViewById(R.id.dang_bai_cong_thuc_layout);
        TextInputEditText stepsInput = findViewById(R.id.dang_bai_cong_thuc_input);
        MaterialButton submitButton = findViewById(R.id.dang_bai_gui_button);
        RatingBar ratingBar = findViewById(R.id.dang_bai_danh_gia_ratingbar);

        backButton.setOnClickListener(v -> onBackPressed());

        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
            String duration = durationInput.getText() != null ? durationInput.getText().toString().trim() : "";
            String description = descriptionInput.getText() != null ? descriptionInput.getText().toString().trim() : "";
            String imageUrl = imageInput.getText() != null ? imageInput.getText().toString().trim() : "";
            String steps = stepsInput.getText() != null ? stepsInput.getText().toString().trim() : "";
            float ratingValue = ratingBar.getRating();

            boolean hasError = false;
            if (TextUtils.isEmpty(name)) {
                nameLayout.setError(getString(R.string.create_recipe_validate_name));
                hasError = true;
            } else {
                nameLayout.setError(null);
            }
            if (TextUtils.isEmpty(duration)) {
                durationLayout.setError(getString(R.string.create_recipe_validate_duration));
                hasError = true;
            } else {
                durationLayout.setError(null);
            }
            if (TextUtils.isEmpty(description)) {
                descriptionLayout.setError(getString(R.string.create_recipe_validate_description));
                hasError = true;
            } else {
                descriptionLayout.setError(null);
            }
            if (TextUtils.isEmpty(imageUrl) || !Patterns.WEB_URL.matcher(imageUrl).matches()) {
                imageLayout.setError(getString(R.string.create_recipe_validate_image));
                hasError = true;
            } else {
                imageLayout.setError(null);
            }
            if (TextUtils.isEmpty(steps)) {
                stepsLayout.setError(getString(R.string.create_recipe_validate_steps));
                hasError = true;
            } else {
                stepsLayout.setError(null);
            }
            if (ratingValue <= 0f) {
                Toast.makeText(this, R.string.create_recipe_validate_rating, Toast.LENGTH_SHORT).show();
                hasError = true;
            }

            if (hasError) {
                return;
            }

            BaiChoDuyet recipe = new BaiChoDuyet(
                    UUID.randomUUID().toString(),
                    name,
                    duration,
                    description,
                    imageUrl,
                    steps,
                    System.currentTimeMillis(),
                    ratingValue,
                    BaiChoDuyet.TrangThai.PENDING
            );
            baiChoDuyetStore.themBaiChoDuyet(currentUserEmail, recipe);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("extra_pending_recipe_name", name);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, R.string.create_recipe_success, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

