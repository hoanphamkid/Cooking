package fpoly.ph62768.cooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.model.BaiChoDuyet;

public class BaiChoDuyetAdapter extends RecyclerView.Adapter<BaiChoDuyetAdapter.BaiChoViewHolder> {

    private final List<BaiChoDuyet> danhSach = new ArrayList<>();
    private final SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public void capNhatDanhSach(List<BaiChoDuyet> items) {
        danhSach.clear();
        if (items != null) {
            danhSach.addAll(items);
        }
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
        holder.bind(danhSach.get(position), dinhDangNgay);
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static class BaiChoViewHolder extends RecyclerView.ViewHolder {

        private final TextView tenMonView;
        private final TextView thoiGianView;
        private final TextView moTaView;
        private final android.widget.RatingBar danhGiaView;
        private final TextView giaTriDanhGiaView;
        private final TextView trangThaiView;
        private final TextView thoiGianTaoView;

        BaiChoViewHolder(@NonNull View itemView) {
            super(itemView);
            tenMonView = itemView.findViewById(R.id.bai_cho_ten_mon);
            thoiGianView = itemView.findViewById(R.id.bai_cho_thoi_gian);
            moTaView = itemView.findViewById(R.id.bai_cho_mo_ta);
            danhGiaView = itemView.findViewById(R.id.bai_cho_danh_gia);
            giaTriDanhGiaView = itemView.findViewById(R.id.bai_cho_gia_tri_danh_gia);
            trangThaiView = itemView.findViewById(R.id.bai_cho_trang_thai);
            thoiGianTaoView = itemView.findViewById(R.id.bai_cho_thoi_gian_tao);
        }

        void bind(BaiChoDuyet item, SimpleDateFormat dinhDangNgay) {
            tenMonView.setText(item.getTenMon());
            thoiGianView.setText(item.getThoiGianNau());
            moTaView.setText(item.getMoTa());
            danhGiaView.setRating(item.getDiemDanhGia());
            giaTriDanhGiaView.setText(String.format(Locale.getDefault(), "%.1f", item.getDiemDanhGia()));
            thoiGianTaoView.setText(dinhDangNgay.format(new Date(item.getThoiGianTao())));

            int textId;
            switch (item.getTrangThai()) {
                case APPROVED:
                    textId = R.string.pending_status_approved;
                    break;
                case REJECTED:
                    textId = R.string.pending_status_rejected;
                    break;
                default:
                    textId = R.string.pending_status_pending;
                    break;
            }
            trangThaiView.setText(textId);
        }
    }
}

