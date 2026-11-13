package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {
    // Agent with regular strings but irregular IDs
    private Agent sampleAgent1 = new Agent(
            -1,
            -1,
            "Agent P",
            "2007-08-17",
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

    // Agent with regular fields
    private Agent sampleAgent3 = new Agent(
            7,
            1,
            "Bond",
            "1962-10-05",
            null,
            "");

    private User sampleUser1 = new User(1, "user1");
    private User sampleUser2 = new User(2, "user2");
    private User sampleUser3 = new User(3, "user3");

    // Non-empty message
    private String testContent1 = "Great Googlymoogly!";

    // Empty message
    private String testContent2 = "";

    @Test
    void testCase1() {
        Message testMessage = new Message(sampleUser1, sampleAgent1, testContent1);

        assertEquals(sampleUser1, testMessage.getSender());
        assertEquals(sampleAgent1, testMessage.getRecipient());
        assertEquals(
                testContent1,
                testMessage.getMessageContent());
        assertFalse(testMessage.isRead());
    }

    @Test
    void testCase2()  {
        Message testMessage = new Message(sampleUser2,
                sampleAgent2,
                testContent1);

        assertEquals(sampleUser2, testMessage.getSender());
        assertEquals(sampleAgent2, testMessage.getRecipient());
        assertEquals(
                testContent1,
                testMessage.getMessageContent());
        assertFalse(testMessage.isRead());
    }

    @Test
    void testCase3() {
        Message testMessage = new Message(sampleUser3,
                sampleAgent3,
                testContent1, false);

        assertEquals(sampleUser3, testMessage.getSender());
        assertEquals(sampleAgent3, testMessage.getRecipient());
        assertEquals(
                testContent1,
                testMessage.getMessageContent());
        assertFalse(testMessage.isRead());
    }

    @Test
    void testCase4() {
        Message testMessage = new Message(sampleUser1,
                sampleAgent1,
                testContent2,true);

        assertEquals(sampleUser1, testMessage.getSender());
        assertEquals(sampleAgent1, testMessage.getRecipient());
        assertEquals(
                testContent2,
                testMessage.getMessageContent());
        assertTrue(testMessage.isRead());
    }

    @Test
    void testCase5() {
        Message testMessage = new Message(sampleUser3,
                sampleAgent2,
                testContent2);

        assertEquals(sampleUser3, testMessage.getSender());
        assertEquals(sampleAgent2, testMessage.getRecipient());
        assertEquals(
                testContent2,
                testMessage.getMessageContent());
        assertFalse(testMessage.isRead());
    }

    @Test
    void testCase6() {
        Message testMessage = new Message(sampleUser2,
                sampleAgent3,
                testContent2);

        assertEquals(sampleUser2, testMessage.getSender());
        assertEquals(sampleAgent3, testMessage.getRecipient());
        assertEquals(
                testContent2,
                testMessage.getMessageContent());
        assertFalse(testMessage.isRead());
    }
}
