package data;

import model.TaiKhoan;
import model.VaiTro;
import utils.Constants;
import utils.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDataService {
    private static final String KY_TU_PHAN_CACH = "|";

    // đọc tất cả các tài khoản từ file
    public List<TaiKhoan> docDanhSachTaiKhoan() {
        List<String> danhSachDong = FileHandler.docFile(Constants.FILE_TAI_KHOAN);

        List<TaiKhoan> danhSachTaiKhoan = new ArrayList<>();

        for (String x : danhSachDong) {
            if (x == null || x.trim().isEmpty()) {
                continue;
            }

            try {
                TaiKhoan taiKhoan = chuyenDongThanhTaiKhoan(x);

                danhSachTaiKhoan.add(taiKhoan);
            } catch (IllegalArgumentException e) {
                System.out.println("Bỏ qua dòng tài khoản không hợp lệ: " + x);
            }
        }

        return danhSachTaiKhoan;
    }

    // chuyển một dòng trong file thành tài khoản
    private TaiKhoan chuyenDongThanhTaiKhoan(String x) {
        // ký tự | đặc biệt trong regex nên phải dùng \\| trong split
        String[] duLieu = x.split("\\|", -1); // -1 giúp giữ lại các trường rỗng

        if (duLieu.length != 4) {
            throw new IllegalArgumentException(
                    "Dòng dữ liệu phải có 4 thành phần"
            );
        }

        String tenDangNhap = duLieu[0].trim();
        String matKhau = duLieu[1];
        VaiTro vaiTro = VaiTro.valueOf(duLieu[2].trim());
        boolean trangThai = Boolean.parseBoolean(duLieu[3].trim());

        TaiKhoan taiKhoan = new TaiKhoan(tenDangNhap, matKhau, vaiTro, trangThai);

        kiemTraTaiKhoan(taiKhoan);

        return taiKhoan;
    }

    // kiểm tra tài khoản trước khi lưu
    private void kiemTraTaiKhoan(TaiKhoan x) {
        if (x == null) {
            throw new IllegalArgumentException(
                    "tài khoản không được null"
            );
        }

        if (x.getTenDangNhap() == null || x.getTenDangNhap().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập không được để trống"
            );
        }

        if (x.getMatKhau() == null || x.getMatKhau().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mật khẩu không được để trống"
            );
        }

        if (x.getVaiTro() == null) {
            throw new IllegalArgumentException(
                    "Vai trò không được để trống"
            );
        }

        if (x.getTenDangNhap().contains("|") || x.getMatKhau().contains("|")) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập và mật khẩu không được chứa ký tự |"
            );
        }
    }

    // chuyển tài khoản thành dòng
    private String chuyenTaiKhoanThanhDong(TaiKhoan taiKhoan) {
        return taiKhoan.getTenDangNhap()
                + KY_TU_PHAN_CACH
                + taiKhoan.getMatKhau()
                + KY_TU_PHAN_CACH
                + taiKhoan.getVaiTro().name()
                + KY_TU_PHAN_CACH
                + taiKhoan.isTrangThai();
    }

    // tìm tài khoản theo tên đăng nhập
    public TaiKhoan timTheoTenDangNhap(String x) {
        if (x == null || x.trim().isEmpty()) {
            return null;
        }

        List<TaiKhoan> danhSachTaiKhoan = docDanhSachTaiKhoan();

        for (TaiKhoan y : danhSachTaiKhoan) {
            if (y.getTenDangNhap().equalsIgnoreCase(x.trim())) {
                return y;
            }
        }

        return null;
    }

    // kiểm tra tên đăng nhập đã tồn tại hay chưa
    public boolean tonTaiTenDangNhap(String x){
        return timTheoTenDangNhap(x) != null;
    }

    // thêm 1 tài khoản vào cuối file - true: thành công - false: tên đăng nhập tồn tại
    public boolean themTaiKhoan(TaiKhoan x){
        kiemTraTaiKhoan(x);

        if (tonTaiTenDangNhap(x.getTenDangNhap())){
            return false;
        }

        String dong = chuyenTaiKhoanThanhDong(x);

        FileHandler.ghiThem(Constants.FILE_TAI_KHOAN, dong);

        return true;
    }

    // ghi đè toàn bộ danh sách tài khoản - dùng khi đổi mật khẩu, khóa hoặc mở tài khoản
    public void luuDanhSachTaiKhoan(List<TaiKhoan> danhSachTaiKhoan){
        if (danhSachTaiKhoan == null){
            throw new IllegalArgumentException(
                    "Danh sách tài khoản không được null"
            );
        }

        List<String> danhSachDong = new ArrayList<>();

        for (TaiKhoan x : danhSachTaiKhoan){
            kiemTraTaiKhoan(x);

            String dong = chuyenTaiKhoanThanhDong(x);

            danhSachDong.add(dong);
        }

        FileHandler.ghiFile(Constants.FILE_TAI_KHOAN, danhSachDong);
    }
}
