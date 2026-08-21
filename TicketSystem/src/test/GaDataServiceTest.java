package test;

import data.GaDataService;
import model.Ga;

import java.util.List;

public class GaDataServiceTest {

    public static void main(String[] args) {
        GaDataService dataService = new GaDataService();
        List<Ga> danhSachGa = dataService.docDanhSachGa();

        System.out.println("Danh sách ga:");

        for (Ga x : danhSachGa) {
            System.out.println(
                    x.getMaGa()
                            + " | "
                            + x.getTenGa()
                            + " | "
                            + x.getViTri()
                            + " | thứ tự "
                            + x.getThuTu()
            );
        }

        Ga gaDau = dataService.timTheoMa("G01");
        Ga gaCuoi = dataService.timTheoMa("G08");

        System.out.println("\nGa đầu: " + gaDau);
        System.out.println("Ga cuối: " + gaCuoi);
        System.out.println("Tổng số ga: " + danhSachGa.size());
    }
}