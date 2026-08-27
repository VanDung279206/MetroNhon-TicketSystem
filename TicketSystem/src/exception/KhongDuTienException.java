package exception;

public class KhongDuTienException extends IllegalStateException {
    private final double soDuHienTai;
    private final double soTienCanThanhToan;

    public KhongDuTienException(double soDuHienTai, double soTienCanThanhToan) {
        super("Không đủ tiền để thanh toán. Số dư hiện tại: "
                + Math.round(soDuHienTai) + " VND, cần: "
                + Math.round(soTienCanThanhToan) + " VND");
        this.soDuHienTai = soDuHienTai;
        this.soTienCanThanhToan = soTienCanThanhToan;
    }

    public double getSoDuHienTai() {
        return soDuHienTai;
    }

    public double getSoTienCanThanhToan() {
        return soTienCanThanhToan;
    }
}