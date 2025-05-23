package models;

import java.time.LocalDateTime;

public class Message {
    private int messageID;
    private int senderID;
    private Integer receiverID; // Cho phép null
    private int chatID;
    private String content;
    private MessageType mst;
    private LocalDateTime createAt;

    public Message() {

    }

    public Message(int messageID, int chatID, int senderID, Integer receiverID, String content, MessageType mst, LocalDateTime createAt) {
        this.messageID = messageID;
        this.chatID = chatID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.content = content;
        this.mst = mst;
        this.createAt = createAt;
    }

    public enum MessageType {
        TEXT,
        IMAGE,
        EMOJI,
        FILE
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }
    public int getMessageID() {
        return messageID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setReceiverID(Integer receiverID) {
        this.receiverID = receiverID;
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public int getChatID() {
        return chatID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setMessageType(MessageType mst) {
        this.mst = mst;
    }

    public MessageType getMst() {
        return mst;
    }

    public void setCreateAt(LocalDateTime time) {
        this.createAt = time;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
