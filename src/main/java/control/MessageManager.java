package control;

import model.*;

import java.util.List;

public class MessageManager {
    protected DataStoreInterface dataStore;

    public MessageManager(DataStoreInterface dataStore) {
        this.dataStore = dataStore;
    }

    public Message createMessage(User sender, Agent recipient, String contents) throws TopSecretException {
        Message newMessage = new Message(sender,
                recipient,
                contents);

        try {
            dataStore.addMessage(newMessage);
        } catch (IllegalArgumentException e) {
            throw new TopSecretException(e.getMessage());
        }

        return newMessage;
    }

    public List<Message> retrieveMessages(Agent agent) throws TopSecretException {
        List<Message> availableMessages;

        try {
            availableMessages = dataStore.listMessagesForAgent(agent);
        } catch (IllegalStateException e) {
            throw new TopSecretException(e.getMessage());
        }

        return availableMessages;
    }

    /**
     * changes the read status of a message to true
     * @param msg
     * @throws TopSecretException
     * @return The updated message
     */
    public Message readMessage(Message msg) throws TopSecretException {
        Message newMsg = new Message(msg.getMessageID(),msg.getSender(),msg.getRecipient(),msg.getMessageContent(),true);
        dataStore.updateMessage(newMsg);
        return newMsg;
    }
}
