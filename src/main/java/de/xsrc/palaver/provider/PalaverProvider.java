package de.xsrc.palaver.provider;

import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.AppDataSource;
import de.xsrc.palaver.utils.ColdStorage;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.writer.WriteBackHandler;

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

		// No previous Palaver found
		logger.finer(String.format("Starting new palaver between %s %s", account, recipient));
		Palaver palaver = new Palaver(account, recipient);
		palaver.setClosed(false);
		Platform.runLater(() -> data.add(palaver));
		return palaver;
	}

	public static void openPalaver(String account, String recipient) {
		getById(account, recipient);
	}

	// TODO Why does write back handler do not handle this?
	public static void save() {
		ListProperty<Palaver> data = ApplicationContext.getInstance()
						.getRegisteredObject(PalaverProvider.class).getData();
		ColdStorage.save(Palaver.class, data);
	}

}
