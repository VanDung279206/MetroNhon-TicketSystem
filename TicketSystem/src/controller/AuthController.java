package controller;

import model.TaiKhoan;
import model.VaiTro;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/** Điều khiển đăng ký, đăng nhập, đăng xuất và đổi mật khẩu. */
public class AuthController {
    private static final int SO_VONG_LAP = 120_000;
    private static final int DO_DAI_KHOA_BIT = 256;
    private static final String PHIEN_BAN = "1";

    private final Path fileTaiKhoan;
    private final Map<String, BanGhiTaiKhoan> danhSachTaiKhoan = new LinkedHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private TaiKhoan taiKhoanDangNhap;

    public AuthController() {
        this(Paths.get("data", "tai_khoan.txt"));
    }

    /** Constructor này giúp test bằng một file tạm, không ảnh hưởng dữ liệu thật. */
    public AuthController(Path fileTaiKhoan) {
        if (fileTaiKhoan == null) {
            throw new IllegalArgumentException("Đường dẫn file tài khoản không được để trống");
        }
        this.fileTaiKhoan = fileTaiKhoan.toAbsolutePath().normalize();
        taiLaiDuLieu();
    }

    public synchronized TaiKhoan dangKy(String tenDangNhap, String matKhau) {
        return dangKy(tenDangNhap, matKhau, VaiTro.HANH_KHACH);
    }

    public synchronized TaiKhoan dangKy(String tenDangNhap, String matKhau, VaiTro vaiTro) {
        String ten = chuanHoaTenDangNhap(tenDangNhap);
        kiemTraMatKhauMoi(matKhau);
        if (vaiTro == null) {
            throw new IllegalArgumentException("Vai trò không được để trống");
        }

        String khoa = taoKhoa(ten);
        if (danhSachTaiKhoan.containsKey(khoa)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        byte[] salt = taoSalt();
        BanGhiTaiKhoan banGhi = new BanGhiTaiKhoan(
                ten, salt, bamMatKhau(matKhau.toCharArray(), salt), vaiTro, true);
        danhSachTaiKhoan.put(khoa, banGhi);
        try {
            luuDuLieu();
        } catch (RuntimeException e) {
            danhSachTaiKhoan.remove(khoa);
            throw e;
        }
        return taoTaiKhoanAnToan(banGhi);
    }

    public synchronized TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        String ten = chuanHoaTenDangNhap(tenDangNhap);
        if (matKhau == null || matKhau.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        BanGhiTaiKhoan banGhi = danhSachTaiKhoan.get(taoKhoa(ten));
        // Dùng chung một thông báo để không làm lộ tài khoản nào đang tồn tại.
        if (banGhi == null || !kiemTraMatKhau(matKhau, banGhi)) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }
        if (!banGhi.trangThai) {
            throw new IllegalStateException("Tài khoản đã bị khóa");
        }

        taiKhoanDangNhap = taoTaiKhoanAnToan(banGhi);
        return taiKhoanDangNhap;
    }

    public synchronized void dangXuat() {
        taiKhoanDangNhap = null;
    }

    public synchronized void doiMatKhau(String matKhauCu, String matKhauMoi) {
        if (taiKhoanDangNhap == null) {
            throw new IllegalStateException("Bạn chưa đăng nhập");
        }
        if (matKhauCu == null || matKhauCu.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu cũ không được để trống");
        }
        kiemTraMatKhauMoi(matKhauMoi);
        if (matKhauCu.equals(matKhauMoi)) {
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu cũ");
        }

        String khoa = taoKhoa(taiKhoanDangNhap.getTenDangNhap());
        BanGhiTaiKhoan cu = danhSachTaiKhoan.get(khoa);
        if (cu == null || !kiemTraMatKhau(matKhauCu, cu)) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        byte[] saltMoi = taoSalt();
        BanGhiTaiKhoan moi = new BanGhiTaiKhoan(cu.tenDangNhap, saltMoi,
                bamMatKhau(matKhauMoi.toCharArray(), saltMoi), cu.vaiTro, cu.trangThai);
        danhSachTaiKhoan.put(khoa, moi);
        try {
            luuDuLieu();
        } catch (RuntimeException e) {
            danhSachTaiKhoan.put(khoa, cu);
            throw e;
        }
    }

    public synchronized boolean isDaDangNhap() {
        return taiKhoanDangNhap != null;
    }

    public synchronized TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    /** Danh sách chỉ đọc và tuyệt đối không chứa mật khẩu/mã băm. */
    public synchronized List<TaiKhoan> layDanhSachTaiKhoan() {
        List<TaiKhoan> ketQua = new ArrayList<>();
        for (BanGhiTaiKhoan x : danhSachTaiKhoan.values()) {
            ketQua.add(taoTaiKhoanAnToan(x));
        }
        return Collections.unmodifiableList(ketQua);
    }

