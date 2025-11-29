package fpoly.ph62768.cooking.data.remote;

import fpoly.ph62768.cooking.data.remote.dto.ChatMessageListResponse;
import fpoly.ph62768.cooking.data.remote.dto.ChatMessageResponse;
import fpoly.ph62768.cooking.data.remote.dto.SendMessageRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApiService {

    @POST("asm/messages")
    Call<ChatMessageResponse> sendMessage(@Body SendMessageRequest request);

    @GET("asm/messages/thread")
    Call<ChatMessageListResponse> getThread(
            @Query("userA") String userA,
            @Query("userB") String userB
    );

    @GET("asm/messages/inbox/{email}")
    Call<ChatMessageListResponse> getInbox(
            @Path("email") String email,
            @Query("limit") Integer limit
    );
}


