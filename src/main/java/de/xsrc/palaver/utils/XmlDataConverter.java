package de.xsrc.palaver.utils;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.datafx.reader.converter.InputStreamConverter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlDataConverter<T> extends InputStreamConverter<T> {

	private NodeList dataList;
	private int currentIndex = 0;
	private Class<?> clazz;
	private InputStream inputStream;
	private static final Logger logger = Logger
			.getLogger(Storage.class.getName());

	public XmlDataConverter(Class<?> clazz) {
		this.clazz = clazz;
	}

	public boolean next() {
		if (currentIndex < dataList.getLength())
			return true;
		return false;
	}

	public synchronized void initialize(InputStream is) throws IOException {
		this.inputStream = is;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);
			doc.getDocumentElement().normalize(); // http://tinyurl.com/kms457a
			String name = Introspector.decapitalize(this.clazz.getSimpleName());
			dataList = doc.getElementsByTagName(name);
		} catch (ParserConfigurationException | SAXException e) {
			String errorMsg = "Could not read/parse the AppData Config";
			logger.warning(errorMsg);
			IOException exception = new IOException(errorMsg);
			exception.setStackTrace(e.getStackTrace());
		}

	}

	public T get() {
		int oldIndex = currentIndex;
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(this.clazz);
			Unmarshaller u = jc.createUnmarshaller();
			currentIndex++;
			@SuppressWarnings("unchecked")
			JAXBElement<T> element = (JAXBElement<T>) u.unmarshal(
					dataList.item(oldIndex), clazz);
			return element.getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
			currentIndex++;
			return null;

		}
	}
}