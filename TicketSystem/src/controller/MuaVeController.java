package controller;

import data.VeDataService;
import exception.GaKhongHopLeException;
import exception.VeThangDangHoatDongException;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.LuotSuDungVe;
import model.PhieuHuyVe;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;
import service.GaService;
import service.HuyVeService;
import service.TinhGiaVeService;
import service.SuDungVeService;
import service.VeLuotService;
import service.VeThangService;
import service.ViTienService;

import java.util.Collections;
import java.util.List;

public class MuaVeController {
    private final GaService gaService;
    private final VeLuotService veLuotService;
    private final VeThangService veThangService;
    private final VeDataService veDataService;
    private final TinhGiaVeService tinhGiaVeService;
    private final ViTienService viTienService;
    private final SuDungVeService suDungVeService;
    private final HuyVeService huyVeService;

    public MuaVeController() {
        gaService = new GaService();
        veLuotService = new VeLuotService();
        veThangService = new VeThangService();
        veDataService = new VeDataService();
        tinhGiaVeService = new TinhGiaVeService();
        viTienService = new ViTienService();
        suDungVeService = new SuDungVeService();
        huyVeService = new HuyVeService();
    }

    public List<Ga> getDanhSachGa() {
        return gaService.getDanhSachGa();
    }

    public VeLuot muaVeLuot(HanhKhach hanhKhach, String maGaDi, String maGaDen) {
        kiemTraHanhKhachDangHoatDong(hanhKhach);
        if (isRong(maGaDi) || isRong(maGaDen)) {
            throw new GaKhongHopLeException(
                    "Mã ga đi và mã ga đến không được để trống"
            );
        }

        Ga gaDi = gaService.timTheoMa(maGaDi.trim());
        Ga gaDen = gaService.timTheoMa(maGaDen.trim());

        if (gaDi == null) {
            throw new GaKhongHopLeException("Không tìm thấy ga đi: " + maGaDi);
        }
        if (gaDen == null) {
            throw new GaKhongHopLeException("Không tìm thấy ga đến: " + maGaDen);
        }

        double giaVe = tinhGiaVeService.tinhGiaVe(gaDi, gaDen);
        return thanhToanVaTaoVe(
                hanhKhach, giaVe,
                () -> veLuotService.muaVeLuot(hanhKhach, gaDi, gaDen)
        );
    }

    public VeThang muaVeThang(HanhKhach hanhKhach, LoaiVeThang loaiVe) {
        kiemTraHanhKhachDangHoatDong(hanhKhach);
        VeThang veDangHoatDong = timVeThangConHieuLuc(
                hanhKhach.getMaHanhKhach()
        );
        if (veDangHoatDong != null) {
            throw new VeThangDangHoatDongException(veDangHoatDong);
        }
        double giaVe = veThangService.tinhGiaVeThang(loaiVe);
        return thanhToanVaTaoVe(
                hanhKhach, giaVe,
                () -> veThangService.muaVeThang(hanhKhach, loaiVe)
        );
    }

    public double napTien(HanhKhach hanhKhach, double soTien) {
        kiemTraHanhKhachDangHoatDong(hanhKhach);
        return viTienService.napTien(hanhKhach.getTaiKhoan(), soTien);
    }

    public double getSoDu(HanhKhach hanhKhach) {
        if (hanhKhach == null || hanhKhach.getTaiKhoan() == null) {
            return 0;
        }
        return viTienService.laySoDu(hanhKhach.getTaiKhoan());
    }

    public boolean veConHieuLuc(VeMetro ve) {
        return ve != null && ve.isConHieuLuc();
    }

    public LuotSuDungVe suDungVe(HanhKhach hanhKhach, String maVe,
                                 String maGaDi, String maGaDen) {
        kiemTraHanhKhachDangHoatDong(hanhKhach);
        return suDungVeService.suDungVe(
                hanhKhach, maVe, maGaDi, maGaDen
        );
    }

