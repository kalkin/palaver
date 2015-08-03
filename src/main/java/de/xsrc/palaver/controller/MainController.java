package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.controls.HistoryControl;
import de.xsrc.palaver.controls.OpenPalaverList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.LinkAction;
import org.jxmpp.util.XmppStringUtils;

import java.util.HashMap;
import java.util.logging.Logger;

@FXMLController("/fxml/MainView.fxml")
public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class.getName());
    private static final int STEP_WIDTH = 64;

    @FXML
    @LinkAction(AccountController.class)
    private Button showAccountsButton;

    @FXML
    private BorderPane barPane;

    @FXML
    @LinkAction(ContactController.class)
    private Button showBuddyListButton;

    @FXML
    private BorderPane historyPane;

    @FXML
    private BorderPane titlePane;

    @FXML
    private Button showOpenPalaverButton;

    @FXML
    private OpenPalaverList palaverListControl;
    private HashMap<Conversation, HistoryControl> historyMap = new HashMap<>();

    @FXML
    private void initialize() {

        palaverListControl.selectedPalaver().addListener((observable, oldValue, newValue) -> {
            if (!historyMap.containsKey(newValue)) {
                try {

                    HistoryControl history = new HistoryControl(newValue);
                    historyMap.put(newValue, history);
                } catch (Exception e) {
                    // Auto-generated catch block
                    e.printStackTrace();
                }
            }

            historyPane.setCenter(historyMap.get(newValue));

        });

        showOpenPalaverButton.visibleProperty().bind(palaverListControl.visibleProperty().not());
        showOpenPalaverButton.managedProperty().bind(palaverListControl.managedProperty().not());
        showOpenPalaverButton.cancelButtonProperty().bind(palaverListControl.visibleProperty().not());
        palaverListControl.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                Conversation p = palaverListControl.selectedPalaver().get();
                Text text = new Text(XmppStringUtils.parseLocalpart(p.getRecipient()));
                text.getStyleClass().add("display1");
                titlePane.setCenter(text);
                historyPane.setMaxWidth(1024);
            } else {
                Text text = new Text("Palavers");
                text.getStyleClass().add("display1");
                titlePane.setCenter(text);
                historyPane.setMaxWidth(768);
            }
        });
        Platform.runLater(() ->
                palaverListControl.getScene().getWindow().widthProperty().addListener((observable, oldValue, newValue) -> resize(newValue)));
    }

    private void resize(Number newValue) {
        int t = newValue.intValue() / 64;
        int width = t * 64;
        if (width > 1024) {
            width = 1024;
            t = 16;
        }
        logger.fine(String.format("WIDTH: %d", width));
        barPane.setMaxWidth(width);
        StackPane parent = (StackPane) palaverListControl.getParent();
        parent.setMaxWidth(width);


        if (t >= 11) {
            palaverListControl.hide(false);
            historyPane.setMaxWidth((t - 4) * STEP_WIDTH);
        } else {
            palaverListControl.hide(true);
            historyPane.setMaxWidth(t * 64);
        }


    }

    @FXML
    private void showAction() {
        palaverListControl.hide(false);

    }
}
