package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fpoly.ph62768.cooking.auth.SessionManager;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore.BanGhi;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.data.remote.UserApiService;
import fpoly.ph62768.cooking.data.remote.dto.UserResponse;
import fpoly.ph62768.cooking.model.BaiChoDuyet;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.network.ApiClient;
import fpoly.ph62768.cooking.ui.AdminUserAdapter;
import fpoly.ph62768.cooking.ui.RecipeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private TextView overviewUsersValue;
    private TextView overviewUsersSub;
    private TextView overviewContentValue;
    private TextView overviewContentSub;
    private TextView overviewApprovedValue;
    private TextView overviewApprovedSub;
    private TextView overviewPendingValue;
    private TextView overviewPendingSub;
    private TextView detailUsersTotalValue;
    private TextView detailUsersActiveValue;
    private TextView detailUsersLockedValue;
    private TextView detailRecipesTotalValue;
    private TextView detailRecipesApprovedValue;
    private TextView detailRecipesPendingValue;
    private TextView detailRecipesLikesValue;
    private TextView detailPostsTotalValue;
    private TextView detailPostsApprovedValue;
    private TextView detailPostsPendingValue;
    private View topUserCard;
    private TextView topUserRankView;
    private TextView topUserNameView;
    private TextView topUserEmailView;
    private TextView topUserTotalView;
    private TextView topUserStatsView;

    private View userListAnchor;
    private View recipeListAnchor;
    private View userSectionView;
    private View recipeSectionView;
    private View statsSectionView;

    private UserAccountManager accountManager;
    private BaiChoDuyetStore baiChoDuyetStore;
    private UserApiService userApiService;
    private SessionManager sessionManager;
    private AdminUserAdapter userAdapter;
    private RecipeAdapter recipeAdapter;
    private NestedScrollView scrollView;
    private BottomNavigationView bottomNavigationView;

    private Section currentSection;
    private int totalUsers = 0;
    private int totalRecipes = 0;
    private int totalPending = 0;
    private Map<String, UserPostStats> currentPostStats = new HashMap<>();
    private Map<String, UserAccount> currentUserAccounts = new HashMap<>();
    private int totalUserPosts = 0;
    private int totalApprovedPosts = 0;
    private int totalPendingPosts = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        accountManager = new UserAccountManager(this);
        baiChoDuyetStore = new BaiChoDuyetStore(this);
        userApiService = ApiClient.getInstance().create(UserApiService.class);
        sessionManager = new SessionManager(this);

        adminNameView = findViewById(R.id.admin_name);
        adminEmailView = findViewById(R.id.admin_email);
        userSummaryView = findViewById(R.id.admin_user_summary);
        emptyView = findViewById(R.id.admin_user_empty);
        scrollView = findViewById(R.id.admin_scroll);
        bottomNavigationView = findViewById(R.id.admin_bottom_nav);

        userSectionView = findViewById(R.id.admin_user_section);
        recipeSectionView = findViewById(R.id.admin_recipe_section);
        statsSectionView = findViewById(R.id.admin_stats_section);
        recipeSummaryView = findViewById(R.id.admin_recipe_summary);
        recipeEmptyView = findViewById(R.id.admin_recipe_empty);
        overviewUsersValue = findViewById(R.id.overview_users_value);
        overviewUsersSub = findViewById(R.id.overview_users_sub);
        overviewContentValue = findViewById(R.id.overview_content_value);
        overviewContentSub = findViewById(R.id.overview_content_sub);
        overviewApprovedValue = findViewById(R.id.overview_approved_value);
        overviewApprovedSub = findViewById(R.id.overview_approved_sub);
        overviewPendingValue = findViewById(R.id.overview_pending_value);
        overviewPendingSub = findViewById(R.id.overview_pending_sub);
        detailUsersTotalValue = findViewById(R.id.detail_users_total_value);
        detailUsersActiveValue = findViewById(R.id.detail_users_active_value);
        detailUsersLockedValue = findViewById(R.id.detail_users_locked_value);
        detailRecipesTotalValue = findViewById(R.id.detail_recipes_total_value);
        detailRecipesApprovedValue = findViewById(R.id.detail_recipes_approved_value);
        detailRecipesPendingValue = findViewById(R.id.detail_recipes_pending_value);
        detailRecipesLikesValue = findViewById(R.id.detail_recipes_likes_value);
        detailPostsTotalValue = findViewById(R.id.detail_posts_total_value);
        detailPostsApprovedValue = findViewById(R.id.detail_posts_approved_value);
        detailPostsPendingValue = findViewById(R.id.detail_posts_pending_value);
        topUserCard = findViewById(R.id.admin_top_user_card);
        topUserRankView = findViewById(R.id.admin_top_user_rank);
        topUserNameView = findViewById(R.id.admin_top_user_name);
        topUserEmailView = findViewById(R.id.admin_top_user_email);
        topUserTotalView = findViewById(R.id.admin_top_user_total);
        topUserStatsView = findViewById(R.id.admin_top_user_stats);

        userListAnchor = userSectionView != null
                ? userSectionView
                : findViewById(R.id.admin_user_recycler);
        recipeListAnchor = recipeSectionView != null
                ? recipeSectionView
                : findViewById(R.id.admin_recipe_recycler);

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
        setupActionCard(
                findViewById(R.id.admin_action_manage_distributors),
                R.drawable.ic_storefront,
                R.string.admin_action_manage_distributors_title,
                R.string.admin_action_manage_distributors_sub
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
            public void onDeleteClick(@Nullable String userId, @NonNull String email) {
                showDeleteConfirmation(userId, email);
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
        View manageDistributorsCard = findViewById(R.id.admin_action_manage_distributors);
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
        if (manageDistributorsCard != null) {
            manageDistributorsCard.setOnClickListener(v -> {
                Intent intent = new Intent(this, DistributorCrudActivity.class);
                startActivity(intent);
            });
        }

        View.OnClickListener openReview = v -> {
            Intent intent = new Intent(this, DanhSachChoDuyetActivity.class);
            startActivity(intent);
        };
        View.OnClickListener reviewClick = v -> {
            boolean handledByNav = false;
            if (bottomNavigationView != null) {
                int currentSelected = bottomNavigationView.getSelectedItemId();
                if (currentSelected != R.id.action_review) {
                    bottomNavigationView.setSelectedItemId(R.id.action_review);
                    handledByNav = true;
                }
            }
            if (!handledByNav) {
                openReview.onClick(v);
            }
        };
        if (reviewCard != null) {
            reviewCard.setOnClickListener(reviewClick);
        }
        if (alertView != null) {
            alertView.setOnClickListener(reviewClick);
        }

        View.OnClickListener showStats = v -> {
            showSection(Section.STATS);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.action_stats);
            }
        };
        statsCard.setOnClickListener(showStats);

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
                    showSection(Section.STATS);
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

    private void showSection(@NonNull Section section) {
        currentSection = section;
        if (userSectionView != null) {
            userSectionView.setVisibility(section == Section.USERS ? View.VISIBLE : View.GONE);
        }
        if (recipeSectionView != null) {
            recipeSectionView.setVisibility(section == Section.RECIPES ? View.VISIBLE : View.GONE);
        }
        if (statsSectionView != null) {
            statsSectionView.setVisibility(section == Section.STATS ? View.VISIBLE : View.GONE);
        }
        View anchor = section == Section.USERS
                ? (userSectionView != null ? userSectionView : userListAnchor)
                : section == Section.RECIPES
                ? (recipeSectionView != null ? recipeSectionView : recipeListAnchor)
                : statsSectionView;
        if (scrollView != null && anchor != null) {
            scrollView.post(() -> scrollView.smoothScrollTo(0, anchor.getTop()));
        }
    }

    private void showDeleteConfirmation(@Nullable String userId, @NonNull String email) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_user_delete)
                .setMessage(getString(R.string.admin_confirm_delete_user, email))
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        performUserDeletion(userId, email))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void performUserDeletion(@Nullable String userId, @NonNull String email) {
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, R.string.admin_delete_user_offline, Toast.LENGTH_SHORT).show();
            return;
        }
        if (sessionManager == null) {
            sessionManager = new SessionManager(this);
        }
        String token = sessionManager.getToken();
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, R.string.admin_delete_token_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        if (userApiService == null) {
            Toast.makeText(this, R.string.admin_delete_user_failed_generic, Toast.LENGTH_SHORT).show();
            return;
        }
        userApiService.deleteUser("Bearer " + token, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            ManHinhQuanTriActivity.this,
                            getString(R.string.admin_delete_user_success, email),
                            Toast.LENGTH_SHORT
                    ).show();
                    reloadData();
                } else {
                    String message = response.message();
                    try {
                        if (response.errorBody() != null) {
                            message = response.errorBody().string();
                        }
                    } catch (IOException ignored) {
                    }
                    Toast.makeText(
                            ManHinhQuanTriActivity.this,
                            getString(R.string.admin_delete_user_failed, message),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(
                        ManHinhQuanTriActivity.this,
                        getString(R.string.admin_delete_user_failed, t.getMessage()),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void reloadData() {
        fetchUserStatsAndRecipes();
    }

    private void fetchUserStatsAndRecipes() {
        updateRecipeSection();
        currentPostStats = computePostStats();
        loadUsersFromBackend();
    }

    private String getCurrentAdminEmailNormalized() {
        String email = sessionManager != null ? sessionManager.getEmail() : null;
        if (TextUtils.isEmpty(email)) {
            CharSequence label = adminEmailView != null ? adminEmailView.getText() : null;
            email = label != null ? label.toString() : getString(R.string.admin_default_email);
        }
        if (email == null) {
            email = "";
        }
        return email.trim().toLowerCase(Locale.getDefault());
    }

    private void updateRecipeSection() {
        List<Recipe> recipes = RecipeRepository.getInstance().getRecipes();
        totalRecipes = recipes.size();
        if (recipeAdapter != null) {
            recipeAdapter.submitList(recipes);
        }
        if (recipeSummaryView != null) {
            recipeSummaryView.setText(getString(R.string.admin_recipe_summary, totalRecipes));
        }
        if (recipeEmptyView != null) {
            recipeEmptyView.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private Map<String, UserPostStats> computePostStats() {
        Map<String, UserPostStats> stats = new HashMap<>();
        totalUserPosts = 0;
        totalApprovedPosts = 0;
        totalPendingPosts = 0;
        for (BanGhi record : baiChoDuyetStore.layTatCaBanGhi()) {
            if (record == null || record.baiChoDuyet == null) {
                continue;
            }
            String email = record.email;
            if (TextUtils.isEmpty(email)) {
                continue;
            }
            String normalized = email.trim().toLowerCase(Locale.getDefault());
            UserPostStats stat = stats.get(normalized);
            if (stat == null) {
                stat = new UserPostStats();
                stats.put(normalized, stat);
            }
            stat.total++;
            totalUserPosts++;
            if (record.baiChoDuyet.getTrangThai() == BaiChoDuyet.TrangThai.APPROVED) {
                stat.approved++;
                totalApprovedPosts++;
            } else if (record.baiChoDuyet.getTrangThai() == BaiChoDuyet.TrangThai.PENDING) {
                stat.pending++;
                totalPendingPosts++;
            }
        }
        totalPending = totalPendingPosts;
        return stats;
    }

    private void loadUsersFromBackend() {
        if (userApiService == null) {
            applyLocalUsers();
            return;
        }
        userApiService.getUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call,
                                   @NonNull Response<List<UserResponse>> response) {
                List<UserResponse> body = response.body();
                if (response.isSuccessful() && body != null) {
                    applyRemoteUsers(body);
                } else {
                    applyLocalUsers();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                Toast.makeText(ManHinhQuanTriActivity.this,
                        R.string.admin_user_load_error, Toast.LENGTH_SHORT).show();
                applyLocalUsers();
            }
        });
    }

    private void applyRemoteUsers(@NonNull List<UserResponse> users) {
        List<AdminUserAdapter.Item> refreshed = new ArrayList<>();
        Map<String, UserAccount> accountMap = new HashMap<>();
        String adminEmail = getCurrentAdminEmailNormalized();
        for (UserResponse user : users) {
            String rawEmail = user.getEmail();
            if (TextUtils.isEmpty(rawEmail)) {
                continue;
            }
            rawEmail = rawEmail.trim();
            String normalized = rawEmail.toLowerCase(Locale.getDefault());
            if (normalized.equals(adminEmail)) {
                continue;
            }
            String displayName = TextUtils.isEmpty(user.getName()) ? rawEmail : user.getName();
            long createdAtMillis = user.getCreatedAtMillis();
            String dateText = createdAtMillis > 0
                    ? DATE_FORMAT.format(new Date(createdAtMillis))
                    : getString(R.string.admin_unknown_date);
            int posts = 0;
            UserPostStats stat = currentPostStats.get(normalized);
            if (stat != null) {
                posts = stat.total;
            }
            refreshed.add(new AdminUserAdapter.Item(
                    user.getId(),
                    normalized,
                    displayName,
                    rawEmail,
                    getString(R.string.admin_user_meta, dateText, posts)
            ));

            UserAccount account = new UserAccount();
            account.setName(displayName);
            account.setCreatedAt(createdAtMillis > 0 ? createdAtMillis : System.currentTimeMillis());
            accountMap.put(normalized, account);
        }

        currentUserAccounts.clear();
        currentUserAccounts.putAll(accountMap);
        totalUsers = refreshed.size();
        userAdapter.submitList(refreshed);
        if (userSummaryView != null) {
            userSummaryView.setText(getString(R.string.admin_user_summary, totalUsers));
        }
        if (emptyView != null) {
            emptyView.setVisibility(refreshed.isEmpty() ? View.VISIBLE : View.GONE);
        }
        finalizeDashboard();
    }

    private void applyLocalUsers() {
        Map<String, UserAccount> allAccounts = new HashMap<>(accountManager.getAllAccounts());
        List<Map.Entry<String, UserAccount>> entries = new ArrayList<>(allAccounts.entrySet());
        Collections.sort(entries, (o1, o2) -> {
            String email1 = o1.getKey() != null ? o1.getKey().toLowerCase(Locale.getDefault()) : "";
            String email2 = o2.getKey() != null ? o2.getKey().toLowerCase(Locale.getDefault()) : "";
            int total1 = currentPostStats.containsKey(email1) ? currentPostStats.get(email1).total : 0;
            int total2 = currentPostStats.containsKey(email2) ? currentPostStats.get(email2).total : 0;
            if (total1 == total2) {
                return email1.compareTo(email2);
            }
            return Integer.compare(total2, total1);
        });

        List<AdminUserAdapter.Item> items = new ArrayList<>();
        String adminEmail = getCurrentAdminEmailNormalized();
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
            String displayName = account != null && !TextUtils.isEmpty(account.getName())
                    ? account.getName()
                    : emailKey;
            String dateText = account != null && account.getCreatedAt() > 0
                    ? DATE_FORMAT.format(new Date(account.getCreatedAt()))
                    : getString(R.string.admin_unknown_date);
            int totalPosts = 0;
            UserPostStats stat = currentPostStats.get(normalizedEmail);
            if (stat != null) {
                totalPosts = stat.total;
            }

            items.add(new AdminUserAdapter.Item(
                    null,
                    normalizedEmail,
                    displayName,
                    emailKey,
                    getString(R.string.admin_user_meta, dateText, totalPosts)
            ));
        }

        currentUserAccounts.clear();
        for (Map.Entry<String, UserAccount> entry : entries) {
            if (TextUtils.isEmpty(entry.getKey())) {
                continue;
            }
            String normalizedEmail = entry.getKey().trim().toLowerCase(Locale.getDefault());
            currentUserAccounts.put(normalizedEmail, entry.getValue());
        }

        totalUsers = items.size();
        userAdapter.submitList(items);
        if (userSummaryView != null) {
            userSummaryView.setText(getString(R.string.admin_user_summary, totalUsers));
        }
        if (emptyView != null) {
            emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        }
        finalizeDashboard();
    }

    private void finalizeDashboard() {
        int activeUsers = totalUsers;
        int lockedUsers = 0;
        int totalLikes = 0;
        updateOverviewAndDetail(
                activeUsers,
                lockedUsers,
                totalRecipes,
                totalUserPosts,
                totalApprovedPosts,
                totalPendingPosts,
                totalLikes
        );

        String adminEmailNormalized = getCurrentAdminEmailNormalized();
        updateTopUserCard(currentPostStats, currentUserAccounts, adminEmailNormalized);
        updateReviewSubtitle();
        updateStatsSubtitle();
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

    private void updateOverviewAndDetail(int activeUsers,
                                         int lockedUsers,
                                         int totalRecipesCount,
                                         int totalUserPosts,
                                         int totalApprovedPosts,
                                         int totalPendingPosts,
                                         int totalLikes) {
        int totalUsersCount = activeUsers + lockedUsers;
        if (overviewUsersValue != null) {
            overviewUsersValue.setText(String.valueOf(totalUsersCount));
        }
        if (overviewUsersSub != null) {
            overviewUsersSub.setText(getString(R.string.admin_overview_users_sub, activeUsers, lockedUsers));
        }
        if (overviewContentValue != null) {
            overviewContentValue.setText(String.valueOf(totalRecipesCount + totalUserPosts));
        }
        if (overviewContentSub != null) {
            overviewContentSub.setText(getString(R.string.admin_overview_content_sub, totalRecipesCount, totalUserPosts));
        }
        if (overviewApprovedValue != null) {
            overviewApprovedValue.setText(String.valueOf(totalRecipesCount + totalApprovedPosts));
        }
        if (overviewApprovedSub != null) {
            overviewApprovedSub.setText(getString(R.string.admin_overview_status_sub, totalRecipesCount, totalApprovedPosts));
        }
        if (overviewPendingValue != null) {
            overviewPendingValue.setText(String.valueOf(totalPendingPosts));
        }
        if (overviewPendingSub != null) {
            overviewPendingSub.setText(getString(R.string.admin_overview_status_sub, 0, totalPendingPosts));
        }

        if (detailUsersTotalValue != null) {
            detailUsersTotalValue.setText(String.valueOf(totalUsersCount));
        }
        if (detailUsersActiveValue != null) {
            detailUsersActiveValue.setText(String.valueOf(activeUsers));
        }
        if (detailUsersLockedValue != null) {
            detailUsersLockedValue.setText(String.valueOf(lockedUsers));
        }
        if (detailRecipesTotalValue != null) {
            detailRecipesTotalValue.setText(String.valueOf(totalRecipesCount));
        }
        if (detailRecipesApprovedValue != null) {
            detailRecipesApprovedValue.setText(String.valueOf(totalRecipesCount));
        }
        if (detailRecipesPendingValue != null) {
            detailRecipesPendingValue.setText(String.valueOf(totalPendingPosts));
        }
        if (detailRecipesLikesValue != null) {
            detailRecipesLikesValue.setText(String.valueOf(totalLikes));
        }
        if (detailPostsTotalValue != null) {
            detailPostsTotalValue.setText(String.valueOf(totalUserPosts));
        }
        if (detailPostsApprovedValue != null) {
            detailPostsApprovedValue.setText(String.valueOf(totalApprovedPosts));
        }
        if (detailPostsPendingValue != null) {
            detailPostsPendingValue.setText(String.valueOf(totalPendingPosts));
        }
    }

    private void updateTopUserCard(Map<String, UserPostStats> postStats,
                                   Map<String, UserAccount> allAccounts,
                                   String adminEmail) {
        if (topUserCard == null) {
            return;
        }
        String topEmail = null;
        UserPostStats topStats = null;
        for (Map.Entry<String, UserPostStats> entry : postStats.entrySet()) {
            String email = entry.getKey();
            if (email.equals(adminEmail)) {
                continue;
            }
            UserPostStats stats = entry.getValue();
            if (stats.total <= 0) {
                continue;
            }
            if (topStats == null || stats.total > topStats.total) {
                topStats = stats;
                topEmail = email;
            }
        }
        if (topStats == null || topStats.total <= 0) {
            topUserCard.setVisibility(View.GONE);
            return;
        }

        topUserCard.setVisibility(View.VISIBLE);
        if (topUserRankView != null) {
            topUserRankView.setText("1");
        }
        String displayEmail = topEmail;
        String displayName = displayEmail;
        UserAccount account = allAccounts.get(topEmail);
        if (account != null && !TextUtils.isEmpty(account.getName())) {
            displayName = account.getName();
        }
        if (topUserNameView != null) {
            topUserNameView.setText(displayName);
        }
        if (topUserEmailView != null) {
            topUserEmailView.setText(displayEmail);
        }
        if (topUserTotalView != null) {
            topUserTotalView.setText(getString(R.string.admin_top_user_total, topStats.total));
        }
        if (topUserStatsView != null) {
            topUserStatsView.setText(getString(R.string.admin_top_user_stats, topStats.approved, topStats.pending));
        }
    }

    private static class UserPostStats {
        int total;
        int approved;
        int pending;
    }

    private enum Section {
        USERS,
        RECIPES,
        STATS
    }
}

