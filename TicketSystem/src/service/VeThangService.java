package service;

import data.VeDataService;
import model.HanhKhach;
import model.LoaiVeThang;
import model.VeThang;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeThangService implements GiaVeThang {
    // Vé có hiệu lực trong 30 ngày, tính cả ngày bắt đầu.
    private static final int SO_NGAY_SU_DUNG = 30;
    private final VeDataService veDataService;

    public VeThangService() {
        veDataService = new VeDataService();
    }

    public VeThang muaVeThang(HanhKhach hanhKhach, LoaiVeThang loaiVe) {
        if (hanhKhach == null) {
            throw new IllegalArgumentException("Hành khách không được để trống");
        }
        if (loaiVe == null) {
            throw new IllegalArgumentException("Loại vé không được để trống");
        }

        LocalDate ngayBatDau = LocalDate.now();
        String maVe = veDataService.sinhMaVeThangMoi();
        VeThang veThang = new VeThang(
                maVe, hanhKhach, LocalDateTime.now(), tinhGiaVeThang(loaiVe),
                loaiVe, true, ngayBatDau,
                ngayBatDau.plusDays(SO_NGAY_SU_DUNG - 1)
        );

        if (!veDataService.themVe(veThang)) {
            throw new IllegalStateException("Không thể lưu vé tháng có mã " + maVe);
        }
        return veThang;
    }

    @Override
    public double tinhGiaVeThang(LoaiVeThang loaiVe) {
        if (loaiVe == null) {
            throw new IllegalArgumentException("Loại vé không được để trống");
        }

        switch (loaiVe) {
            case PHO_THONG:
                return GIA_VE_THANG_PHO_THONG;
            case UU_DAI:
                return GIA_VE_THANG_UU_DAI;
            default:
                throw new IllegalArgumentException("Loại vé tháng chưa được hỗ trợ");
        }
    }
}