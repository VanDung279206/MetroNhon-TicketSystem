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

    public NavigationDrawer(Runnable onClose) {
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

        add(drawer, BorderLayout.CENTER);
    }

    public JButton addItem(String label, Runnable action) {
        return addItem(label, null, action);
    }

    public JButton addItem(String label, String iconPath, Runnable action) {
        JButton button = iconPath == null
                ? Theme.navButton(label)
                : Theme.navButton(label, iconPath);
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
        JLabel logo = new JLabel(Theme.icon(
                "/images/hanoi-metro-logo.png", 46, 46
        ));
        logo.setPreferredSize(new Dimension(46, 46));

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

    private void select(JButton selectedButton) {
        for (JButton button : navigationButtons) {
            boolean selected = button == selectedButton;
            Theme.updateNavigationButton(button, selected);
        }
    }
}