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
import fpoly.ph62768.cooking.data.BaiChoDuyetStore;
import fpoly.ph62768.cooking.model.BaiChoDuyet;

public class QuanTriBaiChoAdapter extends RecyclerView.Adapter<QuanTriBaiChoAdapter.ViewHolder> {

    public interface Listener {
        void onApprove(BaiChoDuyetStore.BanGhi record);
        void onReject(BaiChoDuyetStore.BanGhi record);
    }

    private final List<BaiChoDuyetStore.BanGhi> records = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<BaiChoDuyetStore.BanGhi> newRecords) {
        records.clear();
        if (newRecords != null) {
            records.addAll(newRecords);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quan_tri_bai, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(records.get(position), listener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tenMonView;
        private final TextView emailView;
        private final TextView thoiGianView;
        private final TextView moTaView;
        private final android.widget.RatingBar ratingBar;
        private final TextView ratingValueView;
        private final TextView trangThaiView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenMonView = itemView.findViewById(R.id.quan_tri_ten_mon);
            emailView = itemView.findViewById(R.id.quan_tri_email);
            thoiGianView = itemView.findViewById(R.id.quan_tri_thoi_gian);
            moTaView = itemView.findViewById(R.id.quan_tri_mo_ta);
            ratingBar = itemView.findViewById(R.id.quan_tri_danh_gia);
            ratingValueView = itemView.findViewById(R.id.quan_tri_gia_tri_danh_gia);
            trangThaiView = itemView.findViewById(R.id.quan_tri_trang_thai);
        }

        void bind(BaiChoDuyetStore.BanGhi record,
                  Listener listener,
                  SimpleDateFormat dateFormat) {
            BaiChoDuyet item = record.baiChoDuyet;
            tenMonView.setText(item.getTenMon());
            emailView.setText(record.email);
            thoiGianView.setText(item.getThoiGianNau());
            moTaView.setText(item.getMoTa());
            ratingBar.setRating(item.getDiemDanhGia());
            ratingValueView.setText(String.format(Locale.getDefault(), "%.1f", item.getDiemDanhGia()));

            String trangThai;
            switch (item.getTrangThai()) {
                case APPROVED:
                    trangThai = itemView.getContext().getString(R.string.pending_status_approved);
                    break;
                case REJECTED:
                    trangThai = itemView.getContext().getString(R.string.pending_status_rejected);
                    break;
                default:
                    trangThai = itemView.getContext().getString(R.string.pending_status_pending);
                    break;
            }
            trangThaiView.setText(trangThai + " • " + dateFormat.format(new Date(item.getThoiGianTao())));

            View rejectButton = itemView.findViewById(R.id.quan_tri_btn_tu_choi);
            View approveButton = itemView.findViewById(R.id.quan_tri_btn_duyet);

            boolean isPending = item.getTrangThai() == BaiChoDuyet.TrangThai.PENDING;
            rejectButton.setEnabled(isPending);
            approveButton.setEnabled(isPending);
            rejectButton.setAlpha(isPending ? 1f : 0.5f);
            approveButton.setAlpha(isPending ? 1f : 0.5f);

            rejectButton.setOnClickListener(v -> {
                if (isPending && listener != null) {
                    listener.onReject(record);
                }
            });
            approveButton.setOnClickListener(v -> {
                if (isPending && listener != null) {
                    listener.onApprove(record);
                }
            });
        }
    }
}

