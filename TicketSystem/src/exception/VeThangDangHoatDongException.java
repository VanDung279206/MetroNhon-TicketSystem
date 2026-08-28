package exception;

import model.VeThang;

public class VeThangDangHoatDongException extends IllegalStateException {
    public VeThangDangHoatDongException(VeThang veThang) {
        super("Bạn đang có vé tháng " + veThang.getMaVe()
                + " còn hiệu lực đến " + veThang.getNgayHetHan()
                + ". Không thể mua thêm vé tháng.");
    }
}