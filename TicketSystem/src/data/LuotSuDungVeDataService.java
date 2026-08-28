package data;

import model.LuotSuDungVe;
import utils.Constants;
import utils.FileHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LuotSuDungVeDataService {
    private static final String KY_TU_PHAN_CACH = "|";
    private static final String TIEN_TO_MA = "SD";

    public List<LuotSuDungVe> docDanhSachLuotSuDung() {
        List<LuotSuDungVe> ketQua = new ArrayList<>();
        for (String dong : FileHandler.docFile(Constants.FILE_LICH_SU_SU_DUNG)) {
            if (dong == null || dong.trim().isEmpty()) {
                continue;
            }
            try {
                ketQua.add(chuyenDongThanhLuotSuDung(dong));
            } catch (RuntimeException e) {
                System.out.println("Bỏ qua dòng sử dụng vé không hợp lệ: " + dong);
            }
        }
        return ketQua;
    }

    public boolean themLuotSuDung(LuotSuDungVe luotSuDung) {
        kiemTra(luotSuDung);
        if (timTheoMa(luotSuDung.getMaLuotSuDung()) != null) {
            return false;
        }
        FileHandler.ghiThem(
                Constants.FILE_LICH_SU_SU_DUNG,
                chuyenLuotSuDungThanhDong(luotSuDung)
        );
        return true;
    }

    public LuotSuDungVe timTheoMa(String maLuotSuDung) {
        if (isRong(maLuotSuDung)) {
            return null;
        }
        for (LuotSuDungVe luot : docDanhSachLuotSuDung()) {
            if (luot.getMaLuotSuDung().equalsIgnoreCase(maLuotSuDung.trim())) {
                return luot;
            }
        }
        return null;
    }

    public List<LuotSuDungVe> timTheoMaHanhKhach(String maHanhKhach) {
        List<LuotSuDungVe> ketQua = new ArrayList<>();
        if (isRong(maHanhKhach)) {
            return ketQua;
        }
        for (LuotSuDungVe luot : docDanhSachLuotSuDung()) {
            if (luot.getMaHanhKhach().equalsIgnoreCase(maHanhKhach.trim())) {
                ketQua.add(luot);
            }
        }
        return ketQua;
    }

    public int demTheoMaVe(String maVe) {
        if (isRong(maVe)) {
            return 0;
        }
        int soLuot = 0;
        for (LuotSuDungVe luot : docDanhSachLuotSuDung()) {
            if (luot.getMaVe().equalsIgnoreCase(maVe.trim())) {
                soLuot++;
            }
        }
        return soLuot;
    }

    public String sinhMaLuotSuDungMoi() {
        int lonNhat = 0;
        for (LuotSuDungVe luot : docDanhSachLuotSuDung()) {
            String ma = luot.getMaLuotSuDung();
            if (!ma.toUpperCase().startsWith(TIEN_TO_MA)) {
                continue;
            }
            try {
                lonNhat = Math.max(lonNhat,
                        Integer.parseInt(ma.substring(TIEN_TO_MA.length())));
            } catch (NumberFormatException ignored) {
                // Bỏ qua mã không đúng dạng SD001.
            }
        }
        return String.format("SD%03d", lonNhat + 1);
    }

    private LuotSuDungVe chuyenDongThanhLuotSuDung(String dong) {
        String[] duLieu = dong.split("\\|", -1);
        if (duLieu.length != 6) {
            throw new IllegalArgumentException(
                    "Dòng lịch sử sử dụng phải có 6 thành phần"
            );
        }
        LuotSuDungVe luot = new LuotSuDungVe(
                duLieu[0].trim(), duLieu[1].trim(), duLieu[2].trim(),
                duLieu[3].trim(), duLieu[4].trim(),
                LocalDateTime.parse(duLieu[5].trim())
        );
        kiemTra(luot);
        return luot;
    }

    private String chuyenLuotSuDungThanhDong(LuotSuDungVe luot) {
        return luot.getMaLuotSuDung()
                + KY_TU_PHAN_CACH + luot.getMaVe()
                + KY_TU_PHAN_CACH + luot.getMaHanhKhach()
                + KY_TU_PHAN_CACH + luot.getMaGaDi()
                + KY_TU_PHAN_CACH + luot.getMaGaDen()
                + KY_TU_PHAN_CACH + luot.getThoiGianSuDung();
    }

    private void kiemTra(LuotSuDungVe luot) {
        if (luot == null
                || isRong(luot.getMaLuotSuDung())
                || isRong(luot.getMaVe())
                || isRong(luot.getMaHanhKhach())
                || isRong(luot.getMaGaDi())
                || isRong(luot.getMaGaDen())
                || luot.getThoiGianSuDung() == null) {
            throw new IllegalArgumentException(
                    "Thông tin lượt sử dụng vé không hợp lệ"
            );
        }
        if (luot.getMaLuotSuDung().contains(KY_TU_PHAN_CACH)
                || luot.getMaVe().contains(KY_TU_PHAN_CACH)
                || luot.getMaHanhKhach().contains(KY_TU_PHAN_CACH)
                || luot.getMaGaDi().contains(KY_TU_PHAN_CACH)
                || luot.getMaGaDen().contains(KY_TU_PHAN_CACH)) {
            throw new IllegalArgumentException(
                    "Thông tin lượt sử dụng không được chứa ký tự |"
            );
        }
    }

    private boolean isRong(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty();
    }
}