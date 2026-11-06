package control;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessageManagerTest {
    private DataStoreInterface mockDataStore;
    private MessageManager testedMessageManager;

    // Agent with regular fields
    private Agent sampleAgent1 = new Agent(
            7,
            1,
            "Bond",
            "1962-10-05",
            null,
            "");

    // Agent with regular IDs but irregular strings
    private Agent sampleAgent2 = new Agent(
            2,
            3,
            "",
            "1789-07-14",
            "2152-09-17",
            "Loves pasta");

    private Message sampleMessage1 = new Message(
            sampleAgent1,
            "Don't forget the ramen");

    private Message sampleMessage2 = new Message(
            sampleAgent2,
            "Stay away from 5th Street");

    @BeforeEach
    void setUp() {
        mockDataStore = mock(SQLiteDataStore.class);

        testedMessageManager = new MessageManager(mockDataStore);

        // Message store mocking:
        // Mocked data store will return message (successful storage)
        when(mockDataStore.addMessage(any(Message.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
    }

    @Test
    void constructorTest() {
        MessageManager testedMessageManager =
                new MessageManager(mockDataStore);

        assertEquals(
                mockDataStore,
                testedMessageManager.dataStore);

    }

    @Test
    void creationTest1() {
        Message createdMessage = null;

        String sampleString = "Don't forget the ramen";

       try {
           createdMessage = testedMessageManager.createMessage(
                   sampleAgent1,
                   sampleString);
       } catch (TopSecretException e) {
           fail();
        }

        verify(mockDataStore).addMessage(any(Message.class));

       assertEquals(sampleAgent1, createdMessage.getRecipient());
       assertEquals(sampleString, createdMessage.getMessageContent());
    }

    @Test
    void creationTest2() {
        Message createdMessage = null;

        String sampleString = "";

        try {
            createdMessage = testedMessageManager.createMessage(
                    sampleAgent1,
                    sampleString);
        } catch (TopSecretException e) {
            fail();
        }

        verify(mockDataStore).addMessage(any(Message.class));

        assertEquals(sampleAgent1, createdMessage.getRecipient());
        assertEquals(sampleString, createdMessage.getMessageContent());
    }

    @Test
    void creationTest3() {
        Message createdMessage = null;

        String sampleString = "Don't forget the ramen";

        try {
            createdMessage = testedMessageManager.createMessage(
                    sampleAgent2,
                    sampleString);
        } catch (TopSecretException e) {
            fail();
        }

        verify(mockDataStore).addMessage(any(Message.class));

        assertEquals(sampleAgent2, createdMessage.getRecipient());
        assertEquals(sampleString, createdMessage.getMessageContent());
    }

    @Test
    void creationTest4() {
        Message createdMessage = null;

        String sampleString = "";

        try {
            createdMessage = testedMessageManager.createMessage(
                    sampleAgent2,
                    sampleString);
        } catch (TopSecretException e) {
            fail();
        }

        verify(mockDataStore).addMessage(any(Message.class));

        assertEquals(sampleAgent2, createdMessage.getRecipient());
        assertEquals(sampleString, createdMessage.getMessageContent());
    }

    @Test
    void retrievalTest1() {
        List<Message> actualMessageList = null;

        List<Message> sampleMessageList = new ArrayList<Message>();
        when(mockDataStore.listMessagesForAgent(any(Agent.class)))
                .thenReturn(sampleMessageList);

        try {
            actualMessageList = testedMessageManager.retrieveMessages(
                    sampleAgent1);
        } catch (TopSecretException e) {
            fail();
        }

        verify(mockDataStore).listMessagesForAgent(sampleAgent1);

        assertEquals(sampleMessageList, actualMessageList);
    }

    @Test
    void retrievalTest2() {
        List<Message> actualMessageList = null;

        List<Message> sampleMessageList = new ArrayList<Message>();

        when(mockDataStore.listMessagesForAgent(any(Agent.class)))
                .thenReturn(sampleMessageList);

        try {
            actualMessageList = testedMessageManager.retrieveMessages(
                    sampleAgent2);
        } catch (TopSecretException e) {
            fail();
        }

        verify(mockDataStore).listMessagesForAgent(sampleAgent2);

        assertEquals(sampleMessageList, actualMessageList);
    }

    @Test
    void retrievalTest3() {
        List<Message> actualMessageList = null;

        List<Message> sampleMessageList = new ArrayList<Message>();

        sampleMessageList.add(sampleMessage1);

        when(mockDataStore.listMessagesForAgent(any(Agent.class)))
                .thenReturn(sampleMessageList);

        try {
            actualMessageList = testedMessageManager.retrieveMessages(
                    sampleAgent1);
        } catch (TopSecretException e) {
            fail();
        }

        verify(mockDataStore).listMessagesForAgent(sampleAgent1);

        assertEquals(sampleMessageList, actualMessageList);
    }

    @Test
    void retrievalTest4() {
        List<Message> actualMessageList = null;

        List<Message> sampleMessageList = new ArrayList<Message>();

        sampleMessageList.add(sampleMessage2);

        when(mockDataStore.listMessagesForAgent(any(Agent.class)))
                .thenReturn(sampleMessageList);

        try {
            actualMessageList = testedMessageManager.retrieveMessages(
                    sampleAgent2);
        } catch (TopSecretException e) {
            fail();
        }

        verify(mockDataStore).listMessagesForAgent(sampleAgent2);

        assertEquals(sampleMessageList, actualMessageList);
    }
}
