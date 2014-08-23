package de.xsrc.palaver.utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.datafx.util.EntityWithId;

public class Storage {

	protected static ConcurrentHashMap<Class<?>, ObservableList<? extends EntityWithId<?>>> mapOfLists;

	private static final Logger logger = Logger.getLogger(ColdStorage.class
			.getName());

	/**
	 * This method must be run once on in the app! new
	 * Storage().initialiaze(...). After that the static methods should be used
	 * to interact with the storage
	 * 
	 * @param classes
	 * @return
	 * @throws IllegalStateException
	 */
	public synchronized static <T extends EntityWithId<?>> boolean initialize(
			Class<?>... classes) throws IllegalStateException {
		logger.finer("Importing Tables " + classes + " in to the DB");
		mapOfLists = new ConcurrentHashMap<Class<?>, ObservableList<? extends EntityWithId<?>>>(
				4);
		for (Class<?> clazz : classes) {
			ObservableList<T> list = FXCollections
					.synchronizedObservableList(FXCollections
							.observableList(ColdStorage.get(clazz)));
			list.addListener((Change<? extends T> change) -> {
				if (change.next())
					Storage.save(change.getList().get(0).getClass());
			});
			mapOfLists.put(clazz, list);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	protected static synchronized <T extends EntityWithId<?>> void save(
			Class<T> clazz) {
		try {
			ColdStorage.save(clazz, (List<T>) mapOfLists.get(clazz));
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | ClassCastException
				| ParserConfigurationException | JAXBException | IOException e) {
			e.printStackTrace();
			logger.severe("Could not save to cold storage");
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends EntityWithId<?>> ObservableList<T> getList(
			Class<? extends T> clazz) {

		return (ObservableList<T>) mapOfLists.get(clazz);
	}

	public static <T extends EntityWithId<?>> T getById(Class<T> clazz,
			String id) {
		ObservableList<T> list = getList(clazz);
		for (T t : list) {
			if (t.getId().equals(id))
				return t;
		}
		throw new IllegalArgumentException("Id " + id
				+ " does not exist in table " + clazz.getSimpleName());

	}

}
