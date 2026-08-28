package model;

import java.time.LocalDateTime;

public class LuotSuDungVe {
    private final String maLuotSuDung;
    private final String maVe;
    private final String maHanhKhach;
    private final String maGaDi;
    private final String maGaDen;
    private final LocalDateTime thoiGianSuDung;

    public LuotSuDungVe(String maLuotSuDung, String maVe,
                        String maHanhKhach, String maGaDi,
                        String maGaDen, LocalDateTime thoiGianSuDung) {
        this.maLuotSuDung = maLuotSuDung;
        this.maVe = maVe;
        this.maHanhKhach = maHanhKhach;
        this.maGaDi = maGaDi;
        this.maGaDen = maGaDen;
        this.thoiGianSuDung = thoiGianSuDung;
    }

    public String getMaLuotSuDung() {
        return maLuotSuDung;
    }

    public String getMaVe() {
        return maVe;
    }

    public String getMaHanhKhach() {
        return maHanhKhach;
    }

    public String getMaGaDi() {
        return maGaDi;
    }

    public String getMaGaDen() {
        return maGaDen;
    }

    public LocalDateTime getThoiGianSuDung() {
        return thoiGianSuDung;
    }
}