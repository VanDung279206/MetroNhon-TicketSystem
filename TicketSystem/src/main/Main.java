package main;

import server.MetroWebServer;

public class Main {
    public static void main(String[] args) {
        int port = 8080;
        MetroWebServer server = new MetroWebServer(port);
        server.start();

        System.out.println("==============================================");
        System.out.println(" Metro Nhổn Ticket System đang chạy");
        System.out.println(" Mở trình duyệt: http://localhost:" + port);
        System.out.println(" Nhấn Ctrl + C để dừng chương trình");
        System.out.println("==============================================");
    }
}