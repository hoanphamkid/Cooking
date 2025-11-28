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
import com.google.android.material.button.MaterialButton;

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
    private boolean cheDoQuanTri = false;

    public void capNhatDanhSach(List<Item> items) {
        danhSach.clear();
        if (items != null) {
            danhSach.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        notifyDataSetChanged();
    }

    public void setHienThiHanhDong(boolean cheDoQuanTri) {
        this.cheDoQuanTri = cheDoQuanTri;
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
        holder.bind(danhSach.get(position), dinhDangNgay, cheDoQuanTri, listener);
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static class BaiChoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView hinhAnhView;
        private final TextView tenMonView;
        private final TextView moTaView;
        private final TextView emailView;
        private final TextView trangThaiView;
        private final TextView thoiGianTaoView;
        private final LinearLayout hanhDongContainer;
        private final MaterialButton duyetButton;
        private final MaterialButton tuChoiButton;
        private final MaterialButton xoaButton;

        BaiChoViewHolder(@NonNull View itemView) {
            super(itemView);
            hinhAnhView = itemView.findViewById(R.id.bai_cho_hinh_anh);
            tenMonView = itemView.findViewById(R.id.bai_cho_ten_mon);
            moTaView = itemView.findViewById(R.id.bai_cho_mo_ta);
            emailView = itemView.findViewById(R.id.bai_cho_email);
            trangThaiView = itemView.findViewById(R.id.bai_cho_trang_thai);
            thoiGianTaoView = itemView.findViewById(R.id.bai_cho_thoi_gian_tao);
            hanhDongContainer = itemView.findViewById(R.id.bai_cho_actions);
            duyetButton = itemView.findViewById(R.id.bai_cho_duyet_button);
            tuChoiButton = itemView.findViewById(R.id.bai_cho_tu_choi_button);
            xoaButton = itemView.findViewById(R.id.bai_cho_xoa_button);
        }

        void bind(Item item,
                  SimpleDateFormat dinhDangNgay,
                  boolean cheDoQuanTri,
                  Listener listener) {
            BaiChoDuyet recipe = item.recipe;
            tenMonView.setText(recipe.getTenMon());
            if (!TextUtils.isEmpty(recipe.getMoTa())) {
                moTaView.setText(recipe.getMoTa());
                moTaView.setVisibility(View.VISIBLE);
            } else {
                moTaView.setVisibility(View.GONE);
            }

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

            String thoiGian = dinhDangNgay.format(new Date(recipe.getThoiGianTao()));
            thoiGianTaoView.setText(thoiGianTaoView.getContext().getString(R.string.pending_item_sent_at, thoiGian));

            if (listener == null) {
                hanhDongContainer.setVisibility(View.GONE);
                return;
            }

            hanhDongContainer.setVisibility(View.VISIBLE);
            xoaButton.setVisibility(View.VISIBLE);
            xoaButton.setOnClickListener(v -> listener.onDelete(item));

            if (cheDoQuanTri) {
                boolean isPending = recipe.getTrangThai() == BaiChoDuyet.TrangThai.PENDING;
                if (isPending) {
                    duyetButton.setVisibility(View.VISIBLE);
                    tuChoiButton.setVisibility(View.VISIBLE);
                    duyetButton.setOnClickListener(v -> listener.onApprove(item));
                    tuChoiButton.setOnClickListener(v -> listener.onReject(item));
                } else {
                    duyetButton.setVisibility(View.GONE);
                    tuChoiButton.setVisibility(View.GONE);
                    duyetButton.setOnClickListener(null);
                    tuChoiButton.setOnClickListener(null);
                }
            } else {
                duyetButton.setVisibility(View.GONE);
                tuChoiButton.setVisibility(View.GONE);
                duyetButton.setOnClickListener(null);
                tuChoiButton.setOnClickListener(null);
            }
        }
    }

}
