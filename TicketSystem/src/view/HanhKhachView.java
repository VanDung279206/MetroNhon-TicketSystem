package view;

import controller.AuthController;
import controller.MuaVeController;
import model.Ga;
import model.HanhKhach;
import model.LuotSuDungVe;
import model.PhieuHuyVe;
import model.VeLuot;
import model.VeMetro;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class HanhKhachView extends JPanel {
    private static final String TONG_QUAN = "TONG_QUAN";
    private static final String MUA_VE = "MUA_VE";
    private static final String LICH_SU = "LICH_SU";
    private static final String VI_TIEN = "VI_TIEN";

    private final AuthController authController;
    private final MuaVeController muaVeController;
    private final HanhKhach hanhKhach;
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);
    private final JPanel workspace = new JPanel(new BorderLayout());
    private final JLabel sectionLabel = Theme.muted("Tổng quan");
    private NavigationDrawer drawer;
    private JButton accountButton;
    private String currentView = TONG_QUAN;

    public HanhKhachView(AuthController authController,
                         MuaVeController muaVeController,
                         Runnable onLogout) {
        this.authController = authController;
        this.muaVeController = muaVeController;
        this.hanhKhach = authController.getHanhKhachDangNhap();

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        drawer = createDrawer();
        workspace.setOpaque(false);
        workspace.add(drawer, BorderLayout.WEST);

        content.setBackground(Theme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
        workspace.add(content, BorderLayout.CENTER);

        add(createTopBar(onLogout), BorderLayout.NORTH);
        add(workspace, BorderLayout.CENTER);

        refreshContent();
        showView(TONG_QUAN, "Tổng quan");
    }

    private NavigationDrawer createDrawer() {
        NavigationDrawer navigationDrawer = new NavigationDrawer(
                this::toggleDrawer
        );
        navigationDrawer.addItem(
                "Tổng quan", "/images/edit.png",
                () -> showView(TONG_QUAN, "Tổng quan")
        );
        navigationDrawer.addItem(
                "Mua vé", "/images/ticket-alt.png",
                () -> showView(MUA_VE, "Mua vé Metro")
        );
        navigationDrawer.addItem(
                "Lịch sử vé", "/images/ticket.png",
                () -> showView(LICH_SU, "Lịch sử vé")
        );
        navigationDrawer.addItem(
                "Ví và nạp tiền", "/images/wallet.png",
                () -> showView(VI_TIEN, "Ví Metro")
        );
        return navigationDrawer;
    }

    private JPanel createTopBar(Runnable onLogout) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        JPanel bar = new Theme.RoundedPanel(24, Theme.SURFACE);
        bar.setLayout(new BorderLayout(18, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JButton menuButton = Theme.ghostIconButton(
                "/images/menu-burger.png",
                "Mở hoặc thu gọn thanh chức năng"
        );
        menuButton.addActionListener(event -> toggleDrawer());

        sectionLabel.setFont(sectionLabel.getFont().deriveFont(Font.BOLD));
        left.add(menuButton);
        left.add(sectionLabel);

        String name = hanhKhach == null ? "Hành khách" : hanhKhach.getHoTen();
        accountButton = Theme.accountButton(name);
        accountButton.addActionListener(event -> {
            String currentName = hanhKhach == null
                    ? "Hành khách" : hanhKhach.getHoTen();
            String username = hanhKhach == null || hanhKhach.getTaiKhoan() == null
                    ? "hanhkhach" : hanhKhach.getTaiKhoan().getTenDangNhap();
            String detail = hanhKhach == null
                    ? "Tài khoản hành khách" : hanhKhach.getEmail();
            Theme.showAccountMenu(
                    accountButton, currentName, username, detail,
                    this::showAccountDetails, this::editAccountDetails,
                    this::changePassword, onLogout
            );
        });

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

    private void showView(String viewName, String sectionName) {
        currentView = viewName;
        sectionLabel.setText(sectionName);
        if (TONG_QUAN.equals(viewName)
                || LICH_SU.equals(viewName)
                || VI_TIEN.equals(viewName)) {
            refreshContent();
        }
        contentLayout.show(content, viewName);
    }

    private void refreshContent() {
        content.removeAll();
        content.add(createOverviewPanel(), TONG_QUAN);
        content.add(new MuaVeView(muaVeController, hanhKhach, this::afterTicketPurchased),
                MUA_VE);
        content.add(createHistoryPanel(), LICH_SU);
        content.add(createWalletPanel(), VI_TIEN);
        contentLayout.show(content, currentView);
        content.revalidate();
        content.repaint();
    }

    private void afterTicketPurchased() {
        refreshContent();
        contentLayout.show(content, currentView);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);
        panel.add(createWelcomeBanner(), BorderLayout.NORTH);

        List<VeMetro> tickets = muaVeController.getDanhSachVeCuaHanhKhach(
                hanhKhach.getMaHanhKhach()
        );
        long active = tickets.stream().filter(muaVeController::veConHieuLuc).count();

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("SỐ DƯ VÍ METRO",
                Theme.money(hanhKhach.getTaiKhoan().getSoDu()),
                "Dùng để thanh toán vé", Theme.WARNING));
        stats.add(statCard("TỔNG SỐ VÉ", String.valueOf(tickets.size()),
                "Vé đã mua", Theme.PRIMARY));
        stats.add(statCard("ĐANG HOẠT ĐỘNG", String.valueOf(active),
                "Sẵn sàng sử dụng", Theme.ACCENT));
        center.add(stats, BorderLayout.NORTH);
        center.add(createJourneyGuide(), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createWelcomeBanner() {
        JPanel banner = new Theme.RoundedPanel(28, Theme.PRIMARY_DARK);
        banner.setLayout(new BorderLayout(24, 0));
        banner.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("HÀ NỘI METRO • TUYẾN 3");
        eyebrow.setForeground(new Color(147, 197, 253));
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 12f));
        eyebrow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel title = new JLabel("Xin chào, " + hanhKhach.getHoTen());
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 27f));
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Một chạm mua vé, hành trình nhẹ nhàng hơn mỗi ngày.");
        subtitle.setForeground(new Color(219, 234, 254));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        text.add(eyebrow);
        text.add(Box.createVerticalStrut(8));
        text.add(title);
        text.add(Box.createVerticalStrut(7));
        text.add(subtitle);

        JButton buyNowButton = Theme.primaryButton("Mua vé ngay  →");
        buyNowButton.addActionListener(event -> showView(MUA_VE, "Mua vé Metro"));

        banner.add(text, BorderLayout.CENTER);
        banner.add(buyNowButton, BorderLayout.EAST);
        return banner;
    }

    private JPanel statCard(String labelText, String value,
                            String description, Color accent) {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setForeground(Theme.MUTED);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        label.setAlignmentX(LEFT_ALIGNMENT);
        JLabel valueLabel = Theme.title(value, value.length() > 12 ? 18 : 27);
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel descriptionLabel = Theme.muted(description);
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(label);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descriptionLabel);
        return card;
    }

    private JPanel createJourneyGuide() {
        JPanel guide = Theme.card();
        guide.setLayout(new BorderLayout(0, 18));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = Theme.title("Hành trình trong 3 bước", 20);
        JLabel status = new JLabel("● Hệ thống hoạt động bình thường");
        status.setForeground(Theme.ACCENT);
        status.setFont(status.getFont().deriveFont(Font.BOLD, 12f));
        heading.add(title, BorderLayout.WEST);
        heading.add(status, BorderLayout.EAST);

        JPanel steps = new JPanel(new GridLayout(1, 3, 14, 0));
        steps.setOpaque(false);
        steps.add(stepCard("01", "Chọn hành trình",
                "Chọn ga đi và ga đến trên tuyến."));
        steps.add(stepCard("02", "Xác nhận loại vé",
                "Vé lượt hoặc vé tháng phù hợp."));
        steps.add(stepCard("03", "Nhận vé điện tử",
                "Mở lịch sử và sử dụng vé khi lên tàu."));

        guide.add(heading, BorderLayout.NORTH);
        guide.add(steps, BorderLayout.CENTER);
        return guide;
    }

    private JPanel stepCard(String number, String titleText, String description) {
        JPanel step = new Theme.RoundedPanel(18, Theme.SURFACE_SOFT);
        step.setLayout(new BorderLayout(12, 0));
        step.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel numberLabel = new JLabel(number, JLabel.CENTER);
        numberLabel.setOpaque(true);
        numberLabel.setBackground(Theme.PRIMARY_SOFT);
        numberLabel.setForeground(Theme.PRIMARY);
        numberLabel.setFont(numberLabel.getFont().deriveFont(Font.BOLD, 14f));
        numberLabel.setPreferredSize(new Dimension(42, 42));

        JPanel numberHolder = new JPanel(new BorderLayout());
        numberHolder.setOpaque(false);
        numberHolder.setPreferredSize(new Dimension(42, 42));
        numberHolder.add(numberLabel, BorderLayout.NORTH);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(titleText);
        title.setForeground(Theme.TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        JTextArea detail = new JTextArea(description);
        detail.setEditable(false);
        detail.setFocusable(false);
        detail.setOpaque(false);
        detail.setBorder(BorderFactory.createEmptyBorder());
        detail.setForeground(Theme.MUTED);
        detail.setFont(title.getFont().deriveFont(Font.PLAIN, 13f));
        detail.setLineWrap(true);
        detail.setWrapStyleWord(true);
        detail.setRows(2);
        detail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(detail);

        step.add(numberHolder, BorderLayout.WEST);
        step.add(text, BorderLayout.CENTER);
        return step;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = Theme.title("Lịch sử vé", 27);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted(
                "Theo dõi vé đã mua, lượt sử dụng và các khoản hoàn tiền."
        );
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        titles.add(title);
        titles.add(Box.createVerticalStrut(6));
        titles.add(subtitle);

        JTextField searchField = new JTextField();
        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm mã vé, loại vé, trạng thái..."
        );
        searchField.setPreferredSize(new Dimension(270, 42));
        Theme.inputStyle(searchField);
        JButton buyButton = Theme.primaryButton("＋ Mua vé mới");
        buyButton.addActionListener(event -> showView(MUA_VE, "Mua vé Metro"));

        JPanel headingActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headingActions.setOpaque(false);
        headingActions.add(searchField);
        headingActions.add(buyButton);
        heading.add(titles, BorderLayout.WEST);
        heading.add(headingActions, BorderLayout.EAST);

        String[] columns = {
                "Mã vé", "Loại vé", "Ngày mua", "Giá vé",
                "Lượt dùng", "Trạng thái"
        };
        DefaultTableModel ticketModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (VeMetro ticket : muaVeController.getDanhSachVeCuaHanhKhach(
                hanhKhach.getMaHanhKhach())) {
            ticketModel.addRow(new Object[]{
                    ticket.getMaVe(),
                    "VE_LUOT".equals(ticket.getLoaiVe()) ? "Vé lượt" : "Vé tháng",
                    Theme.dateTime(ticket.getNgayMua()),
                    Theme.money(ticket.getGiaVe()),
                    muaVeController.getSoLuotSuDung(ticket.getMaVe()),
                    getTicketStatus(ticket)
            });
        }

        JTable ticketTable = new JTable(ticketModel);
        styleTable(ticketTable);

        JButton cancelButton = Theme.dangerButton("Hủy vé & hoàn tiền");
        cancelButton.addActionListener(event -> cancelSelectedTicket(
                ticketTable, ticketModel
        ));
        JButton useButton = Theme.primaryButton("▶  Sử dụng vé đã chọn");
        useButton.addActionListener(event -> useSelectedTicket(
                ticketTable, ticketModel
        ));
        JPanel ticketActions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        ticketActions.setOpaque(false);
        ticketActions.add(Theme.muted(
                "Chưa sử dụng: vé lượt hoàn 90% • vé tháng hoàn 80%"
        ));
        ticketActions.add(cancelButton);
        ticketActions.add(useButton);

        JPanel ticketPanel = Theme.card();
        ticketPanel.setLayout(new BorderLayout(0, 12));
        ticketPanel.add(new JScrollPane(ticketTable), BorderLayout.CENTER);
        ticketPanel.add(ticketActions, BorderLayout.SOUTH);

        String[] usageColumns = {
                "Mã lượt", "Mã vé", "Ga đi", "Ga đến", "Thời gian sử dụng"
        };
        DefaultTableModel usageModel = new DefaultTableModel(usageColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (LuotSuDungVe usage :
                muaVeController.getDanhSachLuotSuDungCuaHanhKhach(
                        hanhKhach.getMaHanhKhach())) {
            usageModel.addRow(new Object[]{
                    usage.getMaLuotSuDung(),
                    usage.getMaVe(),
                    muaVeController.getTenGa(usage.getMaGaDi()),
                    muaVeController.getTenGa(usage.getMaGaDen()),
                    Theme.dateTime(usage.getThoiGianSuDung())
            });
        }

        JTable usageTable = new JTable(usageModel);
        styleTable(usageTable);
        JPanel usagePanel = Theme.card();
        usagePanel.setLayout(new BorderLayout());
        usagePanel.add(new JScrollPane(usageTable), BorderLayout.CENTER);

        String[] cancellationColumns = {
                "Mã hủy", "Mã vé", "Thời gian", "Giá vé gốc",
                "Tỷ lệ hoàn", "Tiền hoàn", "Lý do"
        };
        DefaultTableModel cancellationModel = new DefaultTableModel(
                cancellationColumns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (PhieuHuyVe cancellation :
                muaVeController.getDanhSachPhieuHuyCuaHanhKhach(
                        hanhKhach.getMaHanhKhach())) {
            cancellationModel.addRow(new Object[]{
                    cancellation.getMaPhieuHuy(),
                    cancellation.getMaVe(),
                    Theme.dateTime(cancellation.getThoiGianHuy()),
                    Theme.money(cancellation.getGiaVeGoc()),
                    Math.round(cancellation.getTyLeHoan() * 100) + "%",
                    Theme.money(cancellation.getSoTienHoan()),
                    cancellation.getLyDo()
            });
        }

        JTable cancellationTable = new JTable(cancellationModel);
        styleTable(cancellationTable);
        JPanel cancellationPanel = Theme.card();
        cancellationPanel.setLayout(new BorderLayout());
        cancellationPanel.add(
                new JScrollPane(cancellationTable), BorderLayout.CENTER
        );

        Theme.enableTableSearch(
                searchField, ticketTable, usageTable, cancellationTable
        );

        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty("JTabbedPane.tabType", "card");
        tabs.addTab("Vé của tôi", ticketPanel);
        tabs.addTab("Lịch sử sử dụng", usagePanel);
        tabs.addTab("Lịch sử hoàn tiền", cancellationPanel);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Theme.PRIMARY_SOFT);
        table.setSelectionForeground(Theme.TEXT);
        table.setShowVerticalLines(false);
    }

    private String getTicketStatus(VeMetro ticket) {
        if (muaVeController.daHuyVe(ticket.getMaVe())) {
            return "Đã hủy";
        }
        if (muaVeController.veConHieuLuc(ticket)) {
            return "Có thể sử dụng";
        }
        if (ticket instanceof VeLuot
                && muaVeController.getSoLuotSuDung(ticket.getMaVe()) > 0) {
            return "Đã sử dụng";
        }
        if (!ticket.isTrangThai()) {
            return "Đã vô hiệu hóa";
        }
        return "Hết hạn";
    }

    private void cancelSelectedTicket(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            Theme.error(this, "Hãy chọn một vé trong bảng.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String maVe = String.valueOf(model.getValueAt(modelRow, 0));
        try {
            double soTienHoan = muaVeController.tinhTienHoanDuKien(
                    hanhKhach, maVe
            );
            JTextField reasonField = new JTextField();
            reasonField.putClientProperty(
                    "JTextField.placeholderText", "Lý do hủy (không bắt buộc)"
            );
            Theme.inputStyle(reasonField);

            JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
            form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            form.add(new JLabel("Mã vé: " + maVe));
            form.add(new JLabel("Số tiền dự kiến hoàn: "
                    + Theme.money(soTienHoan)));
            form.add(new JLabel(
                    "Vé sẽ bị vô hiệu hóa ngay sau khi xác nhận."
            ));
            form.add(reasonField);

            int confirm = JOptionPane.showConfirmDialog(
                    this, form, "Xác nhận hủy vé",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.OK_OPTION) {
                return;
            }

            PhieuHuyVe cancellation = muaVeController.huyVe(
                    hanhKhach, maVe, reasonField.getText()
            );
            Theme.success(this, "Hủy vé thành công\nMã giao dịch: "
                    + cancellation.getMaPhieuHuy()
                    + "\nĐã hoàn: "
                    + Theme.money(cancellation.getSoTienHoan())
                    + "\nSố dư mới: "
                    + Theme.money(hanhKhach.getTaiKhoan().getSoDu()));
            refreshContent();
            contentLayout.show(content, LICH_SU);
        } catch (RuntimeException e) {
            Theme.error(this, e.getMessage());
        }
    }

    private void useSelectedTicket(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            Theme.error(this, "Hãy chọn một vé trong bảng.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String maVe = String.valueOf(model.getValueAt(modelRow, 0));
        VeMetro ticket = muaVeController.timVeCuaHanhKhach(hanhKhach, maVe);
        if (ticket == null) {
            Theme.error(this, "Không tìm thấy vé " + maVe + ".");
            return;
        }

        String maGaDi = null;
        String maGaDen = null;
        if (ticket instanceof VeLuot) {
            VeLuot veLuot = (VeLuot) ticket;
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Sử dụng vé " + maVe + " cho hành trình\n"
                            + veLuot.getGaDi().getTenGa() + " → "
                            + veLuot.getGaDen().getTenGa() + "?\n"
                            + "Vé lượt sẽ hết hiệu lực sau thao tác này.",
                    "Xác nhận sử dụng vé",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm != JOptionPane.OK_OPTION) {
                return;
            }
        } else {
            List<Ga> stations = muaVeController.getDanhSachGa();
            JComboBox<Ga> gaDiCombo = new JComboBox<>(
                    stations.toArray(new Ga[0])
            );
            JComboBox<Ga> gaDenCombo = new JComboBox<>(
                    stations.toArray(new Ga[0])
            );
            if (stations.size() > 1) {
                gaDenCombo.setSelectedIndex(stations.size() - 1);
            }
            Theme.inputStyle(gaDiCombo);
            Theme.inputStyle(gaDenCombo);
            JPanel routeForm = new JPanel(new GridLayout(0, 1, 0, 8));
            routeForm.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            routeForm.add(new JLabel("Ga đi"));
            routeForm.add(gaDiCombo);
            routeForm.add(new JLabel("Ga đến"));
            routeForm.add(gaDenCombo);

            int confirm = JOptionPane.showConfirmDialog(
                    this, routeForm, "Sử dụng vé tháng " + maVe,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );
            if (confirm != JOptionPane.OK_OPTION) {
                return;
            }
            Ga gaDi = (Ga) gaDiCombo.getSelectedItem();
            Ga gaDen = (Ga) gaDenCombo.getSelectedItem();
            maGaDi = gaDi == null ? null : gaDi.getMaGa();
            maGaDen = gaDen == null ? null : gaDen.getMaGa();
        }

        try {
            LuotSuDungVe usage = muaVeController.suDungVe(
                    hanhKhach, maVe, maGaDi, maGaDen
            );
            Theme.success(this, "Sử dụng vé thành công\nMã lượt: "
                    + usage.getMaLuotSuDung() + "\n"
                    + muaVeController.getTenGa(usage.getMaGaDi()) + " → "
                    + muaVeController.getTenGa(usage.getMaGaDen()));
            refreshContent();
            contentLayout.show(content, LICH_SU);
        } catch (RuntimeException e) {
            Theme.error(this, e.getMessage());
        }
    }

    private JPanel createWalletPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = Theme.title("Ví Metro", 27);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted(
                "Nạp tiền demo và sử dụng số dư để thanh toán vé."
        );
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        JPanel cards = new JPanel(new GridLayout(1, 2, 18, 0));
        cards.setOpaque(false);
        cards.add(createBalanceCard());
        cards.add(createTopUpCard());

        panel.add(heading, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBalanceCard() {
        JPanel card = new Theme.RoundedPanel(26, Theme.PRIMARY_DARK);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel label = new JLabel("SỐ DƯ KHẢ DỤNG");
        label.setForeground(new Color(191, 219, 254));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        label.setAlignmentX(LEFT_ALIGNMENT);
        JLabel balance = Theme.title(
                Theme.money(hanhKhach.getTaiKhoan().getSoDu()), 34
        );
        balance.setForeground(Color.WHITE);
        balance.setAlignmentX(LEFT_ALIGNMENT);
        JLabel account = new JLabel(
                "@" + hanhKhach.getTaiKhoan().getTenDangNhap()
        );
        account.setForeground(new Color(219, 234, 254));
        account.setAlignmentX(LEFT_ALIGNMENT);

        card.add(label);
        card.add(Box.createVerticalStrut(16));
        card.add(balance);
        card.add(Box.createVerticalStrut(10));
        card.add(account);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel createTopUpCard() {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = Theme.title("Nạp tiền", 22);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel description = Theme.muted(
                "Nhập số tiền hoặc chọn nhanh một mệnh giá."
        );
        description.setAlignmentX(LEFT_ALIGNMENT);

        JTextField amountField = new JTextField();
        amountField.setToolTipText("Ví dụ: 100000");
        Theme.inputStyle(amountField);
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        amountField.setAlignmentX(LEFT_ALIGNMENT);

        JPanel quickAmounts = new JPanel(new GridLayout(1, 4, 8, 0));
        quickAmounts.setOpaque(false);
        quickAmounts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        for (int amount : new int[]{50_000, 100_000, 200_000, 500_000}) {
            JButton button = Theme.secondaryButton(
                    (amount / 1_000) + "K"
            );
            button.addActionListener(event ->
                    amountField.setText(String.valueOf(amount)));
            quickAmounts.add(button);
        }

        JButton topUpButton = Theme.primaryButton(" Xác nhận nạp tiền");
        topUpButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        topUpButton.setAlignmentX(LEFT_ALIGNMENT);
        topUpButton.addActionListener(event -> napTien(amountField.getText()));

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(description);
        card.add(Box.createVerticalStrut(22));
        card.add(new JLabel("Số tiền (VND)"));
        card.add(Box.createVerticalStrut(8));
        card.add(amountField);
        card.add(Box.createVerticalStrut(14));
        card.add(quickAmounts);
        card.add(Box.createVerticalGlue());
        card.add(topUpButton);
        return card;
    }

    private void napTien(String giaTri) {
        try {
            String daChuanHoa = giaTri == null ? ""
                    : giaTri.replace(".", "")
                    .replace(",", "")
                    .replace(" ", "")
                    .trim();
            double soTien = Double.parseDouble(daChuanHoa);
            double soDuMoi = muaVeController.napTien(hanhKhach, soTien);
            Theme.success(this, "Nạp tiền thành công\nSố dư mới: "
                    + Theme.money(soDuMoi));
            refreshContent();
            contentLayout.show(content, VI_TIEN);
        } catch (NumberFormatException e) {
            Theme.error(this, "Vui lòng nhập số tiền hợp lệ.");
        } catch (RuntimeException e) {
            Theme.error(this, e.getMessage());
        }
    }

    private void changePassword() {
        JPasswordField oldPassword = new JPasswordField();
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();
        Theme.inputStyle(oldPassword);
        Theme.inputStyle(newPassword);
        Theme.inputStyle(confirmPassword);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Mật khẩu hiện tại"));
        form.add(oldPassword);
        form.add(new JLabel("Mật khẩu mới"));
        form.add(newPassword);
        form.add(new JLabel("Nhập lại mật khẩu mới"));
        form.add(confirmPassword);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Đổi mật khẩu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String oldValue = new String(oldPassword.getPassword());
        String newValue = new String(newPassword.getPassword());
        String confirmValue = new String(confirmPassword.getPassword());
        if (!newValue.equals(confirmValue)) {
            Theme.error(this, "Mật khẩu nhập lại chưa trùng khớp.");
            return;
        }
        if (!authController.doiMatKhau(oldValue, newValue)) {
            Theme.error(this, "Mật khẩu hiện tại không đúng hoặc mật khẩu mới chưa hợp lệ.");
            return;
        }
        Theme.success(this, "Đổi mật khẩu thành công.");
    }

    private void showAccountDetails() {
        JPanel details = new JPanel(new GridLayout(0, 2, 18, 12));
        details.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        addAccountRow(details, "Họ và tên", hanhKhach.getHoTen());
        addAccountRow(details, "Tên đăng nhập",
                hanhKhach.getTaiKhoan().getTenDangNhap());
        addAccountRow(details, "Mã hành khách", hanhKhach.getMaHanhKhach());
        addAccountRow(details, "Số điện thoại", hanhKhach.getSoDienThoai());
        addAccountRow(details, "Email", hanhKhach.getEmail());
        addAccountRow(details, "Số dư",
                Theme.money(hanhKhach.getTaiKhoan().getSoDu()));
        addAccountRow(details, "Trạng thái", "Đang hoạt động");

        JOptionPane.showMessageDialog(
                this, details, "Thông tin tài khoản",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void editAccountDetails() {
        JTextField nameField = new JTextField(hanhKhach.getHoTen());
        JTextField phoneField = new JTextField(hanhKhach.getSoDienThoai());
        JTextField emailField = new JTextField(hanhKhach.getEmail());
        Theme.inputStyle(nameField);
        Theme.inputStyle(phoneField);
        Theme.inputStyle(emailField);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Họ và tên"));
        form.add(nameField);
        form.add(new JLabel("Số điện thoại"));
        form.add(phoneField);
        form.add(new JLabel("Email"));
        form.add(emailField);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Thay đổi thông tin",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            boolean success = authController.capNhatThongTinHanhKhach(
                    nameField.getText(), phoneField.getText(), emailField.getText()
            );
            if (!success) {
                Theme.error(this, "Không thể cập nhật thông tin hành khách.");
                return;
            }

            Theme.updateAccountButton(accountButton, hanhKhach.getHoTen());
            refreshContent();
            Theme.success(this, "Cập nhật thông tin thành công.");
        } catch (RuntimeException e) {
            Theme.error(this, e.getMessage());
        }
    }

    private void addAccountRow(JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Theme.MUTED);
        JLabel value = new JLabel(valueText == null || valueText.isBlank() ? "—" : valueText);
        value.setForeground(Theme.TEXT);
        value.setFont(value.getFont().deriveFont(Font.BOLD));
        panel.add(label);
        panel.add(value);
    }
}