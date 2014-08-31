package de.xsrc.palaver.controller;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.provider.ContactProvider;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.UiUtils;
import de.xsrc.palaver.xmpp.model.Contact;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.datafx.controller.FXMLController;
import org.datafx.controller.FxmlLoadException;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.BackAction;
import org.datafx.controller.flow.context.FXMLViewFlowContext;
import org.datafx.controller.flow.context.ViewFlowContext;
import org.datafx.controller.util.VetoException;
import org.jivesoftware.smack.util.StringUtils;

import java.util.logging.Logger;

@FXMLController("/fxml/ContactView.fxml")
public class ContactController {

	private static final Logger logger = Logger.getLogger(ContactController.class
					.getName());
	@FXML
	@BackAction
	private Button back;
	@FXML
	private Button addBuddy;
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML
	private Button startPalaverButton;
	@FXML
	private TextField searchInput;
	@FXML
	private ListView<Contact> list;
	private ContactProvider provider;

	@FXML
	private void initialize() {
		AwesomeDude.setIcon(back, AwesomeIcon.CHEVRON_LEFT, "20");

		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);

		hbox.getChildren().add(AwesomeDude.createIconLabel(AwesomeIcon.PLUS, "24"));
		hbox.getChildren().add(AwesomeDude.createIconLabel(AwesomeIcon.USER, "24"));
		addBuddy.setGraphic(hbox);

		provider = ApplicationContext.getInstance()
						.getRegisteredObject(ContactProvider.class);
		list.setItems(provider.getData());
		list.setManaged(true);
		list.setCellFactory(listView -> new BuddyCell());

		searchInput.textProperty().addListener((observable, oldVal, newVal) -> handleSearchByKey(oldVal, newVal));

		AwesomeDude.setIcon(startPalaverButton, AwesomeIcon.SEARCH, "20");
		Platform.runLater(searchInput::requestFocus);
	}

	public void handleSearchByKey(String oldVal, String newVal) {
		// If the number of characters in the text box is less than last time
		// it must be because the user pressed delete
		if (oldVal != null && (newVal.length() < oldVal.length())) {
			// Restore the lists original set of entries
			// and start from the beginning
			list.setItems(provider.getData());
		}

		// Change to upper case so that case is not an issue
		newVal = newVal.toUpperCase();

		// Filter out the entries that don't contain the entered text
		ObservableList<Contact> sublist = FXCollections.observableArrayList();
		for (Contact entry : list.getItems()) {
			if (entry.toString().toUpperCase().contains(newVal)) {
				sublist.add(entry);
			}
		}
		list.setItems(sublist);
		list.getSelectionModel().select(0);

	}

	@FXML
	private void startPalaverAction() throws VetoException, FlowException {
		Contact buddy = list.getSelectionModel().getSelectedItems().get(0);
		if (buddy != null) {
			logger.fine("Starting palaver with " + buddy.getJid());
			String recipient = StringUtils.parseBareAddress(buddy.getJid());
			Palaver p = PalaverProvider.getById(buddy.getAccount(), recipient);
			p.setClosed(false);

		}
		UiUtils.getFlowHandler(context).navigateBack();
	}

	@FXML
	private void addContactAction() throws FxmlLoadException {
		Flow f = new Flow(AddContactController.class);
		try {
			Utils.getDialog(f, null).show();
		} catch (FlowException e) {
			e.printStackTrace();
		}
	}

}
