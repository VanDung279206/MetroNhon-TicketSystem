package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    // đọc toàn bộ các dòng trong file bằng UTF-8
    public static List<String> docFile(String duongDan) {
        List<String> danhSachDong = new ArrayList<>();
        Path duongDanFile = Paths.get(duongDan);

        try {
            taoFileNeuChuaTonTai(duongDanFile);

            try (BufferedReader reader = Files.newBufferedReader(
                    duongDanFile,
                    StandardCharsets.UTF_8
            )) {
                String dong;

                while ((dong = reader.readLine()) != null) {
                    danhSachDong.add(dong);
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }

        return danhSachDong;
    }

    // ghi đè toàn bộ nội dung file bằng UTF-8
    public static void ghiFile(String duongDan, List<String> danhSachDong) {
        if (danhSachDong == null) {
            throw new IllegalArgumentException(
                    "Danh sách dòng không được null"
            );
        }

        Path duongDanFile = Paths.get(duongDan);

        try {
            taoFileNeuChuaTonTai(duongDanFile);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    duongDanFile,
                    StandardCharsets.UTF_8
            )) {
                for (String dong : danhSachDong) {
                    writer.write(dong);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
        }
    }

    // ghi thêm một dòng vào cuối file bằng UTF-8
    public static void ghiThem(String duongDan, String noiDung) {
        if (noiDung == null) {
            throw new IllegalArgumentException(
                    "Nội dung ghi thêm không được null"
            );
        }

        Path duongDanFile = Paths.get(duongDan);

        try {
            taoFileNeuChuaTonTai(duongDanFile);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    duongDanFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            )) {
                writer.write(noiDung);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi ghi thêm file: " + e.getMessage());
        }
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
}