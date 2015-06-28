package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Account;
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
    private Account account;

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
        if (account == null) {
            account = new Account();
            account.setJid(jidField.getText());
            account.setPassword(passwordField.getText());
            logger.fine("Saving Account " + account);
            AccountProvider provider = ApplicationContext.getInstance()
                    .getRegisteredObject(AccountProvider.class);
            provider.getData().add(account);
        } else {
            account.setJid(jidField.getText());
            account.setPassword(passwordField.getText());
        }
        ListProperty<Account> data = ApplicationContext.getInstance().getRegisteredObject(AccountProvider.class).getData();
        ColdStorage.save(Account.class, data);
        close();

    }

    public void setContext(ViewContext<AddAccountController> context) {
        account = (Account) context.getRegisteredObject("account");
        jidField.setText(account.getJid());
        passwordField.setText(account.getPassword());

    }
}
