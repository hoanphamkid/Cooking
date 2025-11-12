package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.ui.RecipeAdapter;

public class QuanLyCongThucActivity extends AppCompatActivity {

    private RecipeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_cong_thuc);

        ImageButton backButton = findViewById(R.id.admin_recipe_back_button);
        backButton.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.admin_recipe_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new RecipeAdapter();
        adapter.setOnRecipeClickListener(recipe -> {
            android.content.Intent intent = new android.content.Intent(this, GiaoDienChiTietCongThucActivity.class);
            intent.putExtra(GiaoDienChiTietCongThucActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadRecipes();
    }

    private void loadRecipes() {
        List<Recipe> recipes = RecipeRepository.getInstance().getRecipes();
        adapter.submitList(recipes);
    }
}

