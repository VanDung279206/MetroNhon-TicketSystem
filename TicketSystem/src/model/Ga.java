package model;

public class Ga {
    // quản lý thông tin ga (mã ga, tên ga, vị trí)
    private String maGa;
    private String tenGa;
    private String viTri;
    private int thuTu; // tính số ga

    public Ga(String maGa, String tenGa, String viTri, int thuTu) {
        this.maGa = maGa;
        this.tenGa = tenGa;
        this.viTri = viTri;
        this.thuTu = thuTu;
    }

    public String getMaGa() {
        return maGa;
    }

    public void setMaGa(String maGa) {
        this.maGa = maGa;
    }

    public String getTenGa() {
        return tenGa;
    }

    public void setTenGa(String tenGa) {
        this.tenGa = tenGa;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public int getThuTu() {
        return thuTu;
    }

    public void setThuTu(int thuTu) {
        this.thuTu = thuTu;
    }

    @Override
    public String toString() {
        return maGa + " - " + tenGa;
    }
}
