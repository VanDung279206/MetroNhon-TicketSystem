package utils;

import java.nio.file.Path;

public final class Constants {
    private static final String DATA_DIRECTORY =
            System.getProperty("metro.data.dir", "data");

    public static final String FILE_TAI_KHOAN =
            Path.of(DATA_DIRECTORY, "tai_khoan.txt").toString();
    public static final String FILE_HANH_KHACH =
            Path.of(DATA_DIRECTORY, "hanh_khach.txt").toString();
    public static final String FILE_GA =
            Path.of(DATA_DIRECTORY, "ga.txt").toString();
    public static final String FILE_VE_DA_BAN =
            Path.of(DATA_DIRECTORY, "ve_da_ban.txt").toString();

    private Constants() {
    }
}