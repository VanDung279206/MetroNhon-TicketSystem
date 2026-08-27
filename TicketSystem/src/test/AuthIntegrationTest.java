package test;

import controller.AdminController;
import controller.AuthController;
import controller.MuaVeController;
import data.TaiKhoanDataService;
import model.TaiKhoan;
import model.VaiTro;

public class AuthIntegrationTest {

    public static void main(String[] args) {
        AuthController authController = new AuthController();

        boolean dangKyThanhCong = authController.dangKy(
                "integration_user",
                "123456",
                "Nguyễn Văn Dũng",
                "0987654321",
                "integration@example.com"
        );

        kiemTra(dangKyThanhCong, "Phải đăng ký được tài khoản mới");
        kiemTra(
                authController.getDanhSachTaiKhoan().size() == 1,
                "File phải có đúng một tài khoản"
        );
        kiemTra(
                authController.getDanhSachHanhKhach().size() == 1,
                "File phải có đúng một hành khách"
        );

        boolean dangKyTrungTaiKhoan = authController.dangKy(
                "integration_user",
                "matKhauKhac",
                "Người dùng khác",
                "0911111111",
                "nguoidungkhac@example.com"
        );

        kiemTra(
                !dangKyTrungTaiKhoan,
                "Không được đăng ký trùng tên đăng nhập"
        );

        boolean dangKyTrungSoDienThoai = authController.dangKy(
                "integration_user_2",
                "123456",
                "Người dùng khác",
                "0987654321",
                "nguoidung2@example.com"
        );

        kiemTra(
                !dangKyTrungSoDienThoai,
                "Không được đăng ký trùng số điện thoại"
        );

        // Tạo controller mới để mô phỏng việc tắt và mở lại chương trình
        AuthController authSauKhiKhoiDongLai = new AuthController();

        TaiKhoan dangNhapThanhCong = authSauKhiKhoiDongLai.dangNhap(
                "integration_user",
                "123456"
        );

        kiemTra(
                dangNhapThanhCong != null,
                "Tài khoản phải đăng nhập được sau khi khởi động lại"
        );
        kiemTra(
                authSauKhiKhoiDongLai.getHanhKhachDangNhap() != null,
                "Phải tìm được hành khách của tài khoản đăng nhập"
        );

        boolean doiMatKhauThanhCong = authSauKhiKhoiDongLai.doiMatKhau(
                "123456",
                "654321"
        );

        kiemTra(
                doiMatKhauThanhCong,
                "Phải đổi và lưu được mật khẩu mới"
        );

        AuthController authSauKhiDoiMatKhau = new AuthController();

        kiemTra(
                authSauKhiDoiMatKhau.dangNhap(
                        "integration_user",
                        "123456"
                ) == null,
                "Mật khẩu cũ không được đăng nhập"
        );

        kiemTra(
                authSauKhiDoiMatKhau.dangNhap(
                        "integration_user",
                        "654321"
                ) != null,
                "Mật khẩu mới phải đăng nhập được"
        );

        AdminController khongPhaiAdmin = new AdminController(
                authSauKhiDoiMatKhau,
                new MuaVeController()
        );

        kiemTra(
                !khongPhaiAdmin.khoaTaiKhoan("integration_user"),
                "Hành khách không được phép khóa tài khoản"
        );

        TaiKhoan admin = new TaiKhoan(
                "admin_test",
                "admin123",
                VaiTro.ADMIN,
                true
        );

        kiemTra(
                new TaiKhoanDataService().themTaiKhoan(admin),
                "Phải tạo được tài khoản admin kiểm thử"
        );

        AuthController authAdmin = new AuthController();
        kiemTra(
                authAdmin.dangNhap("admin_test", "admin123") != null,
                "Admin phải đăng nhập được"
        );

        AdminController adminController = new AdminController(
                authAdmin,
                new MuaVeController()
        );

        kiemTra(
                adminController.khoaTaiKhoan("integration_user"),
                "Phải khóa và lưu được trạng thái tài khoản"
        );

        AuthController authSauKhiKhoa = new AuthController();

        kiemTra(
                authSauKhiKhoa.dangNhap(
                        "integration_user",
                        "654321"
                ) == null,
                "Tài khoản bị khóa không được đăng nhập sau khi khởi động lại"
        );

        kiemTra(
                authSauKhiKhoa.dangNhap("admin_test", "admin123") != null,
                "Admin phải đăng nhập được để mở khóa tài khoản"
        );

        AdminController adminMoKhoa = new AdminController(
                authSauKhiKhoa,
                new MuaVeController()
        );

        kiemTra(
                adminMoKhoa.moKhoaTaiKhoan("integration_user"),
                "Phải mở khóa và lưu được trạng thái tài khoản"
        );

        AuthController authSauKhiMoKhoa = new AuthController();

        kiemTra(
                authSauKhiMoKhoa.dangNhap(
                        "integration_user",
                        "654321"
                ) != null,
                "Tài khoản phải đăng nhập được sau khi mở khóa"
        );

        System.out.println("TẤT CẢ KIỂM THỬ AUTH ĐỀU THÀNH CÔNG");
    }

    private static void kiemTra(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }
}