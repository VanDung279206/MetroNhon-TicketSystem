package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * thanh điều hướng dùng chung cho giao diện hành khách và admin
 * chỉ quản lý fiao diện, kh gọi trực tiếp
 */
public class NavigationDrawer extends JPanel {
    private final JPanel navigation = new JPanel();
    private final List<JButton> navigationButtons = new ArrayList<>();

    public NavigationDrawer(String role,
                            String displayName,
                            Runnable onClose,
                            Runnable onLogout) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(286, 700));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 0));

        JPanel drawer = new Theme.RoundedPanel(30, Theme.PRIMARY_DARK);
        drawer.setLayout(new BorderLayout(0, 24));
        drawer.setBorder(BorderFactory.createEmptyBorder(22, 18, 20, 18));

        drawer.add(createBrandHeader(onClose), BorderLayout.NORTH);

        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        drawer.add(navigation, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.add(createProfileCard(role, displayName));
        footer.add(Box.createVerticalStrut(12));

        JButton logoutButton = Theme.navButton("↪  Đăng xuất");
        logoutButton.setAlignmentX(LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        logoutButton.addActionListener(event -> onLogout.run());
        footer.add(logoutButton);
        drawer.add(footer, BorderLayout.SOUTH);

        add(drawer, BorderLayout.CENTER);
    }

    public JButton addItem(String label, Runnable action) {
        JButton button = Theme.navButton(label);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.addActionListener(event -> {
            select(button);
            action.run();
        });

        navigationButtons.add(button);
        navigation.add(button);
        navigation.add(Box.createVerticalStrut(8));
        if (navigationButtons.size() == 1) {
            select(button);
        }
        return button;
    }

    private JPanel createBrandHeader(Runnable onClose) {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel("M", JLabel.CENTER);
        logo.setOpaque(true);
        logo.setBackground(Color.WHITE);
        logo.setForeground(Theme.PRIMARY);
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 17f));
        logo.setPreferredSize(new Dimension(38, 38));
        logo.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel title = new JLabel("METRO NHỔN");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        brand.add(logo);
        brand.add(title);

        JButton closeButton = Theme.navIconButton("×");
        closeButton.setToolTipText("Thu gọn danh mục");
        closeButton.addActionListener(event -> onClose.run());

        header.add(brand, BorderLayout.CENTER);
        header.add(closeButton, BorderLayout.EAST);
        return header;
    }

    private JPanel createProfileCard(String role, String displayName) {
        JPanel profile = new Theme.RoundedPanel(20, Theme.PRIMARY_DARKER);
        profile.setLayout(new BorderLayout(12, 0));
        profile.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel avatar = new JLabel(Theme.initials(displayName), JLabel.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(219, 234, 254));
        avatar.setForeground(Theme.PRIMARY_DARK);
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 13f));
        avatar.setPreferredSize(new Dimension(38, 38));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(displayName);
        name.setForeground(Color.WHITE);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
        JLabel roleLabel = new JLabel(role);
        roleLabel.setForeground(new Color(191, 219, 254));
        roleLabel.setFont(roleLabel.getFont().deriveFont(12f));
        text.add(name);
        text.add(Box.createVerticalStrut(3));
        text.add(roleLabel);

        profile.add(avatar, BorderLayout.WEST);
        profile.add(text, BorderLayout.CENTER);
        return profile;
    }

    private void select(JButton selectedButton) {
        for (JButton button : navigationButtons) {
            boolean selected = button == selectedButton;
            button.setBackground(selected ? Theme.PRIMARY : Theme.PRIMARY_DARK);
            button.setForeground(selected ? Color.WHITE : new Color(219, 234, 254));
        }
    }
}