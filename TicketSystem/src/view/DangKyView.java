package view;

import controller.AuthController;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

public class DangKyView extends JPanel {
    private final AuthController authController;
    private final Runnable onBackToLogin;
    private final JTextField tenDangNhapField = new JTextField();
    private final JPasswordField matKhauField = new JPasswordField();
    private final JTextField hoTenField = new JTextField();
    private final JTextField soDienThoaiField = new JTextField();
    private final JTextField emailField = new JTextField();

    public DangKyView(AuthController authController, Runnable onBackToLogin) {
        this.authController = authController;
        this.onBackToLogin = onBackToLogin;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(new MetroBrandPanel(), BorderLayout.WEST);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(430, 580));

        JLabel title = Theme.title("Tạo tài khoản", 29);
        title.setAlignmentX(LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(14));

        addField(form, "Họ và tên", hoTenField);
        addField(form, "Tên đăng nhập", tenDangNhapField);
        addField(form, "Mật khẩu", matKhauField);
        addField(form, "Số điện thoại", soDienThoaiField);
        addField(form, "Email", emailField);

        JButton registerButton = createRoundedButton("Đăng ký tài khoản",
                Theme.PRIMARY, Color.WHITE, false);
        registerButton.setPreferredSize(new Dimension(430, 58));
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        registerButton.setAlignmentX(LEFT_ALIGNMENT);
        registerButton.addActionListener(event -> dangKy());
        JButton backButton = createRoundedButton("Quay lại đăng nhập",
                Theme.SURFACE, Theme.PRIMARY, true);
        backButton.setPreferredSize(new Dimension(430, 58));
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        backButton.setAlignmentX(LEFT_ALIGNMENT);
        backButton.addActionListener(event -> onBackToLogin.run());

        form.add(Box.createVerticalStrut(6));
        form.add(registerButton);
        form.add(Box.createVerticalStrut(12));
        form.add(backButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(24, 50, 24, 50);
        wrapper.add(form, gbc);
        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addField(JPanel form, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Theme.TEXT);
        label.setAlignmentX(LEFT_ALIGNMENT);
        Theme.inputStyle(field);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        form.add(label);
        form.add(Box.createVerticalStrut(4));
        form.add(field);
        form.add(Box.createVerticalStrut(9));
    }

    private JButton createRoundedButton(String text, Color background,
                                        Color foreground, boolean outlined) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = background;
                if (getModel().isPressed() || getModel().isRollover()) {
                    fill = outlined ? Theme.PRIMARY_SOFT : Theme.PRIMARY_DARK;
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                if (outlined) {
                    g2.setColor(Theme.BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                }
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setForeground(foreground);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 15f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private void dangKy() {
        boolean thanhCong = authController.dangKy(
                tenDangNhapField.getText(),
                new String(matKhauField.getPassword()),
                hoTenField.getText(),
                soDienThoaiField.getText(),
                emailField.getText()
        );

        if (!thanhCong) {
            Theme.error(this, "Thông tin chưa hợp lệ hoặc đã được sử dụng.");
            return;
        }

        Theme.success(this, "Đăng ký thành công. Bạn có thể đăng nhập ngay.");
        clearForm();
        onBackToLogin.run();
    }

    private void clearForm() {
        tenDangNhapField.setText("");
        matKhauField.setText("");
        hoTenField.setText("");
        soDienThoaiField.setText("");
        emailField.setText("");
    }
}
