package service;

import model.Ga;

import java.util.ArrayList;
import java.util.List;

public class GaService {
    private List<Ga> danhSachGa;

    public GaService() {
        danhSachGa = new ArrayList<>();
        khoiTaoDanhSachGa();
    }

    // tạo 8 ga Metro Nhổn - Cầu Giấy
    private void khoiTaoDanhSachGa() {
        danhSachGa.add(
                new Ga(
                        "G01",
                        "Nhổn",
                        "Bắc Từ Liêm",
                        1
                )
        );

        danhSachGa.add(
                new Ga(
                        "G02",
                        "Minh Khai",
                        "Bắc Từ Liêm",
                        2
                )
        );

        danhSachGa.add(
                new Ga(
                        "G03",
                        "Phú Diễn",
                        "Bắc Từ Liêm",
                        3
                )
        );

        danhSachGa.add(
                new Ga(
                        "G04",
                        "Cầu Diễn",
                        "Nam Từ Liêm",
                        4
                )
        );

        danhSachGa.add(
                new Ga(
                        "G05",
                        "Lê Đức Thọ",
                        "Nam Từ Liêm",
                        5
                )
        );

        danhSachGa.add(
                new Ga(
                        "G06",
                        "Đại học Quốc gia Hà Nội",
                        "Cầu Giấy",
                        6
                )
        );

        danhSachGa.add(
                new Ga(
                        "G07",
                        "Chùa Hà",
                        "Cầu Giấy",
                        7
                )
        );

        danhSachGa.add(
                new Ga(
                        "G08",
                        "Cầu Giấy",
                        "Cầu Giấy",
                        8
                )
        );
    }

    public List<Ga> getDanhSachGa() {
        return danhSachGa;
    }// lấy toàn bộ danh sách ga

    // tìm theo mã ga
    public Ga timTheoMa(String maGa) {
        if (maGa == null || maGa.trim().isEmpty()) {
            return null;
        }

        for (Ga x : danhSachGa) {
            if (x.getMaGa().equalsIgnoreCase(maGa)) {
                return x;
            }
        }

        return null;
    }

    // tìm theo tên ga
    public Ga timGaTheoTen(String tenGa) {
        if (tenGa == null || tenGa.trim().isEmpty()) {
            return null;
        }

        for (Ga x : danhSachGa) {
            if (x.getTenGa().equalsIgnoreCase(tenGa)) {
                return x;
            }
        }

        return null;
    }

    // hiển thị danh sách ga
    public void hienThiDanhSachGa() {
        for (Ga x : danhSachGa) {
            System.out.println(
                    x.getMaGa() + " - " + x.getTenGa()
            );
        }
    }
}
