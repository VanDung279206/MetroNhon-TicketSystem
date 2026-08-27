package test;

import controller.AuthController;
import controller.MuaVeController;
import data.GaDataService;
import data.HanhKhachDataService;
import data.TaiKhoanDataService;
import data.VeDataService;
import exception.GaKhongHopLeException;
import exception.KhongDuTienException;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.TaiKhoan;
import model.VeMetro;
import utils.Constants;
import utils.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

public class ViTienIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path thuMucDuLieu = Files.createTempDirectory("metro-wallet-test-");
        System.setProperty("metro.data.dir", thuMucDuLieu.toString());

        try {
            kiemTraDuLieuTaiKhoanCu();
            khoiTaoDuLieu();

            AuthController authController = new AuthController();
            kiemTra(authController.dangKy(
                            "wallet_user", "123456", "Khách ví Metro",
                            "0901234567", "wallet@test.local"),
                    "Phải đăng ký được tài khoản kiểm thử"
            );
            kiemTra(authController.dangNhap("wallet_user", "123456") != null,
                    "Phải đăng nhập được tài khoản kiểm thử");

            HanhKhach hanhKhach = authController.getHanhKhachDangNhap();
            MuaVeController muaVeController = new MuaVeController();
            kiemTra(muaVeController.getSoDu(hanhKhach) == 0,
                    "Tài khoản mới phải có số dư bằng 0");

            double soDuSauNap = muaVeController.napTien(hanhKhach, 100_000);
            kiemTra(soDuSauNap == 100_000,
                    "Nạp 100.000 phải cho số dư 100.000");

            TaiKhoan taiKhoanDaLuu = new TaiKhoanDataService()
                    .timTheoTenDangNhap("wallet_user");
            kiemTra(taiKhoanDaLuu != null && taiKhoanDaLuu.getSoDu() == 100_000,
                    "Số dư nạp phải được lưu xuống file");

            muaVeController.muaVeLuot(hanhKhach, "G01", "G08");
            kiemTra(hanhKhach.getTaiKhoan().getSoDu() == 85_000,
                    "Mua vé lượt 15.000 phải còn 85.000");
            kiemTra(new VeDataService().docDanhSachVe().size() == 1,
                    "Mua vé thành công phải lưu đúng một vé");

            boolean daBaoKhongDuTien = false;
            try {
                muaVeController.muaVeThang(
                        hanhKhach, LoaiVeThang.PHO_THONG
                );
            } catch (KhongDuTienException e) {
                daBaoKhongDuTien = true;
                kiemTra(e.getSoDuHienTai() == 85_000,
                        "Exception phải chứa số dư hiện tại");
            }
            kiemTra(daBaoKhongDuTien,
                    "Thiếu tiền phải ném KhongDuTienException");
            kiemTra(new VeDataService().docDanhSachVe().size() == 1,
                    "Thanh toán thất bại không được tạo thêm vé");
            kiemTra(hanhKhach.getTaiKhoan().getSoDu() == 85_000,
                    "Thanh toán thất bại không được trừ tiền");

            boolean daBaoGaKhongHopLe = false;
            try {
                muaVeController.muaVeLuot(hanhKhach, "G99", "G08");
            } catch (GaKhongHopLeException e) {
                daBaoGaKhongHopLe = true;
            }
            kiemTra(daBaoGaKhongHopLe,
                    "Mã ga sai phải ném GaKhongHopLeException");
            kiemTra(hanhKhach.getTaiKhoan().getSoDu() == 85_000,
                    "Ga sai không được trừ tiền");

            muaVeController.napTien(hanhKhach, 200_000);
            muaVeController.muaVeThang(hanhKhach, LoaiVeThang.PHO_THONG);
            kiemTra(hanhKhach.getTaiKhoan().getSoDu() == 5_000,
                    "Nạp thêm và mua vé tháng phải trừ đúng số tiền");

            AuthController authSauKhiMoLai = new AuthController();
            kiemTra(authSauKhiMoLai.dangNhap("wallet_user", "123456") != null,
                    "Tài khoản phải đăng nhập được sau khi mở lại");
            kiemTra(authSauKhiMoLai.getTaiKhoanDangNhap().getSoDu() == 5_000,
                    "Số dư còn lại phải giữ nguyên sau khi mở lại");

            System.out.println("TẤT CẢ KIỂM THỬ VÍ TIỀN ĐỀU THÀNH CÔNG");
        } finally {
            System.clearProperty("metro.data.dir");
            xoaThuMucTam(thuMucDuLieu);
        }
    }

    private static void kiemTraDuLieuTaiKhoanCu() {
        FileHandler.ghiFile(
                Constants.FILE_TAI_KHOAN,
                Collections.singletonList(
                        "legacy_user|123456|HANH_KHACH|true"
                )
        );
        TaiKhoan taiKhoanCu = new TaiKhoanDataService()
                .timTheoTenDangNhap("legacy_user");
        kiemTra(taiKhoanCu != null && taiKhoanCu.getSoDu() == 0,
                "Dữ liệu tài khoản 4 trường phải có số dư mặc định bằng 0");
    }

    private static void khoiTaoDuLieu() {
        new TaiKhoanDataService().luuDanhSachTaiKhoan(
                Collections.<TaiKhoan>emptyList()
        );
        new HanhKhachDataService().luuDanhSachHanhKhach(
                Collections.<HanhKhach>emptyList()
        );
        new VeDataService().luuDanhSachVe(
                Collections.<VeMetro>emptyList()
        );
        new GaDataService().luuDanhSachGa(Arrays.asList(
                new Ga("G01", "Nhổn", "Bắc Từ Liêm", 1),
                new Ga("G08", "Cầu Giấy", "Cầu Giấy", 8)
        ));
    }

    private static void kiemTra(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static void xoaThuMucTam(Path thuMuc) throws IOException {
        try (Stream<Path> danhSach = Files.walk(thuMuc)) {
            danhSach.sorted(Comparator.reverseOrder()).forEach(duongDan -> {
                try {
                    Files.deleteIfExists(duongDan);
                } catch (IOException e) {
                    throw new IllegalStateException(
                            "Không thể xóa dữ liệu kiểm thử: " + duongDan, e
                    );
                }
            });
        }
    }
}