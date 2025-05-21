/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
import javax.swing.*;
import java.awt.*;

public class NotificationPopup extends JWindow {

    public NotificationPopup(String message) {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(0x00205B)); 

      
        JLabel title = new JLabel(" Notification", UIManager.getIcon("OptionPane.informationIcon"), JLabel.LEFT);
        title.setOpaque(true);
        title.setBackground(new Color(0xFFD700)); 
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        content.add(title, BorderLayout.NORTH);

        JLabel label = new JLabel("<html><center>" + message + "</center></html>");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        content.add(label, BorderLayout.CENTER);

        setContentPane(content);
        setSize(380, 130);
        setLocationRelativeTo(null); 
        setAlwaysOnTop(true);
    }

    public void showPopup() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            new Timer(3000, e -> dispose()).start(); 
        });
    }

    public static void showNotification(String message) {
        new NotificationPopup(message).showPopup();
    }
}






