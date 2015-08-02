package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Conversation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import org.datafx.provider.ListDataProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 16.07.15.
 */
public class ConversationManager extends ListDataProvider<Conversation>{
    private static final Logger logger = Logger.getLogger(ConversationManager.class.getName());

    protected final ObservableList<Conversation> data;

    protected final ConcurrentHashMap<String, Conversation> conversationMap;
    private ObservableList<Conversation> openConversations = FXCollections.observableList(new CopyOnWriteArrayList<>());
    private ListDataProvider<Conversation> provider;

    public ConversationManager(ListDataProvider<Conversation> provider) {
        this.provider = provider;
        conversationMap = new ConcurrentHashMap<>();
        this.data = provider.getData();
        this.data.parallelStream().forEach(conversation -> conversationMap.put(conversation.getId(), conversation));
        this.data.addListener((ListChangeListener<Conversation>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().parallelStream().forEach(conversation -> {
                        logger.finer("Adding conversation to conversationMap " + conversation.getId());
                        conversationMap.put(conversation.getId(), conversation);
                        openConversations.add(conversation);
                        conversation.closedProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {
                                openConversations.remove(conversation);
                            } else {
                                openConversations.add(conversation);
                            }
                        });

                        Platform.runLater(() -> getData().add(conversation));

                    });
                } else if (change.wasRemoved()) {
                    change.getRemoved().parallelStream().forEach(conversation -> {
                        logger.finer("Removing conversation from conversationMap" + conversation.getId());
                        conversationMap.remove(conversation.getId());
                        Platform.runLater(() -> getData().remove(conversation));
                    });
                }
            }
        });
        provider.retrieve();
    }

    @Override
    public Worker<ObservableList<Conversation>> retrieve(){
        return super.retrieve();
    }


    public boolean contains(String id){
        return conversationMap.containsKey(id);
    }

    public Conversation get(Contact contact){
        return get(contact.getAccount() + ":" + contact.getJid());
    }

    public Conversation get(String id){
        return conversationMap.get(id);
    }

    public Conversation getById(String account, String recipient) {
        return conversationMap.get(account + ":" + recipient);
    }

    public void add(Conversation conversation){
        provider.getData().add(conversation);
    }

    /**
     * Restores an already existing {@link Conversation} or creates a new.
     *
     * @param accountJid   The jid of the account
     * @param recipientJid The jid of the contact
     * @param conference   true if conference
     * @return A new or restored {@link Conversation}
     */
    public Conversation openConversation(String accountJid, String recipientJid, boolean conference) {
        String id = getConversationId(accountJid, recipientJid);
        if (contains(id)) {
            logger.fine(String.format("Restoring conversation for %s ", id));
            Conversation conversation = get(id);
            if (conversation.getClosed()) {
                conversation.setClosed(false);
            }
            return conversation;
        } else {
            logger.fine(String.format("No previous conversation for %s found ", id));
            Conversation conversation = createConversation(accountJid, recipientJid, conference);
            add(conversation);
            return conversation;
        }
    }

    /**
     * Restores an already existing {@link Conversation} or creates a new.
     * @param contact with whom to start a conversation
     * @return
     */
    public Conversation openConversation(Contact contact) {
        return openConversation(contact.getAccount(), contact.getJid(), contact.isConference());
    }


    /**
     * Helper method to calculate the id of an Conversation. See also {@link Conversation#getId()}.
     *
     * @param accountJid   The jid of the account
     * @param recipientJid The jid of the contact
     * @return A {@link Conversation} id
     */
    private String getConversationId(String accountJid, String recipientJid) {
        return accountJid + ":" + recipientJid;
    }

    /**
     * Creates a new {@link Conversation}
     *
     * @param accountJid   The jid of the account
     * @param recipientJid The jid of the contact
     * @param conference   true if conference
     * @return A new {@link Conversation}
     */
    private Conversation createConversation(String accountJid, String recipientJid, boolean conference) {
        logger.fine(String.format("Creating new conversation %s:%s ", accountJid, recipientJid));
        Conversation conversation = new Conversation(accountJid, recipientJid);
        conversation.setConference(conference);
        return conversation;
    }

    public ObservableList<Conversation> getOpenConversations() {
        return openConversations;
    }

}
