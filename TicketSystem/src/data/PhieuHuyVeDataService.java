package data;

import model.PhieuHuyVe;
import utils.Constants;
import utils.FileHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuHuyVeDataService {
    private static final String KY_TU_PHAN_CACH = "|";
    private static final String TIEN_TO_MA = "HV";

    public List<PhieuHuyVe> docDanhSachPhieuHuy() {
        List<PhieuHuyVe> ketQua = new ArrayList<>();
        for (String dong : FileHandler.docFile(Constants.FILE_LICH_SU_HUY_VE)) {
            if (dong == null || dong.trim().isEmpty()) {
                continue;
            }
            try {
                ketQua.add(chuyenDongThanhPhieuHuy(dong));
            } catch (RuntimeException e) {
                System.out.println("Bỏ qua dòng hủy vé không hợp lệ: " + dong);
            }
        }
        return ketQua;
    }

    public boolean themPhieuHuy(PhieuHuyVe phieuHuy) {
        kiemTra(phieuHuy);
        if (timTheoMaPhieu(phieuHuy.getMaPhieuHuy()) != null
                || timTheoMaVe(phieuHuy.getMaVe()) != null) {
            return false;
        }
        FileHandler.ghiThem(
                Constants.FILE_LICH_SU_HUY_VE,
                chuyenPhieuHuyThanhDong(phieuHuy)
        );
        return true;
    }

    public PhieuHuyVe timTheoMaPhieu(String maPhieuHuy) {
        if (isRong(maPhieuHuy)) {
            return null;
        }
        for (PhieuHuyVe phieu : docDanhSachPhieuHuy()) {
            if (phieu.getMaPhieuHuy().equalsIgnoreCase(maPhieuHuy.trim())) {
                return phieu;
            }
        }
        return null;
    }

    public PhieuHuyVe timTheoMaVe(String maVe) {
        if (isRong(maVe)) {
            return null;
        }
        for (PhieuHuyVe phieu : docDanhSachPhieuHuy()) {
            if (phieu.getMaVe().equalsIgnoreCase(maVe.trim())) {
                return phieu;
            }
        }
        return null;
    }

    public List<PhieuHuyVe> timTheoMaHanhKhach(String maHanhKhach) {
        List<PhieuHuyVe> ketQua = new ArrayList<>();
        if (isRong(maHanhKhach)) {
            return ketQua;
        }
        for (PhieuHuyVe phieu : docDanhSachPhieuHuy()) {
            if (phieu.getMaHanhKhach().equalsIgnoreCase(
                    maHanhKhach.trim())) {
                ketQua.add(phieu);
            }
        }
        return ketQua;
    }

    public String sinhMaPhieuHuyMoi() {
        int lonNhat = 0;
        for (PhieuHuyVe phieu : docDanhSachPhieuHuy()) {
            String ma = phieu.getMaPhieuHuy();
            if (!ma.toUpperCase().startsWith(TIEN_TO_MA)) {
                continue;
            }
            try {
                lonNhat = Math.max(lonNhat,
                        Integer.parseInt(ma.substring(TIEN_TO_MA.length())));
            } catch (NumberFormatException ignored) {
                // Bỏ qua mã không đúng dạng HV001.
            }
        }
        return String.format("HV%03d", lonNhat + 1);
    }

    private PhieuHuyVe chuyenDongThanhPhieuHuy(String dong) {
        String[] duLieu = dong.split("\\|", -1);
        if (duLieu.length != 8) {
            throw new IllegalArgumentException(
                    "Dòng lịch sử hủy vé phải có 8 thành phần"
            );
        }
        PhieuHuyVe phieu = new PhieuHuyVe(
                duLieu[0].trim(), duLieu[1].trim(), duLieu[2].trim(),
                LocalDateTime.parse(duLieu[3].trim()),
                Double.parseDouble(duLieu[4].trim()),
                Double.parseDouble(duLieu[5].trim()),
                Double.parseDouble(duLieu[6].trim()),
                duLieu[7].trim()
        );
        kiemTra(phieu);
        return phieu;
    }

    private String chuyenPhieuHuyThanhDong(PhieuHuyVe phieu) {
        return phieu.getMaPhieuHuy()
                + KY_TU_PHAN_CACH + phieu.getMaVe()
                + KY_TU_PHAN_CACH + phieu.getMaHanhKhach()
                + KY_TU_PHAN_CACH + phieu.getThoiGianHuy()
                + KY_TU_PHAN_CACH + phieu.getGiaVeGoc()
                + KY_TU_PHAN_CACH + phieu.getTyLeHoan()
                + KY_TU_PHAN_CACH + phieu.getSoTienHoan()
                + KY_TU_PHAN_CACH + phieu.getLyDo();
    }

    private void kiemTra(PhieuHuyVe phieu) {
        if (phieu == null
                || isRong(phieu.getMaPhieuHuy())
                || isRong(phieu.getMaVe())
                || isRong(phieu.getMaHanhKhach())
                || phieu.getThoiGianHuy() == null
                || !Double.isFinite(phieu.getGiaVeGoc())
                || phieu.getGiaVeGoc() <= 0
                || !Double.isFinite(phieu.getTyLeHoan())
                || phieu.getTyLeHoan() <= 0 || phieu.getTyLeHoan() > 1
                || !Double.isFinite(phieu.getSoTienHoan())
                || phieu.getSoTienHoan() <= 0
                || isRong(phieu.getLyDo())) {
            throw new IllegalArgumentException(
                    "Thông tin phiếu hủy vé không hợp lệ"
            );
        }
        if (phieu.getMaPhieuHuy().contains(KY_TU_PHAN_CACH)
                || phieu.getMaVe().contains(KY_TU_PHAN_CACH)
                || phieu.getMaHanhKhach().contains(KY_TU_PHAN_CACH)
                || phieu.getLyDo().contains(KY_TU_PHAN_CACH)) {
            throw new IllegalArgumentException(
                    "Thông tin phiếu hủy không được chứa ký tự |"
            );
        }
    }

    private boolean isRong(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty();
    }
}