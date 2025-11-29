package fpoly.ph62768.cooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.model.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> items = new ArrayList<>();
    private OnRecipeClickListener onRecipeClickListener;

    public void submitList(List<Recipe> recipes) {
        items.clear();
        if (recipes != null) {
            items.addAll(recipes);
        }
        notifyDataSetChanged();
    }

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.onRecipeClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(items.get(position), onRecipeClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView titleView;
        private final TextView durationView;
        private final TextView ratingText;
        private final RatingBar ratingBar;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recipe_image);
            titleView = itemView.findViewById(R.id.recipe_title);
            durationView = itemView.findViewById(R.id.recipe_duration);
            ratingText = itemView.findViewById(R.id.recipe_rating_text);
            ratingBar = itemView.findViewById(R.id.recipe_rating_bar);
        }

        void bind(Recipe recipe, OnRecipeClickListener listener) {
            titleView.setText(recipe.getName());
            durationView.setText(recipe.getDuration());
            ratingText.setText(String.format("%.1f", recipe.getRating()));
            ratingBar.setRating((float) (recipe.getRating() / 5f));
            Glide.with(imageView.getContext())
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.ic_burger)
                    .centerCrop()
                    .into(imageView);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }
}

