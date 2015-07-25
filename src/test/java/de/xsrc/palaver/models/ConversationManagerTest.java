package de.xsrc.palaver.models;

import de.svenjacobs.loremipsum.LoremIpsum;
import de.xsrc.palaver.AbstractTest;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.models.ConversationManager;
import javafx.embed.swing.JFXPanel;
import org.datafx.provider.ListDataProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 23.07.15.
 */
public class ConversationManagerTest extends AbstractTest {
    private final LoremIpsum loremIpsum = new LoremIpsum();

    private ConversationManager cm;
    private Contact contact;

    @Before
    public void setUp() {
        new JFXPanel();
        contact = getMockContact();
        cm = new ConversationManager(new ListDataProvider<>());
    }

    @Test
    public void openConversation() {
        cm.openConversation(contact);
        final int result = cm.getOpenConversations().size();
        assertEquals("There should be one open conversation", 1, result);
    }

    @Test
    public void reopenConversation() {
        final Conversation conversation = cm.openConversation(contact);
        final int resultBeforeClose = cm.getOpenConversations().size();
        assertEquals("There should be one open conversation", 1, resultBeforeClose);
        conversation.setClosed(true);
        final int resultAfterClose = cm.getOpenConversations().size();
        assertEquals("There should be no open channel", 0, resultAfterClose);
    }

    @Test
    public void reopenConversationWithHistory(){
        final Conversation conversation = cm.openConversation(contact);
        conversation.addMessage(contact.getJid(), loremIpsum.getWords(5), true);
        conversation.setClosed(true);
        final Conversation reopenedConversation = cm.openConversation(contact);
        List<HistoryEntry> historyEntryList = conversation.history.getEntryList();
        assertFalse("Conversation history should not be empty", historyEntryList.isEmpty());
    }

}
