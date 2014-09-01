package de.xsrc.palaver.provider;

import de.xsrc.palaver.utils.ColdStorage;
import org.datafx.reader.WritableDataReader;
import org.datafx.util.EntityWithId;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class AppDataWriter<T extends EntityWithId<?>> implements
				WritableDataReader<T> {

	private List<T> data;
	private Class<T> clazz;

	public AppDataWriter(List<T> list, Class<T> clazz) {
		this.data = list;
		this.clazz = clazz;
	}

	@Override
	public T get() throws IOException {
		return null;
	}

	@Override
	public boolean next() {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return null;
	}

	@Override
	public void writeBack() {
		ColdStorage.save(clazz, data);
	}

}
