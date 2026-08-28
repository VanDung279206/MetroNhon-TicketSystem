package controller;

import model.TaiKhoan;
import model.VaiTro;
import model.LuotSuDungVe;
import model.PhieuHuyVe;
import model.VeMetro;

import java.util.Collections;
import java.util.List;

/* Chức năng:
 * Xem danh sách tài khoản
 * khóa/mở tài khoản
 * xem danh sách vé đã bán
 * tính tổng doanh thu
 */

public class AdminController {

    private final AuthController authController;
    private final MuaVeController muaVeController;

    public AdminController(AuthController authController, MuaVeController muaVeController){
        if (authController == null || muaVeController == null){
            throw new IllegalArgumentException("Controller không được để trống");
        }
        this.authController = authController;
        this.muaVeController = muaVeController;
    }

    // Lấy danh sách tài khoản
    public List<TaiKhoan> getDanhSachTaiKhoan(){
        if (!coQuyenQuanTri()) {
            return Collections.emptyList();
        }
        return authController.getDanhSachTaiKhoan();
    }

    //Khóa một tài khoản hành khách
    public boolean khoaTaiKhoan(String tenDangNhap){
        if (!coQuyenQuanTri()) {
            return false;
        }
        TaiKhoan taiKhoan = authController.timTaiKhoan(tenDangNhap);

        // Không tìm thấy tài khoản
        if (taiKhoan == null){
            return false;
        }

        //Không cho khóa tài khoản Admin
        if (taiKhoan.getVaiTro() == VaiTro.ADMIN){
            return false;
        }

        //Tài khoản đã bị khóa trước đó
        if (!taiKhoan.isTrangThai()){
            return false;
        }

        return authController.capNhatTrangThaiTaiKhoan(
                tenDangNhap,
                false
        );
    }

    // Mở khóa tài khoản
    public boolean moKhoaTaiKhoan(String tenDangNhap){
        if (!coQuyenQuanTri()) {
            return false;
        }
        TaiKhoan taiKhoan = authController.timTaiKhoan(tenDangNhap);

        // Không tìm thấy tài khoản
        if (taiKhoan == null){
            return false;
        }

        // Tài khoản đang hoạt động thì không cần mở khóa
        if (taiKhoan.isTrangThai()){
            return false;
        }

        return authController.capNhatTrangThaiTaiKhoan(
                tenDangNhap,
                true
        );
    }

    // Lấy danh sách vé đã bán.
    public List<VeMetro> getDanhSachVeDaBan(){
        if (!coQuyenQuanTri()) {
            return Collections.emptyList();
        }
        return muaVeController.getDanhSachVeDaBan();
    }

    public List<LuotSuDungVe> getDanhSachLuotSuDung() {
        if (!coQuyenQuanTri()) {
            return Collections.emptyList();
        }
        return muaVeController.getDanhSachLuotSuDung();
    }

    public int getSoLuotSuDung(String maVe) {
        if (!coQuyenQuanTri()) {
            return 0;
        }
        return muaVeController.getSoLuotSuDung(maVe);
    }

    public boolean daHuyVe(String maVe) {
        return coQuyenQuanTri() && muaVeController.daHuyVe(maVe);
    }

    public String getTenGa(String maGa) {
        if (!coQuyenQuanTri()) {
            return maGa;
        }
        return muaVeController.getTenGa(maGa);
    }

    public List<PhieuHuyVe> getDanhSachPhieuHuyVe() {
        if (!coQuyenQuanTri()) {
            return Collections.emptyList();
        }
        return muaVeController.getDanhSachPhieuHuyVe();
    }

    public double tinhTongTienHoan() {
        double tongTienHoan = 0;
        for (PhieuHuyVe phieu : getDanhSachPhieuHuyVe()) {
            tongTienHoan += phieu.getSoTienHoan();
        }
        return tongTienHoan;
    }

    // Tính doanh thu thực sau khi trừ các khoản hoàn vé.
    public double tinhTongDoanhThu(){
        double tongDoanhThu = 0;

        for (VeMetro ve : getDanhSachVeDaBan()){
            tongDoanhThu += ve.getGiaVe();
        }

        return tongDoanhThu - tinhTongTienHoan();
    }

    private boolean coQuyenQuanTri() {
        TaiKhoan taiKhoanDangNhap = authController.getTaiKhoanDangNhap();
        return taiKhoanDangNhap != null
                && taiKhoanDangNhap.isTrangThai()
                && taiKhoanDangNhap.getVaiTro() == VaiTro.ADMIN;
    }
}