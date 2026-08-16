package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeThang extends VeMetro {
    //kế thừa VeMetro
    private LoaiVeThang loaiVe; // loại vé tháng: phổ thông, ưu tiên,...

    // thời gian hiệu lực
    private LocalDate ngayBatDau;
    private LocalDate ngayHetHan;

    public VeThang(String maVe, HanhKhach hanhKhach, LocalDateTime ngayMua, double giaVe, LoaiVeThang loaiVe, boolean trangThai, LocalDate ngayBatDau, LocalDate ngayHetHan) {
        super(maVe, hanhKhach, ngayMua, giaVe, trangThai);
        this.loaiVe = loaiVe;
        this.ngayBatDau = ngayBatDau;
        this.ngayHetHan = ngayHetHan;
    }

    public LoaiVeThang getLoaiVeThang() {
        return loaiVe;
    }

    public void setLoaiVe(LoaiVeThang loaiVe) {
        this.loaiVe = loaiVe;
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
        return "VE_THANG";
    }

    @Override
    public String toString() {
        return getMaVe() + " | vé tháng | " +
                "từ ngày: " + ngayBatDau + " đến ngày " +
                ngayHetHan + " | " +
                getGiaVe() + "VND" +
                " | trạng thái: " +
                (isTrangThai() ? "Đang hoạt động" : "Đã hết hạn");
    }
}
