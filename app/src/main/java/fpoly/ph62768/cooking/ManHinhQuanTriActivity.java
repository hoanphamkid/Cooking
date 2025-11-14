package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore.BanGhi;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.BaiChoDuyet;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.ui.AdminUserAdapter;
import fpoly.ph62768.cooking.ui.RecipeAdapter;

public class ManHinhQuanTriActivity extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private TextView adminNameView;
    private TextView adminEmailView;
    private TextView userSummaryView;
    private TextView emptyView;
    private TextView reviewSubtitleView;
    private TextView statsSubtitleView;
    private TextView pendingAlertView;
    private TextView recipeSummaryView;
    private TextView recipeEmptyView;

    private View statUsersView;
    private View statRecipesView;
    private View statLikesView;
    private View userListAnchor;
    private View recipeListAnchor;
    private View userSectionView;
    private View recipeSectionView;

    private UserAccountManager accountManager;
    private BaiChoDuyetStore baiChoDuyetStore;
    private AdminUserAdapter userAdapter;
    private RecipeAdapter recipeAdapter;
    private NestedScrollView scrollView;
    private BottomNavigationView bottomNavigationView;

    private Section currentSection;
    private int totalUsers = 0;
    private int totalRecipes = 0;
    private int totalPending = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        accountManager = new UserAccountManager(this);
        baiChoDuyetStore = new BaiChoDuyetStore(this);

        adminNameView = findViewById(R.id.admin_name);
        adminEmailView = findViewById(R.id.admin_email);
        userSummaryView = findViewById(R.id.admin_user_summary);
        emptyView = findViewById(R.id.admin_user_empty);
        scrollView = findViewById(R.id.admin_scroll);
        bottomNavigationView = findViewById(R.id.admin_bottom_nav);

        statUsersView = findViewById(R.id.admin_stat_users);
        statRecipesView = findViewById(R.id.admin_stat_recipes);
        statLikesView = findViewById(R.id.admin_stat_likes);

        userSectionView = findViewById(R.id.admin_user_section);
        recipeSectionView = findViewById(R.id.admin_recipe_section);
        recipeSummaryView = findViewById(R.id.admin_recipe_summary);
        recipeEmptyView = findViewById(R.id.admin_recipe_empty);

        userListAnchor = userSectionView != null
                ? userSectionView
                : findViewById(R.id.admin_user_recycler);
        recipeListAnchor = recipeSectionView != null
                ? recipeSectionView
                : findViewById(R.id.admin_recipe_recycler);

        setupStatCard(statUsersView, getString(R.string.admin_stat_users_label));
        setupStatCard(statRecipesView, getString(R.string.admin_stat_recipes_label));
        setupStatCard(statLikesView, getString(R.string.admin_stat_likes_label));

        reviewSubtitleView = setupActionCard(
                findViewById(R.id.admin_action_review),
                R.drawable.ic_profile_pending,
                R.string.admin_action_review_title,
                R.string.admin_action_review_sub
        );
        statsSubtitleView = setupActionCard(
                findViewById(R.id.admin_action_stats),
                R.drawable.ic_nav_fire,
                R.string.admin_action_stats_title,
                R.string.admin_action_stats_sub
        );
        pendingAlertView = findViewById(R.id.admin_pending_alert);
        setupActionCard(
                findViewById(R.id.admin_action_manage_users),
                R.drawable.ic_profile_history,
                R.string.admin_action_manage_users_title,
                R.string.admin_action_manage_users_sub
        );
        setupActionCard(
                findViewById(R.id.admin_action_manage_recipes),
                R.drawable.ic_nav_add,
                R.string.admin_action_manage_recipes_title,
                R.string.admin_action_manage_recipes_sub
        );

        RecyclerView recipeRecyclerView = findViewById(R.id.admin_recipe_recycler);
        if (recipeRecyclerView != null) {
            recipeRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recipeRecyclerView.setHasFixedSize(false);
            recipeRecyclerView.setNestedScrollingEnabled(false);
            recipeAdapter = new RecipeAdapter();
            recipeAdapter.setOnRecipeClickListener(recipe -> {
                Intent detailIntent = new Intent(this, GiaoDienChiTietCongThucActivity.class);
                detailIntent.putExtra(GiaoDienChiTietCongThucActivity.EXTRA_RECIPE_ID, recipe.getId());
                startActivity(detailIntent);
            });
            recipeRecyclerView.setAdapter(recipeAdapter);
        }

        RecyclerView recyclerView = findViewById(R.id.admin_user_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);

        userAdapter = new AdminUserAdapter();
        userAdapter.setListener(new AdminUserAdapter.Listener() {
            @Override
            public void onPostsClick(@NonNull String email) {
                Intent intent = new Intent(ManHinhQuanTriActivity.this, DanhSachChoDuyetActivity.class);
                intent.putExtra(DanhSachChoDuyetActivity.EXTRA_FILTER_EMAIL, email);
                intent.putExtra(DanhSachChoDuyetActivity.EXTRA_INITIAL_STATUS, DanhSachChoDuyetActivity.StatusFilter.ALL.name());
                startActivity(intent);
            }

            @Override
            public void onLockClick(@NonNull String email) {
                Toast.makeText(ManHinhQuanTriActivity.this, R.string.admin_toast_working, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(@NonNull String email) {
                showDeleteConfirmation(email);
            }
        });
        recyclerView.setAdapter(userAdapter);

        findViewById(R.id.admin_refresh_button).setOnClickListener(v -> reloadData());

        setupActionHandlers();
        showSection(Section.USERS);
        populateAdminInfoFromIntent();
        reloadData();
    }

    private void populateAdminInfoFromIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            setAdminDefaults();
            return;
        }
        String name = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_NAME);
        String email = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL);
        if (TextUtils.isEmpty(name)) {
            name = getString(R.string.admin_default_name);
        }
        if (TextUtils.isEmpty(email)) {
            email = getString(R.string.admin_default_email);
        }
        adminNameView.setText(name);
        adminEmailView.setText(email);
    }

    private void setAdminDefaults() {
        adminNameView.setText(getString(R.string.admin_default_name));
        adminEmailView.setText(getString(R.string.admin_default_email));
    }

    private void setupActionHandlers() {
        View userSectionCard = findViewById(R.id.admin_action_manage_users);
        View manageRecipesCard = findViewById(R.id.admin_action_manage_recipes);
        View reviewCard = findViewById(R.id.admin_action_review);
        View alertView = pendingAlertView;
        View statsCard = findViewById(R.id.admin_action_stats);

        View.OnClickListener openUsers = v -> {
            showSection(Section.USERS);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.action_users);
            }
        };

        View.OnClickListener openRecipesSection = v -> {
            showSection(Section.RECIPES);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.action_recipes);
            }
        };

        if (userSectionCard != null) {
            userSectionCard.setOnClickListener(openUsers);
        }
        if (manageRecipesCard != null) {
            manageRecipesCard.setOnClickListener(openRecipesSection);
        }

        View.OnClickListener openReview = v -> {
            Intent intent = new Intent(this, DanhSachChoDuyetActivity.class);
            startActivity(intent);
        };
        View.OnClickListener reviewClick = v -> {
            openReview.onClick(v);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.action_review);
            }
        };
        reviewCard.setOnClickListener(reviewClick);
        if (alertView != null) {
            alertView.setOnClickListener(reviewClick);
        }

        View.OnClickListener showStats = v -> showStatsDialog();
        statsCard.setOnClickListener(v -> {
            showStats.onClick(v);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.action_stats);
            }
        });

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_users) {
                    showSection(Section.USERS);
                    return true;
                } else if (id == R.id.action_recipes) {
                    showSection(Section.RECIPES);
                    return true;
                } else if (id == R.id.action_review) {
                    openReview.onClick(bottomNavigationView);
                    return true;
                } else if (id == R.id.action_stats) {
                    showStatsDialog();
                    return true;
                } else if (id == R.id.action_logout) {
                    performLogout();
                    return true;
                }
                return false;
            });
            bottomNavigationView.setSelectedItemId(R.id.action_users);
        }
    }

    private void performLogout() {
            accountManager.clearCurrentUser(this);
            Intent intent = new Intent(this, GiaoDienChinhActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
    }

    private TextView setupActionCard(View card, int iconRes, int titleRes, int subtitleRes) {
        TextView titleView = card.findViewById(R.id.admin_action_title);
        TextView subtitleView = card.findViewById(R.id.admin_action_subtitle);
        android.widget.ImageView iconView = card.findViewById(R.id.admin_action_icon);
        iconView.setImageResource(iconRes);
        titleView.setText(titleRes);
        subtitleView.setText(subtitleRes);
        return subtitleView;
    }

    private void setupStatCard(View card, String label) {
        TextView labelView = card.findViewById(R.id.admin_stat_label);
        labelView.setText(label);
    }

    private void showSection(@NonNull Section section) {
        currentSection = section;
        if (userSectionView != null) {
            userSectionView.setVisibility(section == Section.USERS ? View.VISIBLE : View.GONE);
        }
        if (recipeSectionView != null) {
            recipeSectionView.setVisibility(section == Section.RECIPES ? View.VISIBLE : View.GONE);
        }
        View anchor = section == Section.USERS
                ? (userSectionView != null ? userSectionView : userListAnchor)
                : (recipeSectionView != null ? recipeSectionView : recipeListAnchor);
        if (scrollView != null && anchor != null) {
            scrollView.post(() -> scrollView.smoothScrollTo(0, anchor.getTop()));
        }
    }

    private void showDeleteConfirmation(String email) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_user_delete)
                .setMessage(getString(R.string.admin_confirm_delete_user, email))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    accountManager.removeAccount(email);
                    reloadData();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void reloadData() {
        Map<String, UserAccount> allAccounts = accountManager.getAllAccounts();
        Map<String, Integer> postCounts = buildPostCountMap();

        String adminEmail = getString(R.string.admin_default_email).trim().toLowerCase(Locale.getDefault());

        List<Map.Entry<String, UserAccount>> entries = new ArrayList<>(allAccounts.entrySet());
        Collections.sort(entries, (left, right) -> {
            long leftCreated = left.getValue() != null ? left.getValue().getCreatedAt() : 0L;
            long rightCreated = right.getValue() != null ? right.getValue().getCreatedAt() : 0L;
            return Long.compare(rightCreated, leftCreated);
        });

        List<AdminUserAdapter.Item> items = new ArrayList<>();
        for (Map.Entry<String, UserAccount> entry : entries) {
            String emailKey = entry.getKey();
            if (TextUtils.isEmpty(emailKey)) {
                continue;
            }
            String normalizedEmail = emailKey.trim().toLowerCase(Locale.getDefault());
            if (normalizedEmail.equals(adminEmail)) {
                continue;
            }

            UserAccount account = entry.getValue();
            String displayEmail = !TextUtils.isEmpty(emailKey)
                    ? emailKey
                    : getString(R.string.admin_unknown_email);
            String displayName = (account != null && !TextUtils.isEmpty(account.getName()))
                    ? account.getName()
                    : displayEmail;
            if (TextUtils.isEmpty(displayName)) {
                displayName = getString(R.string.admin_unknown_user);
            }

            String dateText;
            if (account != null && account.getCreatedAt() > 0L) {
                dateText = DATE_FORMAT.format(new Date(account.getCreatedAt()));
            } else {
                dateText = getString(R.string.admin_unknown_date);
            }
            int totalPosts = postCounts.getOrDefault(normalizedEmail, 0);
            String meta = getString(R.string.admin_user_meta, dateText, totalPosts);

            items.add(new AdminUserAdapter.Item(
                    normalizedEmail,
                    displayName,
                    displayEmail,
                    meta
            ));
        }

        userAdapter.submitList(items);
        totalUsers = items.size();

        List<Recipe> recipes = RecipeRepository.getInstance().getRecipes();
        totalRecipes = recipes.size();
        totalPending = countPendingRecipes();
        int totalLikes = 0;

        if (recipeAdapter != null) {
            recipeAdapter.submitList(recipes);
        }
        if (recipeSummaryView != null) {
            recipeSummaryView.setText(getString(R.string.admin_recipe_summary, totalRecipes));
        }
        if (recipeEmptyView != null) {
            recipeEmptyView.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.GONE);
        }

        updateStatValue(statUsersView, totalUsers);
        updateStatValue(statRecipesView, totalRecipes);
        updateStatValue(statLikesView, totalLikes);
        updateReviewSubtitle();
        updateStatsSubtitle();

        userSummaryView.setText(getString(R.string.admin_user_summary, totalUsers));
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updateReviewSubtitle() {
        if (reviewSubtitleView != null) {
            String text = getString(R.string.admin_action_review_sub);
            if (totalPending > 0) {
                text = text + " (" + totalPending + ")";
            }
            reviewSubtitleView.setText(text);
        }
        if (pendingAlertView != null) {
            pendingAlertView.setVisibility(totalPending > 0 ? View.VISIBLE : View.GONE);
            if (totalPending > 0) {
                pendingAlertView.setText(getString(R.string.admin_pending_alert, totalPending));
            }
        }
    }

    private void updateStatsSubtitle() {
        if (statsSubtitleView != null) {
            statsSubtitleView.setText(
                    getString(R.string.admin_action_stats_sub)
                            + " (" + totalUsers + " / " + totalRecipes + ")"
            );
        }
    }

    private void updateStatValue(View card, int value) {
        TextView valueView = card.findViewById(R.id.admin_stat_value);
        valueView.setText(String.valueOf(value));
    }

    private int countPendingRecipes() {
        int count = 0;
        for (BanGhi record : baiChoDuyetStore.layTatCaBanGhi()) {
            if (record.baiChoDuyet.getTrangThai() == BaiChoDuyet.TrangThai.PENDING) {
                count++;
            }
        }
        return count;
    }

    private Map<String, Integer> buildPostCountMap() {
        Map<String, Integer> result = new HashMap<>();
        for (BanGhi record : baiChoDuyetStore.layTatCaBanGhi()) {
            String email = record.email;
            if (TextUtils.isEmpty(email)) {
                continue;
            }
            String normalized = email.trim().toLowerCase(Locale.getDefault());
            result.put(normalized, result.getOrDefault(normalized, 0) + 1);
        }
        return result;
    }

    private void showStatsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_action_stats_title)
                .setMessage(getString(
                        R.string.admin_stats_message,
                        totalUsers,
                        totalRecipes,
                        totalPending
                ))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private enum Section {
        USERS,
        RECIPES
    }
}

