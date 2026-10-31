package model;

public class Message {
    private Agent recipient;
    private String content;

    public Message(Agent recipient, String content) {
        this.recipient = recipient;
        this.content = content;
    }

    public String getMessageContent() {
        return content;
    }

    public Agent getRecipient() {
        return recipient;
    }
}
