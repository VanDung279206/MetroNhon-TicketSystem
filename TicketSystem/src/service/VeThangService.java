package service;

import model.HanhKhach;
import model.LoaiVeThang;
import model.VeThang;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VeThangService implements GiaVeThang{
    // vé tháng có thời hạn 30 ngày chứ không phải là 30 hoặc 31 ngày
    private static final int SO_NGAY_SU_DUNG = 30;

    // số thứ tự dùng để sinh mã vé
    private int soThuTuVe = 1;

    // mua vé tháng
    public VeThang muaVeThang(HanhKhach hanhKhach, LoaiVeThang loaiVe){
        // kiểm tra hành khách
        if (hanhKhach == null){
            throw new IllegalArgumentException(
                    "Hành khách không được để trống"
            );
        }

        //kiểm tra loại vé
        if (loaiVe == null){
            throw new IllegalArgumentException(
                    "Loại vé không được để trống"
            );
        }

        // ngày và thời điểm mua vé
        LocalDateTime ngayMua = LocalDateTime.now();

        // ngày bắt đầu
        LocalDate ngayBatDau = LocalDate.now();

        // ngày hết hạn
        LocalDate ngayHetHan = ngayBatDau.plusDays(
                SO_NGAY_SU_DUNG - 1
        );

        // tính giá vé
        double giaVe = tinhGiaVeThang(loaiVe);

        // sinh mã vẽ
        String maVe = sinhMaVe();

        // vé mới được kích hoạt
        boolean trangThai = true;

        // tạo vé tháng
        return new VeThang(maVe, hanhKhach, ngayMua, giaVe, loaiVe, trangThai, ngayBatDau, ngayHetHan);
    }

    //tính giá vé tháng
    @Override
    public double tinhGiaVeThang(LoaiVeThang loaiVe) {
        if (loaiVe == null){
            throw new IllegalArgumentException(
                    "Loại vé không được để trống"
            );
        }

        switch (loaiVe){
            case PHO_THONG:
                return GIA_VE_THANG_PHO_THONG;
            default:
                throw new IllegalArgumentException(
                        "Loại vé tháng chưa được hỗ trợ"
                );
        }
    }

    // hàm sinh mã vé
    private String sinhMaVe(){
        return String.format(
                "VT%03d",
                soThuTuVe++
        );
    }
}
