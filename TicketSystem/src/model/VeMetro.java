package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class VeMetro {
    //class cha
    private String maVe;
    private HanhKhach hanhKhach;
    private LocalDateTime ngayMua;
    private double giaVe;
    private boolean trangThai;

    public VeMetro(String maVe, HanhKhach hanhKhach, LocalDateTime ngayMua, double giaVe, boolean trangThai) {
        this.maVe = maVe;
        this.hanhKhach = hanhKhach;
        this.ngayMua = ngayMua;
        this.giaVe = giaVe;
        this.trangThai = trangThai;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public HanhKhach getHanhKhach() {
        return hanhKhach;
    }

    public void setHanhKhach(HanhKhach hanhKhach) {
        this.hanhKhach = hanhKhach;
    }

    public LocalDateTime getNgayMua() {
        return ngayMua;
    }

    public void setNgayMua(LocalDateTime ngayMua) {
        this.ngayMua = ngayMua;
    }

    public double getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(double giaVe) {
        this.giaVe = giaVe;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isConHieuLuc() {
        return isConHieuLuc(LocalDate.now());
    }

    public abstract boolean isConHieuLuc(LocalDate ngayKiemTra);

    public abstract String getLoaiVe(); // vé lượt và vé tháng
}