package service;

import data.LuotSuDungVeDataService;
import data.VeDataService;
import exception.GaKhongHopLeException;
import exception.VeKhongTheSuDungException;
import model.Ga;
import model.HanhKhach;
import model.LuotSuDungVe;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class SuDungVeService {
    private final VeDataService veDataService;
    private final LuotSuDungVeDataService luotSuDungDataService;
    private final GaService gaService;

    public SuDungVeService() {
        veDataService = new VeDataService();
        luotSuDungDataService = new LuotSuDungVeDataService();
        gaService = new GaService();
    }

    public LuotSuDungVe suDungVe(HanhKhach hanhKhach, String maVe,
                                 String maGaDi, String maGaDen) {
        kiemTraHanhKhach(hanhKhach);
        if (maVe == null || maVe.trim().isEmpty()) {
            throw new VeKhongTheSuDungException("Mã vé không được để trống");
        }

        List<VeMetro> danhSachVe = veDataService.docDanhSachVe();
        VeMetro ve = null;
        for (VeMetro x : danhSachVe) {
            if (x.getMaVe().equalsIgnoreCase(maVe.trim())) {
                ve = x;
                break;
            }
        }

        if (ve == null) {
            throw new VeKhongTheSuDungException("Không tìm thấy vé " + maVe);
        }
        if (!ve.getHanhKhach().getMaHanhKhach().equalsIgnoreCase(
                hanhKhach.getMaHanhKhach())) {
            throw new VeKhongTheSuDungException(
                    "Vé không thuộc tài khoản đang đăng nhập"
            );
        }
        if (!ve.isConHieuLuc()) {
            throw new VeKhongTheSuDungException(
                    "Vé " + ve.getMaVe() + " đã hết hiệu lực hoặc đã được sử dụng"
            );
        }

        Ga gaDi;
        Ga gaDen;
        boolean laVeLuot = ve instanceof VeLuot;
        if (laVeLuot) {
            VeLuot veLuot = (VeLuot) ve;
            gaDi = veLuot.getGaDi();
            gaDen = veLuot.getGaDen();
        } else if (ve instanceof VeThang) {
            gaDi = gaService.timTheoMa(maGaDi);
            gaDen = gaService.timTheoMa(maGaDen);
            kiemTraGa(gaDi, gaDen);
        } else {
            throw new VeKhongTheSuDungException("Loại vé chưa được hỗ trợ");
        }

        LuotSuDungVe luotSuDung = new LuotSuDungVe(
                luotSuDungDataService.sinhMaLuotSuDungMoi(),
                ve.getMaVe(), hanhKhach.getMaHanhKhach(),
                gaDi.getMaGa(), gaDen.getMaGa(), LocalDateTime.now()
        );

        if (laVeLuot) {
            ve.setTrangThai(false);
            veDataService.luuDanhSachVe(danhSachVe);
        }

        try {
            if (!luotSuDungDataService.themLuotSuDung(luotSuDung)) {
                throw new IllegalStateException("Không thể lưu lượt sử dụng vé");
            }
        } catch (RuntimeException e) {
            if (laVeLuot) {
                ve.setTrangThai(true);
                veDataService.luuDanhSachVe(danhSachVe);
            }
            throw e;
        }

        return luotSuDung;
    }

    public List<LuotSuDungVe> getDanhSachLuotSuDung() {
        return Collections.unmodifiableList(
                luotSuDungDataService.docDanhSachLuotSuDung()
        );
    }

    public List<LuotSuDungVe> getDanhSachCuaHanhKhach(String maHanhKhach) {
        return Collections.unmodifiableList(
                luotSuDungDataService.timTheoMaHanhKhach(maHanhKhach)
        );
    }

    public int demSoLuotSuDung(String maVe) {
        return luotSuDungDataService.demTheoMaVe(maVe);
    }

    private void kiemTraHanhKhach(HanhKhach hanhKhach) {
        if (hanhKhach == null || hanhKhach.getTaiKhoan() == null) {
            throw new VeKhongTheSuDungException("Hành khách không hợp lệ");
        }
        if (!hanhKhach.getTaiKhoan().isTrangThai()) {
            throw new VeKhongTheSuDungException("Tài khoản đã bị khóa");
        }
    }

    private void kiemTraGa(Ga gaDi, Ga gaDen) {
        if (gaDi == null || gaDen == null) {
            throw new GaKhongHopLeException("Ga đi hoặc ga đến không tồn tại");
        }
        if (gaDi.getMaGa().equalsIgnoreCase(gaDen.getMaGa())) {
            throw new GaKhongHopLeException(
                    "Ga đi và ga đến không được giống nhau"
            );
        }
    }
}