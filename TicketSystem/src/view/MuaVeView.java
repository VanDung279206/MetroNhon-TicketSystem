package view;

import controller.MuaVeController;
import model.Ga;
import model.HanhKhach;
import model.LoaiVeThang;
import model.VeLuot;
import model.VeThang;
import service.TinhGiaVeService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class MuaVeView extends JPanel {
    private final MuaVeController muaVeController;
    private final HanhKhach hanhKhach;
    private final Runnable onTicketPurchased;
    private final JComboBox<Ga> gaDiCombo;
    private final JComboBox<Ga> gaDenCombo;
    private final JLabel giaVeLuotLabel;

    public MuaVeView(MuaVeController muaVeController,
                     HanhKhach hanhKhach,
                     Runnable onTicketPurchased) {
        this.muaVeController = muaVeController;
        this.hanhKhach = hanhKhach;
        this.onTicketPurchased = onTicketPurchased;
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = Theme.title("Mua vé metro", 27);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted("Chọn loại vé phù hợp với hành trình của bạn.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);
        add(heading, BorderLayout.NORTH);

        List<Ga> danhSachGa = muaVeController.getDanhSachGa();
        gaDiCombo = new JComboBox<>(danhSachGa.toArray(new Ga[0]));
        gaDenCombo = new JComboBox<>(danhSachGa.toArray(new Ga[0]));
        if (danhSachGa.size() > 1) {
            gaDenCombo.setSelectedIndex(danhSachGa.size() - 1);
        }
        Theme.inputStyle(gaDiCombo);
        Theme.inputStyle(gaDenCombo);
        giaVeLuotLabel = Theme.title("—", 24);

        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty("JTabbedPane.tabType", "card");
        tabs.addTab("Vé lượt", taoVeLuotPanel());
        tabs.addTab("Vé tháng", taoVeThangPanel());
        add(tabs, BorderLayout.CENTER);
        capNhatGiaVeLuot();
    }

    private JPanel taoVeLuotPanel() {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(28, 20));

        JPanel form = new JPanel(new GridLayout(2, 1, 0, 18));
        form.setOpaque(false);
        form.add(fieldGroup("Ga đi", gaDiCombo));
        form.add(fieldGroup("Ga đến", gaDenCombo));

        gaDiCombo.addActionListener(event -> capNhatGiaVeLuot());
        gaDenCombo.addActionListener(event -> capNhatGiaVeLuot());

        JPanel summary = new Theme.RoundedPanel(16, Theme.PRIMARY_SOFT);
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
        summary.setPreferredSize(new Dimension(300, 240));
        JLabel label = new JLabel("TỔNG THANH TOÁN");
        label.setForeground(Theme.PRIMARY);
        label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        label.setAlignmentX(LEFT_ALIGNMENT);
        giaVeLuotLabel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel note = Theme.muted("Vé có hiệu lực trong ngày mua");
        note.setAlignmentX(LEFT_ALIGNMENT);
        JButton buyButton = Theme.primaryButton("Xác nhận mua vé lượt");
        buyButton.setAlignmentX(LEFT_ALIGNMENT);
        buyButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        buyButton.addActionListener(event -> muaVeLuot());
        summary.add(label);
        summary.add(Box.createVerticalStrut(12));
        summary.add(giaVeLuotLabel);
        summary.add(Box.createVerticalStrut(8));
        summary.add(note);
        summary.add(Box.createVerticalGlue());
        summary.add(buyButton);

        card.add(form, BorderLayout.CENTER);
        card.add(summary, BorderLayout.EAST);
        return card;
    }

    private JPanel taoVeThangPanel() {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(28, 20));

        JPanel choices = new JPanel(new GridLayout(1, 2, 18, 0));
        choices.setOpaque(false);
        choices.add(monthlyCard("Phổ thông", "Không giới hạn lượt đi trong 30 ngày",
                Theme.money(280000), LoaiVeThang.PHO_THONG));
        choices.add(monthlyCard("Ưu đãi", "Dành cho đối tượng đủ điều kiện ưu đãi",
                Theme.money(140000), LoaiVeThang.UU_DAI));
        card.add(choices, BorderLayout.CENTER);
        return card;
    }

    private JPanel monthlyCard(String titleText, String description,
                               String price, LoaiVeThang loaiVe) {
        JPanel panel = new Theme.RoundedPanel(16, Theme.BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        JLabel title = Theme.title(titleText, 21);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = Theme.muted(description);
        desc.setAlignmentX(LEFT_ALIGNMENT);
        JLabel priceLabel = Theme.title(price, 28);
        priceLabel.setForeground(Theme.PRIMARY);
        priceLabel.setAlignmentX(LEFT_ALIGNMENT);
        JButton button = Theme.primaryButton("Chọn vé " + titleText.toLowerCase());
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.addActionListener(event -> muaVeThang(loaiVe));

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(desc);
        panel.add(Box.createVerticalStrut(28));
        panel.add(priceLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(Theme.muted("Hiệu lực 30 ngày kể từ ngày mua"));
        panel.add(Box.createVerticalGlue());
        panel.add(button);
        return panel;
    }

    private JPanel fieldGroup(String labelText, JComboBox<Ga> comboBox) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setForeground(Theme.TEXT);
        label.setAlignmentX(LEFT_ALIGNMENT);
        comboBox.setAlignmentX(LEFT_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(comboBox);
        return panel;
    }

    private void capNhatGiaVeLuot() {
        Ga gaDi = (Ga) gaDiCombo.getSelectedItem();
        Ga gaDen = (Ga) gaDenCombo.getSelectedItem();
        try {
            double gia = new TinhGiaVeService().tinhGiaVe(gaDi, gaDen);
            giaVeLuotLabel.setText(Theme.money(gia));
        } catch (IllegalArgumentException e) {
            giaVeLuotLabel.setText("Chọn hai ga khác nhau");
        }
    }

    private void muaVeLuot() {
        Ga gaDi = (Ga) gaDiCombo.getSelectedItem();
        Ga gaDen = (Ga) gaDenCombo.getSelectedItem();
        try {
            VeLuot ve = muaVeController.muaVeLuot(
                    hanhKhach, gaDi == null ? null : gaDi.getMaGa(),
                    gaDen == null ? null : gaDen.getMaGa()
            );
            Theme.success(this, "Đã mua vé " + ve.getMaVe() + "\n"
                    + gaDi.getTenGa() + " → " + gaDen.getTenGa() + "\n"
                    + Theme.money(ve.getGiaVe()));
            onTicketPurchased.run();
        } catch (RuntimeException e) {
            Theme.error(this, e.getMessage());
        }
    }

    private void muaVeThang(LoaiVeThang loaiVe) {
        try {
            VeThang ve = muaVeController.muaVeThang(hanhKhach, loaiVe);
            Theme.success(this, "Đã mua vé tháng " + ve.getMaVe() + "\n"
                    + "Hết hạn: " + ve.getNgayHetHan() + "\n"
                    + Theme.money(ve.getGiaVe()));
            onTicketPurchased.run();
        } catch (RuntimeException e) {
            Theme.error(this, e.getMessage());
        }
    }
}