package test;

import data.TaiKhoanDataService;
import model.TaiKhoan;
import model.VaiTro;

import java.util.List;

public class TaiKhoanDataServiceTest {

    public static void main(String[] args) {
        TaiKhoanDataService dataService =
                new TaiKhoanDataService();

        TaiKhoan taiKhoanMoi = new TaiKhoan(
                "vandung",
                "123456",
                VaiTro.HANH_KHACH,
                true
        );

        // Kiểm tra thêm tài khoản
        boolean ketQua =
                dataService.themTaiKhoan(taiKhoanMoi);

        if (ketQua) {
            System.out.println(
                    "Thêm tài khoản thành công"
            );
        } else {
            System.out.println(
                    "Tên đăng nhập đã tồn tại"
            );
        }

        // Kiểm tra đọc danh sách
        List<TaiKhoan> danhSach =
                dataService.docDanhSachTaiKhoan();

        System.out.println(
                "\nDanh sách tài khoản:"
        );

        for (TaiKhoan taiKhoan : danhSach) {
            System.out.println(taiKhoan);
        }

        // Kiểm tra tìm kiếm
        TaiKhoan timThay =
                dataService.timTheoTenDangNhap(
                        "vandung"
                );

        if (timThay != null) {
            System.out.println(
                    "\nTìm thấy: " + timThay
            );
        } else {
            System.out.println(
                    "\nKhông tìm thấy tài khoản"
            );
        }
    }
}