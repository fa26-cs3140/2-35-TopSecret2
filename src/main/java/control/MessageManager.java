package control;

import model.Agent;
import model.DataStoreInterface;
import model.Message;
import model.UserManagerInterface;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    protected UserManagerInterface userAccess;
    protected DataStoreInterface dataStore;

    public MessageManager(UserManagerInterface userAccess, DataStoreInterface dataStore) {
        this.userAccess = userAccess;
        this.dataStore = dataStore;
    }

    public Message createMessage(Agent recipient, String contents) throws TopSecretException {
        Message newMessage = new Message(
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
}
