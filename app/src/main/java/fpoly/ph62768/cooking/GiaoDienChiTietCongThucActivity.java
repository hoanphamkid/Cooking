package fpoly.ph62768.cooking;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.RecipeMetricHelper;
import fpoly.ph62768.cooking.data.RecipePreferenceStore;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.data.RecipeRatingStore;
import fpoly.ph62768.cooking.data.RecipeStatsStore;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.model.RecipeFeedback;
import fpoly.ph62768.cooking.ui.RecipeFeedbackAdapter;
import fpoly.ph62768.cooking.ui.RecipeStepAdapter;

public class GiaoDienChiTietCongThucActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_chi_tiet_cong_thuc);

        String recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        if (recipeId == null) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecipeRepository repository = RecipeRepository.getInstance();
        Recipe recipe = repository.getRecipeById(recipeId);
        if (recipe == null) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecipePreferenceStore preferenceStore = new RecipePreferenceStore(this);
        RecipeRatingStore ratingStore = new RecipeRatingStore(this);
        RecipeStatsStore statsStore = new RecipeStatsStore(this);
        UserAccountManager accountManager = new UserAccountManager(this);
        String currentUserEmail = accountManager.getCurrentUserEmail(this);

        ImageButton backButton = findViewById(R.id.detail_back_button);
        ImageButton bookmarkButton = findViewById(R.id.detail_bookmark_button);
        ImageButton menuButton = findViewById(R.id.detail_menu_button);
        ImageView heroImage = findViewById(R.id.detail_hero_image);
        TextView titleText = findViewById(R.id.detail_title);
        TextView descriptionText = findViewById(R.id.detail_description);
        TextView durationText = findViewById(R.id.detail_duration);
        TextView ratingValueText = findViewById(R.id.detail_rating_text);
        TextView viewCountText = findViewById(R.id.detail_view_count);
        RatingBar ratingBar = findViewById(R.id.detail_rating_bar);
        RecyclerView stepRecyclerView = findViewById(R.id.detail_steps_recycler);
        MaterialButton saveButton = findViewById(R.id.detail_save_button);
        MaterialButton favoriteButton = findViewById(R.id.detail_favorite_button);
        RatingBar userRatingBar = findViewById(R.id.detail_user_rating_bar);
        MaterialButton userRatingButton = findViewById(R.id.detail_user_rating_button);
        RecyclerView feedbackRecycler = findViewById(R.id.detail_feedback_recycler);
        RatingBar feedbackRating = findViewById(R.id.detail_feedback_rating);
        TextInputLayout feedbackInputLayout = findViewById(R.id.detail_feedback_input_layout);
        TextInputEditText feedbackInput = findViewById(R.id.detail_feedback_input);
        MaterialButton feedbackSubmit = findViewById(R.id.detail_feedback_submit);
        TextView authorLabel = findViewById(R.id.detail_author_label);
        View authorCard = findViewById(R.id.detail_author_card);
        ImageView authorAvatar = findViewById(R.id.detail_author_avatar);
        TextView authorNameView = findViewById(R.id.detail_author_name);
        TextView authorEmailView = findViewById(R.id.detail_author_email);
        MaterialButton authorProfileButton = findViewById(R.id.detail_author_profile_button);
        MaterialButton authorMessageButton = findViewById(R.id.detail_author_message_button);

        backButton.setOnClickListener(v -> onBackPressed());
        menuButton.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng chia sẻ sẽ sớm có mặt!", Toast.LENGTH_SHORT).show()
        );

        Glide.with(this)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.ic_burger)
                .centerCrop()
                .into(heroImage);

        titleText.setText(recipe.getName());
        descriptionText.setText(recipe.getDescription());
        durationText.setText(recipe.getDuration());
        float averageRating = ratingStore.getAverageRating(recipe.getId(), (float) recipe.getRating());
        ratingBar.setIsIndicator(true);
        ratingBar.setRating(averageRating);
        ratingValueText.setText(String.format(Locale.getDefault(), "%.1f", averageRating));

        String authorEmail = recipe.getAuthorEmail();
        if (!TextUtils.isEmpty(authorEmail)) {
            String authorDisplayName = !TextUtils.isEmpty(recipe.getAuthorName())
                    ? recipe.getAuthorName()
                    : resolveDisplayNameForEmail(accountManager, authorEmail);
            if (authorLabel != null) {
                authorLabel.setVisibility(View.VISIBLE);
            }
            if (authorCard != null) {
                authorCard.setVisibility(View.VISIBLE);
            }
            if (authorNameView != null) {
                authorNameView.setText(authorDisplayName);
            }
            if (authorEmailView != null) {
                authorEmailView.setText(authorEmail);
            }
            if (authorAvatar != null) {
                Glide.with(this)
                        .load(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(authorAvatar);
            }
            if (authorProfileButton != null) {
                authorProfileButton.setOnClickListener(v -> {
                    Intent profileIntent = new Intent(this, HoSoNguoiDungCongKhaiActivity.class);
                    profileIntent.putExtra(HoSoNguoiDungCongKhaiActivity.EXTRA_TARGET_EMAIL, authorEmail);
                    profileIntent.putExtra(HoSoNguoiDungCongKhaiActivity.EXTRA_TARGET_NAME, authorDisplayName);
                    startActivity(profileIntent);
                });
            }
            boolean isSelf = !TextUtils.isEmpty(currentUserEmail)
                    && currentUserEmail.equalsIgnoreCase(authorEmail);
            if (authorMessageButton != null) {
                if (isSelf) {
                    authorMessageButton.setVisibility(View.GONE);
                } else {
                    authorMessageButton.setVisibility(View.VISIBLE);
                    authorMessageButton.setOnClickListener(v -> {
                        Intent chatIntent = new Intent(this, TinNhanActivity.class);
                        chatIntent.putExtra(TinNhanActivity.EXTRA_TARGET_EMAIL, authorEmail);
                        chatIntent.putExtra(TinNhanActivity.EXTRA_TARGET_NAME, authorDisplayName);
                        startActivity(chatIntent);
                    });
                }
            }
        } else {
            if (authorLabel != null) {
                authorLabel.setVisibility(View.GONE);
            }
            if (authorCard != null) {
                authorCard.setVisibility(View.GONE);
            }
        }

        RecipeStepAdapter stepAdapter = new RecipeStepAdapter();
        stepAdapter.submitList(recipe.getSteps());
        stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stepRecyclerView.setAdapter(stepAdapter);

        preferenceStore.addToHistory(recipe.getId());
        int viewBase = RecipeMetricHelper.calculateBaseMetric(recipe, RecipeMetricHelper.Metric.VIEWS);
        statsStore.incrementView(recipe.getId(), viewBase);
        if (viewCountText != null) {
            int totalViews = RecipeMetricHelper.calculateTotalViews(recipe, statsStore);
            viewCountText.setText(getString(R.string.detail_views_label, totalViews));
        }

        if (TextUtils.isEmpty(currentUserEmail)) {
            userRatingBar.setIsIndicator(true);
            userRatingButton.setEnabled(false);
            userRatingButton.setText(R.string.detail_user_rating_disabled);
            userRatingButton.setAlpha(0.6f);
        } else {
            float userRating = ratingStore.getUserRating(recipe.getId(), currentUserEmail);
            if (userRating > 0f) {
                userRatingBar.setRating(userRating);
                userRatingButton.setText(R.string.detail_user_rating_update);
            }
            userRatingButton.setOnClickListener(v -> {
                //dfd
                float selected = userRatingBar.getRating();
                if (selected <= 0f) {
                    Toast.makeText(this, R.string.detail_user_rating_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                ratingStore.setUserRating(recipe.getId(), currentUserEmail, selected);
                float newAverage = ratingStore.getAverageRating(recipe.getId(), (float) recipe.getRating());
                ratingBar.setRating(newAverage);
                ratingValueText.setText(String.format(Locale.getDefault(), "%.1f", newAverage));
                userRatingButton.setText(R.string.detail_user_rating_update);
                Toast.makeText(this, R.string.detail_user_rating_thanks, Toast.LENGTH_SHORT).show();
            });
        }

        boolean isSaved = preferenceStore.isSaved(recipe.getId());
        boolean isFavorite = preferenceStore.isFavorite(recipe.getId());
        updateToggleButton(saveButton, isSaved, R.string.detail_saved, R.string.detail_save);
        updateToggleButton(favoriteButton, isFavorite, R.string.detail_favorited, R.string.detail_favorite);

        saveButton.setOnClickListener(v -> {
            boolean nowSaved = preferenceStore.toggleSaved(recipe.getId());
            updateToggleButton(saveButton, nowSaved, R.string.detail_saved, R.string.detail_save);
            Toast.makeText(this,
                    getString(nowSaved ? R.string.detail_save_added : R.string.detail_save_removed),
                    Toast.LENGTH_SHORT).show();
        });
        bookmarkButton.setOnClickListener(v -> saveButton.performClick());

        favoriteButton.setOnClickListener(v -> {
            boolean nowFavorite = preferenceStore.toggleFavorite(recipe.getId());
            statsStore.setFavoriteState(recipe.getId(), nowFavorite);
            updateToggleButton(favoriteButton, nowFavorite, R.string.detail_favorited, R.string.detail_favorite);
            Toast.makeText(this,
                    getString(nowFavorite ? R.string.detail_favorite_added : R.string.detail_favorite_removed),
                    Toast.LENGTH_SHORT).show();
        });

        RecipeFeedbackAdapter feedbackAdapter = new RecipeFeedbackAdapter();
        feedbackRecycler.setLayoutManager(new LinearLayoutManager(this));
        feedbackRecycler.setAdapter(feedbackAdapter);
        feedbackAdapter.submitList(repository.getFeedbackForRecipe(recipe.getId()));

        feedbackInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                feedbackInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        feedbackSubmit.setOnClickListener(v -> {
            String comment = feedbackInput.getText() != null
                    ? feedbackInput.getText().toString().trim()
                    : "";
            if (TextUtils.isEmpty(comment)) {
                feedbackInputLayout.setError(getString(R.string.detail_feedback_empty_error));
                feedbackInput.requestFocus();
                return;
            }
            feedbackInputLayout.setError(null);

            float ratingValue = feedbackRating.getRating();
            if (ratingValue <= 0f) {
                ratingValue = 4.0f;
            }

            String displayName = resolveDisplayName(accountManager);
            String createdAt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());

            RecipeFeedback newFeedback = new RecipeFeedback(displayName, ratingValue, comment, createdAt);
            repository.addFeedbackForRecipe(recipe.getId(), newFeedback);
            statsStore.incrementFeedback(recipe.getId());
            feedbackAdapter.submitList(repository.getFeedbackForRecipe(recipe.getId()));
            feedbackRecycler.smoothScrollToPosition(0);

            feedbackInput.setText("");
            feedbackRating.setRating(0f);
            Toast.makeText(this, R.string.detail_feedback_submitted, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateToggleButton(MaterialButton button, boolean active, int activeTextRes, int inactiveTextRes) {
        int accent = ContextCompat.getColor(this, R.color.primaryColor);
        ColorStateList accentList = ColorStateList.valueOf(accent);
        if (active) {
            button.setText(activeTextRes);
            button.setTextColor(Color.WHITE);
            button.setBackgroundTintList(accentList);
            button.setIconTint(ColorStateList.valueOf(Color.WHITE));
            button.setStrokeColor(accentList);
        } else {
            button.setText(inactiveTextRes);
            button.setTextColor(accent);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            button.setIconTint(accentList);
            button.setStrokeColor(accentList);
        }
    }

    private String resolveDisplayName(UserAccountManager accountManager) {
        String email = accountManager.getCurrentUserEmail(this);
        if (!TextUtils.isEmpty(email)) {
            UserAccount account = accountManager.getAccount(email);
            if (account != null && !TextUtils.isEmpty(account.getName())) {
                return account.getName();
            }
            return email;
        }
        return getString(R.string.detail_feedback_anonymous);
    }

    private String resolveDisplayNameForEmail(UserAccountManager accountManager, String email) {
        if (TextUtils.isEmpty(email)) {
            return "";
        }
        if (accountManager == null) {
            return email;
        }
        UserAccount account = accountManager.getAccount(email);
        if (account != null && !TextUtils.isEmpty(account.getName())) {
            return account.getName();
        }
        return email;
    }
}
