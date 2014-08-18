package de.xsrc.palaver;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.datafx.crud.CrudException;
import org.datafx.crud.CrudService;
import org.datafx.util.EntityWithId;
import org.datafx.util.QueryParameter;

public class Storage<S extends EntityWithId<String>, String> implements
		CrudService<S, String> {

	private Class clazz;

	public void delete(S entity) throws CrudException {
		// TODO Auto-generated method stub

	}
	
	public Storage(Class clazz){
		super();
		this.clazz = clazz;
	}

	public S save(S entity) throws CrudException {
		return null;
	}

	public List<S> getAll() throws CrudException {
		try {
			AppDataSource<S> cs = new AppDataSource(clazz);
			ObservableList<S> result = FXCollections.observableArrayList();
			result.add(cs.get());
			while (cs.next()){
				result.add(cs.get());
			}
			return result;
		} catch (IOException e) {
			throw new CrudException("Could not get all "
					+ clazz.getSimpleName());
		}
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
