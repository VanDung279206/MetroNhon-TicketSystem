package test;

import controller.AuthController;
import controller.MuaVeController;
import data.GaDataService;
import data.HanhKhachDataService;
import data.TaiKhoanDataService;
import data.VeDataService;
import exception.VeKhongTheSuDungException;
import exception.VeThangDangHoatDongException;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.LuotSuDungVe;
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

public class ChucNangBoSungIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path thuMucDuLieu = Files.createTempDirectory("metro-final-feature-test-");
        System.setProperty("metro.data.dir", thuMucDuLieu.toString());

        try {
            khoiTaoDuLieu();

            AuthController auth = new AuthController();
            kiemTra(auth.dangKy(
                            "feature_user", "123456", "Người dùng cũ",
                            "0901111111", "old@test.local"),
                    "Phải đăng ký được tài khoản kiểm thử"
            );
            kiemTra(auth.dangNhap("feature_user", "123456") != null,
                    "Phải đăng nhập được tài khoản kiểm thử");

            kiemTra(auth.capNhatThongTinHanhKhach(
                            "Người dùng mới", "0902222222", "new@test.local"),
                    "Phải cập nhật được thông tin hành khách"
            );
            AuthController authSauKhiMoLai = new AuthController();
            kiemTra(authSauKhiMoLai.dangNhap("feature_user", "123456") != null,
                    "Phải đăng nhập được sau khi cập nhật hồ sơ");
            HanhKhach hanhKhach = authSauKhiMoLai.getHanhKhachDangNhap();
            kiemTra("Người dùng mới".equals(hanhKhach.getHoTen())
                            && "0902222222".equals(hanhKhach.getSoDienThoai())
                            && "new@test.local".equals(hanhKhach.getEmail()),
                    "Thông tin mới phải còn sau khi mở lại ứng dụng"
            );

            MuaVeController controller = new MuaVeController();
            controller.napTien(hanhKhach, 1_000_000);
            VeThang veThang = controller.muaVeThang(
                    hanhKhach, LoaiVeThang.PHO_THONG
            );
            double soDuSauVeThang = hanhKhach.getTaiKhoan().getSoDu();

            boolean daChanMuaTrung = false;
            try {
                controller.muaVeThang(hanhKhach, LoaiVeThang.UU_DAI);
            } catch (VeThangDangHoatDongException e) {
                daChanMuaTrung = true;
            }
            kiemTra(daChanMuaTrung,
                    "Vé tháng còn hạn phải chặn mua vé tháng tiếp theo");
            kiemTra(hanhKhach.getTaiKhoan().getSoDu() == soDuSauVeThang,
                    "Mua trùng vé tháng không được trừ tiền");

            VeLuot veLuot = controller.muaVeLuot(
                    hanhKhach, "G01", "G08"
            );
            LuotSuDungVe luotDau = controller.suDungVe(
                    hanhKhach, veLuot.getMaVe(), null, null
            );
            kiemTra(luotDau != null,
                    "Phải tạo được lịch sử sử dụng vé lượt");
            VeLuot veLuotDaLuu = (VeLuot) new VeDataService()
                    .timTheoMaVe(veLuot.getMaVe());
            kiemTra(veLuotDaLuu != null && !veLuotDaLuu.isTrangThai(),
                    "Vé lượt phải hết hiệu lực sau khi sử dụng");

            boolean daChanDungLai = false;
            try {
                controller.suDungVe(
                        hanhKhach, veLuot.getMaVe(), null, null
                );
            } catch (VeKhongTheSuDungException e) {
                daChanDungLai = true;
            }
            kiemTra(daChanDungLai,
                    "Vé lượt đã dùng không được sử dụng lần hai");

            controller.suDungVe(
                    hanhKhach, veThang.getMaVe(), "G01", "G08"
            );
            controller.suDungVe(
                    hanhKhach, veThang.getMaVe(), "G08", "G01"
            );
            VeThang veThangDaLuu = (VeThang) new VeDataService()
                    .timTheoMaVe(veThang.getMaVe());
            kiemTra(veThangDaLuu != null && veThangDaLuu.isConHieuLuc(),
                    "Vé tháng vẫn phải còn hiệu lực sau nhiều lần sử dụng");

            List<LuotSuDungVe> lichSu =
                    new MuaVeController().getDanhSachLuotSuDungCuaHanhKhach(
                            hanhKhach.getMaHanhKhach()
                    );
            kiemTra(lichSu.size() == 3,
                    "Phải lưu đủ ba lượt sử dụng sau khi mở lại");
            kiemTra(controller.getSoLuotSuDung(veThang.getMaVe()) == 2,
                    "Vé tháng phải ghi nhận hai lượt sử dụng");

            System.out.println(
                    "TẤT CẢ KIỂM THỬ CHỨC NĂNG BỔ SUNG ĐỀU THÀNH CÔNG"
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