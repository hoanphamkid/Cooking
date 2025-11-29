package fpoly.ph62768.cooking.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class PendingRecipeDecisionRequest {

    @SerializedName("action")
    private final String action;
    @SerializedName("rejectionReason")
    private final String rejectionReason;

    public PendingRecipeDecisionRequest(String action, String rejectionReason) {
        this.action = action;
        this.rejectionReason = rejectionReason;
    }

    public static PendingRecipeDecisionRequest approve() {
        return new PendingRecipeDecisionRequest("APPROVE", null);
    }

    public static PendingRecipeDecisionRequest reject(String reason) {
        return new PendingRecipeDecisionRequest("REJECT", reason);
    }
}

