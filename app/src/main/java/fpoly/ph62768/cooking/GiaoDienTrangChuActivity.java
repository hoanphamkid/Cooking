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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fpoly.ph62768.cooking.RecipeCollectionActivity.CollectionType;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.model.RecipeCategory;
import fpoly.ph62768.cooking.model.RecipeStep;
import fpoly.ph62768.cooking.ui.RecipeAdapter;

public class GiaoDienTrangChuActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "extra_user_email";
    public static final String EXTRA_USER_NAME = "extra_user_name";
    private static final int REQUEST_CREATE_RECIPE = 101;

    private RecipeAdapter adapter;
    private final List<Recipe> allRecipes = new ArrayList<>();

    private RecipeCategory selectedCategory = RecipeCategory.ALL;
    private String currentQuery = "";
    private String currentUserEmail = "";
    private String currentUserName = "";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private UserAccountManager accountManager;
    private TextInputLayout searchLayout;
    private View categoryContainer;
    private View randomFilterCard;
    private TextInputEditText randomIngredientInput;
    private ChipGroup randomMoodChipGroup;
    private MaterialButton randomClearButton;
    private TextView randomResultLabel;
    private TextView randomEmptyView;

    private String currentIngredient = "";
    private MoodFilter currentMood = MoodFilter.NONE;
    private TabType currentTab = TabType.HOME;
    private final Map<String, String> recipeSearchIndex = new HashMap<>();
    private final Random randomGenerator = new Random();

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
        RecyclerView recyclerView = findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        adapter.setOnRecipeClickListener(recipe -> {
            Intent detailIntent = new Intent(this, GiaoDienChiTietCongThucActivity.class);
            detailIntent.putExtra(GiaoDienChiTietCongThucActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(detailIntent);
        });

        searchLayout = findViewById(R.id.home_search_layout);
        TextInputEditText searchInput = findViewById(R.id.home_search_input);
        ChipGroup chipGroup = findViewById(R.id.category_chip_group);
        categoryContainer = findViewById(R.id.category_container);
        randomFilterCard = findViewById(R.id.random_filter_card);
        randomIngredientInput = findViewById(R.id.random_ingredient_input);
        randomMoodChipGroup = findViewById(R.id.random_mood_chip_group);
        randomClearButton = findViewById(R.id.random_clear_button);
        randomResultLabel = findViewById(R.id.random_result_label);
        randomEmptyView = findViewById(R.id.random_empty_view);

        FloatingActionButton fab = findViewById(R.id.home_fab);
        LinearLayout tabHome = findViewById(R.id.tab_home);
        LinearLayout tabHot = findViewById(R.id.tab_hot);
        LinearLayout tabRandom = findViewById(R.id.tab_random);
        LinearLayout tabProfile = findViewById(R.id.tab_profile);

        tabHome.setOnClickListener(v -> {
            selectTab(TabType.HOME);
            setRandomMode(false);
            applyFilters();
        });
        tabHot.setOnClickListener(v -> {
            selectTab(TabType.HOT);
            setRandomMode(false);
            showComingSoon();
        });
        tabRandom.setOnClickListener(v -> {
            selectTab(TabType.RANDOM);
            setRandomMode(true);
            applyFilters();
        });
        tabProfile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(this, GiaoDienHoSoActivity.class);
            profileIntent.putExtra(EXTRA_USER_EMAIL, currentUserEmail);
            profileIntent.putExtra(EXTRA_USER_NAME, currentUserName);
            startActivity(profileIntent);
        });

        fab.setOnClickListener(v -> {
            if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
                Toast.makeText(this, R.string.create_recipe_no_user, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent createIntent = new Intent(this, DangBaiActivity.class);
            createIntent.putExtra(DangBaiActivity.EXTRA_USER_EMAIL, currentUserEmail);
            startActivityForResult(createIntent, REQUEST_CREATE_RECIPE);
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

        if (searchInput != null) {
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
        }

        if (randomIngredientInput != null) {
            randomIngredientInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentIngredient = s != null ? s.toString() : "";
                    applyFilters();
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });
        }

        if (randomMoodChipGroup != null) {
            randomMoodChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    currentMood = MoodFilter.NONE;
                } else {
                    currentMood = mapMoodChipToFilter(checkedIds.get(0));
                }
                applyFilters();
            });
        }

        if (randomClearButton != null) {
            randomClearButton.setOnClickListener(v -> {
                currentIngredient = "";
                currentMood = MoodFilter.NONE;
                if (randomIngredientInput != null) {
                    randomIngredientInput.setText("");
                }
                if (randomMoodChipGroup != null) {
                    randomMoodChipGroup.clearCheck();
                }
                applyFilters();
            });
        }

        reloadRecipes();
        selectTab(TabType.HOME);
        setRandomMode(false);
        applyFilters();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_RECIPE && resultCode == RESULT_OK) {
            String recipeName = data != null ? data.getStringExtra("extra_pending_recipe_name") : null;
            if (recipeName == null || recipeName.trim().isEmpty()) {
                recipeName = getString(R.string.pending_status_pending);
            }
            View container = findViewById(R.id.home_pending_status_container);
            if (container != null) {
                container.setVisibility(View.VISIBLE);
                TextView messageView = container.findViewById(R.id.home_pending_status_message);
                if (messageView != null) {
                    messageView.setText(getString(R.string.pending_user_notice, recipeName));
                }
            } else {
                Toast.makeText(this, getString(R.string.pending_user_notice, recipeName), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreSession(getIntent());
        updateDrawerHeader();
        reloadRecipes();
        if (currentTab == TabType.RANDOM) {
            setRandomMode(true);
        } else {
            selectTab(TabType.HOME);
            setRandomMode(false);
        }
        applyFilters();
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
        if (currentTab == TabType.RANDOM) {
            String normalizedIngredient = normalizeText(currentIngredient);
            boolean hasIngredientFilter = !normalizedIngredient.isEmpty();
            boolean hasMoodFilter = currentMood != MoodFilter.NONE;

            if (!hasIngredientFilter && !hasMoodFilter) {
                List<Recipe> shuffled = new ArrayList<>(allRecipes);
                Collections.shuffle(shuffled, randomGenerator);
                int limit = Math.min(6, shuffled.size());
                filtered.addAll(shuffled.subList(0, limit));
            } else {
                for (Recipe recipe : allRecipes) {
                    if (matchesIngredient(recipe, normalizedIngredient) && matchesMood(recipe)) {
                        filtered.add(recipe);
                    }
                }
            }
            updateRandomResultState(hasIngredientFilter || hasMoodFilter, filtered.isEmpty());
        } else {
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
            updateRandomResultState(false, false);
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

    private enum MoodFilter {
        NONE, HAPPY, RELAX, HEALTHY, WARM
    }

    private void selectTab(TabType tabType) {
        currentTab = tabType;
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

    private void setRandomMode(boolean enabled) {
        if (searchLayout != null) {
            searchLayout.setVisibility(enabled ? View.GONE : View.VISIBLE);
        }
        if (categoryContainer != null) {
            categoryContainer.setVisibility(enabled ? View.GONE : View.VISIBLE);
        }
        if (randomFilterCard != null) {
            randomFilterCard.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
        if (!enabled) {
            if (randomResultLabel != null) {
                randomResultLabel.setVisibility(View.GONE);
            }
            if (randomEmptyView != null) {
                randomEmptyView.setVisibility(View.GONE);
            }
            currentIngredient = "";
            currentMood = MoodFilter.NONE;
            if (randomIngredientInput != null) {
                randomIngredientInput.setText("");
            }
            if (randomMoodChipGroup != null) {
                randomMoodChipGroup.clearCheck();
            }
        }
    }

    private MoodFilter mapMoodChipToFilter(int chipId) {
        if (chipId == R.id.random_chip_happy) {
            return MoodFilter.HAPPY;
        } else if (chipId == R.id.random_chip_relax) {
            return MoodFilter.RELAX;
        } else if (chipId == R.id.random_chip_healthy) {
            return MoodFilter.HEALTHY;
        } else if (chipId == R.id.random_chip_warm) {
            return MoodFilter.WARM;
        }
        return MoodFilter.NONE;
    }

    private boolean matchesIngredient(Recipe recipe, String normalizedIngredient) {
        if (normalizedIngredient == null || normalizedIngredient.isEmpty()) {
            return true;
        }
        String searchable = getSearchableText(recipe);
        String[] tokens = normalizedIngredient.split("[,;\\s]+");
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            if (searchable.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesMood(Recipe recipe) {
        switch (currentMood) {
            case HAPPY:
                return recipe.getCategory() == RecipeCategory.DESSERT
                        || recipe.getCategory() == RecipeCategory.DRINK;
            case RELAX:
                return recipe.getCategory() == RecipeCategory.DRINK
                        || recipe.getCategory() == RecipeCategory.LOW_CAL;
            case HEALTHY:
                return recipe.getCategory() == RecipeCategory.HEALTHY
                        || recipe.getCategory() == RecipeCategory.LOW_CAL;
            case WARM:
                return recipe.getCategory() == RecipeCategory.TRADITIONAL
                        || recipe.getCategory() == RecipeCategory.QUICK;
            case NONE:
            default:
                return true;
        }
    }

    private void updateRandomResultState(boolean hasActiveFilters, boolean isEmpty) {
        if (randomResultLabel != null) {
            if (currentTab == TabType.RANDOM) {
                randomResultLabel.setVisibility(View.VISIBLE);
                randomResultLabel.setText(getString(hasActiveFilters
                        ? R.string.random_result_title_filter
                        : R.string.random_result_title_default));
            } else {
                randomResultLabel.setVisibility(View.GONE);
            }
        }
        if (randomEmptyView != null) {
            if (currentTab == TabType.RANDOM && isEmpty) {
                randomEmptyView.setVisibility(View.VISIBLE);
                randomEmptyView.setText(R.string.random_result_empty);
            } else {
                randomEmptyView.setVisibility(View.GONE);
            }
        }
    }

    private String getSearchableText(Recipe recipe) {
        String cached = recipeSearchIndex.get(recipe.getId());
        if (cached != null) {
            return cached;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(recipe.getName()).append(' ');
        builder.append(recipe.getDescription()).append(' ');
        for (RecipeStep step : recipe.getSteps()) {
            builder.append(step.getTitle()).append(' ');
            builder.append(step.getDescription()).append(' ');
        }
        String normalized = normalizeText(builder.toString());
        recipeSearchIndex.put(recipe.getId(), normalized);
        return normalized;
    }

    private String normalizeText(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.getDefault()).trim();
    }

    private void reloadRecipes() {
        List<Recipe> latest = RecipeRepository.getInstance().getRecipes();
        allRecipes.clear();
        allRecipes.addAll(latest);
        recipeSearchIndex.clear();
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
