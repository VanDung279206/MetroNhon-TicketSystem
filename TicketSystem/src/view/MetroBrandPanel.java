package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

public class MetroBrandPanel extends JPanel {
    public MetroBrandPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(74, 64, 64, 64));
        setPreferredSize(new Dimension(510, 700));

        JLabel badge = new JLabel("  M  ");
        badge.setOpaque(true);
        badge.setBackground(Color.WHITE);
        badge.setForeground(Theme.PRIMARY);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 28));
        badge.setAlignmentX(LEFT_ALIGNMENT);
        badge.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel title = new JLabel("Metro Nhổn");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Di chuyển thông minh, chạm tới tương lai.");
        subtitle.setForeground(new Color(219, 234, 254));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        add(badge);
        add(Box.createVerticalStrut(30));
        add(title);
        add(Box.createVerticalStrut(12));
        add(subtitle);
        add(Box.createVerticalGlue());
        add(routeLabel("●  Nhổn", "Điểm bắt đầu"));
        add(routeLine());
        add(routeLabel("●  Cầu Giấy", "8 nhà ga • an toàn • tiện lợi"));
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
        line.setMaximumSize(new Dimension(3, 70));
        line.setPreferredSize(new Dimension(3, 70));
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
        g2.dispose();
        super.paintComponent(graphics);
    }
}