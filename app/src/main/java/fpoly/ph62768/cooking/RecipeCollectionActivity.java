package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.data.RecipePreferenceStore;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.ui.RecipeAdapter;

public class RecipeCollectionActivity extends AppCompatActivity {

    public static final String EXTRA_COLLECTION_TYPE = "extra_collection_type";

    public enum CollectionType {
        SAVED,
        FAVORITE,
        HISTORY
    }

    private RecipeAdapter adapter;
    private RecipePreferenceStore preferenceStore;
    private TextView emptyView;
    private TextView titleView;
    private CollectionType collectionType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_collection);

        preferenceStore = new RecipePreferenceStore(this);

        ImageButton backButton = findViewById(R.id.collection_back_button);
        titleView = findViewById(R.id.collection_title);
        emptyView = findViewById(R.id.collection_empty_view);
        RecyclerView recyclerView = findViewById(R.id.collection_recycler_view);

        backButton.setOnClickListener(v -> onBackPressed());

        adapter = new RecipeAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        adapter.setOnRecipeClickListener(recipe -> {
            Intent intent = new Intent(this, GiaoDienChiTietCongThucActivity.class);
            intent.putExtra(GiaoDienChiTietCongThucActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });

        collectionType = parseCollectionType();
        if (collectionType == null) {
            Toast.makeText(this, "Không xác định được danh sách cần hiển thị", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String title = resolveTitle(collectionType);
        titleView.setText(title);

        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Nullable
    private CollectionType parseCollectionType() {
        String raw = getIntent().getStringExtra(EXTRA_COLLECTION_TYPE);
        if (raw == null) {
            return null;
        }
        try {
            return CollectionType.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private List<Recipe> loadRecipes(CollectionType type) {
        RecipeRepository repository = RecipeRepository.getInstance();
        List<String> recipeIds;
        switch (type) {
            case SAVED:
                recipeIds = preferenceStore.getSavedRecipeIds();
                break;
            case FAVORITE:
                recipeIds = preferenceStore.getFavoriteRecipeIds();
                break;
            case HISTORY:
                recipeIds = preferenceStore.getHistoryRecipeIds();
                break;
            default:
                recipeIds = new ArrayList<>();
        }
        List<Recipe> result = new ArrayList<>();
        for (String id : recipeIds) {
            Recipe recipe = repository.getRecipeById(id);
            if (recipe != null) {
                result.add(recipe);
            }
        }
        return result;
    }

    private String resolveTitle(CollectionType type) {
        switch (type) {
            case SAVED:
                return getString(R.string.collection_saved_title);
            case FAVORITE:
                return getString(R.string.collection_favorite_title);
            case HISTORY:
                return getString(R.string.collection_history_title);
            default:
                return getString(R.string.app_name);
        }
    }

    private void refreshData() {
        if (collectionType == null) {
            return;
        }
        List<Recipe> recipes = loadRecipes(collectionType);
        adapter.submitList(recipes);
        emptyView.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.GONE);
        emptyView.setText(resolveEmptyMessage(collectionType));
        titleView.setText(resolveTitle(collectionType));
    }

    private String resolveEmptyMessage(CollectionType type) {
        switch (type) {
            case SAVED:
                return getString(R.string.collection_empty_saved);
            case FAVORITE:
                return getString(R.string.collection_empty_favorite);
            case HISTORY:
                return getString(R.string.collection_empty_history);
            default:
                return "";
        }
    }
}

