package data;

import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;
import utils.Constants;
import utils.FileHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VeDataService {
    private static final String KY_TU_PHAN_CACH = "|";
    private static final String VE_LUOT = "VE_LUOT";
    private static final String VE_THANG = "VE_THANG";

    private final HanhKhachDataService hanhKhachDataService;
    private final GaDataService gaDataService;

    public VeDataService() {
        hanhKhachDataService = new HanhKhachDataService();
        gaDataService = new GaDataService();
    }

    // đọc tất cả vé lượt và vé tháng từ file
    public List<VeMetro> docDanhSachVe() {
        List<String> danhSachDong = FileHandler.docFile(Constants.FILE_VE_DA_BAN);
        List<VeMetro> danhSachVe = new ArrayList<>();

        for (String x : danhSachDong) {
            if (x == null || x.trim().isEmpty()) {
                continue;
            }

            try {
                danhSachVe.add(chuyenDongThanhVe(x));
            } catch (RuntimeException e) {
                System.out.println("Bỏ qua dòng vé không hợp lệ: " + x);
            }
        }

        return danhSachVe;
    }

    private VeMetro chuyenDongThanhVe(String x) {
        String[] duLieu = x.split("\\|", -1);

        if (duLieu.length != 9) {
            throw new IllegalArgumentException(
                    "Dòng dữ liệu vé phải có 9 thành phần"
            );
        }

        String loaiVe = duLieu[0].trim();
        String maVe = duLieu[1].trim();
        String maHanhKhach = duLieu[2].trim();
        LocalDateTime ngayMua = LocalDateTime.parse(duLieu[3].trim());
        double giaVe = Double.parseDouble(duLieu[4].trim());
        boolean trangThai = chuyenChuoiThanhBoolean(duLieu[5]);

        HanhKhach hanhKhach = hanhKhachDataService.timTheoMa(maHanhKhach);

        if (hanhKhach == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy hành khách của vé"
            );
        }

        VeMetro ve;

        if (VE_LUOT.equalsIgnoreCase(loaiVe)) {
            Ga gaDi = gaDataService.timTheoMa(duLieu[6].trim());
            Ga gaDen = gaDataService.timTheoMa(duLieu[7].trim());
            LocalDate ngaySuDung = LocalDate.parse(duLieu[8].trim());

            if (gaDi == null || gaDen == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy ga của vé lượt"
                );
            }

            ve = new VeLuot(
                    maVe,
                    hanhKhach,
                    ngayMua,
                    giaVe,
                    trangThai,
                    gaDi,
                    gaDen,
                    ngaySuDung
            );
        } else if (VE_THANG.equalsIgnoreCase(loaiVe)) {
            LoaiVeThang loaiVeThang = LoaiVeThang.valueOf(duLieu[6].trim());
            LocalDate ngayBatDau = LocalDate.parse(duLieu[7].trim());
            LocalDate ngayHetHan = LocalDate.parse(duLieu[8].trim());

            ve = new VeThang(
                    maVe,
                    hanhKhach,
                    ngayMua,
                    giaVe,
                    loaiVeThang,
                    trangThai,
                    ngayBatDau,
                    ngayHetHan
            );
        } else {
            throw new IllegalArgumentException(
                    "Loại vé không được hỗ trợ"
            );
        }

        kiemTraVe(ve);

        return ve;
    }

    private boolean chuyenChuoiThanhBoolean(String giaTri) {
        String x = giaTri.trim();

        if (!x.equalsIgnoreCase("true") && !x.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException(
                    "Trạng thái vé phải là true hoặc false"
            );
        }

        return Boolean.parseBoolean(x);
    }

    private String chuyenVeThanhDong(VeMetro x) {
        String thongTinChung = x.getLoaiVe()
                + KY_TU_PHAN_CACH
                + x.getMaVe()
                + KY_TU_PHAN_CACH
                + x.getHanhKhach().getMaHanhKhach()
                + KY_TU_PHAN_CACH
                + x.getNgayMua()
                + KY_TU_PHAN_CACH
                + x.getGiaVe()
                + KY_TU_PHAN_CACH
                + x.isTrangThai();

        if (x instanceof VeLuot) {
            VeLuot veLuot = (VeLuot) x;

            return thongTinChung
                    + KY_TU_PHAN_CACH
                    + veLuot.getGaDi().getMaGa()
                    + KY_TU_PHAN_CACH
                    + veLuot.getGaDen().getMaGa()
                    + KY_TU_PHAN_CACH
                    + veLuot.getNgaySuDung();
        }

        if (x instanceof VeThang) {
            VeThang veThang = (VeThang) x;

            return thongTinChung
                    + KY_TU_PHAN_CACH
                    + veThang.getLoaiVeThang().name()
                    + KY_TU_PHAN_CACH
                    + veThang.getNgayBatDau()
                    + KY_TU_PHAN_CACH
                    + veThang.getNgayHetHan();
        }

        throw new IllegalArgumentException(
                "Loại vé không được hỗ trợ"
        );
    }

    private void kiemTraVe(VeMetro x) {
        if (x == null) {
            throw new IllegalArgumentException(
                    "Vé không được null"
            );
        }

        if (x.getMaVe() == null || x.getMaVe().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã vé không được để trống"
            );
        }

        if (x.getHanhKhach() == null) {
            throw new IllegalArgumentException(
                    "Hành khách của vé không được để trống"
            );
        }

        if (x.getHanhKhach().getMaHanhKhach() == null
                || x.getHanhKhach().getMaHanhKhach().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã hành khách của vé không được để trống"
            );
        }

        if (x.getNgayMua() == null) {
            throw new IllegalArgumentException(
                    "Ngày mua vé không được để trống"
            );
        }

        if (x.getGiaVe() < 0) {
            throw new IllegalArgumentException(
                    "Giá vé không được nhỏ hơn 0"
            );
        }

        if (x.getMaVe().contains(KY_TU_PHAN_CACH)
                || x.getHanhKhach().getMaHanhKhach().contains(KY_TU_PHAN_CACH)) {
            throw new IllegalArgumentException(
                    "Mã vé và mã hành khách không được chứa ký tự |"
            );
        }

        if (x instanceof VeLuot) {
            VeLuot veLuot = (VeLuot) x;

            if (veLuot.getGaDi() == null
                    || veLuot.getGaDen() == null
                    || veLuot.getNgaySuDung() == null) {
                throw new IllegalArgumentException(
                        "Thông tin vé lượt không được để trống"
                );
            }

            if (veLuot.getGaDi().getMaGa().equalsIgnoreCase(
                    veLuot.getGaDen().getMaGa()
            )) {
                throw new IllegalArgumentException(
                        "Ga đi và ga đến không được giống nhau"
                );
            }
        } else if (x instanceof VeThang) {
            VeThang veThang = (VeThang) x;

            if (veThang.getLoaiVeThang() == null
                    || veThang.getNgayBatDau() == null
                    || veThang.getNgayHetHan() == null) {
                throw new IllegalArgumentException(
                        "Thông tin vé tháng không được để trống"
                );
            }

            if (veThang.getNgayHetHan().isBefore(veThang.getNgayBatDau())) {
                throw new IllegalArgumentException(
                        "Ngày hết hạn không được trước ngày bắt đầu"
                );
            }
        } else {
            throw new IllegalArgumentException(
                    "Loại vé không được hỗ trợ"
            );
        }
    }

    public VeMetro timTheoMaVe(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return null;
        }

        for (VeMetro x : docDanhSachVe()) {
            if (x.getMaVe().equalsIgnoreCase(maVe.trim())) {
                return x;
            }
        }

        return null;
    }

    public List<VeMetro> timTheoMaHanhKhach(String maHanhKhach) {
        List<VeMetro> ketQua = new ArrayList<>();

        if (maHanhKhach == null || maHanhKhach.trim().isEmpty()) {
            return ketQua;
        }

        for (VeMetro x : docDanhSachVe()) {
            if (x.getHanhKhach().getMaHanhKhach().equalsIgnoreCase(
                    maHanhKhach.trim()
            )) {
                ketQua.add(x);
            }
        }

        return ketQua;
    }

    public boolean themVe(VeMetro x) {
        kiemTraVe(x);

        if (timTheoMaVe(x.getMaVe()) != null) {
            return false;
        }

        kiemTraLienKetDaLuu(x);

        FileHandler.ghiThem(Constants.FILE_VE_DA_BAN, chuyenVeThanhDong(x));

        return true;
    }

    public void luuDanhSachVe(List<VeMetro> danhSachVe) {
        if (danhSachVe == null) {
            throw new IllegalArgumentException(
                    "Danh sách vé không được null"
            );
        }

        List<String> danhSachDong = new ArrayList<>();

        for (VeMetro x : danhSachVe) {
            kiemTraVe(x);
            kiemTraLienKetDaLuu(x);
            danhSachDong.add(chuyenVeThanhDong(x));
        }

        FileHandler.ghiFile(Constants.FILE_VE_DA_BAN, danhSachDong);
    }

    public String sinhMaVeLuotMoi() {
        return sinhMaVeMoi("VL");
    }

    public String sinhMaVeThangMoi() {
        return sinhMaVeMoi("VT");
    }

    private String sinhMaVeMoi(String tienTo) {
        int soLonNhat = 0;

        for (VeMetro x : docDanhSachVe()) {
            String maVe = x.getMaVe();

            if (!maVe.toUpperCase().startsWith(tienTo)) {
                continue;
            }

            try {
                int soThuTu = Integer.parseInt(maVe.substring(tienTo.length()));

                if (soThuTu > soLonNhat) {
                    soLonNhat = soThuTu;
                }
            } catch (NumberFormatException e) {
                // bỏ qua mã không đúng định dạng VL001 hoặc VT001
            }
        }

        return String.format("%s%03d", tienTo, soLonNhat + 1);
    }

    private void kiemTraLienKetDaLuu(VeMetro x) {
        if (hanhKhachDataService.timTheoMa(
                x.getHanhKhach().getMaHanhKhach()
        ) == null) {
            throw new IllegalArgumentException(
                    "Hành khách của vé chưa được lưu"
            );
        }

        if (x instanceof VeLuot) {
            VeLuot veLuot = (VeLuot) x;

            if (gaDataService.timTheoMa(veLuot.getGaDi().getMaGa()) == null
                    || gaDataService.timTheoMa(veLuot.getGaDen().getMaGa()) == null) {
                throw new IllegalArgumentException(
                        "Ga của vé lượt chưa được lưu"
                );
            }
        }
    }
}
