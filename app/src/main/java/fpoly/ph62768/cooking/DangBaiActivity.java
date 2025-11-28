package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fpoly.ph62768.cooking.auth.SessionManager;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.data.remote.PendingRecipeRepository;
import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeRequest;
import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeResponse;
import fpoly.ph62768.cooking.data.remote.dto.RecipeStepDto;
import fpoly.ph62768.cooking.model.RecipeCategory;
import fpoly.ph62768.cooking.model.BaiChoDuyet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangBaiActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "extra_user_email";
    public static final String EXTRA_PENDING_RECIPE_NAME = "extra_pending_recipe_name";

    private String currentUserEmail = "";
    private SessionManager sessionManager;
    private PendingRecipeRepository pendingRecipeRepository;
    private MaterialButton submitButton;
    private BaiChoDuyetStore baiChoDuyetStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_bai);

        sessionManager = new SessionManager(this);
        pendingRecipeRepository = new PendingRecipeRepository();
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
        LinearLayout stepContainer = findViewById(R.id.dang_bai_step_container);
        MaterialButton addStepButton = findViewById(R.id.dang_bai_them_buoc_button);
        submitButton = findViewById(R.id.dang_bai_gui_button);

        LayoutInflater inflater = LayoutInflater.from(this);
        addStepView(inflater, stepContainer, null);

        backButton.setOnClickListener(v -> onBackPressed());

        addStepButton.setOnClickListener(v -> addStepView(inflater, stepContainer, null));

        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
            String duration = durationInput.getText() != null ? durationInput.getText().toString().trim() : "";
            String description = descriptionInput.getText() != null ? descriptionInput.getText().toString().trim() : "";
            String imageUrl = imageInput.getText() != null ? imageInput.getText().toString().trim() : "";

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

            List<RecipeStepDto> steps = new ArrayList<>();
            for (int i = 0; i < stepContainer.getChildCount(); i++) {
                View stepView = stepContainer.getChildAt(i);
                TextInputLayout descLayout = stepView.findViewById(R.id.item_step_desc_layout);
                TextInputEditText descInput = stepView.findViewById(R.id.item_step_desc_input);
                TextInputLayout imageStepLayout = stepView.findViewById(R.id.item_step_image_layout);
                TextInputEditText imageStepInput = stepView.findViewById(R.id.item_step_image_input);

                String desc = descInput.getText() != null ? descInput.getText().toString().trim() : "";
                String stepImage = imageStepInput.getText() != null ? imageStepInput.getText().toString().trim() : "";

                if (TextUtils.isEmpty(desc)) {
                    descLayout.setError(getString(R.string.create_recipe_validate_step_description, i + 1));
                    hasError = true;
                } else {
                    descLayout.setError(null);
                    steps.add(new RecipeStepDto("Bước " + (i + 1), desc, TextUtils.isEmpty(stepImage) ? null : stepImage));
                }
                if (!TextUtils.isEmpty(stepImage) && !Patterns.WEB_URL.matcher(stepImage).matches()) {
                    imageStepLayout.setError(getString(R.string.create_recipe_validate_step_image, i + 1));
                    hasError = true;
                } else {
                    imageStepLayout.setError(null);
                }
            }

            if (steps.isEmpty()) {
                Toast.makeText(this, R.string.create_recipe_step_minimum_toast, Toast.LENGTH_SHORT).show();
                hasError = true;
            }

            if (hasError) {
                return;
            }

            PendingRecipeRequest request = new PendingRecipeRequest(
                    name,
                    description,
                    duration,
                    RecipeCategory.ALL.name(),
                    imageUrl,
                    steps
            );
            submitRecipe(request, steps);
        });
    }

    private void submitRecipe(PendingRecipeRequest request, List<RecipeStepDto> stepsSnapshot) {
        String token = sessionManager.getToken();
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, R.string.admin_delete_token_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        setLoading(true);
        pendingRecipeRepository.submitRecipe(token, request).enqueue(new Callback<PendingRecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<PendingRecipeResponse> call, @NonNull Response<PendingRecipeResponse> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    PendingRecipeResponse body = response.body();
                    cachePendingLocally(body, request, stepsSnapshot);
                    Toast.makeText(DangBaiActivity.this, R.string.create_recipe_success, Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    String recipeName = body != null && !TextUtils.isEmpty(body.getName())
                            ? body.getName()
                            : request.getName();
                    resultIntent.putExtra(EXTRA_PENDING_RECIPE_NAME, recipeName);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    String message = getString(R.string.distributor_error_api);
                    if (response.errorBody() != null) {
                        try {
                            message = response.errorBody().string();
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(DangBaiActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PendingRecipeResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(DangBaiActivity.this, getString(R.string.distributor_error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        submitButton.setEnabled(!loading);
        submitButton.setText(loading ? getString(R.string.loading) : getString(R.string.create_recipe_submit));
    }

    private void addStepView(LayoutInflater inflater, LinearLayout container, @Nullable StepData data) {
        View stepView = inflater.inflate(R.layout.item_dang_bai_step, container, false);
        updateStepView(stepView, container.getChildCount() + 1, data);

        ImageButton removeButton = stepView.findViewById(R.id.item_step_remove_button);
        removeButton.setOnClickListener(v -> {
            if (container.getChildCount() > 1) {
                container.removeView(stepView);
                updateAllStepViews(container);
            } else {
                TextInputLayout descLayout = stepView.findViewById(R.id.item_step_desc_layout);
                descLayout.setError(getString(R.string.create_recipe_step_minimum));
            }
        });

        container.addView(stepView);
        updateAllStepViews(container);
    }

    private void updateAllStepViews(LinearLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            updateStepView(child, i + 1, null);
        }
    }

    private void updateStepView(View stepView, int index, @Nullable StepData data) {
        TextView titleView = stepView.findViewById(R.id.item_step_title);
        TextInputLayout descLayout = stepView.findViewById(R.id.item_step_desc_layout);
        TextInputEditText descInput = stepView.findViewById(R.id.item_step_desc_input);
        TextInputLayout imageLayout = stepView.findViewById(R.id.item_step_image_layout);
        TextInputEditText imageInput = stepView.findViewById(R.id.item_step_image_input);

        titleView.setText(getString(R.string.create_recipe_step_title, index));
        descLayout.setHint(getString(R.string.create_recipe_step_description_hint, index));
        imageLayout.setHint(getString(R.string.create_recipe_step_image_hint, index));

        if (data != null) {
            descInput.setText(data.description);
            imageInput.setText(data.imageUrl);
        } else if (descLayout.getError() != null) {
            descLayout.setError(null);
        }
        imageLayout.setError(null);
    }

    private void cachePendingLocally(@Nullable PendingRecipeResponse response,
                                     PendingRecipeRequest request,
                                     List<RecipeStepDto> steps) {
        if (baiChoDuyetStore == null || TextUtils.isEmpty(currentUserEmail)) {
            return;
        }
        String id = response != null && !TextUtils.isEmpty(response.getId())
                ? response.getId()
                : UUID.randomUUID().toString();
        String name = response != null && !TextUtils.isEmpty(response.getName())
                ? response.getName()
                : request.getName();
        String duration = response != null && !TextUtils.isEmpty(response.getDuration())
                ? response.getDuration()
                : request.getDuration();
        String description = response != null && !TextUtils.isEmpty(response.getDescription())
                ? response.getDescription()
                : request.getDescription();
        String image = response != null && !TextUtils.isEmpty(response.getImageUrl())
                ? response.getImageUrl()
                : request.getImageUrl();
        long createdAt = response != null && response.getCreatedAtMillis() > 0
                ? response.getCreatedAtMillis()
                : System.currentTimeMillis();
        String content = buildStepContent(response != null && response.getSteps() != null
                ? response.getSteps()
                : steps);

        BaiChoDuyet baiChoDuyet = new BaiChoDuyet(
                id,
                name != null ? name : "",
                duration != null ? duration : "",
                description != null ? description : "",
                image != null ? image : "",
                content,
                createdAt,
                0f,
                BaiChoDuyet.TrangThai.PENDING
        );
        baiChoDuyetStore.themBaiChoDuyet(currentUserEmail, baiChoDuyet);
    }

    private String buildStepContent(List<RecipeStepDto> steps) {
        if (steps == null || steps.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            RecipeStepDto step = steps.get(i);
            if (step == null || TextUtils.isEmpty(step.getDescription())) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append("Bước ").append(i + 1).append(": ")
                    .append(step.getDescription().trim());
            if (!TextUtils.isEmpty(step.getImageUrl())) {
                builder.append(" (").append(step.getImageUrl().trim()).append(')');
            }
        }
        return builder.toString();
    }

    private static class StepData {
        final String description;
        final String imageUrl;

        StepData(String description, String imageUrl) {
            this.description = description;
            this.imageUrl = imageUrl;
        }
    }
}

