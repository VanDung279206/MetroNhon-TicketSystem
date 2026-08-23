package main;

import view.Theme;
import view.TrangChuView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Theme.install();
        SwingUtilities.invokeLater(() -> new TrangChuView().setVisible(true));
    }
}