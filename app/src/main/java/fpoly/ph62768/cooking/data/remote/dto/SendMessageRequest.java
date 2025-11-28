package fpoly.ph62768.cooking.data.remote.dto;

public class SendMessageRequest {

    private final String senderEmail;
    private final String receiverEmail;
    private final String content;

    public SendMessageRequest(String senderEmail, String receiverEmail, String content) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.content = content;
    }
}


