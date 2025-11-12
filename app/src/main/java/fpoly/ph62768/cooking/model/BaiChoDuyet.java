package fpoly.ph62768.cooking.model;

import androidx.annotation.NonNull;

public class BaiChoDuyet {

    public enum TrangThai {
        PENDING,
        APPROVED,
        REJECTED
    }

    private final String id;
    private final String tenMon;
    private final String thoiGianNau;
    private final String moTa;
    private final long thoiGianTao;
    private final float diemDanhGia;
    private final TrangThai trangThai;

    public BaiChoDuyet(@NonNull String id,
                       @NonNull String tenMon,
                       @NonNull String thoiGianNau,
                       @NonNull String moTa,
                       long thoiGianTao,
                       float diemDanhGia,
                       @NonNull TrangThai trangThai) {
        this.id = id;
        this.tenMon = tenMon;
        this.thoiGianNau = thoiGianNau;
        this.moTa = moTa;
        this.thoiGianTao = thoiGianTao;
        this.diemDanhGia = diemDanhGia;
        this.trangThai = trangThai;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTenMon() {
        return tenMon;
    }

    @NonNull
    public String getThoiGianNau() {
        return thoiGianNau;
    }

    @NonNull
    public String getMoTa() {
        return moTa;
    }

    public long getThoiGianTao() {
        return thoiGianTao;
    }

    public float getDiemDanhGia() {
        return diemDanhGia;
    }

    @NonNull
    public TrangThai getTrangThai() {
        return trangThai;
    }
}

