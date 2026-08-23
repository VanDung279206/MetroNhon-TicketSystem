package service;

import data.VeDataService;
import model.HanhKhach;
import model.VeLuot;
import model.VeMetro;
import model.VeThang;

import java.util.ArrayList;
import java.util.List;

public class VeService {
    private final VeDataService veDataService;

    public VeService() {
        veDataService = new VeDataService();
    }

    public List<VeMetro> layDanhSachVe() {
        return veDataService.docDanhSachVe();
    }

    public VeMetro timTheoMaVe(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return null;
        }

        return veDataService.timTheoMaVe(maVe);
    }

    public List<VeMetro> timTheoHanhKhach(HanhKhach hanhKhach) {
        List<VeMetro> ketQua = new ArrayList<>();

        if (hanhKhach == null
                || hanhKhach.getMaHanhKhach() == null
                || hanhKhach.getMaHanhKhach().trim().isEmpty()) {
            return ketQua;
        }

        return veDataService.timTheoMaHanhKhach(
                hanhKhach.getMaHanhKhach()
        );
    }

    public List<VeLuot> layDanhSachVeLuot() {
        List<VeLuot> ketQua = new ArrayList<>();

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            if (ve instanceof VeLuot) {
                ketQua.add((VeLuot) ve);
            }
        }

        return ketQua;
    }

    public List<VeThang> layDanhSachVeThang() {
        List<VeThang> ketQua = new ArrayList<>();

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            if (ve instanceof VeThang) {
                ketQua.add((VeThang) ve);
            }
        }

        return ketQua;
    }

    public boolean kiemTraTrangThai(String maVe) {
        VeMetro ve = timTheoMaVe(maVe);

        if (ve == null) {
            return false;
        }

        return ve.isTrangThai();
    }

    public boolean capNhatTrangThai(String maVe, boolean trangThai) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return false;
        }

        List<VeMetro> danhSachVe = veDataService.docDanhSachVe();

        for (VeMetro ve : danhSachVe) {
            if (ve.getMaVe().equalsIgnoreCase(maVe.trim())) {
                ve.setTrangThai(trangThai);
                veDataService.luuDanhSachVe(danhSachVe);
                return true;
            }
        }

        return false;
    }

    public boolean kichHoatVe(String maVe) {
        return capNhatTrangThai(maVe, true);
    }

    public boolean voHieuHoaVe(String maVe) {
        return capNhatTrangThai(maVe, false);
    }

    public List<VeMetro> layVeDangHoatDong() {
        List<VeMetro> ketQua = new ArrayList<>();

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            if (ve.isTrangThai()) {
                ketQua.add(ve);
            }
        }

        return ketQua;
    }

    public List<VeMetro> layVeKhongHoatDong() {
        List<VeMetro> ketQua = new ArrayList<>();

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            if (!ve.isTrangThai()) {
                ketQua.add(ve);
            }
        }

        return ketQua;
    }

    public double tinhTongDoanhThu() {
        double tong = 0;

        for (VeMetro ve : veDataService.docDanhSachVe()) {
            tong += ve.getGiaVe();
        }

        return tong;
    }
}