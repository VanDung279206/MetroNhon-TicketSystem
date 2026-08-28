package test;

import controller.AuthController;
import controller.MuaVeController;
import data.GaDataService;
import data.HanhKhachDataService;
import data.PhieuHuyVeDataService;
import data.TaiKhoanDataService;
import data.VeDataService;
import exception.VeKhongTheHuyException;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.PhieuHuyVe;
import model.TaiKhoan;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;
import utils.Constants;
import utils.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class HuyVeIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path thuMucDuLieu = Files.createTempDirectory("metro-cancel-test-");
        System.setProperty("metro.data.dir", thuMucDuLieu.toString());

        try {
            khoiTaoDuLieu();
            AuthController auth = new AuthController();
            kiemTra(auth.dangKy(
                            "cancel_user", "123456", "Người hủy vé",
                            "0903333333", "cancel@test.local"),
                    "Phải tạo được tài khoản kiểm thử"
            );
            kiemTra(auth.dangNhap("cancel_user", "123456") != null,
                    "Phải đăng nhập được");

            HanhKhach hanhKhach = auth.getHanhKhachDangNhap();
            MuaVeController controller = new MuaVeController();
            controller.napTien(hanhKhach, 1_500_000);

            double soDuTruocVeLuot = controller.getSoDu(hanhKhach);
            VeLuot veLuot = controller.muaVeLuot(
                    hanhKhach, "G01", "G08"
            );
            double tienHoanVeLuot = Math.round(veLuot.getGiaVe() * 0.90);
            PhieuHuyVe huyVeLuot = controller.huyVe(
                    hanhKhach, veLuot.getMaVe(), "Thay đổi kế hoạch"
            );
            kiemTraBang(tienHoanVeLuot, huyVeLuot.getSoTienHoan(),
                    "Vé lượt phải hoàn đúng 90%");
            kiemTraBang(
                    soDuTruocVeLuot - veLuot.getGiaVe() + tienHoanVeLuot,
                    controller.getSoDu(hanhKhach),
                    "Tiền hoàn vé lượt phải được cộng vào ví"
            );
            VeMetro veLuotDaLuu = new VeDataService()
                    .timTheoMaVe(veLuot.getMaVe());
            kiemTra(veLuotDaLuu != null && !veLuotDaLuu.isTrangThai(),
                    "Vé đã hủy phải bị vô hiệu hóa");

            double soDuSauLanHuyDau = controller.getSoDu(hanhKhach);
            kiemTraBiTuChoi(
                    () -> controller.huyVe(
                            hanhKhach, veLuot.getMaVe(), "Hủy lại"),
                    "Không được hủy và hoàn tiền hai lần"
            );
            kiemTraBang(soDuSauLanHuyDau, controller.getSoDu(hanhKhach),
                    "Hủy lần hai không được làm thay đổi số dư");

            VeThang veThang = controller.muaVeThang(
                    hanhKhach, LoaiVeThang.PHO_THONG
            );
            double tienHoanVeThang = Math.round(veThang.getGiaVe() * 0.80);
            PhieuHuyVe huyVeThang = controller.huyVe(
                    hanhKhach, veThang.getMaVe(), "Không còn nhu cầu"
            );
            kiemTraBang(tienHoanVeThang, huyVeThang.getSoTienHoan(),
                    "Vé tháng phải hoàn đúng 80%");

            VeThang veThangMoi = controller.muaVeThang(
                    hanhKhach, LoaiVeThang.UU_DAI
            );
            controller.suDungVe(
                    hanhKhach, veThangMoi.getMaVe(), "G01", "G08"
            );
            kiemTraBiTuChoi(
                    () -> controller.huyVe(
                            hanhKhach, veThangMoi.getMaVe(), "Đã dùng"),
                    "Vé tháng đã dùng không được hủy"
            );

            VeLuot veLuotDaDung = controller.muaVeLuot(
                    hanhKhach, "G08", "G01"
            );
            controller.suDungVe(
                    hanhKhach, veLuotDaDung.getMaVe(), null, null
            );
            kiemTraBiTuChoi(
                    () -> controller.huyVe(
                            hanhKhach, veLuotDaDung.getMaVe(), "Đã dùng"),
                    "Vé lượt đã dùng không được hủy"
            );

            List<PhieuHuyVe> lichSu = new PhieuHuyVeDataService()
                    .timTheoMaHanhKhach(hanhKhach.getMaHanhKhach());
            kiemTra(lichSu.size() == 2,
                    "Chỉ hai giao dịch hủy hợp lệ được lưu");

            System.out.println(
                    "TẤT CẢ KIỂM THỬ HỦY VÉ ĐỀU THÀNH CÔNG"
            );
        } finally {
            System.clearProperty("metro.data.dir");
            xoaThuMucTam(thuMucDuLieu);
        }
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
        FileHandler.ghiFile(
                Constants.FILE_LICH_SU_SU_DUNG,
                Collections.<String>emptyList()
        );
        FileHandler.ghiFile(
                Constants.FILE_LICH_SU_HUY_VE,
                Collections.<String>emptyList()
        );
        new GaDataService().luuDanhSachGa(Arrays.asList(
                new Ga("G01", "Nhổn", "Bắc Từ Liêm", 1),
                new Ga("G08", "Cầu Giấy", "Cầu Giấy", 8)
        ));
    }

    private static void kiemTraBiTuChoi(Runnable thaoTac, String thongBao) {
        boolean daTuChoi = false;
        try {
            thaoTac.run();
        } catch (VeKhongTheHuyException e) {
            daTuChoi = true;
        }
        kiemTra(daTuChoi, thongBao);
    }

    private static void kiemTraBang(double mongDoi, double thucTe,
                                    String thongBao) {
        kiemTra(Math.abs(mongDoi - thucTe) < 0.001, thongBao);
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