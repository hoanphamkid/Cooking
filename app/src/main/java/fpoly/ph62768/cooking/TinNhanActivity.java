package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import fpoly.ph62768.cooking.auth.SessionManager;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.data.ChatStore;
import fpoly.ph62768.cooking.data.remote.ChatApiService;
import fpoly.ph62768.cooking.data.remote.ChatRemoteMapper;
import fpoly.ph62768.cooking.data.remote.dto.ChatMessageListResponse;
import fpoly.ph62768.cooking.data.remote.dto.ChatMessageResponse;
import fpoly.ph62768.cooking.data.remote.dto.SendMessageRequest;
import fpoly.ph62768.cooking.network.ApiClient;
import fpoly.ph62768.cooking.ui.ChatMessageAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TinNhanActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET_EMAIL = "extra_target_email";
    public static final String EXTRA_TARGET_NAME = "extra_target_name";

    private ChatStore chatStore;
    private ChatMessageAdapter adapter;
    private TextInputLayout inputLayout;
    private TextInputEditText inputEditText;
    private MaterialButton sendButton;
    private TextView emptyView;
    private RecyclerView recyclerView;

    private ChatApiService chatApiService;

    private String currentUserEmail = "";
    private String targetEmail = "";
    private String targetName = "";
    private boolean isSending = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        targetEmail = getIntent().getStringExtra(EXTRA_TARGET_EMAIL);
        targetName = getIntent().getStringExtra(EXTRA_TARGET_NAME);

        if (TextUtils.isEmpty(targetEmail)) {
            Toast.makeText(this, R.string.chat_missing_user, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UserAccountManager accountManager = new UserAccountManager(this);
        currentUserEmail = accountManager.getCurrentUserEmail(this);
        if (TextUtils.isEmpty(currentUserEmail)) {
            SessionManager sessionManager = new SessionManager(this);
            currentUserEmail = sessionManager.getEmail();
            if (!TextUtils.isEmpty(currentUserEmail)) {
                accountManager.setCurrentUser(this, currentUserEmail);
            }
        }
        if (TextUtils.isEmpty(targetName)) {
            UserAccount account = accountManager.getAccount(targetEmail);
            if (account != null && !TextUtils.isEmpty(account.getName())) {
                targetName = account.getName();
            } else {
                targetName = targetEmail;
            }
        }

        chatStore = new ChatStore(this);
        chatApiService = ApiClient.getInstance().create(ChatApiService.class);
        adapter = new ChatMessageAdapter(currentUserEmail);

        ImageButton backButton = findViewById(R.id.chat_back_button);
        TextView titleView = findViewById(R.id.chat_title);
        TextView subtitleView = findViewById(R.id.chat_subtitle);
        recyclerView = findViewById(R.id.chat_recycler);
        emptyView = findViewById(R.id.chat_empty_view);
        inputLayout = findViewById(R.id.chat_input_layout);
        inputEditText = findViewById(R.id.chat_input);
        sendButton = findViewById(R.id.chat_send_button);

        backButton.setOnClickListener(v -> onBackPressed());
        titleView.setText(getString(R.string.chat_title, targetName));
        subtitleView.setText(getString(R.string.chat_subtitle_with_email, targetEmail));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        boolean canSend = !TextUtils.isEmpty(currentUserEmail);
        sendButton.setEnabled(canSend);
        if (!canSend) {
            sendButton.setAlpha(0.5f);
            sendButton.setOnClickListener(v ->
                    Toast.makeText(this, R.string.chat_need_login, Toast.LENGTH_SHORT).show()
            );
        } else {
            sendButton.setAlpha(1f);
            sendButton.setOnClickListener(v -> sendMessage());
        }

        loadMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }

    private void sendMessage() {
        if (TextUtils.isEmpty(currentUserEmail)) {
            Toast.makeText(this, R.string.chat_need_login, Toast.LENGTH_SHORT).show();
            return;
        }
        String content = inputEditText.getText() != null
                ? inputEditText.getText().toString().trim()
                : "";
        if (TextUtils.isEmpty(content)) {
            inputLayout.setError(getString(R.string.chat_input_error));
            return;
        }
        inputLayout.setError(null);
        sendButton.setEnabled(false);
        isSending = true;
        SendMessageRequest request = new SendMessageRequest(currentUserEmail, targetEmail, content);
        chatApiService.sendMessage(request).enqueue(new Callback<ChatMessageResponse>() {
            @Override
            public void onResponse(Call<ChatMessageResponse> call, Response<ChatMessageResponse> response) {
                isSending = false;
                sendButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    inputEditText.setText("");
                    loadMessages();
                } else {
                    fallbackSend(content);
                }
            }

            @Override
            public void onFailure(Call<ChatMessageResponse> call, Throwable t) {
                isSending = false;
                sendButton.setEnabled(true);
                fallbackSend(content);
            }
        });
    }

    private void loadMessages() {
        if (TextUtils.isEmpty(currentUserEmail) || chatApiService == null) {
            updateMessagesFromLocal();
            return;
        }
        chatApiService.getThread(currentUserEmail, targetEmail).enqueue(new Callback<ChatMessageListResponse>() {
            @Override
            public void onResponse(Call<ChatMessageListResponse> call, Response<ChatMessageListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ChatStore.Message> messages = ChatRemoteMapper.toMessages(response.body().getData());
                    chatStore.saveMessages(currentUserEmail, targetEmail, messages);
                    updateMessages(messages);
                } else {
                    showChatSyncError();
                    updateMessagesFromLocal();
                }
            }

            @Override
            public void onFailure(Call<ChatMessageListResponse> call, Throwable t) {
                showChatSyncError();
                updateMessagesFromLocal();
            }
        });
    }

    private void updateMessagesFromLocal() {
        List<ChatStore.Message> messages = chatStore.getMessages(currentUserEmail, targetEmail);
        updateMessages(messages);
    }

    private void updateMessages(List<ChatStore.Message> messages) {
        adapter.submitList(messages);
        if (emptyView != null) {
            emptyView.setVisibility(messages.isEmpty() ? View.VISIBLE : View.GONE);
        }
        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
            ChatStore.Message last = messages.get(messages.size() - 1);
            if (last != null) {
                chatStore.markConversationRead(currentUserEmail, targetEmail, last.timestamp);
            }
        }
    }

    private void fallbackSend(String content) {
        chatStore.addMessage(currentUserEmail, targetEmail, content, System.currentTimeMillis());
        inputEditText.setText("");
        Toast.makeText(this, R.string.chat_sync_failed, Toast.LENGTH_SHORT).show();
        loadMessages();
    }

    private void showChatSyncError() {
        Toast.makeText(this, R.string.chat_sync_failed, Toast.LENGTH_SHORT).show();
    }
}

