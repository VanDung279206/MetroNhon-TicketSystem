package model;

public class TaiKhoan {
    // quản lý tài khoản đăng nhập
    private String tenDangNhap;
    private String matKhau;
    private VaiTro vaiTro;
    private boolean trangThai;

    public TaiKhoan(String tenDangNhap, String matKhau, VaiTro vaiTro, boolean trangThai) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Tài Khoản{" +
                "Tên Đăng Nhập = " + tenDangNhap + '\'' +
                ", Vai Trò = " + vaiTro +
                ", Trạng Thái = " + trangThai + "}";
    } // không đưa mật khẩu vào in tài khoản (không để lộ thông tin bảo mật)
}
