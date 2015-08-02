package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.utils.ColdStorage;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.datafx.controller.FXMLController;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.context.ViewContext;
import org.datafx.controller.flow.context.FXMLViewFlowContext;
import org.datafx.controller.flow.context.ViewFlowContext;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

@FXMLController("/fxml/AddAccountView.fxml")
public class AddAccountController {

    private static final Logger logger = Logger
            .getLogger(AddAccountController.class.getName());
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private Credentials credentials;

    @FXML
    private Button back;

    @FXML
    private TextField jidField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button saveButton;

    @FXML
    private void initialize() {
        jidField.textProperty().addListener(observable -> {
            boolean isJid = XmppStringUtils.isFullJID(jidField.textProperty().get() + "/Foo");
            saveButton.setDisable(!isJid);
        });
    }

    @FXML
    private void close() {
        Stage stage = (Stage) back.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveAccount() {
        if (credentials == null) {
            credentials = new Credentials();
            credentials.setJid(jidField.getText());
            credentials.setPassword(passwordField.getText());
            logger.fine("Saving Account " + credentials);
            AccountProvider provider = ApplicationContext.getInstance()
                    .getRegisteredObject(AccountProvider.class);
            provider.getData().add(credentials);
        } else {
            credentials.setJid(jidField.getText());
            credentials.setPassword(passwordField.getText());
        }
        ListProperty<Credentials> data = ApplicationContext.getInstance().getRegisteredObject(AccountProvider.class).getData();
        ColdStorage.save(Credentials.class, data);
        close();

    }

    public void setContext(ViewContext<AddAccountController> context) {
        credentials = (Credentials) context.getRegisteredObject("account");
        jidField.setText(credentials.getJid());
        passwordField.setText(credentials.getPassword());

    }
}
