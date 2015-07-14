package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.beans.Conversation;
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

    private static final Logger logger = Logger.getLogger(PalaverModel.class
            .getName());
    protected static ObservableList<Conversation> openConversationList;
    protected ConcurrentHashMap<String, Conversation> palaverMap;
    private ObservableList<Conversation> data;

    private PalaverModel() {
        palaverMap = new ConcurrentHashMap<>();
        openConversationList = FXCollections.observableList(new CopyOnWriteArrayList<>());

        data = FXCollections.observableList(new CopyOnWriteArrayList<>());

        data.addListener((ListChangeListener<Conversation>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    // sync data with our Concurrent HashMap
                    c.getAddedSubList().forEach(palaver -> palaverMap.put(palaver.getId(), palaver));
                    // auto save when history entries are added
                    c.getAddedSubList().forEach(palaver -> palaver.history.entryListProperty().addListener((ListChangeListener<HistoryEntry>) change -> save()));
                    c.getAddedSubList().forEach(palaver -> {
                        if (palaver.isOpen()) openConversationList.add(palaver);
                    });

                    // auto add to openPalaversList if a palaver closedProperty changes to true
                    c.getAddedSubList().forEach(palaver -> palaver.closedProperty().addListener((observable, oldValue, newValue) -> {
                        if (oldValue && !newValue) {
                            // Palaver changed state to open
                            Platform.runLater(() ->
                                    openConversationList.add(palaver));
                            save();
                        } else if (!oldValue && newValue) {
                            // Palaver changed state to closed
                            Platform.runLater(() -> openConversationList.remove(palaver));
                            save();
                        }
                    }));
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

    public Conversation openPalaver(Credentials credentials, String jid){
        return openPalaver(credentials.getJid(), jid, false);
    }

    public synchronized Conversation openPalaver(String accountJid, String recipientJid, boolean conference) {
        String id = accountJid + ":" + recipientJid;
        Conversation conversation = palaverMap.get(id);
        if (conversation == null) {
            logger.finer(String.format("No previous palaver for %s found ", id));
            conversation = createPalaver(accountJid, recipientJid, conference);
            data.add(conversation);
            save();
        } else if (conversation.getClosed()) {
            conversation.setClosed(false);
        }
        return conversation;
    }

    private Conversation createPalaver(String accountJid, String recipientJid, boolean conference) {
        logger.finer(String.format("Creating new palaver %s:%s ", accountJid, recipientJid));
        Conversation conversation = new Conversation();
        conversation.setAccount(accountJid);
        conversation.setRecipient(recipientJid);
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
        return palaverMap.get(account + ":" + recipient);
    }

    private static final class InstanceHolder {
        static final PalaverModel INSTANCE = new PalaverModel();
    }

}
