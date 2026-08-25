package service;

import data.TaiKhoanDataService;
import model.HanhKhach;
import model.TaiKhoan;
import model.VaiTro;

import java.util.List;

public class AuthService {
    // Khai báo service thao tác với dữ liệu tài khoản và hành khách
    private final TaiKhoanDataService taiKhoanDataService;
    private final HanhKhachService hanhKhachService;

    // Khởi tạo các service cần sử dụng
    public AuthService() {
        taiKhoanDataService = new TaiKhoanDataService();
        hanhKhachService = new HanhKhachService();
    }

    // Xử lý đăng ký tài khoản và tạo thông tin hành khách
    public boolean dangKy(TaiKhoan taiKhoan, HanhKhach hanhKhach) {
        // Kiểm tra dữ liệu đầu vào không được null
    if (taiKhoan == null || hanhKhach == null) {
            return false;
        }

        String tenDangNhap = taiKhoan.getTenDangNhap();
        String matKhau = taiKhoan.getMatKhau();

        // Kiểm tra tên đăng nhập không được để trống
    // Kiểm tra các thông tin đầu vào không được để trống
    if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return false;
        }

        // Kiểm tra mật khẩu không được để trống
    if (matKhau == null || matKhau.isEmpty()) {
            return false;
        }

        // Kiểm tra tên đăng nhập đã tồn tại hay chưa
    if (taiKhoanDataService.tonTaiTenDangNhap(tenDangNhap)) {
            return false;
        }

        // Tài khoản đăng ký thông thường luôn có vai trò hành khách
    taiKhoan.setVaiTro(VaiTro.HANH_KHACH);
        // Chuyển trạng thái tài khoản sang đang hoạt động
    taiKhoan.setTrangThai(true);
        hanhKhach.setTaiKhoan(taiKhoan);

        // Tự sinh mã hành khách nếu chưa có mã
    if (hanhKhach.getMaHanhKhach() == null
                || hanhKhach.getMaHanhKhach().trim().isEmpty()) {
            hanhKhach.setMaHanhKhach(
                    hanhKhachService.sinhMaHanhKhachMoi()
            );
        }

        // Kiểm tra số điện thoại và email có bị trùng hay không
    if (hanhKhachService.tonTaiSoDienThoai(hanhKhach.getSoDienThoai())
                || hanhKhachService.tonTaiEmail(hanhKhach.getEmail())) {
            return false;
        }

        // Lưu tài khoản trước
    if (!taiKhoanDataService.themTaiKhoan(taiKhoan)) {
            return false;
        }

        // Lưu thông tin hành khách
    if (!hanhKhachService.themHanhKhach(hanhKhach)) {
            // Đọc danh sách tài khoản để cập nhật dữ liệu
    List<TaiKhoan> danhSach =
                    taiKhoanDataService.docDanhSachTaiKhoan();
            danhSach.removeIf(x -> x.getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap));
            taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
            return false;
        }

        return true;
    }

    // Xử lý đăng nhập và trả về tài khoản hợp lệ
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()
                || matKhau == null || matKhau.isEmpty()) {
            return null;
        }

        // Tìm tài khoản theo tên đăng nhập
    TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        // Không cho tài khoản không tồn tại hoặc đang bị khóa đăng nhập
    if (taiKhoan == null || !taiKhoan.isTrangThai()) {
            return null;
        }

        // So sánh mật khẩu, có phân biệt chữ hoa và chữ thường
    if (!taiKhoan.getMatKhau().equals(matKhau)) {
            return null;
        }

        return taiKhoan;
    }

    // Xử lý đổi mật khẩu cho tài khoản
    public boolean doiMatKhau(
            String tenDangNhap,
            String matKhauCu,
            String matKhauMoi) {

        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()
                || matKhauCu == null || matKhauCu.isEmpty()
                || matKhauMoi == null || matKhauMoi.isEmpty()) {
            return false;
        }

        TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null || !taiKhoan.isTrangThai()
                || !taiKhoan.getMatKhau().equals(matKhauCu)) {
            return false;
        }

        // Cập nhật mật khẩu mới
    taiKhoan.setMatKhau(matKhauMoi);

        List<TaiKhoan> danhSach =
                taiKhoanDataService.docDanhSachTaiKhoan();

        // Duyệt danh sách để tìm tài khoản cần cập nhật
    for (int i = 0; i < danhSach.size(); i++) {
            // Tìm đúng tài khoản cần cập nhật
    if (danhSach.get(i).getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap.trim())) {
                danhSach.set(i, taiKhoan);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
                return true;
            }
        }

        return false;
    }

    // Khóa tài khoản theo tên đăng nhập
    public boolean khoaTaiKhoan(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return false;
        }

        TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null) {
            return false;
        }

        // Chuyển trạng thái tài khoản sang bị khóa
    taiKhoan.setTrangThai(false);

        List<TaiKhoan> danhSach =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap.trim())) {
                danhSach.set(i, taiKhoan);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
                return true;
            }
        }

        return false;
    }

    // Mở khóa tài khoản theo tên đăng nhập
    public boolean moKhoaTaiKhoan(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return false;
        }

        TaiKhoan taiKhoan =
                taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null) {
            return false;
        }

        taiKhoan.setTrangThai(true);

        List<TaiKhoan> danhSach =
                taiKhoanDataService.docDanhSachTaiKhoan();

        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getTenDangNhap()
                    .equalsIgnoreCase(tenDangNhap.trim())) {
                danhSach.set(i, taiKhoan);
                taiKhoanDataService.luuDanhSachTaiKhoan(danhSach);
                return true;
            }
        }

        return false;
    }
}
