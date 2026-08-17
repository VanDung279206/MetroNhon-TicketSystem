package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    // đọc/ghi file

    // đọc toàn bộ các dòng trong file
    public static List<String> docFile(String duongDan) {
        List<String> danhSachDong = new ArrayList<>();

        File file = new File(duongDan);

        try {
            taoFileNeuChuaTonTai(file);

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String dong;
            while ((dong = reader.readLine()) != null) {
                danhSachDong.add(dong);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }

        return danhSachDong;
    }

    // ghi đè toàn bộ nội dung file
    public static void ghiFile(String duongDan, List<String> danhSachDong) {
        File file = new File(duongDan);

        try {
            taoFileNeuChuaTonTai(file);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (String dong : danhSachDong) {
                writer.write(dong);
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
        }
    }

    // ghi thêm một dòng vào cuối file
    public static void ghiThem(String duongDan, String noiDung) {
        File file = new File(duongDan);

        try {
            taoFileNeuChuaTonTai(file);

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(file, true));

            writer.write(noiDung);
            writer.newLine();

            writer.close();

        } catch (IOException e) {
            System.out.println("Lỗi ghi thêm file: " + e.getMessage());
        }
    }

    private static void taoFileNeuChuaTonTai(File file) throws IOException {
        File thuMucCha = file.getParentFile();

        if (thuMucCha != null && !thuMucCha.exists()) {
            thuMucCha.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }
    }
}