package controller;

import data.VeDataService;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;
import service.GaService;
import service.VeLuotService;
import service.VeThangService;

import java.util.Collections;
import java.util.List;

public class MuaVeController {
    private final GaService gaService;
    private final VeLuotService veLuotService;
    private final VeThangService veThangService;
    private final VeDataService veDataService;

    public MuaVeController() {
        gaService = new GaService();
        veLuotService = new VeLuotService();
        veThangService = new VeThangService();
        veDataService = new VeDataService();
    }

    public List<Ga> getDanhSachGa() {
        return gaService.getDanhSachGa();
    }

    public VeLuot muaVeLuot(HanhKhach hanhKhach, String maGaDi, String maGaDen) {
        if (hanhKhach == null) {
            throw new IllegalArgumentException("Hành khách không được để trống");
        }
        if (isRong(maGaDi) || isRong(maGaDen)) {
            throw new IllegalArgumentException("Mã ga đi và mã ga đến không được để trống");
        }

        Ga gaDi = gaService.timTheoMa(maGaDi.trim());
        Ga gaDen = gaService.timTheoMa(maGaDen.trim());

        if (gaDi == null) {
            throw new IllegalArgumentException("Không tìm thấy ga đi: " + maGaDi);
        }
        if (gaDen == null) {
            throw new IllegalArgumentException("Không tìm thấy ga đến: " + maGaDen);
        }

        // Service vừa tạo vé vừa lưu xuống file.
        return veLuotService.muaVeLuot(hanhKhach, gaDi, gaDen);
    }

    public VeThang muaVeThang(HanhKhach hanhKhach, LoaiVeThang loaiVe) {
        return veThangService.muaVeThang(hanhKhach, loaiVe);
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
}