package fpoly.ph62768.cooking.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatStore {

    public static class Message {
        public final String senderEmail;
        public final String content;
        public final long timestamp;

        public Message(@NonNull String senderEmail, @NonNull String content, long timestamp) {
            this.senderEmail = senderEmail;
            this.content = content;
            this.timestamp = timestamp;
        }
    }

    public static class ConversationSummary {
        public final String otherEmail;
        public final String lastMessage;
        public final long timestamp;
        public final boolean unread;

        public ConversationSummary(String otherEmail, String lastMessage, long timestamp, boolean unread) {
            this.otherEmail = otherEmail;
            this.lastMessage = lastMessage;
            this.timestamp = timestamp;
            this.unread = unread;
        }
    }

    private static final String PREF_NAME = "chat_store";
    private static final String KEY_PREFIX = "chat_";
    private static final String KEY_READ_PREFIX = "chat_read_";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<Message>>() { }.getType();

    public ChatStore(@NonNull Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    public List<Message> getMessages(@Nullable String userA, @Nullable String userB) {
        String key = buildKey(userA, userB);
        if (key == null) {
            return Collections.emptyList();
        }
        String raw = prefs.getString(key, null);
        if (raw == null || raw.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Message> messages = gson.fromJson(raw, listType);
        if (messages == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(messages);
    }

    public void addMessage(@Nullable String fromEmail,
                           @Nullable String toEmail,
                           @NonNull String content,
                           long timestamp) {
        List<Message> current = getMessages(fromEmail, toEmail);
        current.add(new Message(
                normalizeEmail(fromEmail),
                content,
                timestamp
        ));
        saveMessages(fromEmail, toEmail, current);
    }

    public void saveMessages(@Nullable String userA,
                             @Nullable String userB,
                             @NonNull List<Message> messages) {
        String key = buildKey(userA, userB);
        if (key == null) {
            return;
        }
        List<Message> copy = new ArrayList<>(messages);
        prefs.edit().putString(key, gson.toJson(copy)).apply();
    }

    public List<ConversationSummary> getConversations(@Nullable String currentUserEmail) {
        String normalized = normalizeEmail(currentUserEmail);
        if (normalized.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, ?> all = prefs.getAll();
        List<ConversationSummary> result = new ArrayList<>();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String key = entry.getKey();
            if (key == null || !key.startsWith(KEY_PREFIX)) {
                continue;
            }
            Object value = entry.getValue();
            if (!(value instanceof String)) {
                continue;
            }
            String participants = key.substring(KEY_PREFIX.length());
            String[] parts = participants.split("_");
            if (parts.length != 2) {
                continue;
            }
            if (!normalized.equals(parts[0]) && !normalized.equals(parts[1])) {
                continue;
            }
            String otherEmail = normalized.equals(parts[0]) ? parts[1] : parts[0];
            List<Message> messages = gson.fromJson((String) value, listType);
            if (messages == null || messages.isEmpty()) {
                continue;
            }
            Message last = messages.get(messages.size() - 1);
            String lastContent = last != null ? last.content : "";
            long timestamp = last != null ? last.timestamp : 0L;
            boolean incoming = last != null
                    && !TextUtils.isEmpty(last.senderEmail)
                    && !normalized.equals(last.senderEmail);
            long lastRead = getLastReadTimestamp(normalized, otherEmail);
            boolean unread = incoming && timestamp > lastRead;
            result.add(new ConversationSummary(otherEmail, lastContent, timestamp, unread));
        }
        Collections.sort(result, (left, right) -> Long.compare(right.timestamp, left.timestamp));
        return result;
    }

    public void markConversationRead(@Nullable String currentUserEmail,
                                     @Nullable String otherEmail,
                                     long timestamp) {
        String key = buildReadKey(currentUserEmail, otherEmail);
        if (key == null) {
            return;
        }
        long current = prefs.getLong(key, 0L);
        if (timestamp > current) {
            prefs.edit().putLong(key, timestamp).apply();
        }
    }

    public int countUnreadConversations(@Nullable String currentUserEmail) {
        int count = 0;
        for (ConversationSummary summary : getConversations(currentUserEmail)) {
            if (summary.unread) {
                count++;
            }
        }
        return count;
    }

    private String buildKey(@Nullable String userA, @Nullable String userB) {
        String normalizedA = normalizeEmail(userA);
        String normalizedB = normalizeEmail(userB);
        if (normalizedA.isEmpty() || normalizedB.isEmpty()) {
            return null;
        }
        if (normalizedA.compareTo(normalizedB) > 0) {
            String temp = normalizedA;
            normalizedA = normalizedB;
            normalizedB = temp;
        }
        return KEY_PREFIX + normalizedA + "_" + normalizedB;
    }

    private String buildReadKey(@Nullable String currentUserEmail, @Nullable String otherEmail) {
        String normalizedCurrent = normalizeEmail(currentUserEmail);
        String normalizedOther = normalizeEmail(otherEmail);
        if (normalizedCurrent.isEmpty() || normalizedOther.isEmpty()) {
            return null;
        }
        return KEY_READ_PREFIX + normalizedCurrent + "_" + normalizedOther;
    }

    private long getLastReadTimestamp(String normalizedCurrent, String otherEmail) {
        String key = buildReadKey(normalizedCurrent, otherEmail);
        if (key == null) {
            return 0L;
        }
        return prefs.getLong(key, 0L);
    }

    @NonNull
    private String normalizeEmail(@Nullable String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.getDefault());
    }
}

