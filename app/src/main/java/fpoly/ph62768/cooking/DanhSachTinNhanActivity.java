package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import fpoly.ph62768.cooking.auth.SessionManager;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.ChatStore;
import fpoly.ph62768.cooking.data.remote.ChatApiService;
import fpoly.ph62768.cooking.data.remote.ChatRemoteMapper;
import fpoly.ph62768.cooking.data.remote.dto.ChatMessageListResponse;
import fpoly.ph62768.cooking.network.ApiClient;
import fpoly.ph62768.cooking.ui.ConversationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhSachTinNhanActivity extends AppCompatActivity {

    private ConversationAdapter adapter;
    private ChatStore chatStore;
    private ChatApiService chatApiService;
    private UserAccountManager accountManager;
    private String currentUserEmail = "";
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_tin_nhan);

        chatStore = new ChatStore(this);
        chatApiService = ApiClient.getInstance().create(ChatApiService.class);
        accountManager = new UserAccountManager(this);
        currentUserEmail = accountManager.getCurrentUserEmail(this);
        if (TextUtils.isEmpty(currentUserEmail)) {
            SessionManager sessionManager = new SessionManager(this);
            currentUserEmail = sessionManager.getEmail();
            if (!TextUtils.isEmpty(currentUserEmail)) {
                accountManager.setCurrentUser(this, currentUserEmail);
            }
        }
        if (currentUserEmail == null) {
            currentUserEmail = "";
        }

        ImageButton backButton = findViewById(R.id.message_list_back_button);
        backButton.setOnClickListener(v -> onBackPressed());
        emptyView = findViewById(R.id.message_list_empty);

        RecyclerView recyclerView = findViewById(R.id.message_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConversationAdapter(accountManager, currentUserEmail);
        adapter.setListener(summary -> {
            Intent intent = new Intent(this, TinNhanActivity.class);
            intent.putExtra(TinNhanActivity.EXTRA_TARGET_EMAIL, summary.otherEmail);
            intent.putExtra(TinNhanActivity.EXTRA_TARGET_NAME, resolveDisplayName(summary.otherEmail));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadConversations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        if (!TextUtils.isEmpty(currentUserEmail)) {
            loadRemoteConversations();
        } else {
            updateConversationList(chatStore.getConversations(currentUserEmail));
        }
    }

    private void loadRemoteConversations() {
        chatApiService.getInbox(currentUserEmail, 100).enqueue(new Callback<ChatMessageListResponse>() {
            @Override
            public void onResponse(Call<ChatMessageListResponse> call, Response<ChatMessageListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ChatStore.ConversationSummary> summaries =
                            ChatRemoteMapper.toConversationSummaries(response.body().getData(), currentUserEmail);
                    Map<String, List<ChatStore.Message>> grouped =
                            ChatRemoteMapper.mapMessagesByPartner(response.body().getData(), currentUserEmail);
                    for (Map.Entry<String, List<ChatStore.Message>> entry : grouped.entrySet()) {
                        chatStore.saveMessages(currentUserEmail, entry.getKey(), entry.getValue());
                    }
                    updateConversationList(summaries);
                } else {
                    updateConversationList(chatStore.getConversations(currentUserEmail));
                }
            }

            @Override
            public void onFailure(Call<ChatMessageListResponse> call, Throwable t) {
                updateConversationList(chatStore.getConversations(currentUserEmail));
            }
        });
    }

    private void updateConversationList(List<ChatStore.ConversationSummary> conversations) {
        adapter.submitList(conversations);
        if (emptyView != null) {
            emptyView.setVisibility(conversations.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private String resolveDisplayName(String email) {
        if (TextUtils.isEmpty(email)) {
            return "";
        }
        if (accountManager == null) {
            accountManager = new UserAccountManager(this);
        }
        if (accountManager != null) {
            UserAccount account = accountManager.getAccount(email);
            if (account != null && !TextUtils.isEmpty(account.getName())) {
                return account.getName();
            }
        }
        return email;
    }
}
