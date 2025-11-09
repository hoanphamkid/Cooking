package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.ui.RecipeStepAdapter;

public class GiaoDienChiTietCongThucActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_chi_tiet_cong_thuc);

        String recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        if (recipeId == null) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecipeRepository repository = RecipeRepository.getInstance();
        Recipe recipe = repository.getRecipeById(recipeId);
        if (recipe == null) {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageButton backButton = findViewById(R.id.detail_back_button);
        ImageButton bookmarkButton = findViewById(R.id.detail_bookmark_button);
        ImageButton menuButton = findViewById(R.id.detail_menu_button);
        ImageView heroImage = findViewById(R.id.detail_hero_image);
        TextView titleText = findViewById(R.id.detail_title);
        TextView descriptionText = findViewById(R.id.detail_description);
        TextView durationText = findViewById(R.id.detail_duration);
        TextView ratingValueText = findViewById(R.id.detail_rating_text);
        RatingBar ratingBar = findViewById(R.id.detail_rating_bar);
        RecyclerView stepRecyclerView = findViewById(R.id.detail_steps_recycler);

        backButton.setOnClickListener(v -> onBackPressed());
        bookmarkButton.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng lưu công thức sẽ sớm có mặt!", Toast.LENGTH_SHORT).show()
        );
        menuButton.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng chia sẻ sẽ sớm có mặt!", Toast.LENGTH_SHORT).show()
        );

        Glide.with(this)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.ic_burger)
                .centerCrop()
                .into(heroImage);

        titleText.setText(recipe.getName());
        descriptionText.setText(recipe.getDescription());
        durationText.setText(recipe.getDuration());
        ratingValueText.setText(String.format("%.1f", recipe.getRating()));
        ratingBar.setRating((float) (recipe.getRating() / 5f));

        RecipeStepAdapter adapter = new RecipeStepAdapter();
        adapter.submitList(recipe.getSteps());
        stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stepRecyclerView.setAdapter(adapter);
    }
}

