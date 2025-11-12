package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fpoly.ph62768.cooking.RecipeCollectionActivity.CollectionType;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.model.RecipeCategory;
import fpoly.ph62768.cooking.ui.RecipeAdapter;

public class GiaoDienTrangChuActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "extra_user_email";
    public static final String EXTRA_USER_NAME = "extra_user_name";

    private RecipeAdapter adapter;
    private final List<Recipe> allRecipes = new ArrayList<>();

    private RecipeCategory selectedCategory = RecipeCategory.ALL;
    private String currentQuery = "";
    private String currentUserEmail = "";
    private String currentUserName = "";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private UserAccountManager accountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_trang_chu);

        accountManager = new UserAccountManager(this);
        drawerLayout = findViewById(R.id.home_drawer_layout);
        navigationView = findViewById(R.id.home_navigation_view);
        ImageButton menuButton = findViewById(R.id.home_menu_button);

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        navigationView.setCheckedItem(R.id.nav_home);

        restoreSession(getIntent());
        updateDrawerHeader();

        adapter = new RecipeAdapter();
        allRecipes.addAll(RecipeRepository.getInstance().getRecipes());

        RecyclerView recyclerView = findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        adapter.setOnRecipeClickListener(recipe -> {
            Intent detailIntent = new Intent(this, GiaoDienChiTietCongThucActivity.class);
            detailIntent.putExtra(GiaoDienChiTietCongThucActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(detailIntent);
        });

        TextInputEditText searchInput = findViewById(R.id.home_search_input);
        ChipGroup chipGroup = findViewById(R.id.category_chip_group);
        FloatingActionButton fab = findViewById(R.id.home_fab);
        LinearLayout tabHome = findViewById(R.id.tab_home);
        LinearLayout tabHot = findViewById(R.id.tab_hot);
        LinearLayout tabRandom = findViewById(R.id.tab_random);
        LinearLayout tabProfile = findViewById(R.id.tab_profile);

        tabHome.setOnClickListener(v -> selectTab(TabType.HOME));
        tabHot.setOnClickListener(v -> {
            selectTab(TabType.HOT);
            showComingSoon();
        });
        tabRandom.setOnClickListener(v -> {
            selectTab(TabType.RANDOM);
            showComingSoon();
        });
        tabProfile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(this, GiaoDienHoSoActivity.class);
            profileIntent.putExtra(EXTRA_USER_EMAIL, currentUserEmail);
            profileIntent.putExtra(EXTRA_USER_NAME, currentUserName);
            startActivity(profileIntent);
        });

        selectTab(TabType.HOME);

        fab.setOnClickListener(v -> {
            if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
                Toast.makeText(this, R.string.create_recipe_no_user, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent createIntent = new Intent(this, DangBaiActivity.class);
            createIntent.putExtra(DangBaiActivity.EXTRA_USER_EMAIL, currentUserEmail);
            startActivity(createIntent);
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCategory = RecipeCategory.ALL;
            } else {
                int id = checkedIds.get(0);
                selectedCategory = mapChipToCategory(id);
            }
            applyFilters();
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s != null ? s.toString() : "";
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        applyFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTab(TabType.HOME);
        restoreSession(getIntent());
        updateDrawerHeader();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.nav_home) {
            return true;
        } else if (id == R.id.nav_profile) {
            intent = new Intent(this, GiaoDienHoSoActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, currentUserEmail);
            intent.putExtra(EXTRA_USER_NAME, currentUserName);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_pending) {
            Intent pendingIntent = new Intent(this, DanhSachChoDuyetActivity.class);
            pendingIntent.putExtra(DanhSachChoDuyetActivity.EXTRA_USER_EMAIL, currentUserEmail);
            startActivity(pendingIntent);
            return true;
        } else if (id == R.id.nav_saved) {
            openCollection(RecipeCollectionActivity.CollectionType.SAVED);
            return true;
        } else if (id == R.id.nav_history) {
            openCollection(RecipeCollectionActivity.CollectionType.HISTORY);
            return true;
        } else if (id == R.id.nav_favorite) {
            openCollection(RecipeCollectionActivity.CollectionType.FAVORITE);
            return true;
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else if (id == R.id.nav_logout) {
            accountManager.clearCurrentUser(this);
            intent = new Intent(this, GiaoDienChinhActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else {
            return true;
        }
    }

    private void updateDrawerHeader() {
        if (navigationView == null) {
            return;
        }
        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) {
            return;
        }
        TextView nameText = headerView.findViewById(R.id.nav_header_name);
        TextView emailText = headerView.findViewById(R.id.nav_header_email);
        ImageView avatar = headerView.findViewById(R.id.nav_header_avatar);

        String displayName = currentUserName != null && !currentUserName.trim().isEmpty()
                ? currentUserName
                : getString(R.string.profile_user_name);
        String displayEmail = currentUserEmail != null && !currentUserEmail.trim().isEmpty()
                ? currentUserEmail
                : getString(R.string.profile_user_email);

        nameText.setText(displayName);
        emailText.setText(displayEmail);
        avatar.setImageResource(R.drawable.ic_profile_placeholder);
    }

    private void openCollection(CollectionType type) {
        Intent intent = new Intent(this, RecipeCollectionActivity.class);
        intent.putExtra(RecipeCollectionActivity.EXTRA_COLLECTION_TYPE, type.name());
        startActivity(intent);
    }

    private void applyFilters() {
        List<Recipe> filtered = new ArrayList<>();
        Locale locale = Locale.getDefault();
        String queryLower = currentQuery.toLowerCase(locale);
        for (Recipe recipe : allRecipes) {
            boolean matchesCategory = selectedCategory == RecipeCategory.ALL || recipe.getCategory() == selectedCategory;
            boolean matchesQuery = currentQuery.isEmpty() ||
                    recipe.getName().toLowerCase(locale).contains(queryLower);
            if (matchesCategory && matchesQuery) {
                filtered.add(recipe);
            }
        }
        adapter.submitList(filtered);
    }

    private RecipeCategory mapChipToCategory(int chipId) {
        if (chipId == R.id.chip_lowcal) {
            return RecipeCategory.LOW_CAL;
        } else if (chipId == R.id.chip_healthy) {
            return RecipeCategory.HEALTHY;
        } else if (chipId == R.id.chip_quick) {
            return RecipeCategory.QUICK;
        } else if (chipId == R.id.chip_traditional) {
            return RecipeCategory.TRADITIONAL;
        } else if (chipId == R.id.chip_dessert) {
            return RecipeCategory.DESSERT;
        } else if (chipId == R.id.chip_drink) {
            return RecipeCategory.DRINK;
        }
        return RecipeCategory.ALL;
    }

    private enum TabType {
        HOME, HOT, RANDOM, PROFILE
    }

    private void selectTab(TabType tabType) {
        updateTabState(R.id.tab_home, R.id.tab_home_icon, R.id.tab_home_label, tabType == TabType.HOME);
        updateTabState(R.id.tab_hot, R.id.tab_hot_icon, R.id.tab_hot_label, tabType == TabType.HOT);
        updateTabState(R.id.tab_random, R.id.tab_random_icon, R.id.tab_random_label, tabType == TabType.RANDOM);
        updateTabState(R.id.tab_profile, R.id.tab_profile_icon, R.id.tab_profile_label, tabType == TabType.PROFILE);
    }

    private void updateTabState(int containerId, int iconId, int labelId, boolean selected) {
        LinearLayout container = findViewById(containerId);
        ImageView icon = findViewById(iconId);
        TextView label = findViewById(labelId);
        int color = getColor(selected ? R.color.bottom_nav_active : R.color.bottom_nav_inactive);
        icon.setColorFilter(color);
        label.setTextColor(color);
        container.setSelected(selected);
    }

    private void showComingSoon() {
        Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
    }

    private void restoreSession(Intent intent) {
        if (intent != null) {
            String email = intent.getStringExtra(EXTRA_USER_EMAIL);
            String name = intent.getStringExtra(EXTRA_USER_NAME);
            if (email != null && !email.isEmpty()) {
                currentUserEmail = email;
                accountManager.setCurrentUser(this, email);
            }
            if (name != null && !name.isEmpty()) {
                currentUserName = name;
            }
        }
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            currentUserEmail = accountManager.getCurrentUserEmail(this);
        }
        if (currentUserName == null || currentUserName.trim().isEmpty()) {
            UserAccount account = accountManager.getAccount(currentUserEmail);
            if (account != null) {
                currentUserName = account.getName();
            }
        }
        if (currentUserName == null) {
            currentUserName = "";
        }
    }
}
