package fpoly.ph62768.cooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.model.Recipe;

public class HotRecipeAdapter extends RecyclerView.Adapter<HotRecipeAdapter.HotViewHolder> {

    public interface Listener {
        void onRecipeSelected(@NonNull Recipe recipe);
    }

    public static class HotItem {
        public final Recipe recipe;
        public final int metricValue;
        public final String metricLabel;

        public HotItem(Recipe recipe, int metricValue, String metricLabel) {
            this.recipe = recipe;
            this.metricValue = metricValue;
            this.metricLabel = metricLabel;
        }
    }

    private final List<HotItem> items = new ArrayList<>();
    private Listener listener;

    public void submitList(List<HotItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_recipe, parent, false);
        return new HotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotViewHolder holder, int position) {
        holder.bind(items.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HotViewHolder extends RecyclerView.ViewHolder {

        private final TextView rankView;
        private final ImageView imageView;
        private final TextView titleView;
        private final TextView metricView;

        HotViewHolder(@NonNull View itemView) {
            super(itemView);
            rankView = itemView.findViewById(R.id.hot_item_rank);
            imageView = itemView.findViewById(R.id.hot_item_image);
            titleView = itemView.findViewById(R.id.hot_item_title);
            metricView = itemView.findViewById(R.id.hot_item_metric);
        }

        void bind(HotItem item, int adapterPosition, Listener listener) {
            rankView.setText(String.valueOf(adapterPosition + 1));
            titleView.setText(item.recipe.getName());
            metricView.setText(item.metricLabel);
            Glide.with(imageView.getContext())
                    .load(item.recipe.getImageUrl())
                    .placeholder(R.drawable.ic_burger)
                    .centerCrop()
                    .into(imageView);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeSelected(item.recipe);
                }
            });
        }
    }
}

