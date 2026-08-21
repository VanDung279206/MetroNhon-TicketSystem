package controller;

import model.*;
import service.GaService;
import service.VeLuotService;
import service.VeThangService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Chức năng:
* Hiển thị danh sách ga thông qua service
* Nhận ga đi, ga đến
* mua vé lượt
* mua vé tháng
* trả kết quả cho view xem
 */

public class MuaVeController {

    private final GaService gaService;
    private final VeLuotService veLuotService;
    private final VeThangService veThangService;

    private final List<VeMetro> danhSachVeDaBan;

    public MuaVeController(){
        gaService = new GaService();
        veLuotService = new VeLuotService();
        veThangService = new VeThangService();

        danhSachVeDaBan = new ArrayList<>();
    }

    // Lấy danh sách ga để View hiển thị.
    public List<Ga> getDanhSachGa(){
        return gaService.getDanhSachGa();
    }

    // Mua vé lượt dựa trên mã ga đi và mã ga đến.
    public VeLuot muaVeLuot(HanhKhach hanhKhach, String maGaDi, String maGaDen){
        if (hanhKhach == null){
            throw new IllegalArgumentException("Mã ga đi và mã ga đến không được để trống");
        }

        if (isRong(maGaDi) || isRong(maGaDen)){
            throw new IllegalArgumentException("Mã ga đi và mã ga đến không được để trống");
        }

        // Tìm ga đi và ga đến theo mã
        Ga gaDi = gaService.timTheoMa(maGaDi.trim());

        Ga gaDen = gaService.timTheoMa(maGaDen.trim());

        if (gaDi == null){
            throw new IllegalArgumentException("Không tìm thấy ga đến : " + maGaDi);
        }

        if (gaDen == null){
            throw  new IllegalArgumentException("Không tìm thấy ga đến : " + maGaDen);
        }

        VeLuot veLuot = veLuotService.muaVeLuot(hanhKhach, gaDi, gaDen);

        danhSachVeDaBan.add(veLuot);

        return veLuot;
    }


    //  Mua ve thang
    public VeThang muaVeThang(HanhKhach hanhKhach, LoaiVeThang loaiVe){
        VeThang veThang = veThangService.muaVeThang(hanhKhach, loaiVe);
        danhSachVeDaBan.add(veThang);
        return veThang;
    }

    // Lấy danh sách vẽ đã bán
    public List<VeMetro> getDanhSachVeDaBan(){
        return Collections.unmodifiableList(danhSachVeDaBan);
    }

    // Kiểm tra chuỗi null hoặc rỗng
    private boolean isRong(String giaTri){
        return giaTri == null || giaTri.trim().isEmpty();
    }
}
