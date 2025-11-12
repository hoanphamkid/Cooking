package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.text.TextUtils;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore.BanGhi;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.BaiChoDuyet;
import fpoly.ph62768.cooking.ui.QuanTriBaiChoAdapter;
import fpoly.ph62768.cooking.ui.QuanTriNguoiDungAdapter;

public class ManHinhQuanTriActivity extends AppCompatActivity {

    private TextView userTotalView;
    private TextView recipeTotalView;
    private TextView likeTotalView;
    private TextView pendingTitleView;
    private TextView adminNameView;
    private TextView adminEmailView;
    private TextView pendingEmptyView;
    private TextView userEmptyView;

    private QuanTriNguoiDungAdapter userAdapter;
    private QuanTriBaiChoAdapter pendingAdapter;

    private UserAccountManager accountManager;
    private BaiChoDuyetStore baiChoDuyetStore;
    private int lastUserCount;
    private int lastRecipeCount;
    private int lastPendingCount;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_tri);

        accountManager = new UserAccountManager(this);
        baiChoDuyetStore = new BaiChoDuyetStore(this);

        ImageButton backButton = findViewById(R.id.admin_back_button);
        backButton.setOnClickListener(v -> finish());

        adminNameView = findViewById(R.id.admin_name);
        adminEmailView = findViewById(R.id.admin_email);
        userTotalView = findViewById(R.id.admin_user_total);
        recipeTotalView = findViewById(R.id.admin_recipe_total);
        likeTotalView = findViewById(R.id.admin_like_total);
        pendingTitleView = findViewById(R.id.admin_pending_title);
        pendingEmptyView = findViewById(R.id.admin_pending_empty);
        userEmptyView = findViewById(R.id.admin_user_empty);

        findViewById(R.id.admin_card_manage_users).setOnClickListener(v -> {
            startActivity(new Intent(this, QuanLyNguoiDungActivity.class));
        });
        findViewById(R.id.admin_card_manage_recipes).setOnClickListener(v -> {
            startActivity(new Intent(this, QuanLyCongThucActivity.class));
        });
        findViewById(R.id.admin_card_review).setOnClickListener(v -> {
            Intent reviewIntent = new Intent(this, DanhSachChoDuyetActivity.class);
            startActivity(reviewIntent);
        });
        findViewById(R.id.admin_card_stats).setOnClickListener(v -> showStatsDialog());

        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_NAME);
            String email = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL);
            if (name != null && !name.trim().isEmpty()) {
                adminNameView.setText(name);
            }
            if (email != null && !email.trim().isEmpty()) {
                adminEmailView.setText(email);
            }
        }

        RecyclerView userRecycler = findViewById(R.id.admin_user_recycler);
        userAdapter = new QuanTriNguoiDungAdapter();
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        userRecycler.setAdapter(userAdapter);
        userAdapter.setListener(new QuanTriNguoiDungAdapter.Listener() {
            @Override
            public void onPostsClick(String email) {
                Intent intent = new Intent(ManHinhQuanTriActivity.this, DanhSachChoDuyetActivity.class);
                intent.putExtra(DanhSachChoDuyetActivity.EXTRA_FILTER_EMAIL, email);
                startActivity(intent);
            }

            @Override
            public void onLockClick(String email) {
                Toast.makeText(ManHinhQuanTriActivity.this, getString(R.string.admin_toast_working), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(String email) {
                new androidx.appcompat.app.AlertDialog.Builder(ManHinhQuanTriActivity.this)
                        .setTitle(R.string.admin_user_delete)
                        .setMessage(getString(R.string.admin_confirm_delete_user, email))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            accountManager.removeAccount(email);
                            reloadData();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

        RecyclerView pendingRecycler = findViewById(R.id.admin_pending_recycler);
        pendingAdapter = new QuanTriBaiChoAdapter();
        pendingRecycler.setLayoutManager(new LinearLayoutManager(this));
        pendingRecycler.setAdapter(pendingAdapter);

        pendingAdapter.setListener(new QuanTriBaiChoAdapter.Listener() {
            @Override
            public void onApprove(BanGhi record) {
                baiChoDuyetStore.capNhatTrangThai(record.email, record.baiChoDuyet.getId(), BaiChoDuyet.TrangThai.APPROVED);
                Toast.makeText(ManHinhQuanTriActivity.this, R.string.admin_update_success, Toast.LENGTH_SHORT).show();
                reloadData();
            }

            @Override
            public void onReject(BanGhi record) {
                baiChoDuyetStore.capNhatTrangThai(record.email, record.baiChoDuyet.getId(), BaiChoDuyet.TrangThai.REJECTED);
                Toast.makeText(ManHinhQuanTriActivity.this, R.string.admin_update_success, Toast.LENGTH_SHORT).show();
                reloadData();
            }
        });

        findViewById(R.id.admin_refresh_button).setOnClickListener(v -> reloadData());

        setupBottomNav();

        reloadData();
    }

    private void setupBottomNav() {
        LinearLayout tabUsers = findViewById(R.id.admin_tab_users);
        LinearLayout tabRecipes = findViewById(R.id.admin_tab_recipes);
        LinearLayout tabReview = findViewById(R.id.admin_tab_review);
        LinearLayout tabStats = findViewById(R.id.admin_tab_stats);
        LinearLayout tabLogout = findViewById(R.id.admin_tab_logout);

        tabUsers.setOnClickListener(v -> startActivity(new Intent(this, QuanLyNguoiDungActivity.class)));
        tabRecipes.setOnClickListener(v -> startActivity(new Intent(this, QuanLyCongThucActivity.class)));
        tabReview.setOnClickListener(v -> startActivity(new Intent(this, DanhSachChoDuyetActivity.class)));
        tabStats.setOnClickListener(v -> showStatsDialog());
        tabLogout.setOnClickListener(v -> {
            accountManager.clearCurrentUser(this);
            Intent intent = new Intent(this, GiaoDienChinhActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void reloadData() {
        Map<String, UserAccount> accounts = accountManager.getAllAccounts();
        List<QuanTriNguoiDungAdapter.Item> userItems = new ArrayList<>();
        String adminEmail = getString(R.string.admin_default_email).trim().toLowerCase(Locale.getDefault());
        Map<String, Integer> postCounts = new HashMap<>();
        for (BaiChoDuyetStore.BanGhi record : baiChoDuyetStore.layTatCaBanGhi()) {
            String email = record.email;
            if (TextUtils.isEmpty(email)) {
                continue;
            }
            String normalized = email.trim().toLowerCase(Locale.getDefault());
            postCounts.put(normalized, postCounts.getOrDefault(normalized, 0) + 1);
        }
        List<Map.Entry<String, UserAccount>> entries = new ArrayList<>(accounts.entrySet());
        Collections.sort(entries, (e1, e2) -> {
            long t1 = e1.getValue() != null ? e1.getValue().getCreatedAt() : 0L;
            long t2 = e2.getValue() != null ? e2.getValue().getCreatedAt() : 0L;
            return Long.compare(t2, t1);
        });
        for (Map.Entry<String, UserAccount> entry : entries) {
            String email = entry.getKey();
            if (TextUtils.isEmpty(email) || email.equals(adminEmail)) {
                continue;
            }
            UserAccount account = entry.getValue();
            String normalizedEmail = email.trim().toLowerCase(Locale.getDefault());
            String displayEmail = !TextUtils.isEmpty(email)
                    ? email
                    : getString(R.string.admin_unknown_email);
            String displayName = (account != null && !TextUtils.isEmpty(account.getName()))
                    ? account.getName()
                    : displayEmail;
            if (TextUtils.isEmpty(displayName)) {
                displayName = getString(R.string.admin_unknown_user);
            }
            String dateText;
            if (account != null && account.getCreatedAt() > 0) {
                dateText = dateFormat.format(new Date(account.getCreatedAt()));
            } else {
                dateText = getString(R.string.admin_unknown_date);
            }
            int totalPosts = postCounts.getOrDefault(normalizedEmail, 0);
            String meta = getString(R.string.admin_user_meta, dateText, totalPosts);
            userItems.add(new QuanTriNguoiDungAdapter.Item(
                    normalizedEmail,
                    displayName,
                    displayEmail,
                    meta
            ));
        }
        userAdapter.submitList(userItems);
        userEmptyView.setVisibility(userItems.isEmpty() ? View.VISIBLE : View.GONE);

        List<BanGhi> pendingRecords = new ArrayList<>();
        for (BanGhi record : baiChoDuyetStore.layTatCaBanGhi()) {
            if (record.baiChoDuyet.getTrangThai() == BaiChoDuyet.TrangThai.PENDING) {
                pendingRecords.add(record);
            }
        }
        pendingAdapter.submitList(pendingRecords);
        pendingEmptyView.setVisibility(pendingRecords.isEmpty() ? View.VISIBLE : View.GONE);

        int recipeCount = RecipeRepository.getInstance().getRecipes().size();
        updateCounts(userItems.size(), recipeCount, pendingRecords.size());
    }

    private void updateCounts(int visibleUsers, int totalRecipes, int pendingCount) {
        lastUserCount = visibleUsers;
        lastRecipeCount = totalRecipes;
        lastPendingCount = pendingCount;
        userTotalView.setText(String.format(Locale.getDefault(), "%d", visibleUsers));
        recipeTotalView.setText(String.format(Locale.getDefault(), "%d", totalRecipes));
        likeTotalView.setText("0");
        pendingTitleView.setText(getString(R.string.admin_review_recipes) + " (" + pendingCount + ")");
    }

    private void showStatsDialog() {
        String message = getString(R.string.admin_stats_message, lastUserCount, lastRecipeCount, lastPendingCount);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.admin_stats_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}

