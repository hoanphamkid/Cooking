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
import fpoly.ph62768.cooking.model.RecipeStep;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.StepViewHolder> {

    private final List<RecipeStep> steps = new ArrayList<>();

    public void submitList(List<RecipeStep> newSteps) {
        steps.clear();
        if (newSteps != null) {
            steps.addAll(newSteps);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(position + 1, steps.get(position));
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {

        private final TextView stepNumber;
        private final TextView stepTitle;
        private final TextView stepDescription;
        private final ImageView stepImage;

        StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.step_number);
            stepTitle = itemView.findViewById(R.id.step_title);
            stepDescription = itemView.findViewById(R.id.step_description);
            stepImage = itemView.findViewById(R.id.step_image);
        }

        void bind(int index, RecipeStep step) {
            stepNumber.setText(String.valueOf(index));
            stepTitle.setText(step.getTitle());
            stepDescription.setText(step.getDescription());
            Glide.with(stepImage.getContext())
                    .load(step.getImageUrl())
                    .placeholder(R.drawable.ic_burger)
                    .centerCrop()
                    .into(stepImage);
        }
    }
}

