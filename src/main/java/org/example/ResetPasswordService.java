package org.example;

public class ResetPasswordService {
    private final EmailService emailService;

    public ResetPasswordService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void resetPassword(String userEmail) {
        // 1. Sinh mật khẩu mới
        String newPassword = PasswordGenerator.generateRandomPassword(10);
        System.out.println("🔐 Mật khẩu mới: " + newPassword);

        // 2. Gửi mail
        emailService.sendEmail(userEmail, "Reset mật khẩu",
                "Mật khẩu mới của bạn là: " + newPassword +
                        "\nVui lòng đăng nhập và đổi lại mật khẩu ngay!");

        // 3. Giả lập lưu vào DB (bạn có thể bổ sung phần hash + save thực tế sau)
        System.out.println("✅ Mật khẩu đã được băm và lưu vào DB (demo)");
    }
}
