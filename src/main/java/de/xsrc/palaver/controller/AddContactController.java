package de.xsrc.palaver.controller;

import de.xsrc.palaver.ConnectionManager;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.models.ContactManager;
import de.xsrc.palaver.models.ConversationManager;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConferenceBookmarkManager;
import de.xsrc.palaver.xmpp.RosterManager;
import de.xsrc.palaver.xmpp.exception.BookmarkException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.datafx.controller.FXMLController;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

@FXMLController("/fxml/AddContactView.fxml")
public class AddContactController {

    private static final Logger logger = Logger.getLogger(AccountController.class
            .getName());

    @FXML
    private Button back;

    @FXML
    private TextField jid;

    @FXML
    private Button saveButton;

    @FXML
    private ChoiceBox<Credentials> accountChoice;

    @FXML
    private void initialize() {
        ListProperty<Credentials> credentialses = ApplicationContext.getInstance()
                .getRegisteredObject(AccountProvider.class).getData();
        accountChoice.getItems().addAll(credentialses);
        if (credentialses.size() > 0) {
            accountChoice.getSelectionModel().select(0);
        }

        jid.textProperty().addListener(observable -> {
            boolean isJid = XmppStringUtils.isFullJID(jid.textProperty().get() + "/Foo");
            saveButton.setDisable(!isJid);
        });
    }

    @FXML
    private void addContactAction() throws ConnectionFailedException, BookmarkException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {

        final Credentials credentials = accountChoice.getSelectionModel().getSelectedItem();
        final ConnectionManager connectionManager = ApplicationContext.getInstance().getRegisteredObject(ConnectionManager.class);
        final XMPPTCPConnection connection = connectionManager.getConnection(credentials.getJid());
        final String jidToAdd = jid.getText();
        final ConversationManager conversationManager = ApplicationContext.getInstance().getRegisteredObject(ConversationManager.class);

        if (Utils.isMuc(connection, jidToAdd)) {
            final ConferenceBookmarkManager conferenceBookmarkManager = ApplicationContext.getInstance().getRegisteredObject(ConferenceBookmarkManager.class);
            final Contact contact = ContactManager.createContact(credentials.getJid(), jidToAdd, null, true);
            conferenceBookmarkManager.addBookmark(contact);
            conversationManager.openConversation(credentials.getJid(), jidToAdd, true);
        } else {
            final RosterManager rosterManager = ApplicationContext.getInstance().getRegisteredObject(RosterManager.class);
            rosterManager.subscribe(credentials, jidToAdd);
            conversationManager.openConversation(credentials.getJid(), jidToAdd, false);
        }
        close();
    }

    @FXML
    private void close() {
        Stage stage = (Stage) back.getScene().getWindow();
        stage.close();
    }
}
