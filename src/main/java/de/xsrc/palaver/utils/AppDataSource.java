package de.xsrc.palaver.utils;

import java.io.IOException;

import org.datafx.reader.FileSource;

public class AppDataSource<T> extends FileSource<T> {

	public AppDataSource(Class<?> c) throws IOException {
		super(Utils.getFile(c), new AppDataConverter<T>(c));
	}

}
