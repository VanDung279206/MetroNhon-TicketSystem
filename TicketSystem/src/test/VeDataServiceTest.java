package test;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class VeDataServiceTest {

    public static void main(String[] args) {
        TaiKhoanDataService taiKhoanDataService = new TaiKhoanDataService();
        HanhKhachDataService hanhKhachDataService = new HanhKhachDataService();
        GaDataService gaDataService = new GaDataService();
        VeDataService veDataService = new VeDataService();

        TaiKhoan taiKhoan = taiKhoanDataService.timTheoTenDangNhap("vandung");

        if (taiKhoan == null) {
            taiKhoan = new TaiKhoan(
                    "vandung",
                    "123456",
                    VaiTro.HANH_KHACH,
                    true
            );

            taiKhoanDataService.themTaiKhoan(taiKhoan);
        }

        HanhKhach hanhKhach =
                hanhKhachDataService.timTheoTenDangNhap("vandung");

        if (hanhKhach == null) {
            hanhKhach = new HanhKhach(
                    hanhKhachDataService.sinhMaHanhKhachMoi(),
                    "Nguyễn Văn Dũng",
                    "0123456789",
                    "nguyenvandung@gmail.com",
                    taiKhoan
            );

            hanhKhachDataService.themHanhKhach(hanhKhach);
        }

        List<VeMetro> veCuaHanhKhach = veDataService.timTheoMaHanhKhach(
                hanhKhach.getMaHanhKhach()
        );

        if (veCuaHanhKhach.isEmpty()) {
            Ga gaDi = gaDataService.timTheoMa("G01");
            Ga gaDen = gaDataService.timTheoMa("G08");
            LocalDateTime ngayMua = LocalDateTime.now();

            VeLuot veLuot = new VeLuot(
                    veDataService.sinhMaVeLuotMoi(),
                    hanhKhach,
                    ngayMua,
                    15000,
                    true,
                    gaDi,
                    gaDen,
                    LocalDate.now()
            );

            VeThang veThang = new VeThang(
                    veDataService.sinhMaVeThangMoi(),
                    hanhKhach,
                    ngayMua,
                    280000,
                    LoaiVeThang.PHO_THONG,
                    true,
                    LocalDate.now(),
                    LocalDate.now().plusDays(29)
            );

            System.out.println("Thêm vé lượt: " + veDataService.themVe(veLuot));
            System.out.println("Thêm vé tháng: " + veDataService.themVe(veThang));
        } else {
            System.out.println("Hành khách kiểm tra đã có vé");
        }

        System.out.println("\nDanh sách vé đã bán:");

        for (VeMetro x : veDataService.docDanhSachVe()) {
            System.out.println(x);
        }

        System.out.println(
                "\nMã vé lượt tiếp theo: "
                        + veDataService.sinhMaVeLuotMoi()
        );

        System.out.println(
                "Mã vé tháng tiếp theo: "
                        + veDataService.sinhMaVeThangMoi()
        );
    }
}