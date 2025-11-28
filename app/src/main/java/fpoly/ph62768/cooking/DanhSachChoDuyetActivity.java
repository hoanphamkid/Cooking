package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.auth.SessionManager;
import fpoly.ph62768.cooking.data.remote.PendingRecipeRepository;
import fpoly.ph62768.cooking.data.remote.dto.PendingRecipeResponse;
import fpoly.ph62768.cooking.model.BaiChoDuyet;
import fpoly.ph62768.cooking.ui.BaiChoDuyetAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhSachChoDuyetActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "extra_user_email";
    public static final String EXTRA_FILTER_EMAIL = "extra_filter_email";
    public static final String EXTRA_INITIAL_STATUS = "extra_initial_status";

    public enum StatusFilter {
        ALL,
        PENDING,
        APPROVED,
        REJECTED
    }

    private BaiChoDuyetAdapter adapter;
    private String currentUserEmail = "";
    private String filterEmail = "";
    private StatusFilter statusFilter = StatusFilter.PENDING;
    private TextView emptyView;
    private ChipGroup filterChipGroup;
    private View progressView;
    private PendingRecipeRepository pendingRecipeRepository;
    private SessionManager sessionManager;
    private Call<List<PendingRecipeResponse>> currentCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_bai_cho);

        pendingRecipeRepository = new PendingRecipeRepository();
        sessionManager = new SessionManager(this);

        currentUserEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        if (currentUserEmail == null) {
            currentUserEmail = "";
        }
        filterEmail = getIntent().getStringExtra(EXTRA_FILTER_EMAIL);
        if (filterEmail == null) {
            filterEmail = "";
        }
        String statusRaw = getIntent().getStringExtra(EXTRA_INITIAL_STATUS);
        if (!TextUtils.isEmpty(statusRaw)) {
            try {
                statusFilter = StatusFilter.valueOf(statusRaw);
            } catch (IllegalArgumentException ignored) {
                statusFilter = StatusFilter.PENDING;
            }
        }

        ImageButton backButton = findViewById(R.id.danh_sach_quay_lai);
        emptyView = findViewById(R.id.danh_sach_trong);
        progressView = findViewById(R.id.danh_sach_progress);
        RecyclerView recyclerView = findViewById(R.id.danh_sach_recycler);
        filterChipGroup = findViewById(R.id.danh_sach_filter_chip_group);

        backButton.setOnClickListener(v -> onBackPressed());

        boolean cheDoQuanTri = laCheDoQuanTri();
        adapter = new BaiChoDuyetAdapter();
        adapter.setHienThiHanhDong(cheDoQuanTri);
        adapter.setListener(new BaiChoDuyetAdapter.Listener() {
            @Override
            public void onApprove(@NonNull BaiChoDuyetAdapter.Item item) {
                if (!cheDoQuanTri) {
                    return;
                }
                xuLyCapNhatTrangThai(item, BaiChoDuyet.TrangThai.APPROVED, R.string.pending_action_approve_success, null);
            }

            @Override
            public void onReject(@NonNull BaiChoDuyetAdapter.Item item) {
                if (!cheDoQuanTri) {
                    return;
                }
                showRejectReasonDialog(item);
            }

            @Override
            public void onDelete(@NonNull BaiChoDuyetAdapter.Item item) {
                String token = sessionManager.getToken();
                if (TextUtils.isEmpty(token)) {
                    Toast.makeText(DanhSachChoDuyetActivity.this, R.string.admin_delete_token_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoading(true);
                pendingRecipeRepository.delete(token, item.recipe.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        showLoading(false);
                        if (response.isSuccessful()) {
                Toast.makeText(DanhSachChoDuyetActivity.this, R.string.pending_action_delete_success, Toast.LENGTH_SHORT).show();
                taiDanhSach();
                        } else {
                            Toast.makeText(DanhSachChoDuyetActivity.this, R.string.distributor_error_api, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        showLoading(false);
                        if (!call.isCanceled()) {
                            Toast.makeText(DanhSachChoDuyetActivity.this, R.string.distributor_error_api, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (filterChipGroup != null) {
            filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    return;
                }
                int id = checkedIds.get(0);
                statusFilter = mapChipToFilter(id);
                adapter.setHienThiHanhDong(laCheDoQuanTri());
                taiDanhSach();
            });
            filterChipGroup.check(mapFilterToChipId(statusFilter));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSach();
    }

    private void taiDanhSach() {
        if (currentCall != null) {
            currentCall.cancel();
        }
        String token = sessionManager.getToken();
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, R.string.admin_delete_token_missing, Toast.LENGTH_SHORT).show();
            adapter.capNhatDanhSach(new ArrayList<>());
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        showLoading(true);
        String statusQuery = statusFilter == StatusFilter.ALL ? null : statusFilter.name();
        String authorParam = !TextUtils.isEmpty(filterEmail)
                ? filterEmail
                : (!TextUtils.isEmpty(currentUserEmail) ? currentUserEmail : null);
        currentCall = pendingRecipeRepository.getPendingRecipes(token, statusQuery, authorParam);
        currentCall.enqueue(new Callback<List<PendingRecipeResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<PendingRecipeResponse>> call,
                                   @NonNull Response<List<PendingRecipeResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
        List<BaiChoDuyetAdapter.Item> items = new ArrayList<>();
                    for (PendingRecipeResponse pending : response.body()) {
                        BaiChoDuyet mapped = mapPendingRecipe(pending);
                        if (mapped != null) {
                            items.add(new BaiChoDuyetAdapter.Item(pending.getAuthorEmail(), mapped));
                        }
                    }
                    adapter.capNhatDanhSach(items);
                    emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    adapter.capNhatDanhSach(new ArrayList<>());
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(DanhSachChoDuyetActivity.this, R.string.distributor_error_api, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PendingRecipeResponse>> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                showLoading(false);
                adapter.capNhatDanhSach(new ArrayList<>());
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(DanhSachChoDuyetActivity.this, getString(R.string.distributor_error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean laCheDoQuanTri() {
        return sessionManager != null && sessionManager.isAdmin();
    }

    private StatusFilter mapChipToFilter(int chipId) {
        if (chipId == R.id.danh_sach_chip_all) {
            return StatusFilter.ALL;
        } else if (chipId == R.id.danh_sach_chip_pending) {
            return StatusFilter.PENDING;
        } else if (chipId == R.id.danh_sach_chip_approved) {
            return StatusFilter.APPROVED;
        } else if (chipId == R.id.danh_sach_chip_rejected) {
            return StatusFilter.REJECTED;
        }
        return StatusFilter.PENDING;
    }

    private int mapFilterToChipId(StatusFilter filter) {
        switch (filter) {
            case ALL:
                return R.id.danh_sach_chip_all;
            case APPROVED:
                return R.id.danh_sach_chip_approved;
            case REJECTED:
                return R.id.danh_sach_chip_rejected;
            case PENDING:
            default:
                return R.id.danh_sach_chip_pending;
        }
    }

    private void xuLyCapNhatTrangThai(@NonNull BaiChoDuyetAdapter.Item item,
                                      @NonNull BaiChoDuyet.TrangThai trangThai,
                                      int thongBaoRes,
                                      @Nullable String rejectionReason) {
        if (TextUtils.isEmpty(item.email)) {
            return;
        }
        String token = sessionManager.getToken();
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, R.string.admin_delete_token_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        Call<PendingRecipeResponse> call = trangThai == BaiChoDuyet.TrangThai.APPROVED
                ? pendingRecipeRepository.approve(token, item.recipe.getId())
                : pendingRecipeRepository.reject(token, item.recipe.getId(), rejectionReason);
        showLoading(true);
        call.enqueue(new Callback<PendingRecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<PendingRecipeResponse> call, @NonNull Response<PendingRecipeResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(DanhSachChoDuyetActivity.this, thongBaoRes, Toast.LENGTH_SHORT).show();
        taiDanhSach();
                } else {
                    Toast.makeText(DanhSachChoDuyetActivity.this, R.string.distributor_error_api, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PendingRecipeResponse> call, @NonNull Throwable t) {
                showLoading(false);
                if (!call.isCanceled()) {
                    Toast.makeText(DanhSachChoDuyetActivity.this, R.string.distributor_error_api, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showRejectReasonDialog(@NonNull BaiChoDuyetAdapter.Item item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reject_reason, null);
        RadioGroup reasonGroup = dialogView.findViewById(R.id.reject_reason_group);
        TextInputLayout reasonInputLayout = dialogView.findViewById(R.id.reject_reason_input_layout);
        TextInputEditText reasonInput = dialogView.findViewById(R.id.reject_reason_input);

        reasonGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.reject_reason_other) {
                reasonInputLayout.setVisibility(View.VISIBLE);
                if (reasonInput != null) {
                    reasonInput.requestFocus();
                }
            } else {
                reasonInputLayout.setVisibility(View.GONE);
                reasonInputLayout.setError(null);
            }
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.pending_reject_dialog_title)
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.pending_reject_confirm, null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                int selectedId = reasonGroup.getCheckedRadioButtonId();
                if (selectedId == View.NO_ID) {
                    Toast.makeText(this, R.string.pending_reject_reason_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                String reasonText;
                if (selectedId == R.id.reject_reason_other) {
                    String custom = reasonInput != null && reasonInput.getText() != null
                            ? reasonInput.getText().toString().trim()
                            : "";
                    if (custom.isEmpty()) {
                        reasonInputLayout.setError(getString(R.string.pending_reject_reason_required));
                        if (reasonInput != null) {
                            reasonInput.requestFocus();
                        }
                        return;
                    }
                    reasonInputLayout.setError(null);
                    reasonText = custom;
                } else {
                    RadioButton selectedButton = dialogView.findViewById(selectedId);
                    reasonText = selectedButton != null ? selectedButton.getText().toString() : "";
                }
                if (TextUtils.isEmpty(reasonText)) {
                    Toast.makeText(this, R.string.pending_reject_reason_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                xuLyCapNhatTrangThai(item, BaiChoDuyet.TrangThai.REJECTED, R.string.pending_action_reject_success, reasonText);
            });
        });
        dialog.show();
    }

    private BaiChoDuyet mapPendingRecipe(PendingRecipeResponse response) {
        if (response == null || TextUtils.isEmpty(response.getId())) {
            return null;
        }
        BaiChoDuyet.TrangThai trangThai;
        try {
            trangThai = BaiChoDuyet.TrangThai.valueOf(response.getStatus());
        } catch (IllegalArgumentException ex) {
            trangThai = BaiChoDuyet.TrangThai.PENDING;
        }
        return new BaiChoDuyet(
                response.getId(),
                response.getName() != null ? response.getName() : "",
                response.getDuration() != null ? response.getDuration() : "",
                response.getDescription() != null ? response.getDescription() : "",
                response.getImageUrl() != null ? response.getImageUrl() : "",
                convertStepsToContent(response),
                response.getCreatedAtMillis(),
                0f,
                trangThai
        );
    }

    private String convertStepsToContent(PendingRecipeResponse response) {
        if (response.getSteps() == null || response.getSteps().isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < response.getSteps().size(); i++) {
            String description = response.getSteps().get(i).getDescription();
            String image = response.getSteps().get(i).getImageUrl();
            if (description == null || description.trim().isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(description.trim());
            if (image != null && !image.trim().isEmpty()) {
                builder.append("<<<").append(image.trim());
            }
        }
        return builder.toString();
    }

    private void showLoading(boolean loading) {
        if (progressView != null) {
            progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}



