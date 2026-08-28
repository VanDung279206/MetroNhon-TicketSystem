package service;

import data.LuotSuDungVeDataService;
import data.PhieuHuyVeDataService;
import data.VeDataService;
import exception.VeKhongTheHuyException;
import model.HanhKhach;
import model.PhieuHuyVe;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class HuyVeService {
    public static final double TY_LE_HOAN_VE_LUOT = 0.90;
    public static final double TY_LE_HOAN_VE_THANG = 0.80;

    private final VeDataService veDataService;
    private final LuotSuDungVeDataService luotSuDungDataService;
    private final PhieuHuyVeDataService phieuHuyDataService;
    private final ViTienService viTienService;

    public HuyVeService() {
        veDataService = new VeDataService();
        luotSuDungDataService = new LuotSuDungVeDataService();
        phieuHuyDataService = new PhieuHuyVeDataService();
        viTienService = new ViTienService();
    }

    public PhieuHuyVe huyVe(HanhKhach hanhKhach, String maVe, String lyDo) {
        List<VeMetro> danhSachVe = veDataService.docDanhSachVe();
        VeMetro ve = timVaKiemTraVe(hanhKhach, maVe, danhSachVe);
        double tyLeHoan = getTyLeHoan(ve);
        double soTienHoan = Math.round(ve.getGiaVe() * tyLeHoan);
        String lyDoDaChuanHoa = chuanHoaLyDo(lyDo);

        PhieuHuyVe phieuHuy = new PhieuHuyVe(
                phieuHuyDataService.sinhMaPhieuHuyMoi(),
                ve.getMaVe(), hanhKhach.getMaHanhKhach(),
                LocalDateTime.now(), ve.getGiaVe(), tyLeHoan,
                soTienHoan, lyDoDaChuanHoa
        );

        boolean daVoHieuHoaVe = false;
        boolean daHoanTien = false;
        try {
            ve.setTrangThai(false);
            veDataService.luuDanhSachVe(danhSachVe);
            daVoHieuHoaVe = true;

            viTienService.hoanTien(
                    hanhKhach.getTaiKhoan(), soTienHoan
            );
            daHoanTien = true;

            if (!phieuHuyDataService.themPhieuHuy(phieuHuy)) {
                throw new VeKhongTheHuyException(
                        "Vé đã có giao dịch hủy trước đó"
                );
            }
            return phieuHuy;
        } catch (RuntimeException loiHuyVe) {
            if (daHoanTien) {
                try {
                    viTienService.thanhToan(
                            hanhKhach.getTaiKhoan(), soTienHoan
                    );
                } catch (RuntimeException loiThuHoiTien) {
                    loiHuyVe.addSuppressed(loiThuHoiTien);
                }
            }
            if (daVoHieuHoaVe) {
                try {
                    ve.setTrangThai(true);
                    veDataService.luuDanhSachVe(danhSachVe);
                } catch (RuntimeException loiKhoiPhucVe) {
                    loiHuyVe.addSuppressed(loiKhoiPhucVe);
                }
            }
            throw loiHuyVe;
        }
    }

    public double tinhTienHoanDuKien(HanhKhach hanhKhach, String maVe) {
        VeMetro ve = timVaKiemTraVe(
                hanhKhach, maVe, veDataService.docDanhSachVe()
        );
        return Math.round(ve.getGiaVe() * getTyLeHoan(ve));
    }

    public boolean daHuyVe(String maVe) {
        return phieuHuyDataService.timTheoMaVe(maVe) != null;
    }

    public PhieuHuyVe timPhieuHuyTheoMaVe(String maVe) {
        return phieuHuyDataService.timTheoMaVe(maVe);
    }

    public List<PhieuHuyVe> getDanhSachPhieuHuy() {
        return Collections.unmodifiableList(
                phieuHuyDataService.docDanhSachPhieuHuy()
        );
    }

    public List<PhieuHuyVe> getDanhSachCuaHanhKhach(String maHanhKhach) {
        return Collections.unmodifiableList(
                phieuHuyDataService.timTheoMaHanhKhach(maHanhKhach)
        );
    }

    private VeMetro timVaKiemTraVe(HanhKhach hanhKhach, String maVe,
                                   List<VeMetro> danhSachVe) {
        kiemTraHanhKhach(hanhKhach);
        if (maVe == null || maVe.trim().isEmpty()) {
            throw new VeKhongTheHuyException("Mã vé không được để trống");
        }

        VeMetro ve = null;
        for (VeMetro x : danhSachVe) {
            if (x.getMaVe().equalsIgnoreCase(maVe.trim())) {
                ve = x;
                break;
            }
        }
        if (ve == null) {
            throw new VeKhongTheHuyException("Không tìm thấy vé " + maVe);
        }
        if (!ve.getHanhKhach().getMaHanhKhach().equalsIgnoreCase(
                hanhKhach.getMaHanhKhach())) {
            throw new VeKhongTheHuyException(
                    "Vé không thuộc tài khoản đang đăng nhập"
            );
        }
        if (phieuHuyDataService.timTheoMaVe(ve.getMaVe()) != null) {
            throw new VeKhongTheHuyException("Vé đã được hủy trước đó");
        }
        if (luotSuDungDataService.demTheoMaVe(ve.getMaVe()) > 0) {
            throw new VeKhongTheHuyException(
                    "Vé đã có lượt sử dụng nên không thể hủy"
            );
        }
        if (!ve.isConHieuLuc()) {
            throw new VeKhongTheHuyException(
                    "Chỉ có thể hủy vé chưa sử dụng và còn hiệu lực"
            );
        }
        return ve;
    }

    private double getTyLeHoan(VeMetro ve) {
        if (ve instanceof VeLuot) {
            return TY_LE_HOAN_VE_LUOT;
        }
        if (ve instanceof VeThang) {
            return TY_LE_HOAN_VE_THANG;
        }
        throw new VeKhongTheHuyException("Loại vé chưa hỗ trợ hủy");
    }

    private void kiemTraHanhKhach(HanhKhach hanhKhach) {
        if (hanhKhach == null || hanhKhach.getTaiKhoan() == null) {
            throw new VeKhongTheHuyException("Hành khách không hợp lệ");
        }
        if (!hanhKhach.getTaiKhoan().isTrangThai()) {
            throw new VeKhongTheHuyException("Tài khoản đã bị khóa");
        }
    }

    private String chuanHoaLyDo(String lyDo) {
        String ketQua = lyDo == null ? "" : lyDo.trim();
        if (ketQua.isEmpty()) {
            ketQua = "Hành khách chủ động hủy vé";
        }
        if (ketQua.length() > 200) {
            throw new IllegalArgumentException(
                    "Lý do hủy không được dài quá 200 ký tự"
            );
        }
        if (ketQua.contains("|")) {
            throw new IllegalArgumentException(
                    "Lý do hủy không được chứa ký tự |"
            );
        }
        return ketQua;
    }
}