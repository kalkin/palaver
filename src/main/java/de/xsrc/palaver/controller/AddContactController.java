package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.provider.AccountProvider;
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
    private void addContactAction() throws SmackException,
            XMPPException {

        Credentials credentials = accountChoice.getSelectionModel().getSelectedItem();
        final ContactModel contactModel = ApplicationContext.getInstance().getRegisteredObject(ContactModel.class);
		contactModel.subscribe(credentials, jid.getText());
        PalaverModel palaverModel = PalaverModel.getInstance();
		palaverModel.openPalaver(credentials, jid.getText());
        close();
    }

    @FXML
    private void close() {
        Stage stage = (Stage) back.getScene().getWindow();
        stage.close();
    }
}
