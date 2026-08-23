package service;

import data.HanhKhachDataService;
import model.HanhKhach;

import java.util.List;

public class HanhKhachService {
    private final HanhKhachDataService hanhKhachDataService;

    public HanhKhachService() {
        hanhKhachDataService = new HanhKhachDataService();
    }

    public List<HanhKhach> layDanhSachHanhKhach() {
        return hanhKhachDataService.docDanhSachHanhKhach();
    }

    public HanhKhach timTheoMa(String maHanhKhach) {
        return hanhKhachDataService.timTheoMa(maHanhKhach);
    }

    public HanhKhach timTheoTenDangNhap(String tenDangNhap) {
        return hanhKhachDataService.timTheoTenDangNhap(tenDangNhap);
    }

    public boolean tonTaiSoDienThoai(String soDienThoai) {
        return hanhKhachDataService.tonTaiSoDienThoai(soDienThoai);
    }

    public boolean tonTaiEmail(String email) {
        return hanhKhachDataService.tonTaiEmail(email);
    }

    public boolean themHanhKhach(HanhKhach hanhKhach) {
        if (hanhKhach == null) {
            return false;
        }

        return hanhKhachDataService.themHanhKhach(hanhKhach);
    }

    public String sinhMaHanhKhachMoi() {
        return hanhKhachDataService.sinhMaHanhKhachMoi();
    }

    public void luuDanhSachHanhKhach(
            List<HanhKhach> danhSachHanhKhach) {
        hanhKhachDataService.luuDanhSachHanhKhach(
                danhSachHanhKhach);
    }
}
