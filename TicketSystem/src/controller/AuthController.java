package controller;

import data.HanhKhachDataService;
import data.TaiKhoanDataService;
import model.HanhKhach;
import model.TaiKhoan;
import model.VaiTro;

import java.util.Collections;
import java.util.List;

/* Chức năng:
 * Đăng ký
 * Đăng nhập
 * Đổi mật khẩu
 * Lưu tài khoản đang đăng nhập
 */

public class AuthController {
    private final TaiKhoanDataService taiKhoanDataService;
    private final HanhKhachDataService hanhKhachDataService;

    // Thông tin người dùng đang đăng nhập
    private TaiKhoan taiKhoanDangNhap;
    private HanhKhach hanhKhachDangNhap;

    public AuthController() {
        taiKhoanDataService = new TaiKhoanDataService();
        hanhKhachDataService = new HanhKhachDataService();
    }

    // Đăng ký một tài khoản hành khách mới
    public boolean dangKy(
            String tenDangNhap,
            String matKhau,
            String hoTen,
            String soDienThoai,
            String email
    ) {
        // Kiểm tra các thông tin bắt buộc
        if (isRong(tenDangNhap)
                || isRong(matKhau)
                || isRong(hoTen)
                || isRong(soDienThoai)
                || isRong(email)) {
            return false;
        }

        // Xóa khoảng trắng thừa ở đầu và cuối
        tenDangNhap = tenDangNhap.trim();
        hoTen = hoTen.trim();
        soDienThoai = soDienThoai.trim();
        email = email.trim();

        // Không cho phép trùng tài khoản, số điện thoại hoặc email
        if (taiKhoanDataService.tonTaiTenDangNhap(tenDangNhap)
                || hanhKhachDataService.tonTaiSoDienThoai(soDienThoai)
                || hanhKhachDataService.tonTaiEmail(email)) {
            return false;
        }

        TaiKhoan taiKhoan = new TaiKhoan(
                tenDangNhap,
                matKhau,
                VaiTro.HANH_KHACH,
                true
        );

        HanhKhach hanhKhach = new HanhKhach(
                hanhKhachDataService.sinhMaHanhKhachMoi(),
                hoTen,
                soDienThoai,
                email,
                taiKhoan
        );

        // Phải lưu tài khoản trước vì hành khách tham chiếu tên đăng nhập
        if (!taiKhoanDataService.themTaiKhoan(taiKhoan)) {
            return false;
        }

        if (hanhKhachDataService.themHanhKhach(hanhKhach)) {
            return true;
        }

        // Nếu không lưu được hành khách thì xóa tài khoản vừa thêm,
        // tránh tạo tài khoản không có hồ sơ hành khách đi kèm.
        xoaTaiKhoanVuaThem(tenDangNhap);

        return false;
    }

    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        if (isRong(tenDangNhap) || isRong(matKhau)) {
            return null;
        }

        TaiKhoan taiKhoan = taiKhoanDataService.timTheoTenDangNhap(
                tenDangNhap.trim()
        );

        if (taiKhoan == null) {
            return null;
        }

        if (!taiKhoan.isTrangThai()) {
            return null;
        }

        if (!taiKhoan.getMatKhau().equals(matKhau)) {
            return null;
        }

        // Lưu thông tin phiên đăng nhập hiện tại
        taiKhoanDangNhap = taiKhoan;

        hanhKhachDangNhap = hanhKhachDataService.timTheoTenDangNhap(
                taiKhoan.getTenDangNhap()
        );

        if (taiKhoan.getVaiTro() == VaiTro.HANH_KHACH && hanhKhachDangNhap == null) {
            dangXuat();
            return null;
        }

        if (hanhKhachDangNhap != null) {
            // Dùng chung một đối tượng tài khoản trong phiên để số dư cập nhật ngay.
            hanhKhachDangNhap.setTaiKhoan(taiKhoanDangNhap);
        }

        return taiKhoanDangNhap;
    }

    public void dangXuat() {
        taiKhoanDangNhap = null;
        hanhKhachDangNhap = null;
    }

    public boolean doiMatKhau(String matKhauCu, String matKhauMoi) {
        // Phải đăng nhập trước
        if (taiKhoanDangNhap == null) {
            return false;
        }

        // Hai mật khẩu không được để trống
        if (isRong(matKhauCu) || isRong(matKhauMoi)) {
            return false;
        }

        // Kiểm tra mật khẩu cũ
        if (!taiKhoanDangNhap.getMatKhau().equals(matKhauCu)) {
            return false;
        }

        // Mật khẩu mới phải khác mật khẩu cũ
        if (matKhauCu.equals(matKhauMoi)) {
            return false;
        }

        List<TaiKhoan> danhSachTaiKhoan =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (TaiKhoan x : danhSachTaiKhoan) {
            if (x.getTenDangNhap().equalsIgnoreCase(
                    taiKhoanDangNhap.getTenDangNhap()
            )) {
                x.setMatKhau(matKhauMoi);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSachTaiKhoan);

                taiKhoanDangNhap = x;

                if (hanhKhachDangNhap != null) {
                    hanhKhachDangNhap.setTaiKhoan(x);
                }

                return true;
            }
        }

        return false;
    }

    public TaiKhoan timTaiKhoan(String tenDangNhap) {
        if (isRong(tenDangNhap)) {
            return null;
        }

        return taiKhoanDataService.timTheoTenDangNhap(
                tenDangNhap.trim()
        );
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    public HanhKhach getHanhKhachDangNhap() {
        return hanhKhachDangNhap;
    }

    public boolean isDaDangNhap() {
        return taiKhoanDangNhap != null;
    }

    public List<TaiKhoan> getDanhSachTaiKhoan() {
        return Collections.unmodifiableList(
                taiKhoanDataService.docDanhSachTaiKhoan()
        );
    }

    public List<HanhKhach> getDanhSachHanhKhach() {
        return Collections.unmodifiableList(
                hanhKhachDataService.docDanhSachHanhKhach()
        );
    }

    // Ghi trạng thái khóa/mở tài khoản xuống file
    public boolean capNhatTrangThaiTaiKhoan(
            String tenDangNhap,
            boolean trangThaiMoi
    ) {
        if (isRong(tenDangNhap)) {
            return false;
        }

        List<TaiKhoan> danhSachTaiKhoan =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (TaiKhoan x : danhSachTaiKhoan) {
            if (x.getTenDangNhap().equalsIgnoreCase(tenDangNhap.trim())) {
                x.setTrangThai(trangThaiMoi);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSachTaiKhoan);

                if (taiKhoanDangNhap != null
                        && taiKhoanDangNhap.getTenDangNhap().equalsIgnoreCase(
                        x.getTenDangNhap()
                )) {
                    taiKhoanDangNhap = x;

                    if (!trangThaiMoi) {
                        dangXuat();
                    }
                }

                return true;
            }
        }

        return false;
    }

    private void xoaTaiKhoanVuaThem(String tenDangNhap) {
        List<TaiKhoan> danhSachTaiKhoan =
                taiKhoanDataService.docDanhSachTaiKhoan();

        danhSachTaiKhoan.removeIf(
                x -> x.getTenDangNhap().equalsIgnoreCase(tenDangNhap)
        );

        taiKhoanDataService.luuDanhSachTaiKhoan(danhSachTaiKhoan);
    }

    private boolean isRong(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty();
    }
}