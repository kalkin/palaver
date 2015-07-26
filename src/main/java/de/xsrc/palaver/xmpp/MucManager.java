package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.models.ConversationManager;
import de.xsrc.palaver.xmpp.task.JoinMucTask;
import javafx.application.Platform;
import javafx.collections.*;
import org.datafx.concurrent.ObservableExecutor;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class MucManager {

    private static final Logger logger = Logger.getLogger(MucManager.class.getName());

    private final ObservableMap<Conversation, MultiUserChat> joinedMucMap = FXCollections.observableMap(new
            ConcurrentHashMap<>());
    private final ObservableExecutor executor;
    private final WeakHashMap<String, XMPPTCPConnection> connectionMap = new WeakHashMap<>();
    private ConversationManager conversationManager;

    public MucManager(ConversationManager conversationManager, ObservableExecutor executor) {
        this.conversationManager = conversationManager;
        this.executor = executor;
        conversationManager.getOpenConversations().addListener((ListChangeListener<Conversation>) change -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            final List<? extends Conversation> conversations = change.getAddedSubList();
                            conversations.parallelStream().filter(this::isJoinableConference).forEach(this::joinMuc);
                        } else if (change.wasRemoved()) {
                            final List<? extends Conversation> removedConversations = change.getRemoved();
                            removedConversations.parallelStream().filter(Conversation::isConference).forEach(this::leaveMuc);
                        }
                    }
                }

        );
        joinedMucMap.addListener((MapChangeListener<Conversation, MultiUserChat>) change -> {
            if (change.wasAdded()) {
                final MultiUserChat muc = change.getValueAdded();
                final Conversation conversation = change.getKey();
                conversation.history.entryListProperty().addListener((ListChangeListener<HistoryEntry>) c -> sendNewMsgs(conversation, c));
            }
        });

    }

    public void sendNewMsgs(Conversation conversation, ListChangeListener.Change<? extends HistoryEntry> c) {
        while (c.next()) if (c.wasAdded()) {
            final MultiUserChat multiUserChat = joinedMucMap.get(conversation);
            c.getAddedSubList().stream().filter(historyEntry -> !historyEntry.getSendState())
                    .forEach(historyEntry -> {
                        try {
                            logger.severe(historyEntry.getBody());
                            multiUserChat.sendMessage(historyEntry.getBody());
                            historyEntry.setSendState(true);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                            logger.severe("Could not send muc msg to " + conversation);
                        }
                    });
        }
    }

    /**
     * Return true if conversation is conference and we have an {@link XMPPTCPConnection} in {@link
     * MucManager#connectionMap} for it.
     *
     * @param conversation
     * @return
     */
    protected boolean isJoinableConference(Conversation conversation) {
        return conversation.isConference() && connectionMap.containsKey(conversation.getAccount());
    }

    protected void joinMuc(Conversation conversation) {
        logger.severe("Joining muc " + conversation);
        JoinMucTask task = new JoinMucTask(conversation, connectionMap.get(conversation.getAccount()));
        Platform.runLater(() -> task.setOnSucceeded(event -> joinedMucMap.put(conversation, (MultiUserChat) task.getValue())));
        executor.submit(task);
    }

    protected void leaveMuc(Conversation conversation) {
//						executor.submit(() -> {
//							try {
//								BookmarkManager bm = BookmarkManager.getBookmarkManager(ConnectionManager.getConnection(palaver.getAccount()));
//								bm.getBookmarkedConferences().parallelStream().filter(bookmarkedConference -> bookmarkedConference.getJid().equals(palaver.getRecipient()) && bookmarkedConference.isAutoJoin()).forEach(bookmarkedConference -> {
//									try {
//										bm.addBookmarkedConference(XmppStringUtils.parseLocalpart(palaver.getRecipient()), palaver.getRecipient(), false, XmppStringUtils.parseLocalpart(palaver.getAccount()), null);
//									} catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
//										logger.warning(String.format("Could not remove autjoin on %s", palaver.toString()));
//									}
//								});
//							} catch (XMPPException | SmackException e) {
//								logger.warning(String.format("Could not get Bookmark manager on %s", palaver.toString()));
//							}
//						});
//                });

    }


    public void registerConnection(Connection connection) {
        final String accountJid = connection.getCredentials().getJid();
        logger.finer("Register connection " + accountJid);
        connectionMap.put(accountJid, connection.xmpptcpConnection);
        final ObservableList<Conversation> openConversations = conversationManager.getOpenConversations();
        openConversations.parallelStream().filter(Conversation::isConference)
                .filter(conversation -> conversation.getAccount().equals(accountJid)).forEach(this::joinMuc);
    }

}
