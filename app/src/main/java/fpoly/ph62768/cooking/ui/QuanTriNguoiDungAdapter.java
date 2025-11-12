package fpoly.ph62768.cooking.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.auth.UserAccount;

public class QuanTriNguoiDungAdapter extends RecyclerView.Adapter<QuanTriNguoiDungAdapter.ViewHolder> {

    public interface Listener {
        void onPostsClick(String email);
        void onLockClick(String email);
        void onDeleteClick(String email);
    }

    private final List<Item> items = new ArrayList<>();
    private Listener listener;

    public static class Item {
        public final String email;
        public final String displayName;
        public final String displayEmail;
        public final String meta;

        public Item(String email, String displayName, String displayEmail, String meta) {
            this.email = email;
            this.displayName = displayName;
            this.displayEmail = displayEmail;
            this.meta = meta;
        }
    }

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
                .inflate(R.layout.item_quan_tri_user, parent, false);
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
        private final ImageView avatarView;
        private final TextView metaView;
        private final View postsButton;
        private final View lockButton;
        private final View deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.quan_tri_user_name);
            emailView = itemView.findViewById(R.id.quan_tri_user_email);
            avatarView = itemView.findViewById(R.id.quan_tri_user_avatar);
            metaView = itemView.findViewById(R.id.quan_tri_user_meta);
            postsButton = itemView.findViewById(R.id.quan_tri_user_posts_button);
            lockButton = itemView.findViewById(R.id.quan_tri_user_lock_button);
            deleteButton = itemView.findViewById(R.id.quan_tri_user_delete_button);
        }

        void bind(Item item) {
            String displayName = !TextUtils.isEmpty(item.displayName)
                    ? item.displayName
                    : itemView.getContext().getString(R.string.admin_unknown_user);
            String displayEmail = !TextUtils.isEmpty(item.displayEmail)
                    ? item.displayEmail
                    : itemView.getContext().getString(R.string.admin_unknown_email);

            nameView.setText(displayName);
            nameView.setVisibility(View.VISIBLE);
            emailView.setText(displayEmail);
            emailView.setVisibility(View.VISIBLE);
            avatarView.setImageResource(R.drawable.ic_profile_placeholder);
            String metaText = !TextUtils.isEmpty(item.meta)
                    ? item.meta
                    : itemView.getContext().getString(R.string.admin_user_meta_empty);
            metaView.setText(metaText);
            metaView.setVisibility(View.VISIBLE);

            postsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPostsClick(item.email);
                }
            });
            lockButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLockClick(item.email);
                }
            });
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item.email);
                }
            });
        }
    }
}

