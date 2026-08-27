package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;

public class MetroBrandPanel extends JPanel {
    public MetroBrandPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(54, 64, 54, 64));
        setPreferredSize(new Dimension(510, 700));

        add(createMetroLogo());
        add(Box.createVerticalGlue());
        add(createRouteCard());
    }

    private JLabel createMetroLogo() {
        URL logoUrl = getClass().getResource("/images/hanoi-metro-logo.png");
        JLabel logo = new JLabel();
        logo.setAlignmentX(LEFT_ALIGNMENT);
        logo.setPreferredSize(new Dimension(102, 102));
        logo.setMaximumSize(new Dimension(102, 102));
        if (logoUrl != null) {
            Image logoImage = new ImageIcon(logoUrl).getImage();
            logo.setIcon(new ImageIcon(logoImage.getScaledInstance(
                    102, 102, Image.SCALE_SMOOTH)));
        }
        return logo;
    }

    private JPanel createRouteCard() {
        JPanel card = new Theme.RoundedPanel(24, new Color(255, 255, 255, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setPreferredSize(new Dimension(360, 214));
        card.setMaximumSize(new Dimension(360, 214));

        JLabel routeTitle = new JLabel("TUYẾN SỐ 3");
        routeTitle.setForeground(new Color(219, 234, 254));
        routeTitle.setFont(routeTitle.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        routeTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel routeMeta = new JLabel("8 GA • KẾT NỐI ĐÔ THỊ");
        routeMeta.setForeground(Color.WHITE);
        routeMeta.setFont(routeMeta.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        routeMeta.setAlignmentX(LEFT_ALIGNMENT);

        card.add(routeTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(routeMeta);
        card.add(Box.createVerticalStrut(18));
        card.add(routeLabel("●  Nhổn", "Điểm bắt đầu"));
        card.add(routeLine());
        card.add(routeLabel("●  Cầu Giấy", "8 nhà ga • an toàn • tiện lợi"));
        return card;
    }

    private JPanel routeLabel(String station, String note) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel stationLabel = new JLabel(station);
        stationLabel.setForeground(Color.WHITE);
        stationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel noteLabel = new JLabel(note);
        noteLabel.setForeground(new Color(191, 219, 254));
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(stationLabel);
        panel.add(Box.createVerticalStrut(3));
        panel.add(noteLabel);
        return panel;
    }

    private JPanel routeLine() {
        JPanel line = new JPanel();
        line.setBackground(new Color(96, 165, 250));
        line.setMaximumSize(new Dimension(3, 48));
        line.setPreferredSize(new Dimension(3, 48));
        line.setAlignmentX(LEFT_ALIGNMENT);
        return line;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(15, 55, 111),
                getWidth(), getHeight(), new Color(30, 99, 235)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(255, 255, 255, 18));
        g2.fillOval(getWidth() - 210, -80, 300, 300);
        g2.fillOval(-130, getHeight() - 190, 300, 300);
        drawDecorativeRoute(g2);
        g2.dispose();
        super.paintComponent(graphics);
    }

    private void drawDecorativeRoute(Graphics2D g2) {
        int routeY = Math.max(170, getHeight() / 3);
        g2.setColor(new Color(255, 255, 255, 28));
        g2.setStroke(new java.awt.BasicStroke(2.5f,
                java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
        g2.drawLine(-40, routeY + 65, getWidth() + 40, routeY - 50);

        g2.setColor(new Color(147, 197, 253, 100));
        int[] offsets = {0, 95, 190, 285, 380};
        for (int offset : offsets) {
            int x = offset - 20;
            int y = routeY + 38 - (int) Math.round(offset * 0.72);
            g2.fillOval(x - 5, y - 5, 10, 10);
        }
    }
}
