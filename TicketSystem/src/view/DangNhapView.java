package view;

import controller.AuthController;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.net.URL;

public class DangNhapView extends JPanel {
    private final AuthController authController;
    private final Runnable onLoginSuccess;
    private final JTextField tenDangNhapField = new JTextField();
    private final JPasswordField matKhauField = new JPasswordField();

    public DangNhapView(AuthController authController,
                        Runnable onLoginSuccess,
                        Runnable onOpenRegister) {
        this.authController = authController;
        this.onLoginSuccess = onLoginSuccess;

        setLayout(new BorderLayout());
        MetroLoginBackgroundPanel background = new MetroLoginBackgroundPanel();
        background.setLayout(new GridBagLayout());

        JPanel loginCard = createLoginCard(onOpenRegister);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(30, 56, 30, 30);
        background.add(loginCard, gbc);
        add(background, BorderLayout.CENTER);
    }

    private JPanel createLoginCard(Runnable onOpenRegister) {
        JPanel card = new Theme.RoundedPanel(30, new Color(255, 255, 255, 248));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 36, 26, 36));
        card.setPreferredSize(new Dimension(480, 610));

        JLabel logo = createMetroLogo();

        JLabel title = Theme.title("METRO NHỔN – CẦU GIẤY", 20);
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel product = new JLabel("Hệ thống vé điện tử");
        product.setForeground(Theme.PRIMARY);
        product.setFont(product.getFont().deriveFont(Font.BOLD, 13f));
        product.setAlignmentX(CENTER_ALIGNMENT);

        Theme.inputStyle(tenDangNhapField);
        Theme.inputStyle(matKhauField);
        tenDangNhapField.setBackground(Theme.SURFACE_SOFT);
        matKhauField.setBackground(Theme.SURFACE_SOFT);
        tenDangNhapField.putClientProperty("JTextField.placeholderText",
                "Nhập tên đăng nhập");
        matKhauField.putClientProperty("JTextField.placeholderText",
                "Nhập mật khẩu");
        matKhauField.putClientProperty("JPasswordField.showRevealButton", true);
        configureInput(tenDangNhapField);
        configureInput(matKhauField);

        JButton loginButton = createRoundedLoginButton();
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(370, 50));
        loginButton.setPreferredSize(new Dimension(370, 50));
        loginButton.addActionListener(event -> dangNhap());
        matKhauField.addActionListener(event -> dangNhap());

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        registerRow.setOpaque(false);
        registerRow.setAlignmentX(CENTER_ALIGNMENT);
        registerRow.setMaximumSize(new Dimension(370, 32));
        registerRow.setPreferredSize(new Dimension(370, 32));
        JLabel registerQuestion = Theme.muted("Chưa có tài khoản?");
        JButton registerButton = Theme.linkButton("Đăng ký ngay");
        registerButton.addActionListener(event -> onOpenRegister.run());
        registerRow.add(registerQuestion);
        registerRow.add(registerButton);

        card.add(logo);
        card.add(Box.createVerticalStrut(16));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(product);
        card.add(Box.createVerticalStrut(6));
        card.add(Box.createVerticalStrut(22));
        card.add(fieldLabel("Tên đăng nhập"));
        card.add(Box.createVerticalStrut(7));
        card.add(tenDangNhapField);
        card.add(Box.createVerticalStrut(14));
        card.add(fieldLabel("Mật khẩu"));
        card.add(Box.createVerticalStrut(7));
        card.add(matKhauField);
        card.add(Box.createVerticalStrut(18));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(registerRow);
        return card;
    }

    private JLabel createMetroLogo() {
        URL logoUrl = getClass().getResource("/images/hanoi-metro-logo.png");
        JLabel logo = new JLabel();
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setPreferredSize(new Dimension(82, 82));
        logo.setMaximumSize(new Dimension(82, 82));

        if (logoUrl != null) {
            Image original = new ImageIcon(logoUrl).getImage();
            Image scaled = original.getScaledInstance(82, 82, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
        }
        return logo;
    }

    private JButton createRoundedLoginButton() {
        JButton button = new JButton("Đăng nhập") {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = getModel().isPressed() ? Theme.PRIMARY_DARK : Theme.PRIMARY;
                if (getModel().isRollover()) {
                    fill = Theme.PRIMARY_DARK;
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private void configureInput(JTextField field) {
        field.setAlignmentX(CENTER_ALIGNMENT);
        field.setMargin(new Insets(8, 14, 8, 14));
        field.setMaximumSize(new Dimension(370, 54));
        field.setPreferredSize(new Dimension(370, 54));
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        Dimension labelSize = label.getPreferredSize();
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(370, labelSize.height));
        label.setPreferredSize(new Dimension(370, labelSize.height));
        return label;
    }

    private void dangNhap() {
        String tenDangNhap = tenDangNhapField.getText();
        String matKhau = new String(matKhauField.getPassword());
        try {
            if (authController.dangNhap(tenDangNhap, matKhau) == null) {
                Theme.error(this,
                        "Tên đăng nhập, mật khẩu không đúng hoặc tài khoản đã bị khóa.");
                return;
            }
        } catch (RuntimeException e) {
            Theme.error(this, "Không thể truy cập dữ liệu đăng nhập. Vui lòng thử lại.");
            return;
        }
        matKhauField.setText("");
        onLoginSuccess.run();
    }
}