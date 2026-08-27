package view;

import controller.AdminController;
import model.TaiKhoan;
import model.VeMetro;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class AdminView extends JPanel {
    private final AdminController adminController;
    private final JPanel content = new JPanel(new BorderLayout());
    private final JPanel workspace = new JPanel(new BorderLayout());
    private NavigationDrawer drawer;
    private JTable accountTable;

    public AdminView(AdminController adminController,
                     TaiKhoan taiKhoan,
                     Runnable onLogout) {
        this.adminController = adminController;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        drawer = taoSidebar(taiKhoan, onLogout);
        workspace.setOpaque(false);
        workspace.add(drawer, BorderLayout.WEST);
        content.setBackground(Theme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
        workspace.add(content, BorderLayout.CENTER);

        add(taoTopBar(taiKhoan, onLogout), BorderLayout.NORTH);
        add(workspace, BorderLayout.CENTER);
        refresh();
    }

    private NavigationDrawer taoSidebar(TaiKhoan taiKhoan, Runnable onLogout) {
        NavigationDrawer navigationDrawer = new NavigationDrawer(
                "Quản trị hệ thống", taiKhoan.getTenDangNhap(),
                this::toggleDrawer, onLogout
        );
        navigationDrawer.addItem("▦  Bảng điều khiển", this::refresh);
        return navigationDrawer;
    }

    private JPanel taoTopBar(TaiKhoan taiKhoan, Runnable onLogout) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        JPanel bar = new Theme.RoundedPanel(24, Theme.SURFACE);
        bar.setLayout(new BorderLayout(18, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JButton menuButton = Theme.ghostButton("☰  Danh mục");
        menuButton.addActionListener(event -> toggleDrawer());
        JLabel section = Theme.muted("Bảng điều khiển quản trị");
        section.setFont(section.getFont().deriveFont(java.awt.Font.BOLD));
        left.add(menuButton);
        left.add(section);

        JButton accountButton = Theme.accountButton(taiKhoan.getTenDangNhap());
        accountButton.addActionListener(event -> Theme.showAccountMenu(
                accountButton,
                taiKhoan.getTenDangNhap(),
                taiKhoan.getTenDangNhap(),
                "Quản trị viên hệ thống",
                () -> showAccountDetails(taiKhoan),
                null,
                onLogout
        ));

        bar.add(left, BorderLayout.WEST);
        bar.add(accountButton, BorderLayout.EAST);
        wrapper.add(bar, BorderLayout.CENTER);
        return wrapper;
    }

    private void toggleDrawer() {
        drawer.setVisible(!drawer.isVisible());
        workspace.revalidate();
        workspace.repaint();
    }

    private void refresh() {
        content.removeAll();

        JPanel main = new JPanel(new BorderLayout(0, 22));
        main.setOpaque(false);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = Theme.title("Bảng điều khiển", 28);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted("Theo dõi tài khoản, vé đã bán và doanh thu hệ thống.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        List<TaiKhoan> accounts = adminController.getDanhSachTaiKhoan();
        List<VeMetro> tickets = adminController.getDanhSachVeDaBan();
        long activeAccounts = accounts.stream().filter(TaiKhoan::isTrangThai).count();
        JPanel stats = new JPanel(new GridLayout(1, 3, 18, 0));
        stats.setOpaque(false);
        stats.add(statCard("Tài khoản hoạt động", String.valueOf(activeAccounts), Theme.SUCCESS));
        stats.add(statCard("Tổng vé đã bán", String.valueOf(tickets.size()), Theme.PRIMARY));
        stats.add(statCard("Tổng doanh thu", Theme.money(adminController.tinhTongDoanhThu()),
                Theme.WARNING));

        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty("JTabbedPane.tabType", "card");
        tabs.addTab("Tài khoản", taoTaiKhoanPanel(accounts));
        tabs.addTab("Vé đã bán", taoVePanel(tickets));

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);
        main.add(heading, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        content.add(main, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private JPanel statCard(String labelText, String value, Color accent) {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel label = Theme.muted(labelText);
        label.setAlignmentX(LEFT_ALIGNMENT);
        JLabel valueLabel = Theme.title(value, value.length() > 12 ? 21 : 29);
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(label);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        return card;
    }

    private JPanel taoTaiKhoanPanel(List<TaiKhoan> accounts) {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        String[] columns = {"Tên đăng nhập", "Vai trò", "Trạng thái"};
        DefaultTableModel model = nonEditableModel(columns);
        for (TaiKhoan account : accounts) {
            model.addRow(new Object[]{
                    account.getTenDangNhap(), account.getVaiTro(),
                    account.isTrangThai() ? "Đang hoạt động" : "Đã khóa"
            });
        }
        accountTable = new JTable(model);
        styleTable(accountTable);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton unlockButton = Theme.secondaryButton("Mở khóa");
        JButton lockButton = Theme.dangerButton("Khóa tài khoản");
        unlockButton.addActionListener(event -> capNhatTaiKhoan(false));
        lockButton.addActionListener(event -> capNhatTaiKhoan(true));
        actions.add(unlockButton);
        actions.add(lockButton);

        panel.add(new JScrollPane(accountTable), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoVePanel(List<VeMetro> tickets) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        String[] columns = {"Mã vé", "Hành khách", "Loại", "Ngày mua", "Giá vé"};
        DefaultTableModel model = nonEditableModel(columns);
        for (VeMetro ticket : tickets) {
            model.addRow(new Object[]{
                    ticket.getMaVe(),
                    ticket.getHanhKhach().getHoTen(),
                    "VE_LUOT".equals(ticket.getLoaiVe()) ? "Vé lượt" : "Vé tháng",
                    Theme.dateTime(ticket.getNgayMua()),
                    Theme.money(ticket.getGiaVe())
            });
        }
        JTable table = new JTable(model);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel nonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Theme.PRIMARY_SOFT);
        table.setSelectionForeground(Theme.TEXT);
    }

    private void capNhatTaiKhoan(boolean khoaTaiKhoan) {
        int row = accountTable.getSelectedRow();
        if (row < 0) {
            Theme.error(this, "Hãy chọn một tài khoản trong bảng.");
            return;
        }
        String username = String.valueOf(accountTable.getValueAt(row, 0));
        boolean success = khoaTaiKhoan
                ? adminController.khoaTaiKhoan(username)
                : adminController.moKhoaTaiKhoan(username);
        if (!success) {
            Theme.error(this, "Không thể cập nhật tài khoản " + username + ".");
            return;
        }
        Theme.success(this, "Đã cập nhật tài khoản " + username + ".");
        refresh();
    }

    private void showAccountDetails(TaiKhoan taiKhoan) {
        JPanel details = new JPanel(new GridLayout(0, 2, 18, 12));
        details.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        addAccountRow(details, "Tên đăng nhập", taiKhoan.getTenDangNhap());
        addAccountRow(details, "Vai trò", "Quản trị viên");
        addAccountRow(details, "Trạng thái",
                taiKhoan.isTrangThai() ? "Đang hoạt động" : "Đã khóa");
        javax.swing.JOptionPane.showMessageDialog(
                this, details, "Thông tin tài khoản",
                javax.swing.JOptionPane.PLAIN_MESSAGE
        );
    }

    private void addAccountRow(JPanel panel, String labelText, String valueText) {
        JLabel label = Theme.muted(labelText);
        JLabel value = new JLabel(valueText);
        value.setForeground(Theme.TEXT);
        value.setFont(value.getFont().deriveFont(java.awt.Font.BOLD));
        panel.add(label);
        panel.add(value);
    }
}