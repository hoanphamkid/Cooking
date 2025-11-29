package fpoly.ph62768.cooking.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatMessageDto {

    @SerializedName("_id")
    private String id;

    private List<String> participants;
    private String senderEmail;
    private String content;
    private List<String> readBy;
    private String createdAt;

    public String getId() {
        return id;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getContent() {
        return content;
    }

    public List<String> getReadBy() {
        return readBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}


