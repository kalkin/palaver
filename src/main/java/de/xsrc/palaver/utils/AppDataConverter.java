package de.xsrc.palaver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.datafx.reader.converter.InputStreamConverter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AppDataConverter<T> extends InputStreamConverter<T> {

	private String tag;
	private NodeList dataList;
	private int currentIndex = 0;
	private Class<T> clazz;
	private InputStream inputStream;

	public AppDataConverter(Class<T> clazz) {
		this.tag = clazz.getSimpleName().toLowerCase() + "s";
		this.clazz = clazz;
	}

	public boolean next() {
		if (currentIndex < dataList.getLength())
			return true;
		return false;
	}

	public synchronized void initialize(InputStream is) {
		this.inputStream = is;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);
			dataList = doc.getElementsByTagName("account");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public T get() {
		int oldIndex = currentIndex;
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(this.clazz);
			Unmarshaller u = jc.createUnmarshaller();
			currentIndex++;
			JAXBElement<T> element = u
					.unmarshal(dataList.item(oldIndex), clazz);
			return element.getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
			currentIndex++;
			return null;
		}
	}
}
