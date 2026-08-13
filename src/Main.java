import gui.StudentManagementGUI;

import javax.swing.*;

/**
 * Entry point for the Student Management System application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to default look and feel
            }
            new StudentManagementGUI().setVisible(true);
        });
    }
}
