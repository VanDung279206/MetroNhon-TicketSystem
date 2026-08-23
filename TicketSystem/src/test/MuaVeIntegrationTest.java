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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MuaVeIntegrationTest {
    public static void main(String[] args) {
        khoiTaoDuLieuKiemThu();

        TaiKhoan taiKhoan = new TaiKhoan(
                "khach_test", "123456", VaiTro.HANH_KHACH, true
        );
        kiemTra(new TaiKhoanDataService().themTaiKhoan(taiKhoan),
                "Phải lưu được tài khoản kiểm thử");

        HanhKhach hanhKhach = new HanhKhach(
                "HK001", "Khách kiểm thử", "0900000000",
                "khach@test.local", taiKhoan
        );
        kiemTra(new HanhKhachDataService().themHanhKhach(hanhKhach),
                "Phải lưu được hành khách kiểm thử");

        MuaVeController controller = new MuaVeController();
        kiemTra(controller.getDanhSachGa().size() == 2,
                "Controller phải đọc danh sách ga từ file");

        VeLuot veLuotDau = controller.muaVeLuot(hanhKhach, "G01", "G08");
        VeThang vePhoThong = controller.muaVeThang(
                hanhKhach, LoaiVeThang.PHO_THONG
        );
        VeThang veUuDai = controller.muaVeThang(
                hanhKhach, LoaiVeThang.UU_DAI
        );

        kiemTra("VL001".equals(veLuotDau.getMaVe()),
                "Mã vé lượt đầu tiên phải là VL001");
        kiemTra("VT001".equals(vePhoThong.getMaVe()),
                "Mã vé tháng đầu tiên phải là VT001");
        kiemTra(veUuDai.getGiaVe() == 140000,
                "Vé tháng ưu đãi phải có giá 140000");

        // Tạo controller mới để mô phỏng người dùng đóng rồi mở lại ứng dụng.
        MuaVeController controllerSauKhiMoLai = new MuaVeController();
        List<VeMetro> danhSachDaLuu = controllerSauKhiMoLai.getDanhSachVeDaBan();
        kiemTra(danhSachDaLuu.size() == 3,
                "Ba vé phải còn trong file sau khi mở lại ứng dụng");

        VeLuot veLuotTiepTheo = controllerSauKhiMoLai.muaVeLuot(
                hanhKhach, "G08", "G01"
        );
        kiemTra("VL002".equals(veLuotTiepTheo.getMaVe()),
                "Mã vé lượt phải tăng tiếp từ dữ liệu đã lưu");
        kiemTra(controllerSauKhiMoLai
                        .getDanhSachVeCuaHanhKhach("HK001").size() == 4,
                "Phải tìm được toàn bộ vé của hành khách");

        System.out.println("TẤT CẢ KIỂM THỬ MUA VÉ ĐỀU THÀNH CÔNG");
    }

    private static void khoiTaoDuLieuKiemThu() {
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
}