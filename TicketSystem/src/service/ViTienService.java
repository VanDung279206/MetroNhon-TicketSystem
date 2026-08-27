package service;

import data.TaiKhoanDataService;
import exception.KhongDuTienException;
import model.TaiKhoan;

import java.util.List;

/**
 * Ví tiền demo: nạp tiền và thanh toán trực tiếp bằng số dư tài khoản.
 */
public class ViTienService {
    private static final double SO_TIEN_NAP_TOI_DA = 10_000_000;

    private final TaiKhoanDataService taiKhoanDataService;

    public ViTienService() {
        taiKhoanDataService = new TaiKhoanDataService();
    }

    public double laySoDu(TaiKhoan taiKhoan) {
        TaiKhoan taiKhoanDaLuu = timTaiKhoanDaLuu(taiKhoan);
        taiKhoan.setSoDu(taiKhoanDaLuu.getSoDu());
        return taiKhoanDaLuu.getSoDu();
    }

    public double napTien(TaiKhoan taiKhoan, double soTien) {
        kiemTraSoTien(soTien, "Số tiền nạp");
        if (soTien > SO_TIEN_NAP_TOI_DA) {
            throw new IllegalArgumentException(
                    "Mỗi lần chỉ được nạp tối đa 10.000.000 VND"
            );
        }
        return thayDoiSoDu(taiKhoan, soTien, false);
    }

    public double thanhToan(TaiKhoan taiKhoan, double soTien) {
        kiemTraSoTien(soTien, "Số tiền thanh toán");
        return thayDoiSoDu(taiKhoan, -soTien, true);
    }

    public double hoanTien(TaiKhoan taiKhoan, double soTien) {
        kiemTraSoTien(soTien, "Số tiền hoàn");
        return thayDoiSoDu(taiKhoan, soTien, false);
    }

    private double thayDoiSoDu(TaiKhoan taiKhoan, double thayDoi,
                               boolean laThanhToan) {
        kiemTraTaiKhoan(taiKhoan);
        List<TaiKhoan> danhSachTaiKhoan =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (TaiKhoan taiKhoanDaLuu : danhSachTaiKhoan) {
            if (!taiKhoanDaLuu.getTenDangNhap().equalsIgnoreCase(
                    taiKhoan.getTenDangNhap())) {
                continue;
            }
            if (!taiKhoanDaLuu.isTrangThai()) {
                throw new IllegalStateException("Tài khoản đã bị khóa");
            }

            double soDuMoi = taiKhoanDaLuu.getSoDu() + thayDoi;
            if (laThanhToan && soDuMoi < 0) {
                throw new KhongDuTienException(
                        taiKhoanDaLuu.getSoDu(), -thayDoi
                );
            }

            taiKhoanDaLuu.setSoDu(soDuMoi);
            taiKhoanDataService.luuDanhSachTaiKhoan(danhSachTaiKhoan);
            taiKhoan.setSoDu(soDuMoi);
            return soDuMoi;
        }

        throw new IllegalArgumentException("Tài khoản chưa được lưu");
    }

    private TaiKhoan timTaiKhoanDaLuu(TaiKhoan taiKhoan) {
        kiemTraTaiKhoan(taiKhoan);
        TaiKhoan taiKhoanDaLuu = taiKhoanDataService.timTheoTenDangNhap(
                taiKhoan.getTenDangNhap()
        );
        if (taiKhoanDaLuu == null) {
            throw new IllegalArgumentException("Tài khoản chưa được lưu");
        }
        return taiKhoanDaLuu;
    }

    private void kiemTraTaiKhoan(TaiKhoan taiKhoan) {
        if (taiKhoan == null || taiKhoan.getTenDangNhap() == null
                || taiKhoan.getTenDangNhap().isBlank()) {
            throw new IllegalArgumentException("Tài khoản không hợp lệ");
        }
    }

    private void kiemTraSoTien(double soTien, String tenTruong) {
        if (!Double.isFinite(soTien) || soTien <= 0) {
            throw new IllegalArgumentException(tenTruong + " phải lớn hơn 0");
        }
    }
}