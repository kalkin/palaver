package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.ColdStorage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * This should be the crosspoint between disk and XMPP
 * TODO: Refactor it as normal Object and not a Singleton
 */
public class PalaverModel {

    private static final Logger logger = Logger.getLogger(PalaverModel.class.getName());
    protected static ObservableList<Conversation> openConversationList;
    protected ConcurrentHashMap<String, Conversation> conversationMap;
    private ObservableList<Conversation> data;

    private PalaverModel() {
        conversationMap = new ConcurrentHashMap<>();
        openConversationList = FXCollections.observableList(new CopyOnWriteArrayList<>());

        data = FXCollections.observableList(new CopyOnWriteArrayList<>());

        data.addListener((ListChangeListener<Conversation>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    while (c.next()) {
                        c.getAddedSubList().forEach(conversation -> {
                            logger.fine("New conversation added");
                            // sync data with our Concurrent HashMap
                            conversationMap.put(conversation.getId(), conversation);
                            // auto save when history entries are added
                            conversation.history.entryListProperty().addListener((ListChangeListener<HistoryEntry>)
                                    change -> save());

                            // auto add to openConversationList if a palaver closedProperty changes to true
                            conversation.closedProperty().addListener((observable, oldValue, newValue) -> {
                                if (oldValue && !newValue) {
                                    // Palaver changed state to open
                                    logger.finer("Adding palaver " + conversation.getId() + " to list of open palavers");
                                    Platform.runLater(() ->
                                            openConversationList.add(conversation));
                                    save();
                                } else if (!oldValue && newValue) {
                                    // Palaver changed state to closed
                                    logger.finer("Removing palaver " + conversation.getId() + " from list of open palavers");
                                    Platform.runLater(() -> openConversationList.remove(conversation));
                                    save();
                                }
                            });

                            if (conversation.isOpen()) Platform.runLater(() -> {
                                logger.finer("Adding Conversation top openConversationList " + conversation.getId());
                                openConversationList.add(conversation);
                            });


                        });
                    }
                }
            }
        });
        PalaverProvider provider = new PalaverProvider();
        provider.setResultObservableList(data);
        provider.retrieve();

    }

    public static PalaverModel getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Conversation openPalaver(Contact contact) {
        return openPalaver(contact.getAccount(), contact.getJid(), contact.isConference());
    }

    public Conversation openPalaver(Credentials credentials, String jid) {
        return openPalaver(credentials.getJid(), jid, false);
    }

    public synchronized Conversation openPalaver(String accountJid, String recipientJid, boolean conference) {
        String id = accountJid + ":" + recipientJid;
        Conversation conversation = conversationMap.get(id);
        logger.fine("A conversation between  " + accountJid + " and " + recipientJid + "will be opened");
        if (conversation == null) {
            logger.finer(String.format("No previous conversation for %s found ", id));
            Conversation newConversation = createPalaver(accountJid, recipientJid, conference);
            Platform.runLater(() -> {
                data.add(newConversation);
                save();
            });
            return newConversation;

        } else if (conversation.getClosed()) {
            logger.finer("Reopening conversation " + conversation.getId());
            conversation.setClosed(false);
        }
        return conversation;
    }

    private Conversation createPalaver(String accountJid, String recipientJid, boolean conference) {
        logger.finer(String.format("Creating new conversation %s:%s ", accountJid, recipientJid));
        Conversation conversation = new Conversation(accountJid, recipientJid);
        conversation.setConference(conference);
        conversation.setClosed(false);
        return conversation;
    }

    public ObservableList<Conversation> getOpenPalavers() {
        return FXCollections.unmodifiableObservableList(openConversationList);
    }

    private synchronized void save() {
        ColdStorage.save(Conversation.class, data);
    }

    public Conversation getById(String account, String recipient) {
        return conversationMap.get(account + ":" + recipient);
    }

    private static final class InstanceHolder {
        static final PalaverModel INSTANCE = new PalaverModel();
    }

}
