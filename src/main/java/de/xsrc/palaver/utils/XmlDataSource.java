package de.xsrc.palaver.utils;

import org.datafx.reader.FileSource;
import org.datafx.reader.WritableDataReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

public class XmlDataSource<T> extends FileSource<T> implements
				WritableDataReader<T> {
	private static final Logger logger = Logger.getLogger(XmlDataSource.class
					.getName());

	public XmlDataSource(Class<?> c) throws FileNotFoundException, IOException {
		super(Utils.getFile(c), new XmlDataConverter(c));
	}

	public XmlDataSource(String workingDir, Class<?> c)
					throws FileNotFoundException, IOException {
		super(Utils.getFile(workingDir, c), new XmlDataConverter(c));
	}

	@Override
	public void writeBack() {
		logger.info("would write back ");

	}

}
