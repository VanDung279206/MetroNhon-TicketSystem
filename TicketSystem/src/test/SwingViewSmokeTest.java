package test;

import controller.AdminController;
import controller.AuthController;
import controller.MuaVeController;
import data.GaDataService;
import data.TaiKhoanDataService;
import model.Ga;
import model.TaiKhoan;
import model.VaiTro;
import view.AdminView;
import view.DangKyView;
import view.DangNhapView;
import view.HanhKhachView;
import view.Theme;

import javax.swing.SwingUtilities;
import java.util.Arrays;

public class SwingViewSmokeTest {
    public static void main(String[] args) throws Exception {
        Theme.install();
        new GaDataService().luuDanhSachGa(Arrays.asList(
                new Ga("G01", "Nhổn", "Bắc Từ Liêm", 1),
                new Ga("G08", "Cầu Giấy", "Cầu Giấy", 8)
        ));

        AuthController auth = new AuthController();
        boolean registered = auth.dangKy(
                "ui_test", "123456", "Người dùng giao diện",
                "0912345678", "ui@test.local"
        );
        kiemTra(registered, "Không tạo được tài khoản cho UI test");
        kiemTra(auth.dangNhap("ui_test", "123456") != null,
                "Không đăng nhập được tài khoản cho UI test");

        MuaVeController muaVe = new MuaVeController();
        SwingUtilities.invokeAndWait(() -> {
            new DangNhapView(auth, () -> { }, () -> { });
            new DangKyView(auth, () -> { });
            new HanhKhachView(auth, muaVe, () -> { });
        });

        auth.dangXuat();
        TaiKhoan admin = new TaiKhoan("admin", "admin123", VaiTro.ADMIN, true);
        kiemTra(new TaiKhoanDataService().themTaiKhoan(admin),
                "Không tạo được admin cho UI test");
        kiemTra(auth.dangNhap("admin", "admin123") != null,
                "Không đăng nhập được admin cho UI test");

        AdminController adminController = new AdminController(auth, muaVe);
        SwingUtilities.invokeAndWait(() ->
                new AdminView(adminController, auth.getTaiKhoanDangNhap(), () -> { })
        );

        System.out.println("TẤT CẢ KIỂM THỬ GIAO DIỆN SWING ĐỀU THÀNH CÔNG");
    }

    private static void kiemTra(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
