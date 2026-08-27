package view;

import controller.AdminController;
import controller.AuthController;
import controller.MuaVeController;
import model.TaiKhoan;
import model.VaiTro;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

public class TrangChuView extends JFrame {
    private static final String DANG_NHAP = "DANG_NHAP";
    private static final String DANG_KY = "DANG_KY";
    private static final String HANH_KHACH = "HANH_KHACH";
    private static final String ADMIN = "ADMIN";

    private final AuthController authController;
    private final MuaVeController muaVeController;
    private final AdminController adminController;
    private final CardLayout cardLayout;
    private final JPanel root;
    private AdminView adminView;
    private HanhKhachView hanhKhachView;

    public TrangChuView() {
        super("Metro Nhổn – Hệ thống vé điện tử");
        authController = new AuthController();
        muaVeController = new MuaVeController();
        adminController = new AdminController(authController, muaVeController);

        cardLayout = new CardLayout();
        root = new JPanel(cardLayout);
        root.add(new DangNhapView(authController, this::sauKhiDangNhap,
                this::hienThiDangKy), DANG_NHAP);
        root.add(new DangKyView(authController, this::hienThiDangNhap), DANG_KY);

        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        hienThiDangNhap();
    }

    private void sauKhiDangNhap() {
        TaiKhoan taiKhoan = authController.getTaiKhoanDangNhap();
        if (taiKhoan == null) {
            return;
        }

        if (taiKhoan.getVaiTro() == VaiTro.ADMIN) {
            xoaManHinhPhienCu();
            adminView = new AdminView(adminController, taiKhoan, this::dangXuat);
            root.add(adminView, ADMIN);
            cardLayout.show(root, ADMIN);
        } else {
            xoaManHinhPhienCu();
            hanhKhachView = new HanhKhachView(authController, muaVeController,
                    this::dangXuat);
            root.add(hanhKhachView, HANH_KHACH);
            cardLayout.show(root, HANH_KHACH);
        }
        root.revalidate();
        root.repaint();
    }

    private void dangXuat() {
        authController.dangXuat();
        xoaManHinhPhienCu();
        hienThiDangNhap();
    }

    private void xoaManHinhPhienCu() {
        if (adminView != null) {
            root.remove(adminView);
            adminView = null;
        }
        if (hanhKhachView != null) {
            root.remove(hanhKhachView);
            hanhKhachView = null;
        }
    }

    private void hienThiDangNhap() {
        cardLayout.show(root, DANG_NHAP);
    }

    private void hienThiDangKy() {
        cardLayout.show(root, DANG_KY);
    }
}