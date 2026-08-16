package test;

import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.TaiKhoan;
import model.VaiTro;
import model.VeLuot;
import model.VeThang;

import service.GaService;
import service.TinhGiaVeService;
import service.VeLuotService;
import service.VeThangService;

public class ModelTest {

    public static void main(String[] args) {

        // kiểm tra Model + Service

        // tạo tài khoản
        TaiKhoan taiKhoan = new TaiKhoan(
                "NguyenVanDung",
                "123456",
                VaiTro.HANH_KHACH,
                true
        );

        System.out.println("\n[1] Tài khoản:");
        System.out.println(taiKhoan);

        // tạo hành khách
        HanhKhach hanhKhach = new HanhKhach(
                "HK001",
                "Nguyễn Văn Dũng",
                "0123456789",
                "nguyenvandung@gmail.com",
                taiKhoan
        );

        System.out.println("\n[2] Hành khách:");
        System.out.println(hanhKhach);


        // khởi tạo danh sách ga
        GaService gaService = new GaService();

        System.out.println("\n[3] Danh sách ga:");

        for (Ga ga : gaService.getDanhSachGa()) {

            System.out.println(
                    ga.getMaGa() + " - " +
                            ga.getTenGa()
            );
        }

        // tìm ga
        Ga gaDi = gaService.timTheoMa("G01");
        Ga gaDen = gaService.timTheoMa("G08");

        System.out.println("\n[4] Ga đi / ga đến:");

        System.out.println(
                "Ga đi: " +
                        gaDi.getMaGa() +
                        " - " +
                        gaDi.getTenGa()
        );

        System.out.println(
                "Ga đến: " +
                        gaDen.getMaGa() +
                        " - " +
                        gaDen.getTenGa()
        );


        // tính giá vé lượt
        TinhGiaVeService tinhGiaVeService =
                new TinhGiaVeService();

        double giaVe = tinhGiaVeService.tinhGiaVe(
                gaDi,
                gaDen
        );

        System.out.println("\n[5] Tính giá vé lượt:");

        System.out.println(
                "Giá vé G01 -> G08: " +
                        giaVe +
                        " VND"
        );


        // mua vé lượt
        VeLuotService veLuotService =
                new VeLuotService();

        VeLuot veLuot = veLuotService.muaVeLuot(
                hanhKhach,
                gaDi,
                gaDen
        );

        System.out.println("\n[6] Vé lượt:");

        System.out.println(veLuot);


        // mua vé tháng
        VeThangService veThangService =
                new VeThangService();

        VeThang veThang =
                veThangService.muaVeThang(
                        hanhKhach,
                        LoaiVeThang.PHO_THONG
                );

        System.out.println("\n[7] Vé tháng:");

        System.out.println(veThang);


        // kiểm tra ga đi = ga đến
        System.out.println("\n[8] Kiểm tra ga đi = ga đến:");

        try {

            veLuotService.muaVeLuot(
                    hanhKhach,
                    gaDi,
                    gaDi
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Bắt được lỗi: " +
                            e.getMessage()
            );
        }


        // kiểm tra khách hàng = null
        System.out.println("\n[9] Kiểm tra hành khách null:");

        try {

            veLuotService.muaVeLuot(
                    null,
                    gaDi,
                    gaDen
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Bắt được lỗi: " +
                            e.getMessage()
            );
        }


        // kiểm tra ga đi null
        System.out.println("\n[10] Kiểm tra ga đi null:");

        try {

            veLuotService.muaVeLuot(
                    hanhKhach,
                    null,
                    gaDen
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Bắt được lỗi: " +
                            e.getMessage()
            );
        }


        // kiểm tra loại vé tháng = null
        System.out.println("\n[11] Kiểm tra loại vé tháng null:");

        try {

            veThangService.muaVeThang(
                    hanhKhach,
                    null
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Bắt được lỗi: " +
                            e.getMessage()
            );
        }
    }
}