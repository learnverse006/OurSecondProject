package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MailSenderPanel extends JFrame {

    private JTextField toEmailField;

    public MailSenderPanel() {
        setTitle(" Hệ thống gửi lại mật khẩu ");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center screen

        // ✅ Setup toàn bộ UI dùng Tahoma
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("Label.font", new Font("Tahoma", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Tahoma", Font.BOLD, 14));
        UIManager.put("TextField.font", new Font("Tahoma", Font.PLAIN, 14));

        // ---- Panel chính ----
        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(255, 183, 197); // Hồng pastel
                Color color2 = new Color(255, 255, 204); // Vàng nhạt
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // ---- Tiêu đề ----
        JLabel titleLabel = new JLabel( "Gửi lại mật khẩu ");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        titleLabel.setForeground(new Color(102, 0, 102));   // Tím đậm
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ---- Panel nhập email ----
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailPanel.setOpaque(false);

        JLabel emailLabel = new JLabel("Nhập email người nhận:");
        emailLabel.setForeground(new Color(51, 51, 51)); // Xám đậm

        toEmailField = new JTextField(20);

        emailPanel.add(emailLabel);
        emailPanel.add(toEmailField);
        mainPanel.add(emailPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ---- Nút gửi ----
        JButton sendButton = new JButton("Gửi mật khẩu mới");   // ✅ emoji trong text
        sendButton.setFont(new Font("Tahoma", Font.BOLD, 14));     // ✅ giữ Tahoma → hết lỗi tiếng Việt
        Color normalColor = new Color(0, 153, 204);     // Xanh dương đậm
        Color hoverColor = new Color(102, 204, 255);    // Xanh dương nhạt
        sendButton.setBackground(normalColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 💡 Hover effect
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(normalColor);
            }
        });

        sendButton.addActionListener(e -> {
            String toEmail = toEmailField.getText().trim();

            if (toEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập email người nhận!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            EmailService emailService = new EmailService();
            ResetPasswordService resetService = new ResetPasswordService(emailService);
            resetService.resetPassword(toEmail);

            JOptionPane.showMessageDialog(this, "✅ Mật khẩu mới đã được gửi!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            toEmailField.setText("");
        });

        mainPanel.add(sendButton);
        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MailSenderPanel());
    }
}
