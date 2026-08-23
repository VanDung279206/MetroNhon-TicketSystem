package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Theme {
    public static final Color PRIMARY = new Color(30, 99, 235);
    public static final Color PRIMARY_DARK = new Color(15, 55, 111);
    public static final Color PRIMARY_SOFT = new Color(232, 240, 254);
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(20, 32, 51);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color BORDER = new Color(221, 228, 238);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(245, 158, 11);

    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Theme() {
    }

    public static void install() {
        // Dùng reflection để source vẫn biên dịch được bằng JDK thuần.
        // Khi chạy bằng Maven, dependency FlatLaf trong pom.xml sẽ được nạp.
        boolean daCaiFlatLaf = false;
        try {
            Class<?> flatLightLaf = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            Method setup = flatLightLaf.getMethod("setup");
            daCaiFlatLaf = Boolean.TRUE.equals(setup.invoke(null));
        } catch (ReflectiveOperationException ignored) {
            // Cho phép chạy dự phòng khi người dùng chưa tải dependency.
        }

        if (!daCaiFlatLaf) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // Nếu Nimbus không có thì giữ Look and Feel mặc định của hệ điều hành.
            }
        }

        UIManager.put("defaultFont", FONT);
        UIManager.put("Label.font", FONT);
        UIManager.put("Button.font", FONT.deriveFont(Font.BOLD));
        UIManager.put("TextField.font", FONT);
        UIManager.put("PasswordField.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Table.font", FONT);
        UIManager.put("Table.rowHeight", 42);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.font", FONT.deriveFont(Font.BOLD));
        UIManager.put("TableHeader.height", 44);
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("ScrollBar.width", 10);
    }

    public static JLabel title(String text, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(FONT.deriveFont(Font.BOLD, size));
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(FONT);
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = button(text, PRIMARY, Color.WHITE);
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = button(text, SURFACE, PRIMARY);
        button.setBorder(compoundBorder(10, 18));
        return button;
    }

    public static JButton dangerButton(String text) {
        return button(text, new Color(254, 226, 226), DANGER);
    }

    public static JButton navButton(String text) {
        JButton button = button(text, PRIMARY_DARK, new Color(219, 234, 254));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        return button;
    }

    private static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return button;
    }

    public static JPanel card() {
        JPanel panel = new RoundedPanel(18, SURFACE);
        panel.setBorder(compoundBorder(22, 22));
        return panel;
    }

    public static Border compoundBorder(int vertical, int horizontal) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal)
        );
    }

    public static void inputStyle(JComponent component) {
        component.putClientProperty("JComponent.roundRect", true);
        component.putClientProperty("JTextField.showClearButton", true);
        component.setBorder(compoundBorder(11, 12));
    }

    public static String money(double value) {
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return format.format(value) + " ₫";
    }

    public static String dateTime(LocalDateTime value) {
        return value == null ? "—" : value.format(DATE_TIME_FORMAT);
    }

    public static void success(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Không thể thực hiện",
                JOptionPane.ERROR_MESSAGE);
    }

    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;

        public RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}