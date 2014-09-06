package de.xsrc.palaver.controller;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.controls.OpenPalaverList;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.FXMLController;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.context.ViewContext;
import org.datafx.controller.flow.action.LinkAction;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

@FXMLController("/fxml/MainView.fxml")
public class MainController {
	private static final Logger logger = Logger.getLogger(MainController.class.getName());

	@FXML
	@LinkAction(AccountController.class)
	private Button showAccountsButton;

	@FXML
	@LinkAction(ContactController.class)
	private Button showBuddyListButton;

	@FXML
	private Pane historyPane;

	@FXML
	private Button showOpenPalaverButton;

	@FXML
	private OpenPalaverList palaverListControl;
	private HashMap<Palaver, ViewContext<HistoryController>> historyMap = new HashMap<Palaver, ViewContext<HistoryController>>();
	private PalaverModel model = PalaverModel.getInstance();

	@FXML
	private void initialize() {
		palaverListControl.selectedPalaver().addListener((observable, oldValue, newValue) -> {
			if (!historyMap.containsKey(newValue)) {
				try {
					ViewContext<HistoryController> context = ViewFactory
									.getInstance().createByController(HistoryController.class);
					context.getController().setPalaver(newValue);
					historyMap.put(newValue, context);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			historyPane.getChildren().clear();
			historyPane.getChildren().addAll(historyMap.get(newValue).getRootNode());
			historyMap.get(newValue).getController().requestFocus();
		});
		showOpenPalaverButton.visibleProperty().bind(palaverListControl.visibleProperty().not());
		showOpenPalaverButton.managedProperty().bind(palaverListControl.managedProperty().not());
		showOpenPalaverButton.cancelButtonProperty().bind(palaverListControl.visibleProperty().not());
	}

	@FXML
	private void showAction(){
		palaverListControl.hide(false);
	}
}
