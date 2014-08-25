package de.xsrc.palaver.provider;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.datafx.reader.WritableDataReader;
import org.datafx.util.EntityWithId;

import de.xsrc.palaver.utils.ColdStorage;

public class AppDataWriter<T extends EntityWithId<?>> implements WritableDataReader<T> {

	private List<T> data;
	private Class<T> clazz;

	public AppDataWriter (List<T> list, Class<T> clazz){
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
		try {
			ColdStorage.save(clazz, data);
		} catch (ClassNotFoundException
				| InstantiationException
				| IllegalAccessException | ClassCastException
				| ParserConfigurationException | JAXBException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
