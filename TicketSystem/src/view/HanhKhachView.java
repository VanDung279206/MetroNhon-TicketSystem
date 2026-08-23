package view;

import controller.AuthController;
import controller.MuaVeController;
import model.HanhKhach;
import model.VeMetro;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class HanhKhachView extends JPanel {
    private static final String TONG_QUAN = "TONG_QUAN";
    private static final String MUA_VE = "MUA_VE";
    private static final String LICH_SU = "LICH_SU";

    private final MuaVeController muaVeController;
    private final HanhKhach hanhKhach;
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);
    private JPanel tongQuanPanel;
    private JPanel lichSuPanel;

    public HanhKhachView(AuthController authController,
                         MuaVeController muaVeController,
                         Runnable onLogout) {
        this.muaVeController = muaVeController;
        this.hanhKhach = authController.getHanhKhachDangNhap();
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(taoSidebar(onLogout), BorderLayout.WEST);

        content.setBackground(Theme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        lamMoiNoiDung();
        add(content, BorderLayout.CENTER);
        contentLayout.show(content, TONG_QUAN);
    }

    private JPanel taoSidebar(Runnable onLogout) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Theme.PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(245, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 20, 24, 20));

        JLabel logo = new JLabel("M  METRO NHỔN");
        logo.setForeground(Color.WHITE);
        logo.setFont(logo.getFont().deriveFont(java.awt.Font.BOLD, 19f));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        JLabel user = new JLabel(hanhKhach == null ? "Hành khách" : hanhKhach.getHoTen());
        user.setForeground(new Color(191, 219, 254));
        user.setAlignmentX(LEFT_ALIGNMENT);

        JButton homeButton = Theme.navButton("⌂  Tổng quan");
        JButton buyButton = Theme.navButton("▣  Mua vé");
        JButton historyButton = Theme.navButton("≡  Lịch sử vé");
        for (JButton button : new JButton[]{homeButton, buyButton, historyButton}) {
            button.setAlignmentX(LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        }
        homeButton.addActionListener(event -> {
            lamMoiNoiDung();
            contentLayout.show(content, TONG_QUAN);
        });
        buyButton.addActionListener(event -> contentLayout.show(content, MUA_VE));
        historyButton.addActionListener(event -> {
            lamMoiNoiDung();
            contentLayout.show(content, LICH_SU);
        });

        JButton logoutButton = Theme.navButton("↪  Đăng xuất");
        logoutButton.setAlignmentX(LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        logoutButton.addActionListener(event -> onLogout.run());

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(user);
        sidebar.add(Box.createVerticalStrut(38));
        sidebar.add(homeButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(buyButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(historyButton);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutButton);
        return sidebar;
    }

    private void lamMoiNoiDung() {
        String current = TONG_QUAN;
        content.removeAll();
        tongQuanPanel = taoTongQuanPanel();
        lichSuPanel = taoLichSuPanel();
        content.add(tongQuanPanel, TONG_QUAN);
        content.add(new MuaVeView(muaVeController, hanhKhach, this::lamMoiNoiDung), MUA_VE);
        content.add(lichSuPanel, LICH_SU);
        content.revalidate();
        content.repaint();
    }

    private JPanel taoTongQuanPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setOpaque(false);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = Theme.title("Xin chào, " + hanhKhach.getHoTen(), 28);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted("Chúc bạn có một hành trình thuận tiện hôm nay.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);
        panel.add(heading, BorderLayout.NORTH);

        List<VeMetro> tickets = muaVeController.getDanhSachVeCuaHanhKhach(
                hanhKhach.getMaHanhKhach()
        );
        long active = tickets.stream().filter(VeMetro::isTrangThai).count();
        JPanel center = new JPanel(new BorderLayout(0, 22));
        center.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 18, 0));
        stats.setOpaque(false);
        stats.add(statCard("Tổng số vé", String.valueOf(tickets.size()), Theme.PRIMARY));
        stats.add(statCard("Vé đang hoạt động", String.valueOf(active), Theme.SUCCESS));
        stats.add(statCard("Tuyến metro", "Nhổn – Cầu Giấy", Theme.WARNING));
        center.add(stats, BorderLayout.NORTH);

        JPanel guide = Theme.card();
        guide.setLayout(new BoxLayout(guide, BoxLayout.Y_AXIS));
        JLabel guideTitle = Theme.title("Hành trình trong vài bước", 20);
        guideTitle.setAlignmentX(LEFT_ALIGNMENT);
        guide.add(guideTitle);
        guide.add(Box.createVerticalStrut(18));
        guide.add(Theme.muted("① Chọn ga đi và ga đến       ② Xác nhận loại vé       ③ Nhận mã vé điện tử"));
        center.add(guide, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel statCard(String labelText, String value, Color accent) {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel label = Theme.muted(labelText);
        label.setAlignmentX(LEFT_ALIGNMENT);
        JLabel valueLabel = Theme.title(value, value.length() > 12 ? 18 : 28);
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(label);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);
        return card;
    }

    private JPanel taoLichSuPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = Theme.title("Lịch sử vé", 27);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted("Danh sách vé được lưu ngay cả khi bạn đóng ứng dụng.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        String[] columns = {"Mã vé", "Loại vé", "Ngày mua", "Giá vé", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (VeMetro ve : muaVeController.getDanhSachVeCuaHanhKhach(
                hanhKhach.getMaHanhKhach())) {
            model.addRow(new Object[]{
                    ve.getMaVe(),
                    "VE_LUOT".equals(ve.getLoaiVe()) ? "Vé lượt" : "Vé tháng",
                    Theme.dateTime(ve.getNgayMua()),
                    Theme.money(ve.getGiaVe()),
                    ve.isTrangThai() ? "Đang hoạt động" : "Hết hiệu lực"
            });
        }
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Theme.PRIMARY_SOFT);
        table.setSelectionForeground(Theme.TEXT);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(Theme.compoundBorder(0, 0));

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}