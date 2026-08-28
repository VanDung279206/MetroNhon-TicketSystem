package service;

import data.HanhKhachDataService;
import model.HanhKhach;

import java.util.List;

public class HanhKhachService {
    // Service dùng để thao tác với dữ liệu hành khách
    private final HanhKhachDataService hanhKhachDataService;

    // Khởi tạo service dữ liệu hành khách
    public HanhKhachService() {
        hanhKhachDataService = new HanhKhachDataService();
    }

    // Lấy toàn bộ danh sách hành khách
    public List<HanhKhach> layDanhSachHanhKhach() {
        return hanhKhachDataService.docDanhSachHanhKhach();
    }

    // Tìm hành khách theo mã
    public HanhKhach timTheoMa(String maHanhKhach) {
        return hanhKhachDataService.timTheoMa(maHanhKhach);
    }

    // Tìm hành khách theo tên đăng nhập
    public HanhKhach timTheoTenDangNhap(String tenDangNhap) {
        return hanhKhachDataService.timTheoTenDangNhap(tenDangNhap);
    }

    // Kiểm tra số điện thoại đã tồn tại hay chưa
    public boolean tonTaiSoDienThoai(String soDienThoai) {
        return hanhKhachDataService.tonTaiSoDienThoai(soDienThoai);
    }

    // Kiểm tra email đã tồn tại hay chưa
    public boolean tonTaiEmail(String email) {
        return hanhKhachDataService.tonTaiEmail(email);
    }

    // Thêm một hành khách mới
    public boolean themHanhKhach(HanhKhach hanhKhach) {
        // Kiểm tra dữ liệu hành khách không được null
        if (hanhKhach == null) {
            return false;
        }

        return hanhKhachDataService.themHanhKhach(hanhKhach);
    }

    // Tự sinh mã hành khách mới
    public String sinhMaHanhKhachMoi() {
        return hanhKhachDataService.sinhMaHanhKhachMoi();
    }

    // Lưu lại danh sách hành khách
    public void luuDanhSachHanhKhach(
            List<HanhKhach> danhSachHanhKhach) {
        hanhKhachDataService.luuDanhSachHanhKhach(
                danhSachHanhKhach);
    }
}