package controller;

import model.TaiKhoan;
import model.VaiTro;
import model.VeMetro;

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
        return authController.getDanhSachTaiKhoan();
    }

    //Khóa một tài khoản hành khách
    public boolean khoaTaiKhoan(String tenDangNhap){
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
        return muaVeController.getDanhSachVeDaBan();
    }

    // Tính tổng doanh thu từ tất cả vé đã bán.
    public double tinhTongDoanhThu(){
        double tongDoanhThu = 0;

        for (VeMetro ve : getDanhSachVeDaBan()){
            tongDoanhThu += ve.getGiaVe();
        }

        return tongDoanhThu;
    }
}