    public synchronized void taiLaiDuLieu() {
        danhSachTaiKhoan.clear();
        taiKhoanDangNhap = null;
        if (!Files.exists(fileTaiKhoan)) return;

        try {
            int soDong = 0;
            for (String dong : Files.readAllLines(fileTaiKhoan, StandardCharsets.UTF_8)) {
                soDong++;
                if (dong.trim().isEmpty()) continue;
                BanGhiTaiKhoan x = docBanGhi(dong, soDong);
                if (danhSachTaiKhoan.put(taoKhoa(x.tenDangNhap), x) != null) {
                    throw new IllegalStateException("Trùng tên đăng nhập tại dòng " + soDong);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc file tài khoản: " + fileTaiKhoan, e);
        }
    }

    private void luuDuLieu() {
        Path fileTam = null;
        try {
            Path thuMuc = fileTaiKhoan.getParent();
            if (thuMuc != null) Files.createDirectories(thuMuc);
            fileTam = Files.createTempFile(thuMuc, "tai_khoan_", ".tmp");
            List<String> cacDong = new ArrayList<>();
            for (BanGhiTaiKhoan x : danhSachTaiKhoan.values()) cacDong.add(ghiBanGhi(x));
            Files.write(fileTam, cacDong, StandardCharsets.UTF_8);
            try {
                Files.move(fileTam, fileTaiKhoan, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(fileTam, fileTaiKhoan, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            if (fileTam != null) {
                try { Files.deleteIfExists(fileTam); } catch (IOException ignored) { }
            }
            throw new IllegalStateException("Không thể lưu file tài khoản: " + fileTaiKhoan, e);
        }
    }

    private String ghiBanGhi(BanGhiTaiKhoan x) {
        Base64.Encoder maHoa = Base64.getUrlEncoder().withoutPadding();
        return String.join("|", PHIEN_BAN,
                maHoa.encodeToString(x.tenDangNhap.getBytes(StandardCharsets.UTF_8)),
                maHoa.encodeToString(x.salt), maHoa.encodeToString(x.maBam),
                x.vaiTro.name(), Boolean.toString(x.trangThai));
    }

    private BanGhiTaiKhoan docBanGhi(String dong, int soDong) {
        try {
            String[] cot = dong.split("\\|", -1);
            if (cot.length != 6 || !PHIEN_BAN.equals(cot[0])) throw new IllegalArgumentException();
            Base64.Decoder giaiMa = Base64.getUrlDecoder();
            String ten = new String(giaiMa.decode(cot[1]), StandardCharsets.UTF_8);
            return new BanGhiTaiKhoan(chuanHoaTenDangNhap(ten), giaiMa.decode(cot[2]),
                    giaiMa.decode(cot[3]), VaiTro.valueOf(cot[4]), Boolean.parseBoolean(cot[5]));
        } catch (RuntimeException e) {
            throw new IllegalStateException("Dữ liệu tài khoản không hợp lệ tại dòng " + soDong, e);
        }
    }

    private String chuanHoaTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null) throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        String ten = tenDangNhap.trim();
        if (ten.length() < 4 || ten.length() > 30)
            throw new IllegalArgumentException("Tên đăng nhập phải có từ 4 đến 30 ký tự");
        if (!ten.matches("[A-Za-z0-9._]+"))
            throw new IllegalArgumentException("Tên đăng nhập chỉ được chứa chữ không dấu, số, dấu chấm và gạch dưới");
        return ten;
    }

    private void kiemTraMatKhauMoi(String matKhau) {
        if (matKhau == null || matKhau.length() < 6 || matKhau.length() > 100)
            throw new IllegalArgumentException("Mật khẩu phải có từ 6 đến 100 ký tự");
        if (matKhau.trim().isEmpty())
            throw new IllegalArgumentException("Mật khẩu không được chỉ gồm khoảng trắng");
    }

    private String taoKhoa(String ten) {
        return ten.toLowerCase(Locale.ROOT);
    }

    private byte[] taoSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private byte[] bamMatKhau(char[] matKhau, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(matKhau, salt, SO_VONG_LAP, DO_DAI_KHOA_BIT);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Môi trường Java không hỗ trợ thuật toán bảo mật", e);
        } finally {
            spec.clearPassword();
        }
    }

    private boolean kiemTraMatKhau(String matKhau, BanGhiTaiKhoan x) {
        return MessageDigest.isEqual(bamMatKhau(matKhau.toCharArray(), x.salt), x.maBam);
    }

    private TaiKhoan taoTaiKhoanAnToan(BanGhiTaiKhoan x) {
        return new TaiKhoan(x.tenDangNhap, "", x.vaiTro, x.trangThai);
    }

    private static final class BanGhiTaiKhoan {
        private final String tenDangNhap;
        private final byte[] salt;
        private final byte[] maBam;
        private final VaiTro vaiTro;
        private final boolean trangThai;

        private BanGhiTaiKhoan(String tenDangNhap, byte[] salt, byte[] maBam,
                              VaiTro vaiTro, boolean trangThai) {
            this.tenDangNhap = tenDangNhap;
            this.salt = salt.clone();
            this.maBam = maBam.clone();
            this.vaiTro = vaiTro;
            this.trangThai = trangThai;
        }
    }
}
