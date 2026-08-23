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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
        form.setPreferredSize(new Dimension(430, 630));

        JLabel title = Theme.title("Tạo tài khoản", 29);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted("Chỉ mất một phút để bắt đầu hành trình.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(22));

        addField(form, "Họ và tên", hoTenField);
        addField(form, "Tên đăng nhập", tenDangNhapField);
        addField(form, "Mật khẩu", matKhauField);
        addField(form, "Số điện thoại", soDienThoaiField);
        addField(form, "Email", emailField);

        JButton registerButton = Theme.primaryButton("Đăng ký tài khoản");
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        registerButton.setAlignmentX(LEFT_ALIGNMENT);
        registerButton.addActionListener(event -> dangKy());
        JButton backButton = Theme.secondaryButton("Quay lại đăng nhập");
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        backButton.setAlignmentX(LEFT_ALIGNMENT);
        backButton.addActionListener(event -> onBackToLogin.run());

        form.add(Box.createVerticalStrut(6));
        form.add(registerButton);
        form.add(Box.createVerticalStrut(10));
        form.add(backButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(24, 50, 24, 50);
        wrapper.add(form, gbc);
        add(new JScrollPane(wrapper), BorderLayout.CENTER);
    }

    private void addField(JPanel form, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Theme.TEXT);
        label.setAlignmentX(LEFT_ALIGNMENT);
        Theme.inputStyle(field);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        form.add(label);
        form.add(Box.createVerticalStrut(6));
        form.add(field);
        form.add(Box.createVerticalStrut(13));
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