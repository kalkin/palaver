package de.xsrc.palaver.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.datafx.provider.ListDataProvider;
import org.datafx.reader.WritableDataReader;
import org.datafx.util.EntityWithId;
import org.datafx.writer.WriteBackHandler;

public class Storage {

	protected static ConcurrentHashMap<Class<?>, ObservableList<? extends EntityWithId<?>>> mapOfLists;

	private static final Logger logger = Logger.getLogger(ColdStorage.class
			.getName());

	/**
	 * This method must be run once on in the app! new Storage().initialiaze(...).
	 * After that the static methods should be used to interact with the storage
	 * 
	 * @param classes
	 * @return
	 * @throws IllegalStateException
	 */
	protected synchronized static <T extends EntityWithId<?>> boolean initialize(
			Class... classes) throws IllegalStateException {
		logger.finer("Importing Tables " + classes + " in to the DB");
		mapOfLists = new ConcurrentHashMap<Class<?>, ObservableList<? extends EntityWithId<?>>>(
				4);
		for (Class<T> clazz : classes) {
			ListDataProvider<T> provider = new ListDataProvider<T>(
					new AppDataSource<T>(clazz));
			ObservableList<T> list = FXCollections
					.synchronizedObservableList(FXCollections
							.observableList(new LinkedList<T>()));
			provider.setResultObservableList(list);
			provider.retrieve();
			provider.setAddEntryHandler(new WriteBackHandler<T>() {

				@Override
				public WritableDataReader<T> createDataSource(T observable) {
					return null;
				}
			});
			mapOfLists.put(clazz, list);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	protected static synchronized <T extends EntityWithId<?>> void save(
			Class<T> clazz) {
		ColdStorage.save(clazz, (List<T>) mapOfLists.get(clazz));
	}

	@SuppressWarnings("unchecked")
	protected static <T extends EntityWithId<?>> ObservableList<T> getList(
			Class<? extends T> clazz) {

		return (ObservableList<T>) mapOfLists.get(clazz);
	}

	protected static <T extends EntityWithId<?>> T getById(Class<T> clazz,
			String id) throws IllegalArgumentException {
		ObservableList<T> list = getList(clazz);
		for (T t : list) {
			if (t.getId().equals(id))
				return t;
		}
		throw new IllegalArgumentException("Id " + id + " does not exist in table "
				+ clazz.getSimpleName());

	}

}
