package model;

import java.time.LocalDateTime;

public class PhieuHuyVe {
    private final String maPhieuHuy;
    private final String maVe;
    private final String maHanhKhach;
    private final LocalDateTime thoiGianHuy;
    private final double giaVeGoc;
    private final double tyLeHoan;
    private final double soTienHoan;
    private final String lyDo;

    public PhieuHuyVe(String maPhieuHuy, String maVe, String maHanhKhach,
                      LocalDateTime thoiGianHuy, double giaVeGoc,
                      double tyLeHoan, double soTienHoan, String lyDo) {
        this.maPhieuHuy = maPhieuHuy;
        this.maVe = maVe;
        this.maHanhKhach = maHanhKhach;
        this.thoiGianHuy = thoiGianHuy;
        this.giaVeGoc = giaVeGoc;
        this.tyLeHoan = tyLeHoan;
        this.soTienHoan = soTienHoan;
        this.lyDo = lyDo;
    }

    public String getMaPhieuHuy() {
        return maPhieuHuy;
    }

    public String getMaVe() {
        return maVe;
    }

    public String getMaHanhKhach() {
        return maHanhKhach;
    }

    public LocalDateTime getThoiGianHuy() {
        return thoiGianHuy;
    }

    public double getGiaVeGoc() {
        return giaVeGoc;
    }

    public double getTyLeHoan() {
        return tyLeHoan;
    }

    public double getSoTienHoan() {
        return soTienHoan;
    }

    public String getLyDo() {
        return lyDo;
    }
}