package model;

public class Message {
    private User sender;
    private Agent recipient;
    private String content;
    private boolean isRead;
    private int messageID;

    /**\
     * used to store existing Messages
     * @param sender
     * @param recipient
     * @param content
     * @param isRead
     */
    public Message(int messageID, User sender, Agent recipient, String content, boolean isRead) {
        this.messageID = messageID;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.isRead = isRead;
    }

    /**
     * used to create a new Message. isRead set to false and messageID set to -1 by default
     * @param sender
     * @param recipient
     * @param content
     */
    public Message(User sender, Agent recipient, String content) {
        this.messageID = -1;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.isRead = false;
    }

    public String getMessageContent() {
        return content;
    }

    public User getSender() {
        return sender;
    }
    public Agent getRecipient() {
        return recipient;
    }
    public boolean isRead() {
        return isRead;
    }
    public int getMessageID() {
        return messageID;
    }
}
