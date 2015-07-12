package de.xsrc.palaver.controls;

import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.utils.UiUtils;
import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.context.ApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class HistoryControl extends BorderPane {
    private static final Logger logger = Logger
            .getLogger(HistoryControl.class.getName());
    private static final String HISTORY_VIEW_FXML = "/fxml/History.fxml";
    @FXML
    private ChatInput chatInput;
    @FXML
    private VBox historyBox;
    @FXML
    private ScrollPane scrollPane;

    private ObservableList<HistoryEntry> history;
    private Conversation conversation;

    public HistoryControl(Conversation conversation) {
        this.conversation = conversation;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(HISTORY_VIEW_FXML));
        fxmlLoader.setResources(UiUtils.getRessourceBundle());
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void initialize() {
        history = conversation.history.entryListProperty();
        add(history);
        chatInput.setConversation(conversation);
        history.addListener((Change<? extends HistoryEntry> change) -> {
            while (change.next()) {
                logger.finer("New Messages were added to " + conversation);
                add(change.getAddedSubList());
            }
        });
        historyBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
            conversation.setUnread(false);
        });
        requestFocus();

    }

    private void add(List<? extends HistoryEntry> list) {
        ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
        executor.submit(() -> {
                    for (HistoryEntry historyEntry : list) {
                        Label l = new Label();
                        Platform.runLater(() -> historyBox.getChildren().add(new HistoryEntryCell(historyEntry, conversation.isConference())));

                    }
                }
        );
    }

    public void requestFocus() {
        Platform.runLater(() -> {
            chatInput.requestFocus();
            chatInput.positionCaret(chatInput.getLength());
        });

    }
}
