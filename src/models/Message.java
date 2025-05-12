package models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private int messageID;
    private int senderID;
    private Integer receiverID; // Cho phép null
    private int chatID;
    private String content;
    private MessageType mst; // mst
    private LocalDateTime createAt;

    public enum MessageType {
        TEXT,
        IMAGE,
        EMOJI,
        FILE
    }

}
