package de.xsrc.palaver.beans;

import de.svenjacobs.loremipsum.LoremIpsum;
import de.xsrc.palaver.AbstractTest;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 18.07.15.
 */
public class ConversationTest extends AbstractTest {

    public static final int MAX_WORD_AMOUNT = 23;
    private final LoremIpsum loremIpsum = new LoremIpsum();
    private Credentials romeosCredentials;
    private Credentials juliasCredentials;
    private Conversation conversation;

    @BeforeClass
    public static void initialiazeJFX() {
        new JFXPanel();
    }

    @Before
    public void setUp() {
        romeosCredentials = getMockCredentials();
        juliasCredentials = getMockCredentials();
        conversation = new Conversation(romeosCredentials.getJid(), juliasCredentials.getJid());
    }

    @Test
    public void testSendMessage() {
        boolean sendState = false;
        final String jid = romeosCredentials.getJid();
        sendMessage(jid, conversation, sendState);

        final int size = conversation.history.getEntryList().size();
        assertEquals("Should only have one message ", 1, size);
        HistoryEntry historyEntry = conversation.history.getEntryList().stream().findFirst().get();
        assertEquals("HistoryEntry from should be romeo's jid " + jid, jid, historyEntry.getFrom());
        assertFalse("HistoryEntry should should be marked as not send", historyEntry.getSendState());
    }

    @Test
    public void testIsOpen() {
        sendMessage(romeosCredentials.getJid(), conversation, false);
        assertTrue("Channel should be open", conversation.isOpen());
        assertTrue("Conversation should be open", conversation.isOpen());
    }

    @Test
    public void testChannelClose() {
        sendMessage(romeosCredentials.getJid(), conversation, false);
        conversation.setClosed(true);
        assertFalse("Channel should be open", conversation.isOpen());
        assertFalse("Conversation should be open", conversation.isOpen());
    }

    @Test
    public void testReceiveMessage() {
        boolean sendState = true;
        final String jid = juliasCredentials.getJid();
        sendMessage(jid, conversation, sendState);
        final int size = conversation.history.getEntryList().size();
        assertEquals("Should have two messages", 1, size);
        HistoryEntry historyEntry = conversation.history.getEntryList().stream().findFirst().get();
        assertEquals("HistoryEntry from should be julia's jid " + jid, jid, historyEntry.getFrom());
        assertTrue("HistoryEntry should be marked as send", historyEntry.getSendState());
    }


    private void sendMessage(String jid, Conversation conversation, boolean sendState) {
        final int amountOfWords = new Random().nextInt() % MAX_WORD_AMOUNT;
        conversation.addMessage(jid, loremIpsum.getWords(amountOfWords), sendState);
    }


}
