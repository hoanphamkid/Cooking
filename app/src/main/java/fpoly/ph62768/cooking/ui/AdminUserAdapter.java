package fpoly.ph62768.cooking.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.R;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    public interface Listener {
        void onPostsClick(@NonNull String email);

        void onLockClick(@NonNull String email);

        void onDeleteClick(@Nullable String userId, @NonNull String email);
    }

    public static class Item {
        @Nullable
        public final String userId;
        public final String normalizedEmail;
        public final String displayName;
        public final String displayEmail;
        public final String meta;

        public Item(@Nullable String userId,
                    String normalizedEmail,
                    String displayName,
                    String displayEmail,
                    String meta) {
            this.userId = userId;
            this.normalizedEmail = normalizedEmail;
            this.displayName = displayName;
            this.displayEmail = displayEmail;
            this.meta = meta;
        }
    }

    private final List<Item> items = new ArrayList<>();
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Item> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView emailView;
        private final TextView metaView;
        private final View postsButton;
        private final View lockButton;
        private final View deleteButton;
        private final ImageView avatarView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.admin_user_name);
            emailView = itemView.findViewById(R.id.admin_user_email);
            metaView = itemView.findViewById(R.id.admin_user_meta);
            postsButton = itemView.findViewById(R.id.admin_user_posts_button);
            lockButton = itemView.findViewById(R.id.admin_user_lock_button);
            deleteButton = itemView.findViewById(R.id.admin_user_delete_button);
            avatarView = itemView.findViewById(R.id.admin_user_avatar);
        }

        void bind(Item item) {
            final String displayName = !TextUtils.isEmpty(item.displayName)
                    ? item.displayName
                    : itemView.getContext().getString(R.string.admin_unknown_user);
            final String displayEmail = !TextUtils.isEmpty(item.displayEmail)
                    ? item.displayEmail
                    : itemView.getContext().getString(R.string.admin_unknown_email);
            final String metaText = !TextUtils.isEmpty(item.meta)
                    ? item.meta
                    : itemView.getContext().getString(R.string.admin_user_meta, "--", 0);

            nameView.setText(displayName);
            emailView.setText(displayEmail);
            metaView.setText(metaText);
            avatarView.setImageResource(R.drawable.ic_profile_placeholder);

            postsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPostsClick(item.normalizedEmail);
                }
            });

            lockButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLockClick(item.normalizedEmail);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item.userId, displayEmail);
                }
            });
        }
    }
}

