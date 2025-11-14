package fpoly.ph62768.cooking.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.model.BaiChoDuyet;

public class BaiChoDuyetAdapter extends RecyclerView.Adapter<BaiChoDuyetAdapter.BaiChoViewHolder> {

    public interface Listener {
        void onApprove(@NonNull Item item);

        void onReject(@NonNull Item item);

        void onDelete(@NonNull Item item);
    }

    public static class Item {
        public final String email;
        public final BaiChoDuyet recipe;

        public Item(@NonNull String email, @NonNull BaiChoDuyet recipe) {
            this.email = email;
            this.recipe = recipe;
        }
    }

    private final List<Item> danhSach = new ArrayList<>();
    private final SimpleDateFormat dinhDangNgay = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());

    private Listener listener;
    private boolean hienThiHanhDong = false;

    public void capNhatDanhSach(List<Item> items) {
        danhSach.clear();
        if (items != null) {
            danhSach.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setHienThiHanhDong(boolean hienThiHanhDong) {
        this.hienThiHanhDong = hienThiHanhDong;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaiChoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bai_cho_duyet, parent, false);
        return new BaiChoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiChoViewHolder holder, int position) {
        holder.bind(danhSach.get(position), dinhDangNgay, hienThiHanhDong, listener);
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static class BaiChoViewHolder extends RecyclerView.ViewHolder {

        private final TextView tenMonView;
        private final TextView thoiGianNauView;
        private final TextView moTaView;
        private final TextView congThucView;
        private final TextView trangThaiView;
        private final TextView thoiGianTaoView;
        private final TextView emailView;
        private final TextView danhGiaView;
        private final ImageView hinhAnhView;
        private final LinearLayout hanhDongContainer;
        private final View duyetButton;
        private final View tuChoiButton;
        private final View xoaButton;

        BaiChoViewHolder(@NonNull View itemView) {
            super(itemView);
            tenMonView = itemView.findViewById(R.id.bai_cho_ten_mon);
            thoiGianNauView = itemView.findViewById(R.id.bai_cho_thoi_gian);
            moTaView = itemView.findViewById(R.id.bai_cho_mo_ta);
            congThucView = itemView.findViewById(R.id.bai_cho_cong_thuc);
            trangThaiView = itemView.findViewById(R.id.bai_cho_trang_thai);
            thoiGianTaoView = itemView.findViewById(R.id.bai_cho_thoi_gian_tao);
            emailView = itemView.findViewById(R.id.bai_cho_email);
            danhGiaView = itemView.findViewById(R.id.bai_cho_gia_tri_danh_gia);
            hinhAnhView = itemView.findViewById(R.id.bai_cho_hinh_anh);
            hanhDongContainer = itemView.findViewById(R.id.bai_cho_actions);
            duyetButton = itemView.findViewById(R.id.bai_cho_duyet_button);
            tuChoiButton = itemView.findViewById(R.id.bai_cho_tu_choi_button);
            xoaButton = itemView.findViewById(R.id.bai_cho_xoa_button);
        }

        void bind(Item item,
                  SimpleDateFormat dinhDangNgay,
                  boolean hienThiHanhDong,
                  Listener listener) {
            BaiChoDuyet recipe = item.recipe;
            tenMonView.setText(recipe.getTenMon());
            thoiGianNauView.setText(thoiGianNauView.getContext().getString(R.string.pending_item_duration, recipe.getThoiGianNau()));
            moTaView.setText(moTaView.getContext().getString(R.string.pending_item_ingredients, recipe.getMoTa()));
            congThucView.setText(congThucView.getContext().getString(R.string.pending_item_steps, recipe.getCongThucChiTiet()));
            thoiGianTaoView.setText(dinhDangNgay.format(new Date(recipe.getThoiGianTao())));
            danhGiaView.setText(danhGiaView.getContext().getString(R.string.pending_item_rating, recipe.getDiemDanhGia()));

            if (!TextUtils.isEmpty(item.email)) {
                emailView.setVisibility(View.VISIBLE);
                emailView.setText(item.email);
            } else {
                emailView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(recipe.getAnhMon())) {
                Glide.with(hinhAnhView.getContext())
                        .load(recipe.getAnhMon())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .centerCrop()
                        .into(hinhAnhView);
            } else {
                hinhAnhView.setImageResource(R.drawable.ic_profile_placeholder);
            }

            int textId;
            int backgroundRes;
            switch (recipe.getTrangThai()) {
                case APPROVED:
                    textId = R.string.pending_status_approved;
                    backgroundRes = R.drawable.bg_status_approved;
                    break;
                case REJECTED:
                    textId = R.string.pending_status_rejected;
                    backgroundRes = R.drawable.bg_status_rejected;
                    break;
                default:
                    textId = R.string.pending_status_pending;
                    backgroundRes = R.drawable.bg_status_pending;
                    break;
            }
            trangThaiView.setText(textId);
            trangThaiView.setBackgroundResource(backgroundRes);

            boolean coTheThaoTac = hienThiHanhDong && recipe.getTrangThai() == BaiChoDuyet.TrangThai.PENDING;
            hanhDongContainer.setVisibility(coTheThaoTac ? View.VISIBLE : View.GONE);
            if (coTheThaoTac && listener != null) {
                duyetButton.setOnClickListener(v -> listener.onApprove(item));
                tuChoiButton.setOnClickListener(v -> listener.onReject(item));
                xoaButton.setOnClickListener(v -> listener.onDelete(item));
            } else {
                duyetButton.setOnClickListener(null);
                tuChoiButton.setOnClickListener(null);
                xoaButton.setOnClickListener(null);
            }
        }
    }
}
