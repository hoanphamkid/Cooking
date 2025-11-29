package fpoly.ph62768.cooking.data.remote.dto;

public class ChatMessageResponse {

    private int status;
    private String messenger;
    private ChatMessageDto data;

    public int getStatus() {
        return status;
    }

    public String getMessenger() {
        return messenger;
    }

    public ChatMessageDto getData() {
        return data;
    }
}


