package data;

import model.HanhKhach;
import model.TaiKhoan;
import utils.Constants;
import utils.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class HanhKhachDataService {
    private static final String KY_TU_PHAN_CACH = "|";
    private static final String TIEN_TO_MA_HANH_KHACH = "HK";

    private final TaiKhoanDataService taiKhoanDataService;

    public HanhKhachDataService() {
        taiKhoanDataService = new TaiKhoanDataService();
    }

    // đọc tất cả hành khách từ file
    public List<HanhKhach> docDanhSachHanhKhach() {
        List<String> danhSachDong = FileHandler.docFile(Constants.FILE_HANH_KHACH);
        List<HanhKhach> danhSachHanhKhach = new ArrayList<>();

        for (String x : danhSachDong) {
            if (x == null || x.trim().isEmpty()) {
                continue;
            }

            try {
                HanhKhach hanhKhach = chuyenDongThanhHanhKhach(x);
                danhSachHanhKhach.add(hanhKhach);
            } catch (IllegalArgumentException e) {
                System.out.println("Bỏ qua dòng hành khách không hợp lệ: " + x);
            }
        }

        return danhSachHanhKhach;
    }

    // chuyển một dòng trong file thành hành khách
    private HanhKhach chuyenDongThanhHanhKhach(String x) {
        String[] duLieu = x.split("\\|", -1);

        if (duLieu.length != 5) {
            throw new IllegalArgumentException(
                    "Dòng dữ liệu hành khách phải có 5 thành phần"
            );
        }

        String maHanhKhach = duLieu[0].trim();
        String hoTen = duLieu[1].trim();
        String soDienThoai = duLieu[2].trim();
        String email = duLieu[3].trim();
        String tenDangNhap = duLieu[4].trim();

        TaiKhoan taiKhoan = taiKhoanDataService.timTheoTenDangNhap(tenDangNhap);

        if (taiKhoan == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy tài khoản của hành khách"
            );
        }

        HanhKhach hanhKhach = new HanhKhach(
                maHanhKhach,
                hoTen,
                soDienThoai,
                email,
                taiKhoan
        );

        kiemTraHanhKhach(hanhKhach);

        return hanhKhach;
    }

    // chuyển hành khách thành một dòng để lưu vào file
    private String chuyenHanhKhachThanhDong(HanhKhach x) {
        return x.getMaHanhKhach()
                + KY_TU_PHAN_CACH
                + x.getHoTen()
                + KY_TU_PHAN_CACH
                + x.getSoDienThoai()
                + KY_TU_PHAN_CACH
                + x.getEmail()
                + KY_TU_PHAN_CACH
                + x.getTaiKhoan().getTenDangNhap();
    }

    // kiểm tra dữ liệu hành khách trước khi lưu
    private void kiemTraHanhKhach(HanhKhach x) {
        if (x == null) {
            throw new IllegalArgumentException(
                    "Hành khách không được null"
            );
        }

        if (x.getMaHanhKhach() == null || x.getMaHanhKhach().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã hành khách không được để trống"
            );
        }

        if (x.getHoTen() == null || x.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Họ tên không được để trống"
            );
        }

        if (x.getSoDienThoai() == null || x.getSoDienThoai().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Số điện thoại không được để trống"
            );
        }

        if (x.getEmail() == null || x.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email không được để trống"
            );
        }

        if (x.getTaiKhoan() == null) {
            throw new IllegalArgumentException(
                    "Tài khoản của hành khách không được để trống"
            );
        }

        if (x.getTaiKhoan().getTenDangNhap() == null
                || x.getTaiKhoan().getTenDangNhap().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập của hành khách không được để trống"
            );
        }

        if (x.getMaHanhKhach().contains(KY_TU_PHAN_CACH)
                || x.getHoTen().contains(KY_TU_PHAN_CACH)
                || x.getSoDienThoai().contains(KY_TU_PHAN_CACH)
                || x.getEmail().contains(KY_TU_PHAN_CACH)
                || x.getTaiKhoan().getTenDangNhap().contains(KY_TU_PHAN_CACH)) {
            throw new IllegalArgumentException(
                    "Thông tin hành khách không được chứa ký tự |"
            );
        }
    }

    // tìm hành khách theo mã
    public HanhKhach timTheoMa(String maHanhKhach) {
        if (maHanhKhach == null || maHanhKhach.trim().isEmpty()) {
            return null;
        }

        for (HanhKhach x : docDanhSachHanhKhach()) {
            if (x.getMaHanhKhach().equalsIgnoreCase(maHanhKhach.trim())) {
                return x;
            }
        }

        return null;
    }

    // tìm hành khách theo tên đăng nhập
    public HanhKhach timTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return null;
        }

        for (HanhKhach x : docDanhSachHanhKhach()) {
            if (x.getTaiKhoan().getTenDangNhap().equalsIgnoreCase(tenDangNhap.trim())) {
                return x;
            }
        }

        return null;
    }

    // kiểm tra số điện thoại đã tồn tại hay chưa
    public boolean tonTaiSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return false;
        }

        for (HanhKhach x : docDanhSachHanhKhach()) {
            if (x.getSoDienThoai().equals(soDienThoai.trim())) {
                return true;
            }
        }

        return false;
    }

    // kiểm tra email đã tồn tại hay chưa
    public boolean tonTaiEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        for (HanhKhach x : docDanhSachHanhKhach()) {
            if (x.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }

        return false;
    }

    // thêm một hành khách vào cuối file
    public boolean themHanhKhach(HanhKhach x) {
        kiemTraHanhKhach(x);

        if (timTheoMa(x.getMaHanhKhach()) != null
                || timTheoTenDangNhap(x.getTaiKhoan().getTenDangNhap()) != null
                || tonTaiSoDienThoai(x.getSoDienThoai())
                || tonTaiEmail(x.getEmail())) {
            return false;
        }

        if (!taiKhoanDataService.tonTaiTenDangNhap(
                x.getTaiKhoan().getTenDangNhap()
        )) {
            throw new IllegalArgumentException(
                    "Tài khoản của hành khách chưa được lưu"
            );
        }

        String dong = chuyenHanhKhachThanhDong(x);
        FileHandler.ghiThem(Constants.FILE_HANH_KHACH, dong);

        return true;
    }

    public boolean capNhatThongTin(String maHanhKhach, String hoTen,
                                   String soDienThoai, String email) {
        if (maHanhKhach == null || maHanhKhach.trim().isEmpty()) {
            return false;
        }

        if (hoTen == null || hoTen.trim().isEmpty()
                || soDienThoai == null || soDienThoai.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Thông tin cập nhật không được để trống"
            );
        }

        List<HanhKhach> danhSach = docDanhSachHanhKhach();
        HanhKhach canCapNhat = null;
        for (HanhKhach x : danhSach) {
            if (x.getMaHanhKhach().equalsIgnoreCase(maHanhKhach.trim())) {
                canCapNhat = x;
            } else {
                if (x.getSoDienThoai().equals(soDienThoai.trim())) {
                    throw new IllegalArgumentException(
                            "Số điện thoại đã được tài khoản khác sử dụng"
                    );
                }
                if (x.getEmail().equalsIgnoreCase(email.trim())) {
                    throw new IllegalArgumentException(
                            "Email đã được tài khoản khác sử dụng"
                    );
                }
            }
        }

        if (canCapNhat == null) {
            return false;
        }

        canCapNhat.setHoTen(hoTen.trim());
        canCapNhat.setSoDienThoai(soDienThoai.trim());
        canCapNhat.setEmail(email.trim());
        kiemTraHanhKhach(canCapNhat);
        luuDanhSachHanhKhach(danhSach);
        return true;
    }

    // ghi đè toàn bộ danh sách hành khách
    public void luuDanhSachHanhKhach(List<HanhKhach> danhSachHanhKhach) {
        if (danhSachHanhKhach == null) {
            throw new IllegalArgumentException(
                    "Danh sách hành khách không được null"
            );
        }

        List<String> danhSachDong = new ArrayList<>();

        for (HanhKhach x : danhSachHanhKhach) {
            kiemTraHanhKhach(x);

            if (!taiKhoanDataService.tonTaiTenDangNhap(
                    x.getTaiKhoan().getTenDangNhap()
            )) {
                throw new IllegalArgumentException(
                        "Tài khoản của hành khách chưa được lưu"
                );
            }

            danhSachDong.add(chuyenHanhKhachThanhDong(x));
        }

        FileHandler.ghiFile(Constants.FILE_HANH_KHACH, danhSachDong);
    }

    // sinh mã mới dựa trên mã lớn nhất đang có trong file
    public String sinhMaHanhKhachMoi() {
        int soLonNhat = 0;

        for (HanhKhach x : docDanhSachHanhKhach()) {
            String maHanhKhach = x.getMaHanhKhach();

            if (!maHanhKhach.toUpperCase().startsWith(TIEN_TO_MA_HANH_KHACH)) {
                continue;
            }

            try {
                int soThuTu = Integer.parseInt(
                        maHanhKhach.substring(TIEN_TO_MA_HANH_KHACH.length())
                );

                if (soThuTu > soLonNhat) {
                    soLonNhat = soThuTu;
                }
            } catch (NumberFormatException e) {
                // bỏ qua mã không đúng định dạng HK001, HK002,...
            }
        }

        return String.format("HK%03d", soLonNhat + 1);
    }
}