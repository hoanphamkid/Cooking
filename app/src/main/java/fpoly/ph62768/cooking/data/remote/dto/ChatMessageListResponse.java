package fpoly.ph62768.cooking.data.remote.dto;

import java.util.List;

public class ChatMessageListResponse {

    private int status;
    private String messenger;
    private List<ChatMessageDto> data;

    public int getStatus() {
        return status;
    }

    public String getMessenger() {
        return messenger;
    }

    public List<ChatMessageDto> getData() {
        return data;
    }
}


