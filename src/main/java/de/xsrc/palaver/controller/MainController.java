package de.xsrc.palaver.controller;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
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
	private ListView<Palaver> palaverListView;

	private HashMap<Palaver, ViewContext<HistoryController>> historyMap = new HashMap<Palaver, ViewContext<HistoryController>>();

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button hidePalaverButton;

	private BorderPane palaverListTmp;

	@FXML
	private Button showPalaverButton;

	private PalaverModel model = PalaverModel.getInstance();

	@FXML
	private void initialize() {

				palaverListView.setItems(model.getOpenPalavers());
		palaverListView
						.setCellFactory(listView -> new PalaverCell());

		MultipleSelectionModel<Palaver> selModel = palaverListView
						.getSelectionModel();

		selModel.setSelectionMode(SelectionMode.SINGLE);

		selModel.selectedItemProperty().addListener(
						(ObservableValue<? extends Palaver> observable, Palaver oldValue,
						 Palaver newValue) -> {
							if (newValue != null) {
								newValue.setUnread(false);
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

								borderPane.setCenter(historyMap.get(newValue).getRootNode());
								historyMap.get(newValue).getController().requestFocus();
							}
						});
		AwesomeDude.setIcon(showAccountsButton, AwesomeIcon.GEAR, "24");
		AwesomeDude.setIcon(hidePalaverButton, AwesomeIcon.CHEVRON_LEFT, "24");
		AwesomeDude.setIcon(showPalaverButton, AwesomeIcon.CHEVRON_RIGHT, "24");
		AwesomeDude.setIcon(showBuddyListButton, AwesomeIcon.PLUS, "24");


	}

	@FXML
	private void hidePalaverList() {
		if (palaverListTmp == null) {
			palaverListTmp = (BorderPane) borderPane.getLeft();
			borderPane.setLeft(null);
			showPalaverButton.setVisible(true);
			showPalaverButton.setManaged(true);
			showPalaverButton.setCancelButton(true);
			hidePalaverButton.setCancelButton(false);

		} else {
			borderPane.setLeft(palaverListTmp);
			palaverListTmp = null;
			showPalaverButton.setVisible(false);
			showPalaverButton.setManaged(false);
			showPalaverButton.setCancelButton(false);
			hidePalaverButton.setCancelButton(true);
		}
	}

	@FXML
	private void removeAction() {
		Palaver p = palaverListView.getSelectionModel().getSelectedItem();
		p.setClosed(true);
	}
}
