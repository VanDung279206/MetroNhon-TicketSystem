package view;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Nen anh toan man hinh cho trang dang nhap.
 * Anh duoc ve theo che do "cover" nen khong bi meo khi thay doi kich thuoc cua so.
 */
public class MetroLoginBackgroundPanel extends JPanel {
    private static final int DISPLAY_TIME_MS = 2_000;
    private static final int SLIDE_TIME_MS = 350;
    private static final int FRAME_TIME_MS = 32;

    private final List<BufferedImage> backgroundImages = new ArrayList<>();
    private final Timer displayTimer;
    private final Timer slideTimer;
    private int currentImageIndex;
    private int nextImageIndex;
    private double slideProgress;

    public MetroLoginBackgroundPanel() {
        setOpaque(true);
        loadBackgrounds();

        displayTimer = new Timer(DISPLAY_TIME_MS, event -> startSlide());
        displayTimer.setRepeats(false);

        slideTimer = new Timer(FRAME_TIME_MS, event -> updateSlide());
        slideTimer.setCoalesce(true);
    }

    private void loadBackgrounds() {
        loadImage("/images/MetroHN.png");
        loadImage("/images/MetroHN_2.png");
    }

    private void loadImage(String resourcePath) {
        URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            return;
        }
        try {
            BufferedImage image = ImageIO.read(imageUrl);
            if (image != null) {
                backgroundImages.add(image);
            }
        } catch (IOException ignored) {
            // paintComponent se dung gradient du phong neu anh khong doc duoc.
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (backgroundImages.size() > 1) {
            displayTimer.restart();
        }
    }

    @Override
    public void removeNotify() {
        displayTimer.stop();
        slideTimer.stop();
        super.removeNotify();
    }

    private void startSlide() {
        if (backgroundImages.size() < 2 || slideTimer.isRunning()) {
            return;
        }
        nextImageIndex = (currentImageIndex + 1) % backgroundImages.size();
        slideProgress = 0.0;
        slideTimer.start();
    }

    private void updateSlide() {
        slideProgress += (double) FRAME_TIME_MS / SLIDE_TIME_MS;
        if (slideProgress >= 1.0) {
            slideProgress = 0.0;
            currentImageIndex = nextImageIndex;
            slideTimer.stop();
            displayTimer.restart();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        if (backgroundImages.isEmpty()) {
            GradientPaint fallback = new GradientPaint(
                    0, 0, Theme.PRIMARY_DARKER,
                    getWidth(), getHeight(), Theme.PRIMARY
            );
            g2.setPaint(fallback);
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else if (slideTimer.isRunning()) {
            int offset = (int) Math.round(getWidth() * slideProgress);
            drawCoverImage(g2, backgroundImages.get(currentImageIndex), -offset);
            drawCoverImage(g2, backgroundImages.get(nextImageIndex), getWidth() - offset);
        } else {
            drawCoverImage(g2, backgroundImages.get(currentImageIndex), 0);
        }

        // Lop toi nhe ben trai giup card dang nhap noi bat tren anh.
        GradientPaint overlay = new GradientPaint(
                0, 0, new Color(5, 25, 55, 118),
                Math.max(1, getWidth() * 2 / 3), 0,
                new Color(5, 25, 55, 6)
        );
        g2.setPaint(overlay);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private void drawCoverImage(Graphics2D g2, Image image, int panelX) {
        int imageWidth = image.getWidth(this);
        int imageHeight = image.getHeight(this);
        if (imageWidth <= 0 || imageHeight <= 0 || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        Graphics2D imageGraphics = (Graphics2D) g2.create();
        imageGraphics.translate(panelX, 0);
        imageGraphics.clipRect(0, 0, getWidth(), getHeight());

        double scale = Math.max(
                (double) getWidth() / imageWidth,
                (double) getHeight() / imageHeight
        );
        int drawWidth = (int) Math.ceil(imageWidth * scale);
        int drawHeight = (int) Math.ceil(imageHeight * scale);
        int drawX = (getWidth() - drawWidth) / 2;
        int drawY = (getHeight() - drawHeight) / 2;
        imageGraphics.drawImage(image, drawX, drawY, drawWidth, drawHeight, this);
        imageGraphics.dispose();
    }
}