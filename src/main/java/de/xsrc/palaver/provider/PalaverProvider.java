package de.xsrc.palaver.provider;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.utils.AppDataSource;
import de.xsrc.palaver.utils.ColdStorage;
import de.xsrc.palaver.xmpp.task.JoinMucTask;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.logging.Logger;

public class PalaverProvider extends ListDataProvider<Palaver> {
	private static final Logger logger = Logger.getLogger(PalaverProvider.class
					.getName());

	public PalaverProvider() {
		super(null, null, null);
		AppDataSource<Palaver> dr = new AppDataSource<>(Palaver.class);
		this.setDataReader(dr);
		this.setAddEntryHandler(new WriteBackHandler<Palaver>() {
			@Override
			public WritableDataReader<Palaver> createDataSource(Palaver observable) {
				ObservableList<Palaver> list = ApplicationContext.getInstance()
								.getRegisteredObject(PalaverProvider.class).getData();
				return new AppDataWriter<>(list, Palaver.class);
			}
		});

		this.setWriteBackHandler(new WriteBackHandler<Palaver>() {
			@Override
			public WritableDataReader<Palaver> createDataSource(Palaver observable) {
				ObservableList<Palaver> list = ApplicationContext.getInstance()
								.getRegisteredObject(PalaverProvider.class).getData();
				return new AppDataWriter<>(list, Palaver.class);
			}
		});
	}

	public static Palaver getById(String account, String recipient) {
		String id = account + ":" + recipient;

		ListProperty<Palaver> data = ApplicationContext.getInstance()
						.getRegisteredObject(PalaverProvider.class).getData();
		for (Palaver palaver : data) {
			if (palaver.getId().equals(id)) {
				return palaver;
			}
		}
		return null;
	}

	public static Palaver createPalaver(String account, String recipient) {
		// No previous Palaver found
		logger.finer(String.format("Starting new palaver between %s %s", account, recipient));
		Palaver palaver = new Palaver(account, recipient);
		palaver.setClosed(false);
		return palaver;
	}


	public static void openPalaver(Contact contact) throws XMPPException.XMPPErrorException, SmackException {
		Palaver palaver = getById(contact.getAccount(), contact.getJid());
		if (palaver != null) {
			palaver.setClosed(false);
			palaver.setConference(contact.isConference());
		} else {
			logger.info("Opening new Palaver with " + contact.getConference());
			palaver = createPalaver(contact.getAccount(), contact.getJid());
			palaver.setClosed(false);
			if (contact.isConference()) {
				palaver.setConference(true);
				ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class).submit(new JoinMucTask(palaver));

			}
			final Palaver finalPalaver = palaver;
			Platform.runLater(() -> {
				ApplicationContext.getInstance()
								.getRegisteredObject(PalaverProvider.class).getData().add(finalPalaver);
				save();
			});
		}
	}

	// TODO Why does write back handler do not handle this?
	public static void save() {
		ListProperty<Palaver> data = ApplicationContext.getInstance()
						.getRegisteredObject(PalaverProvider.class).getData();
		ColdStorage.save(Palaver.class, data);
	}

}
