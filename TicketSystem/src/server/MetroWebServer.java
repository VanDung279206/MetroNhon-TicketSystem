package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import controller.AdminController;
import controller.AuthController;
import controller.MuaVeController;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.TaiKhoan;
import model.VaiTro;
import model.VeMetro;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class MetroWebServer {
    private static final String SESSION_COOKIE = "METRO_SESSION";

    private final int port;
    private final MuaVeController muaVeController;
    private final Map<String, UserSession> sessions;
    private HttpServer httpServer;

    public MetroWebServer(int port) {
        this.port = port;
        muaVeController = new MuaVeController();
        sessions = new ConcurrentHashMap<>();
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/api", this::handleApi);
            httpServer.createContext("/", this::handleStaticFile);
            httpServer.setExecutor(Executors.newFixedThreadPool(8));
            httpServer.start();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi động web server", e);
        }
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    private void handleApi(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath();
            switch (path) {
                case "/api/dang-nhap":
                    dangNhap(exchange);
                    break;
                case "/api/dang-ky":
                    dangKy(exchange);
                    break;
                case "/api/dang-xuat":
                    dangXuat(exchange);
                    break;
                case "/api/phien":
                    layPhien(exchange);
                    break;
                case "/api/ga":
                    layDanhSachGa(exchange);
                    break;
                case "/api/ve":
                    layVeCuaToi(exchange);
                    break;
                case "/api/mua-ve-luot":
                    muaVeLuot(exchange);
                    break;
                case "/api/mua-ve-thang":
                    muaVeThang(exchange);
                    break;
                case "/api/admin/tong-quan":
                    layTongQuanAdmin(exchange);
                    break;
                case "/api/admin/trang-thai":
                    capNhatTrangThai(exchange);
                    break;
                default:
                    sendJson(exchange, 404, Json.object(
                            "success", false,
                            "message", "API không tồn tại"
                    ));
            }
        } catch (ResponseSentException ignored) {
            // Phản hồi 401/403 đã được gửi bởi hàm kiểm tra phiên.
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, Json.object(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            e.printStackTrace();
            sendJson(exchange, 500, Json.object(
                    "success", false,
                    "message", "Hệ thống đang bận, vui lòng thử lại"
            ));
        }
    }

    private void dangNhap(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        Map<String, String> form = readForm(exchange);
        AuthController authController = new AuthController();
        TaiKhoan account = authController.dangNhap(
                form.get("tenDangNhap"), form.get("matKhau")
        );

        if (account == null) {
            sendJson(exchange, 401, Json.object(
                    "success", false,
                    "message", "Tên đăng nhập, mật khẩu không đúng hoặc tài khoản đã bị khóa"
            ));
            return;
        }

        String token = UUID.randomUUID().toString();
        sessions.put(token, new UserSession(authController));
        exchange.getResponseHeaders().add(
                "Set-Cookie",
                SESSION_COOKIE + "=" + token + "; Path=/; HttpOnly; SameSite=Lax"
        );

        HanhKhach passenger = authController.getHanhKhachDangNhap();
        String displayName = passenger == null
                ? account.getTenDangNhap()
                : passenger.getHoTen();
        sendJson(exchange, 200, Json.object(
                "success", true,
                "role", account.getVaiTro().name(),
                "displayName", displayName,
                "username", account.getTenDangNhap()
        ));
    }

    private void dangKy(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        Map<String, String> form = readForm(exchange);
        AuthController auth = new AuthController();
        boolean success = auth.dangKy(
                form.get("tenDangNhap"),
                form.get("matKhau"),
                form.get("hoTen"),
                form.get("soDienThoai"),
                form.get("email")
        );

        if (!success) {
            sendJson(exchange, 400, Json.object(
                    "success", false,
                    "message", "Thông tin chưa hợp lệ hoặc đã được sử dụng"
            ));
            return;
        }
        sendJson(exchange, 201, Json.object(
                "success", true,
                "message", "Đăng ký thành công"
        ));
    }

    private void dangXuat(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        String token = getCookie(exchange, SESSION_COOKIE);
        if (token != null) {
            sessions.remove(token);
        }
        exchange.getResponseHeaders().add(
                "Set-Cookie",
                SESSION_COOKIE + "=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax"
        );
        sendJson(exchange, 200, Json.object("success", true));
    }

    private void layPhien(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "GET");
        UserSession session = requireSession(exchange);
        TaiKhoan account = session.authController.getTaiKhoanDangNhap();
        HanhKhach passenger = session.authController.getHanhKhachDangNhap();
        sendJson(exchange, 200, Json.object(
                "success", true,
                "role", account.getVaiTro().name(),
                "displayName", passenger == null
                        ? account.getTenDangNhap()
                        : passenger.getHoTen(),
                "username", account.getTenDangNhap(),
                "passengerId", passenger == null ? "" : passenger.getMaHanhKhach()
        ));
    }

    private void layDanhSachGa(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "GET");
        List<String> stations = new ArrayList<>();
        for (Ga station : muaVeController.getDanhSachGa()) {
            stations.add(Json.object(
                    "id", station.getMaGa(),
                    "name", station.getTenGa(),
                    "location", station.getViTri(),
                    "order", station.getThuTu()
            ));
        }
        sendJson(exchange, 200, Json.objectRaw(
                "success", true,
                "stations", Json.array(stations)
        ));
    }

    private void layVeCuaToi(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "GET");
        UserSession session = requirePassengerSession(exchange);
        HanhKhach passenger = session.authController.getHanhKhachDangNhap();
        List<VeMetro> tickets = muaVeController.getDanhSachVeCuaHanhKhach(
                passenger.getMaHanhKhach()
        );
        sendJson(exchange, 200, Json.objectRaw(
                "success", true,
                "tickets", ticketsToJson(tickets)
        ));
    }

    private void muaVeLuot(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        UserSession session = requirePassengerSession(exchange);
        Map<String, String> form = readForm(exchange);
        VeMetro ticket = muaVeController.muaVeLuot(
                session.authController.getHanhKhachDangNhap(),
                form.get("maGaDi"),
                form.get("maGaDen")
        );
        sendJson(exchange, 201, Json.objectRaw(
                "success", true,
                "message", "Mua vé lượt thành công",
                "ticket", ticketToJson(ticket)
        ));
    }

    private void muaVeThang(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        UserSession session = requirePassengerSession(exchange);
        Map<String, String> form = readForm(exchange);
        LoaiVeThang type = LoaiVeThang.valueOf(form.get("loaiVe"));
        VeMetro ticket = muaVeController.muaVeThang(
                session.authController.getHanhKhachDangNhap(), type
        );
        sendJson(exchange, 201, Json.objectRaw(
                "success", true,
                "message", "Mua vé tháng thành công",
                "ticket", ticketToJson(ticket)
        ));
    }

    private void layTongQuanAdmin(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "GET");
        UserSession session = requireAdminSession(exchange);
        AdminController admin = new AdminController(
                session.authController, muaVeController
        );

        List<String> accounts = new ArrayList<>();
        for (TaiKhoan account : admin.getDanhSachTaiKhoan()) {
            accounts.add(Json.object(
                    "username", account.getTenDangNhap(),
                    "role", account.getVaiTro().name(),
                    "active", account.isTrangThai()
            ));
        }
        List<VeMetro> tickets = admin.getDanhSachVeDaBan();
        sendJson(exchange, 200, Json.objectRaw(
                "success", true,
                "revenue", admin.tinhTongDoanhThu(),
                "ticketCount", tickets.size(),
                "accounts", Json.array(accounts),
                "tickets", ticketsToJson(tickets)
        ));
    }

    private void capNhatTrangThai(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        UserSession session = requireAdminSession(exchange);
        Map<String, String> form = readForm(exchange);
        boolean active = Boolean.parseBoolean(form.get("active"));
        AdminController admin = new AdminController(
                session.authController, muaVeController
        );
        boolean success = active
                ? admin.moKhoaTaiKhoan(form.get("tenDangNhap"))
                : admin.khoaTaiKhoan(form.get("tenDangNhap"));
        if (!success) {
            sendJson(exchange, 400, Json.object(
                    "success", false,
                    "message", "Không thể cập nhật trạng thái tài khoản"
            ));
            return;
        }
        sendJson(exchange, 200, Json.object(
                "success", true,
                "message", "Đã cập nhật trạng thái tài khoản"
        ));
    }

    private UserSession requireSession(HttpExchange exchange) throws IOException {
        String token = getCookie(exchange, SESSION_COOKIE);
        UserSession session = token == null ? null : sessions.get(token);
        if (session == null || !session.authController.isDaDangNhap()) {
            sendJson(exchange, 401, Json.object(
                    "success", false,
                    "message", "Bạn chưa đăng nhập"
            ));
            throw new ResponseSentException();
        }
        return session;
    }

    private UserSession requirePassengerSession(HttpExchange exchange) throws IOException {
        UserSession session = requireSession(exchange);
        if (session.authController.getHanhKhachDangNhap() == null) {
            sendJson(exchange, 403, Json.object(
                    "success", false,
                    "message", "Chức năng chỉ dành cho hành khách"
            ));
            throw new ResponseSentException();
        }
        return session;
    }

    private UserSession requireAdminSession(HttpExchange exchange) throws IOException {
        UserSession session = requireSession(exchange);
        TaiKhoan account = session.authController.getTaiKhoanDangNhap();
        if (account.getVaiTro() != VaiTro.ADMIN) {
            sendJson(exchange, 403, Json.object(
                    "success", false,
                    "message", "Bạn không có quyền quản trị"
            ));
            throw new ResponseSentException();
        }
        return session;
    }

    private void handleStaticFile(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        if ("/".equals(requestPath)) {
            requestPath = "/index.html";
        }

        Path webRoot = findWebRoot();
        Path file = webRoot.resolve(requestPath.substring(1)).normalize();
        if (!file.startsWith(webRoot) || !Files.isRegularFile(file)) {
            file = webRoot.resolve("index.html");
        }

        byte[] content = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType(file));
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(200, content.length);
        exchange.getResponseBody().write(content);
        exchange.close();
    }

    private Path findWebRoot() {
        Path root = Paths.get("web").toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            throw new IllegalStateException(
                    "Không tìm thấy thư mục web. Hãy chạy Main từ thư mục gốc dự án."
            );
        }
        return root;
    }

    private String contentType(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        if (name.endsWith(".html")) return "text/html; charset=UTF-8";
        if (name.endsWith(".css")) return "text/css; charset=UTF-8";
        if (name.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }

    private void requireMethod(HttpExchange exchange, String method) {
        if (!method.equalsIgnoreCase(exchange.getRequestMethod())) {
            throw new IllegalArgumentException("Phương thức HTTP không hợp lệ");
        }
    }

    private Map<String, String> readForm(HttpExchange exchange) throws IOException {
        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );
        Map<String, String> values = new LinkedHashMap<>();
        if (body.isEmpty()) {
            return values;
        }
        for (String part : body.split("&")) {
            String[] pair = part.split("=", 2);
            String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = pair.length == 2
                    ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                    : "";
            values.put(key, value);
        }
        return values;
    }

    private String getCookie(HttpExchange exchange, String name) {
        List<String> cookieHeaders = exchange.getRequestHeaders().get("Cookie");
        if (cookieHeaders == null) {
            return null;
        }
        for (String header : cookieHeaders) {
            for (String cookie : header.split(";")) {
                String[] pair = cookie.trim().split("=", 2);
                if (pair.length == 2 && name.equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    private String ticketsToJson(List<VeMetro> tickets) {
        List<String> values = new ArrayList<>();
        for (VeMetro ticket : tickets) {
            values.add(ticketToJson(ticket));
        }
        return Json.array(values);
    }

    private String ticketToJson(VeMetro ticket) {
        return Json.object(
                "id", ticket.getMaVe(),
                "type", ticket.getLoaiVe(),
                "passengerName", ticket.getHanhKhach().getHoTen(),
                "purchasedAt", ticket.getNgayMua().toString(),
                "price", ticket.getGiaVe(),
                "active", ticket.isTrangThai(),
                "description", ticket.toString()
        );
    }

    private void sendJson(HttpExchange exchange, int status, String json)
            throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        headers.set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static class UserSession {
        private final AuthController authController;

        private UserSession(AuthController authController) {
            this.authController = authController;
        }
    }

    private static class ResponseSentException extends RuntimeException {
    }

    private static final class Json {
        private Json() {
        }

        private static String object(Object... keyValues) {
            return buildObject(false, keyValues);
        }

        private static String objectRaw(Object... keyValues) {
            return buildObject(true, keyValues);
        }

        private static String buildObject(boolean allowRaw, Object... keyValues) {
            StringBuilder result = new StringBuilder("{");
            for (int i = 0; i < keyValues.length; i += 2) {
                if (i > 0) result.append(',');
                result.append(quote(String.valueOf(keyValues[i]))).append(':');
                Object value = keyValues[i + 1];
                if (allowRaw && value instanceof String
                        && (((String) value).startsWith("[")
                        || ((String) value).startsWith("{"))) {
                    result.append(value);
                } else {
                    result.append(value(value));
                }
            }
            return result.append('}').toString();
        }

        private static String array(List<String> values) {
            return "[" + String.join(",", values) + "]";
        }

        private static String value(Object value) {
            if (value == null) return "null";
            if (value instanceof Number || value instanceof Boolean) {
                return String.valueOf(value);
            }
            return quote(String.valueOf(value));
        }

        private static String quote(String text) {
            StringBuilder result = new StringBuilder("\"");
            for (char character : text.toCharArray()) {
                switch (character) {
                    case '\\': result.append("\\\\"); break;
                    case '"': result.append("\\\""); break;
                    case '\n': result.append("\\n"); break;
                    case '\r': result.append("\\r"); break;
                    case '\t': result.append("\\t"); break;
                    default: result.append(character);
                }
            }
            return result.append('"').toString();
        }
    }
}