package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeThang extends VeMetro {
    //kế thừa VeMetro
    private LocalDate ngayBatDau;
    private LocalDate ngayHetHan;

    public VeThang(String maVe, HanhKhach hanhKhach, LocalDateTime ngayMua, double giaVe, boolean trangThai, LocalDate ngayBatDau, LocalDate ngayHetHan) {
        super(maVe, hanhKhach, ngayMua, giaVe, trangThai);
        this.ngayBatDau = ngayBatDau;
        this.ngayHetHan = ngayHetHan;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDate ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    @Override
    public String getLoaiVe() {
        return "VE_THHANG";
    }

    @Override
    public String toString() {
        return getMaVe() + " | vé tháng | " +
                "từ ngày: " + ngayBatDau + " đến ngày " +
                ngayHetHan + " | " +
                getGiaVe() + "VND";
    }
}
