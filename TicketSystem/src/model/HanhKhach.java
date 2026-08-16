package model;

public class HanhKhach {
    //phương thức, thuộc tính về hành khách
    private String maHanhKhach;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private TaiKhoan taiKhoan;

    public HanhKhach(String maHanhKhach, String hoTen, String soDienThoai, String email, TaiKhoan taiKhoan) {
        this.maHanhKhach = maHanhKhach;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.taiKhoan = taiKhoan;
    }

    public String getMaHanhKhach() {
        return maHanhKhach;
    }

    public void setMaHanhKhach(String maHanhKhach) {
        this.maHanhKhach = maHanhKhach;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    @Override
    public String toString() {
        return maHanhKhach + " - " + hoTen;
    }
}
