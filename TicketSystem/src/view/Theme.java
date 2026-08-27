package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Theme {
    public static final Color PRIMARY = new Color(29, 91, 196);
    public static final Color PRIMARY_DARK = new Color(15, 55, 111);
    public static final Color PRIMARY_DARKER = new Color(12, 43, 86);
    public static final Color PRIMARY_SOFT = new Color(235, 243, 255);
    public static final Color ACCENT = new Color(34, 181, 115);
    public static final Color BACKGROUND = new Color(244, 247, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_SOFT = new Color(249, 251, 253);
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
        UIManager.put("Component.arc", 20);
        UIManager.put("Button.arc", 20);
        UIManager.put("TextComponent.arc", 20);
        UIManager.put("CheckBox.arc", 8);
        UIManager.put("TabbedPane.tabArc", 18);
        UIManager.put("TabbedPane.tabHeight", 42);
        UIManager.put("TabbedPane.showTabSeparators", false);
        UIManager.put("PopupMenu.borderCornerRadius", 18);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
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
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 20, 1),
                BorderFactory.createEmptyBorder(11, 18, 11, 18)
        ));
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = button(text, new Color(254, 226, 226), DANGER);
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    public static JButton navButton(String text) {
        JButton button = button(text, PRIMARY_DARK, new Color(219, 234, 254));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setHorizontalAlignment(JButton.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        return button;
    }

    public static JButton navIconButton(String text) {
        JButton button = button(text, PRIMARY_DARKER, Color.WHITE);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFont(FONT.deriveFont(Font.BOLD, 18f));
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        return button;
    }

    public static JButton ghostButton(String text) {
        JButton button = button(text, PRIMARY_SOFT, PRIMARY_DARK);
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    public static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(PRIMARY);
        button.setFont(FONT.deriveFont(Font.BOLD, 13f));
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton accountButton(String displayName) {
        JButton button = button(initials(displayName) + "   " + displayName + "  ▾",
                SURFACE, TEXT);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 22, 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
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
        JPanel panel = new RoundedPanel(24, SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        return panel;
    }

    public static Border compoundBorder(int vertical, int horizontal) {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 18, 1),
                BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal)
        );
    }

    public static void inputStyle(JComponent component) {
        component.putClientProperty("JComponent.roundRect", true);
        component.putClientProperty("JTextField.showClearButton", true);
        component.putClientProperty("JComponent.outline", BORDER);
        component.setBackground(SURFACE);
        if (component instanceof JTextField textField) {
            textField.setMargin(new Insets(10, 14, 10, 14));
        }
    }

    public static void showAccountMenu(JButton anchor,
                                       String displayName,
                                       String accountName,
                                       String description,
                                       Runnable onViewAccount,
                                       Runnable onChangePassword,
                                       Runnable onLogout) {
        JPopupMenu menu = new JPopupMenu();
        menu.putClientProperty("PopupMenu.borderCornerRadius", 18);
        menu.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 18, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel profile = new JPanel();
        profile.setOpaque(false);
        profile.setLayout(new javax.swing.BoxLayout(profile, javax.swing.BoxLayout.Y_AXIS));
        profile.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10));
        profile.setPreferredSize(new java.awt.Dimension(270, 78));

        JLabel name = title(displayName, 16);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel account = muted("@" + accountName);
        account.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel detail = muted(description);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);
        profile.add(name);
        profile.add(javax.swing.Box.createVerticalStrut(5));
        profile.add(account);
        profile.add(javax.swing.Box.createVerticalStrut(3));
        profile.add(detail);
        menu.add(profile);
        menu.addSeparator();

        JMenuItem accountDetails = popupItem("●  Tài khoản");
        accountDetails.addActionListener(event -> onViewAccount.run());
        menu.add(accountDetails);

        if (onChangePassword != null) {
            JMenuItem changePassword = popupItem("◉  Đổi mật khẩu");
            changePassword.addActionListener(event -> onChangePassword.run());
            menu.add(changePassword);
        }

        JMenuItem logout = popupItem("↪  Đăng xuất");
        logout.setForeground(DANGER);
        logout.addActionListener(event -> onLogout.run());
        menu.add(logout);

        int x = Math.max(0, anchor.getWidth() - menu.getPreferredSize().width);
        menu.show(anchor, x, anchor.getHeight() + 8);
    }

    private static JMenuItem popupItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(FONT.deriveFont(Font.BOLD));
        item.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return item;
    }

    public static String initials(String name) {
        if (name == null || name.isBlank()) {
            return "M";
        }
        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase(Locale.ROOT);
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
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class RoundedBorder implements Border {
        private final Color color;
        private final int radius;
        private final int thickness;

        private RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics,
                                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(thickness));
            int inset = Math.max(1, thickness / 2);
            g2.drawRoundRect(x + inset, y + inset,
                    width - thickness - 1, height - thickness - 1,
                    radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}