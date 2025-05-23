package org.example;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private static final String fromEmail = "clonevclone05@gmail.com";
    private static final String appPassword = "biow arol rqdq aual";

    public void sendEmail(String toEmail, String subject, String body){
        Properties props = new Properties();
        // Server SMTP của Gmail
        props.put("mail.smtp.host", "smtp.gmail.com");

        // Cổng kết nối SMTP của Gmail: 587 = TLS port chuẩn
        props.put("mail.smtp.port", "587");

        // Bắt buộc xác thực tài khoản khi gửi mail
        props.put("mail.smtp.auth", "true");

        // Kích hoạt STARTTLS để mã hóa kết nối (bảo vệ nội dung mail trên đường truyền)
        props.put("mail.smtp.starttls.enable", "true");

        // -------------------- TẠO SESSION SMTP --------------------
        // Session giữ thông tin cấu hình + xác thực + kết nối SMTP
        Session session = Session.getInstance(props, new Authenticator() {
            // Xác thực bằng email + main.java.app password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            // Đặt người nhận (có thể truyền nhiều email cách nhau dấu phẩy)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully!");
        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
