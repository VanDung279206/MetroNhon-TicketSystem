package service;

import data.VeDataService;
import model.HanhKhach;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;

import java.util.ArrayList;
import java.util.List;

public class VeService {
    // Service dùng để thao tác với dữ liệu vé
    private final VeDataService veDataService;

    // Khởi tạo service dữ liệu vé
    public VeService() {
        veDataService = new VeDataService();
    }

    // Lấy toàn bộ danh sách vé
    public List<VeMetro> layDanhSachVe() {
        return veDataService.docDanhSachVe();
    }

    // Tìm vé theo mã vé
    public VeMetro timTheoMaVe(String maVe) {
        // Kiểm tra mã vé không được để trống
    if (maVe == null || maVe.trim().isEmpty()) {
            return null;
        }

        return veDataService.timTheoMaVe(maVe);
    }

    // Tìm danh sách vé thuộc về một hành khách
    public List<VeMetro> timTheoHanhKhach(HanhKhach hanhKhach) {
        List<VeMetro> ketQua = new ArrayList<>();

        // Kiểm tra hành khách và mã hành khách hợp lệ
    if (hanhKhach == null
                || hanhKhach.getMaHanhKhach() == null
                || hanhKhach.getMaHanhKhach().trim().isEmpty()) {
            return ketQua;
        }

        return veDataService.timTheoMaHanhKhach(
                hanhKhach.getMaHanhKhach()
        );
    }

    // Lấy riêng danh sách vé lượt
    public List<VeLuot> layDanhSachVeLuot() {
        List<VeLuot> ketQua = new ArrayList<>();

        // Duyệt toàn bộ vé để lọc theo loại
    for (VeMetro ve : veDataService.docDanhSachVe()) {
            if (ve instanceof VeLuot) {
                ketQua.add((VeLuot) ve);
            }
        }

        return ketQua;
    }

    // Lấy riêng danh sách vé tháng
    public List<VeThang> layDanhSachVeThang() {
        List<VeThang> ketQua = new ArrayList<>();

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            if (ve instanceof VeThang) {
                ketQua.add((VeThang) ve);
            }
        }

        return ketQua;
    }

    // Kiểm tra trạng thái hiện tại của vé
    public boolean kiemTraTrangThai(String maVe) {
        VeMetro ve = timTheoMaVe(maVe);

        // Không tìm thấy vé thì trả về false
    if (ve == null) {
            return false;
        }

        return ve.isTrangThai();
    }

    // Cập nhật trạng thái vé theo yêu cầu
    public boolean capNhatTrangThai(String maVe, boolean trangThai) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return false;
        }

        // Đọc danh sách vé hiện tại để cập nhật
    List<VeMetro> danhSachVe = veDataService.docDanhSachVe();

        for (VeMetro ve : danhSachVe) {
            // Tìm vé theo mã và cập nhật trạng thái
    if (ve.getMaVe().equalsIgnoreCase(maVe.trim())) {
                ve.setTrangThai(trangThai);
                veDataService.luuDanhSachVe(danhSachVe);
                return true;
            }
        }

        return false;
    }

    // Kích hoạt vé
    public boolean kichHoatVe(String maVe) {
        return capNhatTrangThai(maVe, true);
    }

    // Vô hiệu hóa vé
    public boolean voHieuHoaVe(String maVe) {
        return capNhatTrangThai(maVe, false);
    }

    // Lấy danh sách các vé đang hoạt động
    public List<VeMetro> layVeDangHoatDong() {
        List<VeMetro> ketQua = new ArrayList<>();

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            // Lọc các vé có trạng thái hoạt động
    if (ve.isTrangThai()) {
                ketQua.add(ve);
            }
        }

        return ketQua;
    }

    // Lấy danh sách các vé không hoạt động
    public List<VeMetro> layVeKhongHoatDong() {
        List<VeMetro> ketQua = new ArrayList<>();

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            // Lọc các vé có trạng thái không hoạt động
    if (!ve.isTrangThai()) {
                ketQua.add(ve);
            }
        }

        return ketQua;
    }

    // Tính tổng doanh thu từ toàn bộ vé đã bán
    public double tinhTongDoanhThu() {
        // Biến lưu tổng doanh thu
    double tong = 0;

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            // Cộng giá của từng vé vào tổng doanh thu
    tong += ve.getGiaVe();
        }

        return tong;
    }
}
