package fpoly.ph62768.cooking.ui;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.data.ChatStore;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {

    private final List<ChatStore.Message> messages = new ArrayList<>();
    private final String currentUserEmail;
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatMessageAdapter(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail == null ? "" : currentUserEmail.trim().toLowerCase(Locale.getDefault());
    }

    public void submitList(List<ChatStore.Message> newMessages) {
        messages.clear();
        if (newMessages != null) {
            messages.addAll(newMessages);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position), currentUserEmail, timeFormatter);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout container;
        private final TextView messageText;
        private final TextView timeText;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.chat_message_container);
            messageText = itemView.findViewById(R.id.chat_message_text);
            timeText = itemView.findViewById(R.id.chat_message_time);
        }

        void bind(ChatStore.Message message, String currentUserEmail, SimpleDateFormat formatter) {
            boolean isMine = !TextUtils.isEmpty(currentUserEmail)
                    && currentUserEmail.equals(message.senderEmail);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
            params.gravity = isMine ? Gravity.END : Gravity.START;
            container.setLayoutParams(params);
            container.setBackgroundResource(isMine ? R.drawable.bg_chat_self : R.drawable.bg_chat_other);

            messageText.setText(message.content);
            timeText.setText(formatter.format(new Date(message.timestamp)));

            if (isMine) {
                messageText.setTextColor(Color.WHITE);
                timeText.setTextColor(0xFFECD8C9);
            } else {
                messageText.setTextColor(Color.BLACK);
                timeText.setTextColor(0xFF9F9F9F);
            }
        }
    }
}

