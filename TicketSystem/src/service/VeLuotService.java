package service;

import model.Ga;
import model.HanhKhach;
import model.VeLuot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeLuotService {
    // dịch vụ tính giá vé
    private final TinhGiaVeService tinhGiaVeService;

    // số thứ tự dùng để sinh mã vé
    private int soThuTuVe = 1;

    public VeLuotService() {
        tinhGiaVeService = new TinhGiaVeService();
    }

    // mua vé lượt
    public VeLuot muaVeLuot(HanhKhach hanhKhach, Ga gaDi, Ga gaDen) {
        // kiểm tra hành khách
        if (hanhKhach == null) {
            throw new IllegalArgumentException(
                    "Hành khách không được để trống"
            );
        }

        // kiểm tra ga đi
        if (gaDi == null) {
            throw new IllegalArgumentException(
                    "Ga đi không được để trống"
            );
        }

        // kiểm tra ga đến
        if (gaDen == null) {
            throw new IllegalArgumentException(
                    "Ga đến không được để trống"
            );
        }

        // không cho ga đi và ga đến giống nhau
        if (gaDi.getMaGa().equalsIgnoreCase(gaDen.getMaGa())) {
            throw new IllegalArgumentException(
                    "Ga đi và ga đến không được giống nhau"
            );
        }

        // tính giá vé
        double giaVe = tinhGiaVeService.tinhGiaVe(gaDi, gaDen);

        // sinh mã vé
        String maVe = sinhMaVe();

        // ngày và thời điểm mua vé
        LocalDateTime ngayMua = LocalDateTime.now();

        // ngày sử dụng vé
        LocalDate ngaySuDung = LocalDate.now();

        // vé mới được kích hoạt
        boolean trangThai = true;

        // tạo vé lượt
        return new VeLuot(maVe, hanhKhach, ngayMua, giaVe, trangThai, gaDi, gaDen, ngaySuDung);
    }

    // hàm sinh mã vé
    private String sinhMaVe() {
        return String.format(
                "VL%03d",
                soThuTuVe++
        );
    }
}
