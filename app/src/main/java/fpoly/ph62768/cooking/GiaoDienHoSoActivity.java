package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;

public class GiaoDienHoSoActivity extends AppCompatActivity {

    private String currentUserEmail = "";
    private String currentUserName = "";

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

        ImageButton backButton = findViewById(R.id.profile_back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        ImageView avatarImage = findViewById(R.id.profile_avatar);
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1524504388940-b1c1722653e1")
                .placeholder(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(avatarImage);

        LinearLayout editProfileButton = findViewById(R.id.profile_edit_button);
        editProfileButton.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng chỉnh sửa hồ sơ đang phát triển", Toast.LENGTH_SHORT).show()
        );

        TextView nameText = findViewById(R.id.profile_name);
        TextView emailText = findViewById(R.id.profile_email);
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

        setupRows(accountManager);

        FloatingActionButton fab = findViewById(R.id.profile_fab);
        fab.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng thêm công thức đang được phát triển", Toast.LENGTH_SHORT).show()
        );

        LinearLayout tabHome = findViewById(R.id.profile_tab_home);
        LinearLayout tabHot = findViewById(R.id.profile_tab_hot);
        LinearLayout tabRandom = findViewById(R.id.profile_tab_random);
        LinearLayout tabProfile = findViewById(R.id.profile_tab_profile);

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
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        tabRandom.setOnClickListener(v -> {
            selectBottomTab(ProfileTab.RANDOM);
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        tabProfile.setOnClickListener(v -> selectBottomTab(ProfileTab.PROFILE));

        selectBottomTab(ProfileTab.PROFILE);
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
        int[] infoRowIds = {
                R.id.profile_pending_row,
                R.id.profile_saved_row,
                R.id.profile_history_row,
                R.id.profile_favorite_row,
                R.id.profile_settings_row,
                R.id.profile_help_row
        };
        for (int rowId : infoRowIds) {
            LinearLayout row = findViewById(rowId);
            row.setOnClickListener(v ->
                    Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
            );
        }

        LinearLayout logoutRow = findViewById(R.id.profile_logout_row);
        logoutRow.setOnClickListener(v -> {
            accountManager.clearCurrentUser(this);
            Intent intent = new Intent(this, GiaoDienChinhActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

