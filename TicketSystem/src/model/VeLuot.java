package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeLuot extends VeMetro {
    //kế thừa VeMetro
    private Ga gaDi;
    private Ga gaDen;
    private LocalDate ngaySuDung;

    public VeLuot(String maVe, HanhKhach hanhKhach, LocalDateTime ngayMua, double giaVe, boolean trangThai, Ga gaDi, Ga gaDen, LocalDate ngaySuDung) {
        super(maVe, hanhKhach, ngayMua, giaVe, trangThai);
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.ngaySuDung = ngaySuDung;
    }

    public Ga getGaDi() {
        return gaDi;
    }

    public void setGaDi(Ga gaDi) {
        this.gaDi = gaDi;
    }

    public Ga getGaDen() {
        return gaDen;
    }

    public void setGaDen(Ga gaDen) {
        this.gaDen = gaDen;
    }

    public LocalDate getNgaySuDung() {
        return ngaySuDung;
    }

    public void setNgaySuDung(LocalDate ngaySuDung) {
        this.ngaySuDung = ngaySuDung;
    }

    @Override
    public String getLoaiVe() {
        return "VE_LUOT";
    }

    @Override
    public String toString() {
        return getMaVe() + " | vé lượt | " +
                gaDi.getTenGa() + " -> " +
                gaDen.getTenGa() + " | " +
                "ngày sử dụng: " + ngaySuDung + " | " +
                getGiaVe() + "VND";
    }
}
