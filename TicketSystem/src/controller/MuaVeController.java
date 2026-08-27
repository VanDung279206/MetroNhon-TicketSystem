package controller;

import data.VeDataService;
import exception.GaKhongHopLeException;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;
import service.GaService;
import service.TinhGiaVeService;
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

    public MuaVeController() {
        gaService = new GaService();
        veLuotService = new VeLuotService();
        veThangService = new VeThangService();
        veDataService = new VeDataService();
        tinhGiaVeService = new TinhGiaVeService();
        viTienService = new ViTienService();
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