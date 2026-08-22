package service;

import data.VeDataService;
import model.Ga;
import model.HanhKhach;
import model.VeLuot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeLuotService {
    private final TinhGiaVeService tinhGiaVeService;
    private final VeDataService veDataService;

    public VeLuotService() {
        tinhGiaVeService = new TinhGiaVeService();
        veDataService = new VeDataService();
    }

    public VeLuot muaVeLuot(HanhKhach hanhKhach, Ga gaDi, Ga gaDen) {
        if (hanhKhach == null) {
            throw new IllegalArgumentException("Hành khách không được để trống");
        }
        if (gaDi == null) {
            throw new IllegalArgumentException("Ga đi không được để trống");
        }
        if (gaDen == null) {
            throw new IllegalArgumentException("Ga đến không được để trống");
        }
        if (gaDi.getMaGa().equalsIgnoreCase(gaDen.getMaGa())) {
            throw new IllegalArgumentException("Ga đi và ga đến không được giống nhau");
        }

        double giaVe = tinhGiaVeService.tinhGiaVe(gaDi, gaDen);
        String maVe = veDataService.sinhMaVeLuotMoi();
        VeLuot veLuot = new VeLuot(
                maVe, hanhKhach, LocalDateTime.now(), giaVe, true,
                gaDi, gaDen, LocalDate.now()
        );

        if (!veDataService.themVe(veLuot)) {
            throw new IllegalStateException("Không thể lưu vé lượt có mã " + maVe);
        }
        return veLuot;
    }
}