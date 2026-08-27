package utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private FileHandler() {
    }

    public static synchronized List<String> docFile(String duongDan) {
        Path duongDanFile = Paths.get(duongDan);

        try {
            taoFileNeuChuaTonTai(duongDanFile);
            return new ArrayList<>(Files.readAllLines(duongDanFile, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Không thể đọc dữ liệu từ " + duongDanFile, e);
        }
    }

    public static synchronized void ghiFile(String duongDan, List<String> danhSachDong) {
        if (danhSachDong == null) {
            throw new IllegalArgumentException(
                    "Danh sách dòng không được null"
            );
        }

        Path duongDanFile = Paths.get(duongDan);

        try {
            voiKhoaFile(duongDanFile, () -> ghiFileNguyenTu(duongDanFile, danhSachDong));
        } catch (IOException e) {
            throw new UncheckedIOException("Không thể ghi dữ liệu vào " + duongDanFile, e);
        }
    }

    public static synchronized void ghiThem(String duongDan, String noiDung) {
        if (noiDung == null) {
            throw new IllegalArgumentException(
                    "Nội dung ghi thêm không được null"
            );
        }

        Path duongDanFile = Paths.get(duongDan);

        try {
            voiKhoaFile(duongDanFile, () -> {
                taoFileNeuChuaTonTai(duongDanFile);
                List<String> danhSachDong = new ArrayList<>(
                        Files.readAllLines(duongDanFile, StandardCharsets.UTF_8)
                );
                danhSachDong.add(noiDung);
                ghiFileNguyenTu(duongDanFile, danhSachDong);
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Không thể ghi dữ liệu vào " + duongDanFile, e);
        }
    }

    private static void ghiFileNguyenTu(Path duongDanFile, List<String> danhSachDong)
            throws IOException {
        taoFileNeuChuaTonTai(duongDanFile);
        Path thuMucCha = duongDanFile.toAbsolutePath().getParent();
        Path tepTam = Files.createTempFile(thuMucCha,
                duongDanFile.getFileName().toString(), ".tmp");
        try {
            Files.write(tepTam, danhSachDong, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(tepTam, duongDanFile,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tepTam, duongDanFile,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tepTam);
        }
    }

    private static void voiKhoaFile(Path duongDanFile, ThaoTacFile thaoTac)
            throws IOException {
        thaoTac.thucHien();
    }

    private static void taoFileNeuChuaTonTai(Path duongDanFile)
            throws IOException {
        Path thuMucCha = duongDanFile.getParent();

        if (thuMucCha != null) {
            Files.createDirectories(thuMucCha);
        }

        if (Files.notExists(duongDanFile)) {
            Files.createFile(duongDanFile);
        }
    }

    @FunctionalInterface
    private interface ThaoTacFile {
        void thucHien() throws IOException;
    }
}