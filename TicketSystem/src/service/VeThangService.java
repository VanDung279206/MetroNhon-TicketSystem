package service;

import data.VeDataService;
import model.HanhKhach;
import model.LoaiVeThang;
import model.VeMetro;
import model.VeThang;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class VeThangService implements GiaVeThang {

    /*
     * Vé tháng có hiệu lực 30 ngày,
     * tính cả ngày bắt đầu.
     */
    private static final int SO_NGAY_SU_DUNG = 30;

    private final VeDataService veDataService;

    public VeThangService() {
        veDataService = new VeDataService();
    }

    /**
     * Mua vé tháng.
     *
     * Ngày hết hạn được giữ riêng để không mất lịch sử trạng thái vé.
     */
    public VeThang muaVeThang(
            HanhKhach hanhKhach,
            LoaiVeThang loaiVe) {

        if (hanhKhach == null) {
            throw new IllegalArgumentException(
                    "Hành khách không được để trống");
        }

        if (loaiVe == null) {
            throw new IllegalArgumentException(
                    "Loại vé không được để trống");
        }

        LocalDate ngayBatDau = LocalDate.now();

        LocalDate ngayKetThuc =
                ngayBatDau.plusDays(SO_NGAY_SU_DUNG - 1);

        double giaVe = tinhGiaVeThang(loaiVe);

        String maVe =
                veDataService.sinhMaVeThangMoi();

        VeThang veThang = new VeThang(
                maVe,
                hanhKhach,
                LocalDateTime.now(),
                giaVe,
                loaiVe,
                true,
                ngayBatDau,
                ngayKetThuc
        );

        if (!veDataService.themVe(veThang)) {
            throw new IllegalStateException(
                    "Không thể lưu vé tháng có mã " + maVe);
        }

        return veThang;
    }

    /**
     * Tính giá vé tháng theo loại vé.
     */
    @Override
    public double tinhGiaVeThang(LoaiVeThang loaiVe) {

        if (loaiVe == null) {
            throw new IllegalArgumentException(
                    "Loại vé không được để trống");
        }

        switch (loaiVe) {

            case PHO_THONG:
                return GIA_VE_THANG_PHO_THONG;

            case UU_DAI:
                return GIA_VE_THANG_UU_DAI;

            default:
                throw new IllegalArgumentException(
                        "Loại vé tháng chưa được hỗ trợ");
        }
    }

    /**
     * Kiểm tra cả trạng thái đã lưu và khoảng thời gian sử dụng của vé tháng.
     */
    public boolean kiemTraHieuLuc(VeThang veThang) {

        if (veThang == null) {
            return false;
        }

        return veThang.isConHieuLuc();
    }

    /**
     * Lấy trạng thái hiện tại của vé.
     *
     * Hết hạn không làm thay đổi giá trị này.
     */
    public boolean layTrangThai(VeThang veThang) {

        if (veThang == null) {
            return false;
        }

        return veThang.isTrangThai();
    }

    /**
     * Cập nhật trạng thái vé một cách chủ động.
     */
    public boolean capNhatTrangThai(
            VeThang veThang,
            boolean trangThai) {

        if (veThang == null) {
            return false;
        }

        List<VeMetro> danhSachVe = veDataService.docDanhSachVe();
        for (VeMetro ve : danhSachVe) {
            if (ve instanceof VeThang
                    && ve.getMaVe().equalsIgnoreCase(veThang.getMaVe())) {
                ve.setTrangThai(trangThai);
                veDataService.luuDanhSachVe(danhSachVe);
                veThang.setTrangThai(trangThai);
                return true;
            }
        }
        return false;
    }

    /**
     * Kích hoạt vé tháng.
     */
    public boolean kichHoatVe(VeThang veThang) {
        return capNhatTrangThai(veThang, true);
    }

    /**
     * Vô hiệu hóa vé tháng.
     */
    public boolean voHieuHoaVe(VeThang veThang) {
        return capNhatTrangThai(veThang, false);
    }
}