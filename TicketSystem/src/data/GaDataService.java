package data;

import model.Ga;
import utils.Constants;
import utils.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class GaDataService {
    private static final String KY_TU_PHAN_CACH = "|";

    // đọc tất cả ga từ file
    public List<Ga> docDanhSachGa() {
        List<String> danhSachDong = FileHandler.docFile(Constants.FILE_GA);
        List<Ga> danhSachGa = new ArrayList<>();

        for (String x : danhSachDong) {
            if (x == null || x.trim().isEmpty()) {
                continue;
            }

            try {
                danhSachGa.add(chuyenDongThanhGa(x));
            } catch (IllegalArgumentException e) {
                System.out.println("Bỏ qua dòng ga không hợp lệ: " + x);
            }
        }

        return danhSachGa;
    }

    private Ga chuyenDongThanhGa(String x) {
        String[] duLieu = x.split("\\|", -1);

        if (duLieu.length != 4) {
            throw new IllegalArgumentException(
                    "Dòng dữ liệu ga phải có 4 thành phần"
            );
        }

        Ga ga = new Ga(
                duLieu[0].trim(),
                duLieu[1].trim(),
                duLieu[2].trim(),
                Integer.parseInt(duLieu[3].trim())
        );

        kiemTraGa(ga);

        return ga;
    }

    private String chuyenGaThanhDong(Ga x) {
        return x.getMaGa()
                + KY_TU_PHAN_CACH
                + x.getTenGa()
                + KY_TU_PHAN_CACH
                + x.getViTri()
                + KY_TU_PHAN_CACH
                + x.getThuTu();
    }

    private void kiemTraGa(Ga x) {
        if (x == null) {
            throw new IllegalArgumentException(
                    "Ga không được null"
            );
        }

        if (x.getMaGa() == null || x.getMaGa().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã ga không được để trống"
            );
        }

        if (x.getTenGa() == null || x.getTenGa().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên ga không được để trống"
            );
        }

        if (x.getViTri() == null || x.getViTri().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vị trí ga không được để trống"
            );
        }

        if (x.getThuTu() <= 0) {
            throw new IllegalArgumentException(
                    "Thứ tự ga phải lớn hơn 0"
            );
        }

        if (x.getMaGa().contains(KY_TU_PHAN_CACH)
                || x.getTenGa().contains(KY_TU_PHAN_CACH)
                || x.getViTri().contains(KY_TU_PHAN_CACH)) {
            throw new IllegalArgumentException(
                    "Thông tin ga không được chứa ký tự |"
            );
        }
    }

    public Ga timTheoMa(String maGa) {
        if (maGa == null || maGa.trim().isEmpty()) {
            return null;
        }

        for (Ga x : docDanhSachGa()) {
            if (x.getMaGa().equalsIgnoreCase(maGa.trim())) {
                return x;
            }
        }

        return null;
    }

    public Ga timTheoTen(String tenGa) {
        if (tenGa == null || tenGa.trim().isEmpty()) {
            return null;
        }

        for (Ga x : docDanhSachGa()) {
            if (x.getTenGa().equalsIgnoreCase(tenGa.trim())) {
                return x;
            }
        }

        return null;
    }

    public boolean themGa(Ga x) {
        kiemTraGa(x);

        if (timTheoMa(x.getMaGa()) != null
                || timTheoTen(x.getTenGa()) != null
                || tonTaiThuTu(x.getThuTu())) {
            return false;
        }

        FileHandler.ghiThem(Constants.FILE_GA, chuyenGaThanhDong(x));

        return true;
    }

    public boolean tonTaiThuTu(int thuTu) {
        for (Ga x : docDanhSachGa()) {
            if (x.getThuTu() == thuTu) {
                return true;
            }
        }

        return false;
    }

    public void luuDanhSachGa(List<Ga> danhSachGa) {
        if (danhSachGa == null) {
            throw new IllegalArgumentException(
                    "Danh sách ga không được null"
            );
        }

        List<String> danhSachDong = new ArrayList<>();

        for (Ga x : danhSachGa) {
            kiemTraGa(x);
            danhSachDong.add(chuyenGaThanhDong(x));
        }

        FileHandler.ghiFile(Constants.FILE_GA, danhSachDong);
    }
}
