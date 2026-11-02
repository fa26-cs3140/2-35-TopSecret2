package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTest {
    @Test
    void messageTest1() {
        Agent sampleAgent = new Agent(
                -1,
                -1,
                "Agent P",
                "Unknown",
                "None",
                "");

        Message testMessage = new Message(
                sampleAgent,
                "Great Googlymoogly!");

        assertEquals(sampleAgent, testMessage.getRecipient());
        assertEquals(
                "Great Googlymoogly!",
                testMessage.getMessageContent());
    }


}
