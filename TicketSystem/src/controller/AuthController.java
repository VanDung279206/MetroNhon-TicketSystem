package controller;

import model.HanhKhach;
import model.TaiKhoan;
import model.VaiTro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Chức năng:
* Đăng ký
* Đăng nhập
* Đổi mật khẩu
* Lưu tài khoản đang đăng nhập
 */

public class AuthController{
    private final List<TaiKhoan> danhSachTaiKhoan;
    private final List<HanhKhach> danhSachHanhKhach;

    // Thông tin người dùng đang đăng nhập
    private TaiKhoan taiKhoanDangNhap;
    private HanhKhach hanhKhachDangNhap;

    // Dùng để sinh mã hành khách tự động
    private int soThuTuHanhKhach;

    public AuthController(){
        danhSachTaiKhoan = new ArrayList<>();
        danhSachHanhKhach = new ArrayList<>();
        soThuTuHanhKhach = 1;
    }

    // Đăng ký một tài khoản hành khách mới
    public boolean dangKy(String tenDangNhap, String matKhau, String hoTen, String soDienThoai, String email){
        //Kiểm tra các thông tin bắt buộc
        if (isRong(tenDangNhap) || isRong(matKhau) || isRong(hoTen) || isRong(soDienThoai) || isRong(email)){
            return false;
        }

        // Xóa khoảng trắng thừa ở đầu và cuối
        tenDangNhap = tenDangNhap.trim();
        hoTen = hoTen.trim();
        soDienThoai = soDienThoai.trim();
        email = email.trim();

        // Không cho phép trùng tên đăng nhập
        if (timTaiKhoan(tenDangNhap) != null){
            return false;
        }

        TaiKhoan taiKhoan = new TaiKhoan(tenDangNhap, matKhau, VaiTro.HANH_KHACH, true);
        HanhKhach hanhKhach = new HanhKhach(sinhMaHanhKhach(), hoTen, soDienThoai, email, taiKhoan);

        // Thêm thông tin mới vào danh sách
        danhSachTaiKhoan.add(taiKhoan);
        danhSachHanhKhach.add(hanhKhach);

        return true;
    }

    public TaiKhoan dangNhap(String tenDangNhap, String matKhau){
        if (isRong(tenDangNhap) || isRong(matKhau)){
            return  null;
        }

        TaiKhoan taiKhoan = timTaiKhoan(tenDangNhap.trim());

        if (taiKhoan == null){
            return null;
        }

        if (!taiKhoan.isTrangThai()){
            return null;
        }

        if(!taiKhoan.getMatKhau().equals(matKhau)){
            return null;
        }

        // Lưu thông tin phiên đăng nhập hiện tại
        taiKhoanDangNhap = taiKhoan;

        hanhKhachDangNhap = timHanhKhachTheoTaiKhoan(taiKhoan);

        return taiKhoanDangNhap;
    }

    public void dangXuat(){
        taiKhoanDangNhap = null;
        hanhKhachDangNhap = null;
    }


    public boolean doiMatKhau(String matKhauCu, String matKhauMoi){
        // Phải đăng nhập trước
        if (taiKhoanDangNhap == null){
            return false;
        }

        // Hai mật khẩu không được để trống
        if (isRong(matKhauCu) || isRong(matKhauMoi)){
            return false;
        }

        // Kiểm tra mật khẩu cũ
        if (!taiKhoanDangNhap.getMatKhau().equals(matKhauCu)){
            return false;
        }

        // Mật khẩu mới phải khác mật khẩu cũ
        if (matKhauCu.equals(matKhauMoi)){
            return false;
        }

        taiKhoanDangNhap.setMatKhau(matKhauMoi);

        return true;
    }

    public TaiKhoan timTaiKhoan(String tenDangNhap){
        if (isRong(tenDangNhap)){
            return null;
        }

        for (TaiKhoan taiKhoan : danhSachTaiKhoan){
            if (taiKhoan.getTenDangNhap().equalsIgnoreCase(tenDangNhap.trim())){
                return taiKhoan;
            }
        }
        return null;
    }

    public TaiKhoan getTaiKhoanDangNhap(){
        return taiKhoanDangNhap;
    }

    public HanhKhach getHanhKhachDangNhap(){
        return hanhKhachDangNhap;
    }

    public boolean isDaDangNhap() {
        return taiKhoanDangNhap != null;
    }

    public List<TaiKhoan> getDanhSachTaiKhoan() {
        return Collections.unmodifiableList(
                danhSachTaiKhoan
        );
    }

    public List<HanhKhach> getDanhSachHanhKhach() {
        return Collections.unmodifiableList(
                danhSachHanhKhach
        );
    }

    private HanhKhach timHanhKhachTheoTaiKhoan(
            TaiKhoan taiKhoan
    ) {
        for (HanhKhach hanhKhach : danhSachHanhKhach) {
            if (hanhKhach.getTaiKhoan() == taiKhoan) {
                return hanhKhach;
            }
        }

        return null;
    }

    private String sinhMaHanhKhach(){
        return String.format("HK%03d", soThuTuHanhKhach++);
    }

    private  boolean isRong(String giaTri){
        return giaTri == null || giaTri.trim().isEmpty();
    }
}