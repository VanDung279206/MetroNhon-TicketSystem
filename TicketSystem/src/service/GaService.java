package service;

import data.GaDataService;
import model.Ga;

import java.util.Collections;
import java.util.List;

public class GaService {
    private final GaDataService gaDataService;

    public GaService() {
        gaDataService = new GaDataService();
    }

    // Đọc danh sách ga từ file thay vì tạo cứng trong bộ nhớ.
    public List<Ga> getDanhSachGa() {
        return Collections.unmodifiableList(gaDataService.docDanhSachGa());
    }

    public Ga timTheoMa(String maGa) {
        return gaDataService.timTheoMa(maGa);
    }

    public Ga timGaTheoTen(String tenGa) {
        return gaDataService.timTheoTen(tenGa);
    }

    public void hienThiDanhSachGa() {
        for (Ga x : getDanhSachGa()) {
            System.out.println(x.getMaGa() + " - " + x.getTenGa());
        }
    }
}