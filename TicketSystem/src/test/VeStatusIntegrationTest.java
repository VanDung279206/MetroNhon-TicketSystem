package test;

import controller.MuaVeController;
import data.GaDataService;
import data.HanhKhachDataService;
import data.TaiKhoanDataService;
import data.VeDataService;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.TaiKhoan;
import model.VaiTro;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;
import service.VeLuotService;
import service.VeThangService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

public class VeStatusIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path thuMucDuLieu = Files.createTempDirectory("metro-ticket-status-test-");
        System.setProperty("metro.data.dir", thuMucDuLieu.toString());

        try {
            TaiKhoan taiKhoan = new TaiKhoan(
                    "khach_ve", "123456", VaiTro.HANH_KHACH, true
            );
            HanhKhach hanhKhach = new HanhKhach(
                    "HK001", "Khách kiểm thử", "0900000000",
                    "khachve@test.local", taiKhoan
            );

            kiemTra(new TaiKhoanDataService().themTaiKhoan(taiKhoan),
                    "Phải lưu được tài khoản kiểm thử");
            kiemTra(new HanhKhachDataService().themHanhKhach(hanhKhach),
                    "Phải lưu được hành khách kiểm thử");
            new GaDataService().luuDanhSachGa(Arrays.asList(
                    new Ga("G01", "Nhổn", "Bắc Từ Liêm", 1),
                    new Ga("G08", "Cầu Giấy", "Cầu Giấy", 8)
            ));
            new VeDataService().luuDanhSachVe(
                    Collections.<VeMetro>emptyList()
            );

            MuaVeController controller = new MuaVeController();
            VeLuot veLuot = controller.muaVeLuot(hanhKhach, "G01", "G08");
            VeThang veThang = controller.muaVeThang(
                    hanhKhach, LoaiVeThang.PHO_THONG
            );

            LocalDate homNay = LocalDate.now();
            kiemTra(veLuot.isConHieuLuc(homNay),
                    "Vé lượt phải có hiệu lực trong ngày sử dụng");
            kiemTra(!veLuot.isConHieuLuc(homNay.plusDays(1)),
                    "Vé lượt phải hết hiệu lực sau ngày sử dụng");

            kiemTra(veThang.isConHieuLuc(veThang.getNgayBatDau()),
                    "Vé tháng phải có hiệu lực trong ngày bắt đầu");
            kiemTra(veThang.isConHieuLuc(veThang.getNgayHetHan()),
                    "Vé tháng phải có hiệu lực trong ngày hết hạn");
            kiemTra(!veThang.isConHieuLuc(
                            veThang.getNgayBatDau().minusDays(1)),
                    "Vé tháng chưa được dùng trước ngày bắt đầu");
            kiemTra(!veThang.isConHieuLuc(
                            veThang.getNgayHetHan().plusDays(1)),
                    "Vé tháng phải hết hiệu lực sau ngày hết hạn");

            VeLuotService veLuotService = new VeLuotService();
            VeThangService veThangService = new VeThangService();
            kiemTra(veLuotService.voHieuHoaVe(veLuot),
                    "Phải vô hiệu hóa và lưu được vé lượt");
            kiemTra(veThangService.voHieuHoaVe(veThang),
                    "Phải vô hiệu hóa và lưu được vé tháng");

            VeDataService dataSauKhiMoLai = new VeDataService();
            VeLuot veLuotDaLuu = (VeLuot) dataSauKhiMoLai.timTheoMaVe(
                    veLuot.getMaVe()
            );
            VeThang veThangDaLuu = (VeThang) dataSauKhiMoLai.timTheoMaVe(
                    veThang.getMaVe()
            );

            kiemTra(veLuotDaLuu != null && !veLuotDaLuu.isTrangThai(),
                    "Trạng thái vô hiệu của vé lượt phải còn sau khi mở lại");
            kiemTra(veThangDaLuu != null && !veThangDaLuu.isTrangThai(),
                    "Trạng thái vô hiệu của vé tháng phải còn sau khi mở lại");
            kiemTra(!controller.veConHieuLuc(veLuotDaLuu),
                    "Vé lượt đã vô hiệu không được coi là còn hiệu lực");
            kiemTra(!controller.veConHieuLuc(veThangDaLuu),
                    "Vé tháng đã vô hiệu không được coi là còn hiệu lực");

            taiKhoan.setTrangThai(false);
            boolean daChanTaiKhoanBiKhoa = false;
            try {
                controller.muaVeLuot(hanhKhach, "G01", "G08");
            } catch (IllegalStateException e) {
                daChanTaiKhoanBiKhoa = true;
            }
            kiemTra(daChanTaiKhoanBiKhoa,
                    "Tài khoản bị khóa không được phép mua vé");

            System.out.println("TẤT CẢ KIỂM THỬ TRẠNG THÁI VÉ ĐỀU THÀNH CÔNG");
        } finally {
            System.clearProperty("metro.data.dir");
            xoaThuMucTam(thuMucDuLieu);
        }
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