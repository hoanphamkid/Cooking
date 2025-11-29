package fpoly.ph62768.cooking.data.remote;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import fpoly.ph62768.cooking.data.ChatStore;
import fpoly.ph62768.cooking.data.remote.dto.ChatMessageDto;

public final class ChatRemoteMapper {

    private ChatRemoteMapper() {
    }

    public static List<ChatStore.Message> toMessages(List<ChatMessageDto> dtos) {
        List<ChatStore.Message> messages = new ArrayList<>();
        if (dtos == null) {
            return messages;
        }
        for (ChatMessageDto dto : dtos) {
            if (dto == null) continue;
            long timestamp = parseTimestamp(dto.getCreatedAt());
            String sender = safeEmail(dto.getSenderEmail());
            String content = dto.getContent() == null ? "" : dto.getContent();
            messages.add(new ChatStore.Message(sender, content, timestamp));
        }
        messages.sort((left, right) -> Long.compare(left.timestamp, right.timestamp));
        return messages;
    }

    public static List<ChatStore.ConversationSummary> toConversationSummaries(
            List<ChatMessageDto> dtos,
            String currentUserEmail
    ) {
        List<ChatStore.ConversationSummary> summaries = new ArrayList<>();
        if (dtos == null || TextUtils.isEmpty(currentUserEmail)) {
            return summaries;
        }
        String normalizedCurrent = safeEmail(currentUserEmail);
        Set<String> seen = new HashSet<>();
        for (ChatMessageDto dto : dtos) {
            if (dto == null) continue;
            String other = resolveOtherParticipant(dto.getParticipants(), normalizedCurrent);
            if (TextUtils.isEmpty(other) || seen.contains(other)) {
                continue;
            }
            seen.add(other);
            long timestamp = parseTimestamp(dto.getCreatedAt());
            String content = dto.getContent() == null ? "" : dto.getContent();
            boolean unread = isUnread(dto, normalizedCurrent);
            summaries.add(new ChatStore.ConversationSummary(other, content, timestamp, unread));
        }
        summaries.sort((l, r) -> Long.compare(r.timestamp, l.timestamp));
        return summaries;
    }

    public static Map<String, List<ChatStore.Message>> mapMessagesByPartner(
            List<ChatMessageDto> dtos,
            String currentUserEmail
    ) {
        Map<String, List<ChatStore.Message>> result = new HashMap<>();
        if (dtos == null || TextUtils.isEmpty(currentUserEmail)) {
            return result;
        }
        String normalizedCurrent = safeEmail(currentUserEmail);
        for (ChatMessageDto dto : dtos) {
            if (dto == null) continue;
            String other = resolveOtherParticipant(dto.getParticipants(), normalizedCurrent);
            if (TextUtils.isEmpty(other)) {
                continue;
            }
            result.computeIfAbsent(other, key -> new ArrayList<>())
                    .add(new ChatStore.Message(
                            safeEmail(dto.getSenderEmail()),
                            dto.getContent() == null ? "" : dto.getContent(),
                            parseTimestamp(dto.getCreatedAt())
                    ));
        }
        for (List<ChatStore.Message> messages : result.values()) {
            messages.sort((l, r) -> Long.compare(l.timestamp, r.timestamp));
        }
        return result;
    }

    private static boolean isUnread(ChatMessageDto dto, String currentUserEmail) {
        if (dto == null || TextUtils.isEmpty(currentUserEmail)) {
            return false;
        }
        List<String> readBy = dto.getReadBy();
        if (readBy == null || readBy.isEmpty()) {
            return true;
        }
        String normalized = currentUserEmail.trim().toLowerCase(Locale.getDefault());
        for (String user : readBy) {
            if (normalized.equals(user != null ? user.trim().toLowerCase(Locale.getDefault()) : "")) {
                return false;
            }
        }
        return true;
    }

    private static String resolveOtherParticipant(List<String> participants, String currentUserEmail) {
        if (participants == null || participants.size() != 2) {
            return "";
        }
        String a = safeEmail(participants.get(0));
        String b = safeEmail(participants.get(1));
        if (currentUserEmail.equals(a)) {
            return b;
        }
        if (currentUserEmail.equals(b)) {
            return a;
        }
        return "";
    }

    private static final ThreadLocal<SimpleDateFormat> ISO_FORMAT =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                format.setLenient(true);
                return format;
            });

    private static long parseTimestamp(String value) {
        if (TextUtils.isEmpty(value)) {
            return System.currentTimeMillis();
        }
        try {
            return ISO_FORMAT.get().parse(value).getTime();
        } catch (ParseException ignore) {
            return System.currentTimeMillis();
        }
    }

    private static String safeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.getDefault());
    }
}


