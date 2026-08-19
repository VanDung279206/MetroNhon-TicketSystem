package test;

import data.HanhKhachDataService;
import data.TaiKhoanDataService;
import model.HanhKhach;
import model.TaiKhoan;
import model.VaiTro;

import java.util.List;

public class HanhKhachDataServiceTest {

    public static void main(String[] args) {
        TaiKhoanDataService taiKhoanDataService = new TaiKhoanDataService();
        HanhKhachDataService hanhKhachDataService = new HanhKhachDataService();

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

        HanhKhach hanhKhachKiemTra =
                hanhKhachDataService.timTheoTenDangNhap("vandung");

        if (hanhKhachKiemTra == null) {
            String maHanhKhachMoi = hanhKhachDataService.sinhMaHanhKhachMoi();

            hanhKhachKiemTra = new HanhKhach(
                    maHanhKhachMoi,
                    "Nguyễn Văn Dũng",
                    "0123456789",
                    "nguyenvandung@gmail.com",
                    taiKhoan
            );

            boolean ketQua =
                    hanhKhachDataService.themHanhKhach(hanhKhachKiemTra);

            if (ketQua) {
                System.out.println("Thêm hành khách thành công");
            }
        } else {
            System.out.println("Hành khách kiểm tra đã tồn tại");
        }

        List<HanhKhach> danhSach = hanhKhachDataService.docDanhSachHanhKhach();

        System.out.println("\nDanh sách hành khách:");

        for (HanhKhach x : danhSach) {
            System.out.println(
                    x.getMaHanhKhach()
                            + " | "
                            + x.getHoTen()
                            + " | "
                            + x.getSoDienThoai()
                            + " | "
                            + x.getEmail()
                            + " | "
                            + x.getTaiKhoan().getTenDangNhap()
            );
        }

        HanhKhach timThay = hanhKhachDataService.timTheoMa(
                hanhKhachKiemTra.getMaHanhKhach()
        );

        if (timThay != null) {
            System.out.println("\nTìm thấy: " + timThay);
        } else {
            System.out.println("\nKhông tìm thấy hành khách");
        }

        System.out.println(
                "Mã hành khách tiếp theo: "
                        + hanhKhachDataService.sinhMaHanhKhachMoi()
        );
    }
}