package fpoly.ph62768.cooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.model.RecipeFeedback;

public class RecipeFeedbackAdapter extends RecyclerView.Adapter<RecipeFeedbackAdapter.FeedbackViewHolder> {

    private final List<RecipeFeedback> feedbackList = new ArrayList<>();

    public void submitList(List<RecipeFeedback> list) {
        feedbackList.clear();
        if (list != null) {
            feedbackList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        holder.bind(feedbackList.get(position));
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        private final TextView userName;
        private final TextView createdAt;
        private final TextView comment;
        private final RatingBar ratingBar;
        private final ImageView avatar;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.feedback_user_name);
            createdAt = itemView.findViewById(R.id.feedback_created_at);
            comment = itemView.findViewById(R.id.feedback_comment);
            ratingBar = itemView.findViewById(R.id.feedback_rating);
            avatar = itemView.findViewById(R.id.feedback_avatar);
        }

        void bind(RecipeFeedback feedback) {
            userName.setText(feedback.getUserName());
            createdAt.setText(feedback.getCreatedAt());
            comment.setText(feedback.getComment());
            ratingBar.setRating(feedback.getRating());
            avatar.setContentDescription(feedback.getUserName());
        }
    }
}

