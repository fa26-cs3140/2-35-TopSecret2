package control;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageManagerSQLiteDataStoreTest {
    private DataStoreInterface dataStore;
    private MessageManager messageManager;

    @TempDir
    Path tempDir;

    // All test cases in this file utilize a test database derived from
    // tsv files in dataset

    @BeforeEach
    void setUp() {
        String dbLocation = "jdbc:sqlite:" + tempDir.resolve("test.db");
        dataStore = new model.SQLiteDataStore(dbLocation);
        messageManager = new MessageManager(dataStore);
    }

    @Test
    void creationTest() {
        Message returnMessage = null;
        Message lastAgentMessage = null;
        Agent sampleAgent = dataStore.getAgent(4);

        try {
            returnMessage = messageManager.createMessage(sampleAgent,
                    "It's a weather plane!");
        } catch (TopSecretException e){
            fail();
        }

        // Check for correct return value

        assertEquals(sampleAgent, returnMessage.getRecipient());
        assertEquals("It's a weather plane!",
                returnMessage.getMessageContent());

        List<Message> messagesForAgent1 =
                dataStore.listMessagesForAgent(sampleAgent);

        // Fail if there's no last element in list
        try {
            lastAgentMessage = messagesForAgent1.getLast();
        } catch (NoSuchElementException e) {
            fail();
        }

        // Check if message was actually stored in the database

        assertEquals(sampleAgent, lastAgentMessage.getRecipient());
        assertEquals("It's a weather plane!",
                lastAgentMessage.getMessageContent());

    }

    @Test
    void retrievalTest1() {
        List<Message> returnedMessages = null;
        Message lastAgent5Message = null;

        Agent tsvAgent5 = dataStore.getAgent(5);

        try {
            returnedMessages = messageManager.retrieveMessages(tsvAgent5);
        } catch (TopSecretException e) {
            fail();
        }

        // Fail if there's no last element in list
        try {
            lastAgent5Message = returnedMessages.getLast();
        } catch (NoSuchElementException e) {
            fail();
        }

        assertEquals("This is not a friendship; it's a spaceship."
                , lastAgent5Message.getMessageContent());


    }

    @Test
    void retrievalTest2() {
        List<Message> returnedMessages = null;
        Message lastAgent1Message = null;

        Agent tsvAgent1 = dataStore.getAgent(1);

        try {
            returnedMessages = messageManager.retrieveMessages(tsvAgent1);
        } catch (TopSecretException e) {
            fail();
        }

        // Fail if there's no last element in list
        try {
            lastAgent1Message = returnedMessages.getLast();
        } catch (NoSuchElementException e) {
            fail();
        }

        assertEquals("Spend the dollar"
                , lastAgent1Message.getMessageContent());
    }

    @Test
    void retrievalTest3() {
        List<Message> returnedMessages = null;
        Message lastAgent3Message = null;

        Agent tsvAgent3 = dataStore.getAgent(3);

        try {
            returnedMessages = messageManager.retrieveMessages(tsvAgent3);
        } catch (TopSecretException e) {
            fail();
        }

        // Fail if there's no last element in list
        try {
            lastAgent3Message = returnedMessages.getLast();
        } catch (NoSuchElementException e) {
            fail();
        }

        assertEquals("URGENT: Tear it down"
                , lastAgent3Message.getMessageContent());
    }
}
