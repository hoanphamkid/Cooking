package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import fpoly.ph62768.cooking.RecipeCollectionActivity.CollectionType;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.ui.ChinhSuaHoSoActivity;

public class GiaoDienHoSoActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT_PROFILE = 1001;
    private String currentUserEmail = "";
    private String currentUserName = "";
    private BaiChoDuyetStore baiChoDuyetStore;
    private TextView pendingBadgeView;
    private TextView pendingNoticeView;

    private TextView nameText;
    private TextView emailText;
    private ImageView avatarImage;

    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_ho_so);

        Intent intent = getIntent();


        if (intent != null) {
            String email = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL);
            if (email != null) {
                currentUserEmail = email;
            }
            String name = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_NAME);
            if (name != null) {
                currentUserName = name;
            }
        }

        baiChoDuyetStore = new BaiChoDuyetStore(this);
        nameText = findViewById(R.id.profile_name);
        emailText = findViewById(R.id.profile_email);
        ImageButton backButton = findViewById(R.id.profile_back_button);
        backButton.setOnClickListener(v -> onBackPressed());
        avatarImage = findViewById(R.id.profile_avatar);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1524504388940-b1c1722653e1")
                .placeholder(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(avatarImage);





        UserAccountManager accountManager = new UserAccountManager(this);
        UserAccount account = accountManager.getAccount(currentUserEmail);
        if (currentUserName == null || currentUserName.trim().isEmpty()) {
            currentUserName = getString(R.string.profile_user_name);
        }
        if (account != null) {
            String displayName = account.getName() != null && !account.getName().trim().isEmpty()
                    ? account.getName()
                    : currentUserName;
            currentUserName = displayName;
        }
        nameText.setText(currentUserName);
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            emailText.setText(currentUserEmail);
        }

        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Reload user data sau khi chỉnh sửa thành công
                        loadUserData();
                        Toast.makeText(this, "Hồ sơ đã được cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        setupRows(accountManager);

        LinearLayout tabHome = findViewById(R.id.profile_tab_home);
        LinearLayout tabHot = findViewById(R.id.profile_tab_hot);
        LinearLayout tabRandom = findViewById(R.id.profile_tab_random);
        LinearLayout tabProfile = findViewById(R.id.profile_tab_profile);
        LinearLayout pendingRow = findViewById(R.id.profile_pending_row);
        LinearLayout savedRow = findViewById(R.id.profile_saved_row);
        LinearLayout historyRow = findViewById(R.id.profile_history_row);
        LinearLayout favoriteRow = findViewById(R.id.profile_favorite_row);
        LinearLayout settingsRow = findViewById(R.id.profile_settings_row);
        LinearLayout helpRow = findViewById(R.id.profile_help_row);
        LinearLayout editProfileButton = findViewById(R.id.profile_edit_button);
        pendingBadgeView = findViewById(R.id.profile_pending_badge);
        pendingNoticeView = findViewById(R.id.profile_pending_notice);
        //editProfileLauncher = findViewById(R.id.profile_launcher)

        tabHome.setOnClickListener(v -> {
            selectBottomTab(ProfileTab.HOME);
            Intent homeIntent = new Intent(this, GiaoDienTrangChuActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.putExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL, currentUserEmail);
            homeIntent.putExtra(GiaoDienTrangChuActivity.EXTRA_USER_NAME, currentUserName);
            startActivity(homeIntent);
            finish();
        });

        tabHot.setOnClickListener(v -> {
            selectBottomTab(ProfileTab.HOT);
            Intent hotIntent = new Intent(this, HotRecipesActivity.class);
            startActivity(hotIntent);
        });

        tabRandom.setOnClickListener(v -> {
            selectBottomTab(ProfileTab.RANDOM);
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        tabProfile.setOnClickListener(v -> selectBottomTab(ProfileTab.PROFILE));

        pendingRow.setOnClickListener(v -> {
            Intent pendingIntent = new Intent(this, DanhSachChoDuyetActivity.class);
            pendingIntent.putExtra(DanhSachChoDuyetActivity.EXTRA_USER_EMAIL, currentUserEmail);
            pendingIntent.putExtra(DanhSachChoDuyetActivity.EXTRA_FILTER_EMAIL, currentUserEmail);
            startActivity(pendingIntent);
        });
        if (pendingNoticeView != null) {
            pendingNoticeView.setOnClickListener(v -> pendingRow.performClick());
        }
        savedRow.setOnClickListener(v -> openCollection(CollectionType.SAVED));
        historyRow.setOnClickListener(v -> openCollection(CollectionType.HISTORY));
        favoriteRow.setOnClickListener(v -> openCollection(CollectionType.FAVORITE));
        settingsRow.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );
        helpRow.setOnClickListener(v ->
                startActivity(new Intent(this, HelpActivity.class))
        );

        editProfileButton.setOnClickListener(v -> {
            Intent editintent = new Intent(this, ChinhSuaHoSoActivity.class);
            editintent.putExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL, currentUserEmail);
            startActivityForResult(editintent, REQUEST_EDIT_PROFILE);
        });



        updatePendingState();
        selectBottomTab(ProfileTab.PROFILE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePendingState();
    }


    private void loadUserData() {
        UserAccountManager accountManager = new UserAccountManager(this);
        UserAccount account = accountManager.getAccount(currentUserEmail);

        if (account != null) {
            // Cập nhật tên hiển thị
            String displayName = account.getName() != null && !account.getName().trim().isEmpty()
                    ? account.getName()
                    : currentUserName;

            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = getString(R.string.profile_user_name);
            }

            currentUserName = displayName;

            if (nameText != null) {
                nameText.setText(currentUserName);
            }

            // Cập nhật email
            if (emailText != null && currentUserEmail != null && !currentUserEmail.isEmpty()) {
                emailText.setText(currentUserEmail);
            }

            // Cập nhật avatar
            if (avatarImage != null) {
                String avatarUrl = account.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .into(avatarImage);
                } else {
                    // Load avatar mặc định
                    Glide.with(this)
                            .load("https://images.unsplash.com/photo-1524504388940-b1c1722653e1")
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .into(avatarImage);
                }
            }
        } else {
            // Nếu không có account, load dữ liệu mặc định
            if (currentUserName == null || currentUserName.trim().isEmpty()) {
                currentUserName = getString(R.string.profile_user_name);
            }

            if (nameText != null) {
                nameText.setText(currentUserName);
            }

            if (emailText != null && currentUserEmail != null && !currentUserEmail.isEmpty()) {
                emailText.setText(currentUserEmail);
            }

            if (avatarImage != null) {
                Glide.with(this)
                        .load("https://images.unsplash.com/photo-1524504388940-b1c1722653e1")
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(avatarImage);
            }
        }
    }

    private enum ProfileTab {
        HOME, HOT, RANDOM, PROFILE
    }

    private void selectBottomTab(ProfileTab tab) {
        updateNavState(R.id.profile_tab_home_icon, R.id.profile_tab_home_label, tab == ProfileTab.HOME);
        updateNavState(R.id.profile_tab_hot_icon, R.id.profile_tab_hot_label, tab == ProfileTab.HOT);
        updateNavState(R.id.profile_tab_random_icon, R.id.profile_tab_random_label, tab == ProfileTab.RANDOM);
        updateNavState(R.id.profile_tab_profile_icon, R.id.profile_tab_profile_label, tab == ProfileTab.PROFILE);
    }

    private void updateNavState(int iconId, int labelId, boolean selected) {
        ImageView icon = findViewById(iconId);
        TextView label = findViewById(labelId);
        int color = getColor(selected ? R.color.bottom_nav_active : R.color.bottom_nav_inactive);
        icon.setColorFilter(color);
        label.setTextColor(color);
    }

    private void setupRows(UserAccountManager accountManager) {
        LinearLayout logoutRow = findViewById(R.id.profile_logout_row);
        logoutRow.setOnClickListener(v -> {
            accountManager.clearCurrentUser(this);
            Intent intent = new Intent(this, GiaoDienChinhActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void openCollection(CollectionType type) {
        Intent intent = new Intent(this, RecipeCollectionActivity.class);
        intent.putExtra(RecipeCollectionActivity.EXTRA_COLLECTION_TYPE, type.name());
        startActivity(intent);
    }

    private void updatePendingState() {
        if (baiChoDuyetStore == null || currentUserEmail == null) {
            return;
        }
        int pendingCount = baiChoDuyetStore.demSoBaiCho(currentUserEmail);
        if (pendingBadgeView != null) {
            pendingBadgeView.setText(String.valueOf(pendingCount));
            pendingBadgeView.setVisibility(View.VISIBLE);
            pendingBadgeView.setAlpha(pendingCount > 0 ? 1f : 0.4f);
        }
        if (pendingNoticeView != null) {
            if (pendingCount > 0) {
                pendingNoticeView.setText(getString(R.string.profile_pending_notice, pendingCount));
                pendingNoticeView.setVisibility(View.VISIBLE);
            } else {
                pendingNoticeView.setVisibility(View.GONE);
            }
        }
    }
}

