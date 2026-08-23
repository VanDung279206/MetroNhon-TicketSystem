package service;

import data.TaiKhoanDataService;
import model.HanhKhach;
import model.TaiKhoan;
import model.VaiTro;

import java.util.List;

public class AuthService {
    private final TaiKhoanDataService taiKhoanDataService;
    private final HanhKhachService hanhKhachService;

    public AuthService() {
        taiKhoanDataService = new TaiKhoanDataService();
        hanhKhachService = new HanhKhachService();
    }

    public boolean dangKy(TaiKhoan taiKhoan, HanhKhach hanhKhach) {
        if (taiKhoan == null || hanhKhach == null) {
            return false;
        }

        String tenDangNhap = taiKhoan.getTenDangNhap();
        String matKhau = taiKhoan.getMatKhau();

        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return false;
        }

        if (matKhau == null || matKhau.isEmpty()) {
            return false;
        }

        if (taiKhoanDataService.tonTaiTenDangNhap(tenDangNhap)) {
            return false;
        }

        taiKhoan.setVaiTro(VaiTro.HANH_KHACH);
        taiKhoan.setTrangThai(true);
        hanhKhach.setTaiKhoan(taiKhoan);

        if (hanhKhach.getMaHanhKhach() == null
                || hanhKhach.getMaHanhKhach().trim().isEmpty()) {
            hanhKhach.setMaHanhKhach(
                    hanhKhachService.sinhMaHanhKhachMoi()
            );
        }

        if (hanhKhachService.tonTaiSoDienThoai(hanhKhach.getSoDienThoai())
                || hanhKhachService.tonTaiEmail(hanhKhach.getEmail())) {
            return false;
        }

        if (!taiKhoanDataService.themTaiKhoan(taiKhoan)) {
            return false;
        }

        if (!hanhKhachService.themHanhKhach(hanhKhach)) {
            List<TaiKhoan> danhSach =
                    taiKhoanDataService.docDanhSachTaiKhoan();
            danhSach.removeIf(x -> x.getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap));
            taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
            return false;
        }

        return true;
    }

    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()
                || matKhau == null || matKhau.isEmpty()) {
            return null;
        }

        TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null || !taiKhoan.isTrangThai()) {
            return null;
        }

        if (!taiKhoan.getMatKhau().equals(matKhau)) {
            return null;
        }

        return taiKhoan;
    }

    public boolean doiMatKhau(
            String tenDangNhap,
            String matKhauCu,
            String matKhauMoi) {

        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()
                || matKhauCu == null || matKhauCu.isEmpty()
                || matKhauMoi == null || matKhauMoi.isEmpty()) {
            return false;
        }

        TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null || !taiKhoan.isTrangThai()
                || !taiKhoan.getMatKhau().equals(matKhauCu)) {
            return false;
        }

        taiKhoan.setMatKhau(matKhauMoi);

        List<TaiKhoan> danhSach =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap.trim())) {
                danhSach.set(i, taiKhoan);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
                return true;
            }
        }

        return false;
    }

    public boolean khoaTaiKhoan(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return false;
        }

        TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null) {
            return false;
        }

        taiKhoan.setTrangThai(false);

        List<TaiKhoan> danhSach =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap.trim())) {
                danhSach.set(i, taiKhoan);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
                return true;
            }
        }

        return false;
    }

    public boolean moKhoaTaiKhoan(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return false;
        }

        TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null) {
            return false;
        }

        taiKhoan.setTrangThai(true);

        List<TaiKhoan> danhSach =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap.trim())) {
                danhSach.set(i, taiKhoan);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
                return true;
            }
        }

        return false;
    }
}
