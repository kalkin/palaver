package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Entry;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.ColdStorage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * This should be the crosspoint between disk and xmpp
 */
public class PalaverModel {

	protected ConcurrentHashMap<String, Palaver> palaverMap;
	protected static ObservableList<Palaver> openPalaverList;


	private static final Logger logger = Logger.getLogger(PalaverModel.class
					.getName());
	private ObservableList<Palaver> data;

	private static final class InstanceHolder {
		static final PalaverModel INSTANCE = new PalaverModel();
	}

	private PalaverModel() {
		palaverMap = new ConcurrentHashMap<>();
		openPalaverList = FXCollections.observableList(new CopyOnWriteArrayList<>());

		data = FXCollections.observableList(new CopyOnWriteArrayList<>());

		data.addListener((ListChangeListener<Palaver>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					// sync data with our Concurrent HashMap
					c.getAddedSubList().forEach(palaver -> palaverMap.put(palaver.getId(), palaver));
					// auto save when history entries are added
					c.getAddedSubList().forEach(palaver -> palaver.history.entryListProperty().addListener((ListChangeListener<Entry>) change -> save()));
					c.getAddedSubList().forEach(palaver -> {
						if (palaver.isOpen()) openPalaverList.add(palaver);
					});

					// auto add to openPalaversList if a palaver closedProperty changes to true
					c.getAddedSubList().forEach(palaver -> palaver.closedProperty().addListener((observable, oldValue, newValue) -> {
						if (oldValue && !newValue) {
							// Palaver changed state to open
							Platform.runLater(() ->
							openPalaverList.add(palaver));
							save();
						} else if (!oldValue && newValue) {
							// Palaver changed state to closed
							Platform.runLater(() -> openPalaverList.remove(palaver));
							save();
						}
					}));
				}
			}
		});
		PalaverProvider provider = new PalaverProvider();
		provider.setResultObservableList(data);
		provider.retrieve();

	}


	public static PalaverModel getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public Palaver openPalaver(Contact contact){
		return openPalaver(contact.getAccount(), contact.getJid(), contact.isConference());
	}

	public synchronized Palaver openPalaver(String accountJid, String recipientJid, boolean conference) {
		String id = accountJid + ":" + recipientJid;
		Palaver palaver = palaverMap.get(id);
		if (palaver == null) {
			logger.finer(String.format("No previous palaver for %s found ", id));
			palaver = createPalaver(accountJid, recipientJid, conference);
			data.add(palaver);
			save();
		} else if (palaver.getClosed()) {
			palaver.setClosed(false);
		}
		return palaver;
	}

	private Palaver createPalaver(String accountJid, String recipientJid, boolean conference) {
		logger.finer(String.format("Createing new palaver %s:%s ", accountJid, recipientJid));
		Palaver palaver = new Palaver();
		palaver.setAccount(accountJid);
		palaver.setRecipient(recipientJid);
		palaver.setConference(conference);
		palaver.setClosed(false);
		return palaver;
	}

	public ObservableList<Palaver> getOpenPalavers() {

		return FXCollections.unmodifiableObservableList(openPalaverList);
	}

	private synchronized void save() {
		ColdStorage.save(Palaver.class, data);
	}

	public Palaver getById(String account, String recipient) {
		return palaverMap.get(account+":"+recipient);
	}

}
