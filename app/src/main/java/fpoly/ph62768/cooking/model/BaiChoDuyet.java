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
    private final String anhMon;
    private final String congThucChiTiet;
    private final long thoiGianTao;
    private final float diemDanhGia;
    private final TrangThai trangThai;
    private final boolean chuaThongBao;

    public BaiChoDuyet(@NonNull String id,
                       @NonNull String tenMon,
                       @NonNull String thoiGianNau,
                       @NonNull String moTa,
                       @NonNull String anhMon,
                       @NonNull String congThucChiTiet,
                       long thoiGianTao,
                       float diemDanhGia,
                       @NonNull TrangThai trangThai) {
        this(id, tenMon, thoiGianNau, moTa, anhMon, congThucChiTiet, thoiGianTao, diemDanhGia, trangThai, true);
    }

    public BaiChoDuyet(@NonNull String id,
                       @NonNull String tenMon,
                       @NonNull String thoiGianNau,
                       @NonNull String moTa,
                       @NonNull String anhMon,
                       @NonNull String congThucChiTiet,
                       long thoiGianTao,
                       float diemDanhGia,
                       @NonNull TrangThai trangThai,
                       boolean chuaThongBao) {
        this.id = id;
        this.tenMon = tenMon;
        this.thoiGianNau = thoiGianNau;
        this.moTa = moTa;
        this.anhMon = anhMon;
        this.congThucChiTiet = congThucChiTiet;
        this.thoiGianTao = thoiGianTao;
        this.diemDanhGia = diemDanhGia;
        this.trangThai = trangThai;
        this.chuaThongBao = chuaThongBao;
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

    @NonNull
    public String getAnhMon() {
        return anhMon == null ? "" : anhMon;
    }

    @NonNull
    public String getCongThucChiTiet() {
        return congThucChiTiet == null ? "" : congThucChiTiet;
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

    public boolean isChuaThongBao() {
        return chuaThongBao;
    }
}

