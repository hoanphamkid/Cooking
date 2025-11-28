package fpoly.ph62768.cooking.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.ChatStore;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    public interface Listener {
        void onConversationClick(@NonNull ChatStore.ConversationSummary summary);
    }

    private final List<ChatStore.ConversationSummary> items = new ArrayList<>();
    private final UserAccountManager accountManager;
    private final String currentUserEmail;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Listener listener;

    public ConversationAdapter(@NonNull UserAccountManager accountManager, String currentUserEmail) {
        this.accountManager = accountManager;
        this.currentUserEmail = currentUserEmail == null ? "" : currentUserEmail.trim().toLowerCase(Locale.getDefault());
    }

    public void submitList(List<ChatStore.ConversationSummary> conversations) {
        items.clear();
        if (conversations != null) {
            items.addAll(conversations);
        }
        notifyDataSetChanged();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.bind(items.get(position), accountManager, currentUserEmail, timeFormat, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatarView;
        private final TextView nameView;
        private final TextView previewView;
        private final TextView timeView;
        private final View unreadBadge;

        ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.conversation_avatar);
            nameView = itemView.findViewById(R.id.conversation_name);
            previewView = itemView.findViewById(R.id.conversation_preview);
            timeView = itemView.findViewById(R.id.conversation_time);
            unreadBadge = itemView.findViewById(R.id.conversation_unread_badge);
        }

        void bind(ChatStore.ConversationSummary summary,
                  UserAccountManager accountManager,
                  String currentUserEmail,
                  SimpleDateFormat formatter,
                  Listener listener) {
            String displayName = summary.otherEmail;
            if (accountManager != null && !TextUtils.isEmpty(summary.otherEmail)) {
                UserAccount account = accountManager.getAccount(summary.otherEmail);
                if (account != null && !TextUtils.isEmpty(account.getName())) {
                    displayName = account.getName();
                }
            }
            nameView.setText(displayName);
            previewView.setText(summary.lastMessage);
            timeView.setText(formatter.format(new Date(summary.timestamp)));
            unreadBadge.setVisibility(summary.unread ? View.VISIBLE : View.GONE);
            Glide.with(avatarView.getContext())
                    .load(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(avatarView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversationClick(summary);
                }
            });
        }
    }
}

