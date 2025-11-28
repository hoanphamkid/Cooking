package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.ui.RecipeAdapter;

public class HoSoNguoiDungCongKhaiActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET_EMAIL = "extra_target_email";
    public static final String EXTRA_TARGET_NAME = "extra_target_name";

    private String targetEmail = "";
    private String targetName = "";
    private String currentUserEmail = "";

    private RecipeAdapter adapter;
    private TextView emptyView;
    private MaterialButton messageButton;
    private TextView recipeTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_public_profile);

        Intent intent = getIntent();
        if (intent != null) {
            targetEmail = intent.getStringExtra(EXTRA_TARGET_EMAIL);
            targetName = intent.getStringExtra(EXTRA_TARGET_NAME);
        }

        if (TextUtils.isEmpty(targetEmail)) {
            Toast.makeText(this, R.string.public_profile_missing_user, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UserAccountManager accountManager = new UserAccountManager(this);
        currentUserEmail = accountManager.getCurrentUserEmail(this);
        if (TextUtils.isEmpty(targetName)) {
            targetName = resolveDisplayName(accountManager, targetEmail);
        }

        ImageButton backButton = findViewById(R.id.user_profile_back_button);
        TextView headerTitle = findViewById(R.id.user_profile_header_title);
        ImageView avatarView = findViewById(R.id.user_profile_avatar);
        TextView nameView = findViewById(R.id.user_profile_name);
        TextView emailView = findViewById(R.id.user_profile_email);
        messageButton = findViewById(R.id.user_profile_message_button);
        recipeTitleView = findViewById(R.id.user_profile_recipe_title);
        emptyView = findViewById(R.id.user_profile_empty);

        backButton.setOnClickListener(v -> onBackPressed());
        headerTitle.setText(getString(R.string.public_profile_title_short, targetName));
        nameView.setText(targetName);
        emailView.setText(targetEmail);

        Glide.with(this)
                .load(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(avatarView);

        boolean isSelf = !TextUtils.isEmpty(currentUserEmail)
                && currentUserEmail.equalsIgnoreCase(targetEmail);
        if (isSelf) {
            messageButton.setVisibility(View.GONE);
        } else {
            messageButton.setVisibility(View.VISIBLE);
            messageButton.setOnClickListener(v -> {
                Intent chatIntent = new Intent(this, TinNhanActivity.class);
                chatIntent.putExtra(TinNhanActivity.EXTRA_TARGET_EMAIL, targetEmail);
                chatIntent.putExtra(TinNhanActivity.EXTRA_TARGET_NAME, targetName);
                startActivity(chatIntent);
            });
        }

        adapter = new RecipeAdapter();
        RecyclerView recyclerView = findViewById(R.id.user_profile_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        adapter.setOnRecipeClickListener(recipe -> {
            Intent detailIntent = new Intent(this, GiaoDienChiTietCongThucActivity.class);
            detailIntent.putExtra(GiaoDienChiTietCongThucActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(detailIntent);
        });

        loadRecipes();
    }

    private void loadRecipes() {
        RecipeRepository repository = RecipeRepository.getInstance();
        List<Recipe> recipes = repository.getRecipesByAuthor(targetEmail);
        adapter.submitList(recipes);
        if (recipeTitleView != null) {
            recipeTitleView.setText(getString(R.string.public_profile_recipe_list_named, targetName));
        }
        if (emptyView != null) {
            if (recipes.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.public_profile_empty_named, targetName));
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    private String resolveDisplayName(UserAccountManager accountManager, String email) {
        if (accountManager == null || TextUtils.isEmpty(email)) {
            return email;
        }
        UserAccount account = accountManager.getAccount(email);
        if (account != null && !TextUtils.isEmpty(account.getName())) {
            return account.getName();
        }
        return email;
    }
}

