package model;

public class Message {
    private User sender;
    private Agent recipient;
    private String content;
    private boolean isRead;

    /**\
     * used to store existing Messages
     * @param sender
     * @param recipient
     * @param content
     * @param isRead
     */
    public Message(User sender, Agent recipient, String content, boolean isRead) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.isRead = isRead;
    }

    /**
     * used to create a new Message
     * @param sender
     * @param recipient
     * @param content
     */
    public Message(User sender, Agent recipient, String content) {
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
}
