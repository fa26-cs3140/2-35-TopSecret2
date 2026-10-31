package controller;

import model.Agent;
import model.DataStoreInterface;
import model.Message;
import model.UserManagerInterface;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    UserManagerInterface userAccess;
    DataStoreInterface dataStore;

    public MessageManager(UserManagerInterface userAccess, DataStoreInterface dataStore) {
        this.userAccess = userAccess;
        this.dataStore = dataStore;
    }

    public Message createMessage(Agent recipient, String contents) {
        // TODO: Replace stub with actual message creation
        Agent sampleAgent = new Agent(
                -1,
                -1,
                "Agent P",
                "Unknown",
                "None",
                "");

        Message sampleMessage = new Message(
                sampleAgent,
                "Great Googlymoogly!");

        return sampleMessage;
    }

    public List<Message> retrieveMessages(Agent agent) {
        // TODO: Replace stub with actual message retrieval.
        return new ArrayList<Message>();
    }
}
