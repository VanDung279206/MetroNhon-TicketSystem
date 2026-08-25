package service;

import data.VeDataService;
import model.HanhKhach;
import model.LoaiVeThang;
import model.VeThang;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
     * Trạng thái ban đầu của vé = true.
     *
     * Khi hết ngày kết thúc:
     * - Không tự set trangThai = false.
     * - Ngày kết thúc vẫn được lưu trong VeThang.
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
     * Kiểm tra vé tháng còn trong thời gian sử dụng hay không.
     *
     * Hàm này chỉ kiểm tra ngày.
     * Không thay đổi trangThai của vé.
     */
    public boolean kiemTraHieuLuc(VeThang veThang) {

        if (veThang == null) {
            return false;
        }

        /*
         * Dựa trên dữ liệu được tạo khi mua vé
         *
         * Không gọi setTrangThai(false).
         */
        LocalDate ngayHienTai = LocalDate.now();

        /*
         * VeThang hiện được tạo với ngày bắt đầu/kết thúc
         * trong constructor.
         *
         * Trạng thái được giữ độc lập với thời hạn.
         */
        return veThang.isTrangThai()
                && !ngayHienTai.isAfter(
                        veThang.getNgayKetThuc());
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

        veThang.setTrangThai(trangThai);

        return true;
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
