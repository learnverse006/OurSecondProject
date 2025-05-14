package models.entity;

import jakarta.persistence.*;
import lombok.*;
import models.enums.MessageType;

import java.time.LocalDateTime;
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageID;
    private int senderID;
    private Integer receiverID; // Cho phép null
    private int chatID;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType mst; // mst
    private LocalDateTime createAt;

}
