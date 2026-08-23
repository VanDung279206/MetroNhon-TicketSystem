package view;

import controller.AuthController;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class DangNhapView extends JPanel {
    private final AuthController authController;
    private final Runnable onLoginSuccess;
    private final JTextField tenDangNhapField;
    private final JPasswordField matKhauField;

    public DangNhapView(AuthController authController,
                        Runnable onLoginSuccess,
                        Runnable onOpenRegister) {
        this.authController = authController;
        this.onLoginSuccess = onLoginSuccess;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(new MetroBrandPanel(), BorderLayout.WEST);

        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setOpaque(false);
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(410, 480));

        JLabel eyebrow = new JLabel("CỔNG VÉ ĐIỆN TỬ");
        eyebrow.setForeground(Theme.PRIMARY);
        eyebrow.setFont(eyebrow.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        eyebrow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel title = Theme.title("Chào mừng bạn trở lại", 29);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted("Đăng nhập để mua vé và quản lý hành trình của bạn.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        tenDangNhapField = new JTextField();
        matKhauField = new JPasswordField();
        Theme.inputStyle(tenDangNhapField);
        Theme.inputStyle(matKhauField);
        tenDangNhapField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        matKhauField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JButton loginButton = Theme.primaryButton("Đăng nhập");
        loginButton.setAlignmentX(LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginButton.addActionListener(event -> dangNhap());
        matKhauField.addActionListener(event -> dangNhap());

        JButton registerButton = Theme.secondaryButton("Tạo tài khoản mới");
        registerButton.setAlignmentX(LEFT_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        registerButton.addActionListener(event -> onOpenRegister.run());

        form.add(eyebrow);
        form.add(Box.createVerticalStrut(10));
        form.add(title);
        form.add(Box.createVerticalStrut(10));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(34));
        form.add(fieldLabel("Tên đăng nhập"));
        form.add(Box.createVerticalStrut(8));
        form.add(tenDangNhapField);
        form.add(Box.createVerticalStrut(18));
        form.add(fieldLabel("Mật khẩu"));
        form.add(Box.createVerticalStrut(8));
        form.add(matKhauField);
        form.add(Box.createVerticalStrut(26));
        form.add(loginButton);
        form.add(Box.createVerticalStrut(12));
        form.add(registerButton);
        form.add(Box.createVerticalStrut(22));
        JLabel demo = Theme.muted("Tài khoản demo quản trị: admin / admin123");
        demo.setAlignmentX(LEFT_ALIGNMENT);
        form.add(demo);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 50, 30, 50);
        formWrapper.add(form, gbc);
        add(formWrapper, BorderLayout.CENTER);
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private void dangNhap() {
        String tenDangNhap = tenDangNhapField.getText();
        String matKhau = new String(matKhauField.getPassword());
        if (authController.dangNhap(tenDangNhap, matKhau) == null) {
            Theme.error(this, "Tên đăng nhập, mật khẩu không đúng hoặc tài khoản đã bị khóa.");
            return;
        }
        matKhauField.setText("");
        onLoginSuccess.run();
    }
}