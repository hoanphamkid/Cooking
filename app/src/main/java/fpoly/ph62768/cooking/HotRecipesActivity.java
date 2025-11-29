package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fpoly.ph62768.cooking.data.RecipeMetricHelper;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.data.RecipeStatsStore;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.ui.HotRecipeAdapter;

public class HotRecipesActivity extends AppCompatActivity {

    private HotRecipeAdapter adapter;
    private TextView emptyView;
    private List<HotRecipeAdapter.HotItem> viewItems = new ArrayList<>();
    private List<HotRecipeAdapter.HotItem> favoriteItems = new ArrayList<>();
    private List<HotRecipeAdapter.HotItem> feedbackItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_recipes);

        ImageButton backButton = findViewById(R.id.hot_back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        emptyView = findViewById(R.id.hot_empty_view);
        RecyclerView recyclerView = findViewById(R.id.hot_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HotRecipeAdapter();
        adapter.setListener(recipe -> {
            Intent detailIntent = new Intent(this, GiaoDienChiTietCongThucActivity.class);
            detailIntent.putExtra(GiaoDienChiTietCongThucActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(detailIntent);
        });
        recyclerView.setAdapter(adapter);

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.hot_toggle_group);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            if (checkedId == R.id.hot_toggle_views) {
                showList(viewItems);
            } else if (checkedId == R.id.hot_toggle_favorites) {
                showList(favoriteItems);
            } else if (checkedId == R.id.hot_toggle_feedback) {
                showList(feedbackItems);
            }
        });

        buildDatasets();
        toggleGroup.check(R.id.hot_toggle_views);
    }

    private void showList(List<HotRecipeAdapter.HotItem> data) {
        adapter.submitList(data);
        emptyView.setVisibility(data == null || data.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void buildDatasets() {
        RecipeRepository repository = RecipeRepository.getInstance();
        RecipeStatsStore statsStore = new RecipeStatsStore(this);
        List<Recipe> recipes = repository.getRecipes();

        viewItems = buildItems(recipes, statsStore, RecipeMetricHelper.Metric.VIEWS);
        favoriteItems = buildItems(recipes, statsStore, RecipeMetricHelper.Metric.FAVORITES);
        feedbackItems = buildItems(recipes, statsStore, RecipeMetricHelper.Metric.FEEDBACKS);
    }

    private List<HotRecipeAdapter.HotItem> buildItems(List<Recipe> recipes,
                                                      RecipeStatsStore statsStore,
                                                      RecipeMetricHelper.Metric metric) {
        List<HotRecipeAdapter.HotItem> results = new ArrayList<>();
        for (Recipe recipe : recipes) {
            int total = RecipeMetricHelper.calculateTotalMetric(recipe, metric, statsStore);
            String label;
            switch (metric) {
                case FAVORITES:
                    label = getString(R.string.hot_metric_favorites, total);
                    break;
                case FEEDBACKS:
                    label = getString(R.string.hot_metric_feedback, total);
                    break;
                case VIEWS:
                default:
                    label = getString(R.string.hot_metric_views, total);
                    break;
            }
            results.add(new HotRecipeAdapter.HotItem(recipe, total, label));
        }
        results.sort(Comparator.comparingInt((HotRecipeAdapter.HotItem item) -> item.metricValue).reversed());
        if (results.size() > 10) {
            results = new ArrayList<>(results.subList(0, 10));
        }
        return results;
    }
}

