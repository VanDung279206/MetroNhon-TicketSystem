package service;

import data.VeDataService;
import exception.GaKhongHopLeException;
import model.Ga;
import model.HanhKhach;
import model.VeLuot;
import model.VeMetro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     * Vé lượt có hiệu lực trong ngày mua. Trạng thái lưu trữ và
     * thời hạn sử dụng được kiểm tra riêng khi người dùng xem hoặc dùng vé.
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
            throw new GaKhongHopLeException(
                    "Ga đi không được để trống");
        }

        if (gaDen == null) {
            throw new GaKhongHopLeException(
                    "Ga đến không được để trống");
        }

        if (gaDi.getMaGa() == null
                || gaDen.getMaGa() == null) {
            throw new GaKhongHopLeException(
                    "Mã ga không được để trống");
        }

        if (gaDi.getMaGa().equalsIgnoreCase(gaDen.getMaGa())) {
            throw new GaKhongHopLeException(
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
     * Kiểm tra cả trạng thái đã lưu và ngày sử dụng của vé lượt.
     */
    public boolean kiemTraHieuLuc(VeLuot veLuot) {

        if (veLuot == null) {
            return false;
        }
        return veLuot.isConHieuLuc();
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

        List<VeMetro> danhSachVe = veDataService.docDanhSachVe();
        for (VeMetro ve : danhSachVe) {
            if (ve instanceof VeLuot
                    && ve.getMaVe().equalsIgnoreCase(veLuot.getMaVe())) {
                ve.setTrangThai(trangThai);
                veDataService.luuDanhSachVe(danhSachVe);
                veLuot.setTrangThai(trangThai);
                return true;
            }
        }
        return false;
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
