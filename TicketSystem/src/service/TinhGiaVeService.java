package service;

import exception.GaKhongHopLeException;
import model.Ga;

public class TinhGiaVeService implements GiaVe {
    /*
     * Tính giá vé lượt dựa trên ga đi và ga đến
     * Giá vé = 8k + số ga * 1k
     * */

    @Override
    public double tinhGiaVe(Ga gaDi, Ga gaDen) {
        // Kiểm tra ga đi
        if (gaDi == null) {
            throw new GaKhongHopLeException(
                    "Ga đi không được để trống"
            );
        }

        // Kiểm tra ga đến
        if (gaDen == null) {
            throw new GaKhongHopLeException(
                    "Ga đến không được để trống"
            );
        }

        // Không cho phép ga đi và ga đến giống nhau
        if (gaDi.getMaGa().equalsIgnoreCase(gaDen.getMaGa())) {
            throw new GaKhongHopLeException(
                    "Ga đi và ga đến không được giống nhau"
            );
        }

        // Tính số ga
        int soGa = Math.abs(
                gaDen.getThuTu() - gaDi.getThuTu()
        );

        // Tính giá
        return GIA_CO_BAN + soGa * GIA_MOI_GA;
    }
}