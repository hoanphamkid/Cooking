package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.model.BaiChoDuyet;
import fpoly.ph62768.cooking.ui.BaiChoDuyetAdapter;

public class DanhSachChoDuyetActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "extra_user_email";
    public static final String EXTRA_FILTER_EMAIL = "extra_filter_email";

    private BaiChoDuyetStore baiChoDuyetStore;
    private BaiChoDuyetAdapter adapter;
    private String currentUserEmail = "";
    private String filterEmail = "";
    private TextView emptyView;

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

        ImageButton backButton = findViewById(R.id.danh_sach_quay_lai);
        emptyView = findViewById(R.id.danh_sach_trong);
        RecyclerView recyclerView = findViewById(R.id.danh_sach_recycler);

        backButton.setOnClickListener(v -> onBackPressed());

        adapter = new BaiChoDuyetAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSach();
    }

    private void taiDanhSach() {
        String emailToLoad = !filterEmail.isEmpty() ? filterEmail : currentUserEmail;
        List<BaiChoDuyet> recipes = baiChoDuyetStore.layDanhSachChoDuyet(emailToLoad);
        adapter.capNhatDanhSach(recipes);
        emptyView.setVisibility(recipes.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}