    public VeMetro timVeCuaHanhKhach(HanhKhach hanhKhach, String maVe) {
        if (hanhKhach == null || isRong(maVe)) {
            return null;
        }
        VeMetro ve = veDataService.timTheoMaVe(maVe.trim());
        if (ve == null || !ve.getHanhKhach().getMaHanhKhach()
                .equalsIgnoreCase(hanhKhach.getMaHanhKhach())) {
            return null;
        }
        return ve;
    }

    public List<LuotSuDungVe> getDanhSachLuotSuDung() {
        return suDungVeService.getDanhSachLuotSuDung();
    }

    public List<LuotSuDungVe> getDanhSachLuotSuDungCuaHanhKhach(
            String maHanhKhach) {
        return suDungVeService.getDanhSachCuaHanhKhach(maHanhKhach);
    }

    public int getSoLuotSuDung(String maVe) {
        return suDungVeService.demSoLuotSuDung(maVe);
    }

    public PhieuHuyVe huyVe(HanhKhach hanhKhach, String maVe, String lyDo) {
        kiemTraHanhKhachDangHoatDong(hanhKhach);
        return huyVeService.huyVe(hanhKhach, maVe, lyDo);
    }

    public double tinhTienHoanDuKien(HanhKhach hanhKhach, String maVe) {
        return huyVeService.tinhTienHoanDuKien(hanhKhach, maVe);
    }

    public boolean daHuyVe(String maVe) {
        return huyVeService.daHuyVe(maVe);
    }

    public List<PhieuHuyVe> getDanhSachPhieuHuyVe() {
        return huyVeService.getDanhSachPhieuHuy();
    }

    public List<PhieuHuyVe> getDanhSachPhieuHuyCuaHanhKhach(
            String maHanhKhach) {
        return huyVeService.getDanhSachCuaHanhKhach(maHanhKhach);
    }

    public String getTenGa(String maGa) {
        Ga ga = gaService.timTheoMa(maGa);
        return ga == null ? maGa : ga.getTenGa();
    }

    public List<VeMetro> getDanhSachVeDaBan() {
        return Collections.unmodifiableList(veDataService.docDanhSachVe());
    }

    public List<VeMetro> getDanhSachVeCuaHanhKhach(String maHanhKhach) {
        return Collections.unmodifiableList(
                veDataService.timTheoMaHanhKhach(maHanhKhach)
        );
    }

    private boolean isRong(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty();
    }

    private void kiemTraHanhKhachDangHoatDong(HanhKhach hanhKhach) {
        if (hanhKhach == null || hanhKhach.getTaiKhoan() == null) {
            throw new IllegalArgumentException("Hành khách không được để trống");
        }
        if (!hanhKhach.getTaiKhoan().isTrangThai()) {
            throw new IllegalStateException("Tài khoản hành khách đã bị khóa");
        }
    }

    private VeThang timVeThangConHieuLuc(String maHanhKhach) {
        for (VeMetro ve : veDataService.timTheoMaHanhKhach(maHanhKhach)) {
            if (ve instanceof VeThang && ve.isConHieuLuc()) {
                return (VeThang) ve;
            }
        }
        return null;
    }

    private <T extends VeMetro> T thanhToanVaTaoVe(
            HanhKhach hanhKhach, double giaVe, TaoVe<T> taoVe) {
        viTienService.thanhToan(hanhKhach.getTaiKhoan(), giaVe);
        try {
            return taoVe.tao();
        } catch (RuntimeException loiTaoVe) {
            try {
                viTienService.hoanTien(hanhKhach.getTaiKhoan(), giaVe);
            } catch (RuntimeException loiHoanTien) {
                loiTaoVe.addSuppressed(loiHoanTien);
            }
            throw loiTaoVe;
        }
    }

    @FunctionalInterface
    private interface TaoVe<T extends VeMetro> {
        T tao();
    }
}