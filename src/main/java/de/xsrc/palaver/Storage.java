package de.xsrc.palaver;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.datafx.crud.CrudException;
import org.datafx.crud.CrudService;
import org.datafx.provider.ListDataProvider;
import org.datafx.util.EntityWithId;
import org.datafx.util.QueryParameter;

public class Storage<S extends EntityWithId<String>, String> implements
		CrudService<S, String> {

	private Class<S> clazz;

	private static ObservableList cacheList;

	public void delete(S entity) throws CrudException {
		// TODO Auto-generated method stub

	}

	public Storage(Class<S> clazz) {
		super();
		this.clazz = clazz;
	}

	public S save(S entity) throws CrudException {
		getAll().add(entity);
		return null;
	}

	public List<S> getAll() throws CrudException {
		if (cacheList == null) {
			try {
				@SuppressWarnings("unchecked")
				AppDataSource<S> cs = new AppDataSource<S>(clazz);

				ListDataProvider lodp = new ListDataProvider(cs);
				ObservableList<S> result = FXCollections.observableArrayList();
				lodp.setResultObservableList(result);
				result.add(cs.get());
				while (cs.next()) {
					result.add(cs.get());
				}
				cacheList = result;
			} catch (IOException e) {
				throw new CrudException("Could not get all "
						+ clazz.getSimpleName());
			}
		}
		return cacheList;
	}

	public S getById(String id) throws CrudException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<S> query(java.lang.String name, QueryParameter... params)
			throws CrudException {
		// TODO Auto-generated method stub
		return null;
	}

}
