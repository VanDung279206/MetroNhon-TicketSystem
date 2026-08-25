package service;

import data.VeDataService;
import model.Ga;
import model.HanhKhach;
import model.VeLuot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeLuotService {

    private final TinhGiaVeService tinhGiaVeService;
    private final VeDataService veDataService;

    public VeLuotService() {
        tinhGiaVeService = new TinhGiaVeService();
        veDataService = new VeDataService();
    }

    /**
     * Mua vé lượt.
     *
     * Vé lượt có hiệu lực trong ngày mua.
     * Trạng thái ban đầu = true.
     *
     * Lưu ý:
     * Ngày hết hạn không làm thay đổi trangThai của vé.
     */
    public VeLuot muaVeLuot(
            HanhKhach hanhKhach,
            Ga gaDi,
            Ga gaDen) {

        if (hanhKhach == null) {
            throw new IllegalArgumentException(
                    "Hành khách không được để trống");
        }

        if (gaDi == null) {
            throw new IllegalArgumentException(
                    "Ga đi không được để trống");
        }

        if (gaDen == null) {
            throw new IllegalArgumentException(
                    "Ga đến không được để trống");
        }

        if (gaDi.getMaGa() == null
                || gaDen.getMaGa() == null) {
            throw new IllegalArgumentException(
                    "Mã ga không được để trống");
        }

        if (gaDi.getMaGa().equalsIgnoreCase(gaDen.getMaGa())) {
            throw new IllegalArgumentException(
                    "Ga đi và ga đến không được giống nhau");
        }

        double giaVe =
                tinhGiaVeService.tinhGiaVe(gaDi, gaDen);

        String maVe =
                veDataService.sinhMaVeLuotMoi();

        LocalDate ngaySuDung = LocalDate.now();

        VeLuot veLuot = new VeLuot(
                maVe,
                hanhKhach,
                LocalDateTime.now(),
                giaVe,
                true,
                gaDi,
                gaDen,
                ngaySuDung
        );

        if (!veDataService.themVe(veLuot)) {
            throw new IllegalStateException(
                    "Không thể lưu vé lượt có mã " + maVe);
        }

        return veLuot;
    }

    /**
     * Kiểm tra vé lượt còn hiệu lực hay không.
     *
     * Không thay đổi trangThai của vé.
     */
    public boolean kiemTraHieuLuc(VeLuot veLuot) {

        if (veLuot == null) {
            return false;
        }
        return veLuot.isTrangThai();
    }

    /**
     * Lấy trạng thái hiện tại của vé.
     *
     * Không tự động thay đổi trạng thái khi vé hết hạn.
     */
    public boolean layTrangThai(VeLuot veLuot) {

        if (veLuot == null) {
            return false;
        }

        return veLuot.isTrangThai();
    }

    /**
     * Cập nhật trạng thái vé một cách chủ động.
     *
     * true  = kích hoạt
     * false = vô hiệu hóa
     */
    public boolean capNhatTrangThai(
            VeLuot veLuot,
            boolean trangThai) {

        if (veLuot == null) {
            return false;
        }

        veLuot.setTrangThai(trangThai);

        return true;
    }

    /**
     * Kích hoạt vé lượt.
     */
    public boolean kichHoatVe(VeLuot veLuot) {
        return capNhatTrangThai(veLuot, true);
    }

    /**
     * Vô hiệu hóa vé lượt.
     */
    public boolean voHieuHoaVe(VeLuot veLuot) {
        return capNhatTrangThai(veLuot, false);
    }
}
