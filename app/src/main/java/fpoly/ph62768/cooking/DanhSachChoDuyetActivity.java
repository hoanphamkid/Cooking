package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.data.RecipeRepository;
import fpoly.ph62768.cooking.model.BaiChoDuyet;
import fpoly.ph62768.cooking.ui.BaiChoDuyetAdapter;

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

    private BaiChoDuyetStore baiChoDuyetStore;
    private BaiChoDuyetAdapter adapter;
    private String currentUserEmail = "";
    private String filterEmail = "";
    private StatusFilter statusFilter = StatusFilter.PENDING;
    private TextView emptyView;
    private ChipGroup filterChipGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_bai_cho);

        baiChoDuyetStore = new BaiChoDuyetStore(this);

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
        RecyclerView recyclerView = findViewById(R.id.danh_sach_recycler);
        filterChipGroup = findViewById(R.id.danh_sach_filter_chip_group);

        backButton.setOnClickListener(v -> onBackPressed());

        adapter = new BaiChoDuyetAdapter();
        boolean cheDoQuanTri = laCheDoQuanTri();
        adapter.setHienThiHanhDong(cheDoQuanTri);
        if (cheDoQuanTri) {
            adapter.setListener(new BaiChoDuyetAdapter.Listener() {
                @Override
                public void onApprove(@NonNull BaiChoDuyetAdapter.Item item) {
                    xuLyCapNhatTrangThai(item, BaiChoDuyet.TrangThai.APPROVED, R.string.pending_action_approve_success);
                }

                @Override
                public void onReject(@NonNull BaiChoDuyetAdapter.Item item) {
                    xuLyCapNhatTrangThai(item, BaiChoDuyet.TrangThai.REJECTED, R.string.pending_action_reject_success);
                }

                @Override
                public void onDelete(@NonNull BaiChoDuyetAdapter.Item item) {
                    if (TextUtils.isEmpty(item.email)) {
                        return;
                    }
                    baiChoDuyetStore.xoaBai(item.email, item.recipe.getId());
                    Toast.makeText(DanhSachChoDuyetActivity.this, R.string.pending_action_delete_success, Toast.LENGTH_SHORT).show();
                    taiDanhSach();
                }
            });
        }
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
        List<BaiChoDuyetAdapter.Item> items = new ArrayList<>();

        if (!TextUtils.isEmpty(filterEmail)) {
            for (BaiChoDuyet item : baiChoDuyetStore.layDanhSachChoDuyet(filterEmail)) {
                if (shouldInclude(item.getTrangThai())) {
                    items.add(new BaiChoDuyetAdapter.Item(filterEmail, item));
                }
            }
        } else if (!TextUtils.isEmpty(currentUserEmail)) {
            for (BaiChoDuyet item : baiChoDuyetStore.layDanhSachChoDuyet(currentUserEmail)) {
                if (shouldInclude(item.getTrangThai())) {
                    items.add(new BaiChoDuyetAdapter.Item(currentUserEmail, item));
                }
            }
        } else {
            for (BaiChoDuyetStore.BanGhi record : baiChoDuyetStore.layTatCaBanGhi()) {
                if (shouldInclude(record.baiChoDuyet.getTrangThai())) {
                    items.add(new BaiChoDuyetAdapter.Item(record.email, record.baiChoDuyet));
                }
            }
        }

        Collections.sort(items, (left, right) ->
                Long.compare(right.recipe.getThoiGianTao(), left.recipe.getThoiGianTao()));

        adapter.capNhatDanhSach(items);
        emptyView.setVisibility(items.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private boolean laCheDoQuanTri() {
        return TextUtils.isEmpty(filterEmail) && TextUtils.isEmpty(currentUserEmail);
    }

    private boolean shouldInclude(BaiChoDuyet.TrangThai trangThai) {
        if (statusFilter == StatusFilter.ALL) {
            return true;
        }
        if (statusFilter == StatusFilter.PENDING) {
            return trangThai == BaiChoDuyet.TrangThai.PENDING;
        }
        if (statusFilter == StatusFilter.APPROVED) {
            return trangThai == BaiChoDuyet.TrangThai.APPROVED;
        }
        return trangThai == BaiChoDuyet.TrangThai.REJECTED;
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
                                      int thongBaoRes) {
        if (TextUtils.isEmpty(item.email)) {
            return;
        }
        baiChoDuyetStore.capNhatTrangThaiToanCuc(item.email, item.recipe.getId(), trangThai);
        if (trangThai == BaiChoDuyet.TrangThai.APPROVED) {
            RecipeRepository.getInstance().addUserRecipe(item.recipe);
        }
        Toast.makeText(this, thongBaoRes, Toast.LENGTH_SHORT).show();
        taiDanhSach();
    }
}


