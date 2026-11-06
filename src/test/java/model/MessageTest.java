package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    // Non-empty message
    private String testContent1 = "Great Googlymoogly!";

    // Empty message
    private String testContent2 = "";

    @Test
    void testCase1() {
        Message testMessage = new Message(
                sampleAgent1,
                testContent1);

        assertEquals(sampleAgent1, testMessage.getRecipient());
        assertEquals(
                testContent1,
                testMessage.getMessageContent());
    }

    @Test
    void testCase2()  {
        Message testMessage = new Message(
                sampleAgent2,
                testContent1);

        assertEquals(sampleAgent2, testMessage.getRecipient());
        assertEquals(
                testContent1,
                testMessage.getMessageContent());
    }

    @Test
    void testCase3() {
        Message testMessage = new Message(
                sampleAgent3,
                testContent1);

        assertEquals(sampleAgent3, testMessage.getRecipient());
        assertEquals(
                testContent1,
                testMessage.getMessageContent());
    }

    @Test
    void testCase4() {
        Message testMessage = new Message(
                sampleAgent1,
                testContent2);

        assertEquals(sampleAgent1, testMessage.getRecipient());
        assertEquals(
                testContent2,
                testMessage.getMessageContent());
    }

    @Test
    void testCase5() {
        Message testMessage = new Message(
                sampleAgent2,
                testContent2);

        assertEquals(sampleAgent2, testMessage.getRecipient());
        assertEquals(
                testContent2,
                testMessage.getMessageContent());
    }

    @Test
    void testCase6() {
        Message testMessage = new Message(
                sampleAgent3,
                testContent2);

        assertEquals(sampleAgent3, testMessage.getRecipient());
        assertEquals(
                testContent2,
                testMessage.getMessageContent());
    }
}
