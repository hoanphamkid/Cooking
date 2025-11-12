package fpoly.ph62768.cooking;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;
import fpoly.ph62768.cooking.ui.QuanTriNguoiDungAdapter;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.data.BaiChoDuyetStore.BanGhi;

public class QuanLyNguoiDungActivity extends AppCompatActivity {

    private QuanTriNguoiDungAdapter adapter;
    private UserAccountManager accountManager;
    private BaiChoDuyetStore baiChoDuyetStore;
    private TextView emptyView;
    private TextView summaryView;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_nguoi_dung);

        accountManager = new UserAccountManager(this);
        baiChoDuyetStore = new BaiChoDuyetStore(this);

        ImageButton backButton = findViewById(R.id.admin_user_back_button);
        backButton.setOnClickListener(v -> finish());

        findViewById(R.id.admin_user_refresh).setOnClickListener(v -> reloadData());

        RecyclerView recyclerView = findViewById(R.id.admin_user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyView = findViewById(R.id.admin_user_empty_view);
        summaryView = findViewById(R.id.admin_user_summary);

        adapter = new QuanTriNguoiDungAdapter();
        adapter.setListener(new QuanTriNguoiDungAdapter.Listener() {
            @Override
            public void onPostsClick(String email) {
                Intent intent = new Intent(QuanLyNguoiDungActivity.this, DanhSachChoDuyetActivity.class);
                intent.putExtra(DanhSachChoDuyetActivity.EXTRA_FILTER_EMAIL, email);
                startActivity(intent);
            }

            @Override
            public void onLockClick(String email) {
                Toast.makeText(QuanLyNguoiDungActivity.this, R.string.admin_toast_working, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(String email) {
                new androidx.appcompat.app.AlertDialog.Builder(QuanLyNguoiDungActivity.this)
                        .setTitle(R.string.admin_user_delete)
                        .setMessage(getString(R.string.admin_confirm_delete_user, email))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            accountManager.removeAccount(email);
                            reloadData();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        reloadData();
    }

    private void reloadData() {
        Map<String, UserAccount> allAccounts = accountManager.getAllAccounts();
        List<QuanTriNguoiDungAdapter.Item> items = new ArrayList<>();
        String adminEmail = getString(R.string.admin_default_email).trim().toLowerCase(Locale.getDefault());
        Map<String, Integer> postCounts = new HashMap<>();
        for (BanGhi record : baiChoDuyetStore.layTatCaBanGhi()) {
            String email = record.email;
            if (TextUtils.isEmpty(email)) {
                continue;
            }
            String normalized = email.trim().toLowerCase(Locale.getDefault());
            postCounts.put(normalized, postCounts.getOrDefault(normalized, 0) + 1);
        }
        List<Map.Entry<String, UserAccount>> entries = new ArrayList<>(allAccounts.entrySet());
        Collections.sort(entries, (e1, e2) -> {
            long t1 = e1.getValue() != null ? e1.getValue().getCreatedAt() : 0L;
            long t2 = e2.getValue() != null ? e2.getValue().getCreatedAt() : 0L;
            return Long.compare(t2, t1);
        });
        for (Map.Entry<String, UserAccount> entry : entries) {
            String email = entry.getKey();
            if (TextUtils.isEmpty(email) || email.equals(adminEmail)) {
                continue;
            }
            UserAccount account = entry.getValue();
            String normalizedEmail = email.trim().toLowerCase(Locale.getDefault());
            String displayEmail = !TextUtils.isEmpty(email)
                    ? email
                    : getString(R.string.admin_unknown_email);
            String displayName = (account != null && !TextUtils.isEmpty(account.getName()))
                    ? account.getName()
                    : displayEmail;
            if (TextUtils.isEmpty(displayName)) {
                displayName = getString(R.string.admin_unknown_user);
            }
            String dateText;
            if (account != null && account.getCreatedAt() > 0) {
                dateText = dateFormat.format(new Date(account.getCreatedAt()));
            } else {
                dateText = getString(R.string.admin_unknown_date);
            }
            int totalPosts = postCounts.getOrDefault(normalizedEmail, 0);
            String meta = getString(R.string.admin_user_meta, dateText, totalPosts);
            items.add(new QuanTriNguoiDungAdapter.Item(
                    normalizedEmail,
                    displayName,
                    displayEmail,
                    meta
            ));
        }
        adapter.submitList(items);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        summaryView.setText(getString(R.string.admin_user_summary, items.size()));
    }
}

