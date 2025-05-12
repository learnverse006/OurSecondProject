package models;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Chat {
    private int chatId;
    private String chatType;
    private String name;
    private Timestamp createdAt;
}
