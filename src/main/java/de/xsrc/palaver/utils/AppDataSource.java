package de.xsrc.palaver.utils;

import org.datafx.reader.AbstractDataReader;
import org.datafx.reader.DataReader;

import java.io.IOException;
import java.util.logging.Logger;

public class AppDataSource<T> extends AbstractDataReader<T> {

    private static final Logger logger = Logger.getLogger(ColdStorage.class
            .getName());
    private DataReader<T> converter;

    public AppDataSource(String workingDir, Class<T> clazz) {
        try {
            converter = new XmlDataSource<T>(workingDir, clazz);
        } catch (IOException e) {
            logger.info("Save file for class  " + clazz.getSimpleName()
                    + " contained no data");
        }

    }

    public AppDataSource(Class<T> clazz) {
        try {
            converter = new XmlDataSource<T>(clazz);
        } catch (IOException e) {
            logger.info("Save file for class  " + clazz.getSimpleName()
                    + " contained no data");
        }

    }

    @Override
    public T get() {
        try {
            return converter.get();
        } catch (IOException e) {
            logger.finer("No data in the converter any more");
            return null;
        }
    }

    @Override
    public boolean next() {
        if (converter != null) {
            return converter.next();
        }
        return false;
    }
}
