package de.xsrc.palaver.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.datafx.crud.CrudException;
import org.datafx.util.EntityWithId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Storage<S extends EntityWithId<String>, String> {

	private Class<S> clazz;
	private static final Logger logger = Logger.getLogger(Storage.class
			.getName());
	private ObservableList<S> cacheList;

	public void delete(S entity) throws CrudException {
		// TODO Auto-generated method stub

	}

	public Storage(Class<S> clazz) {
		super();
		this.clazz = clazz;
	}

	public void save(S entity) {
		ObservableList<S> list = getAll();
		if (list.contains(entity)) {
			logger.finest("Entity " + entity + " from model "
					+ clazz.getSimpleName() + " is already in the list");
			saveModel();
		} else {
			list.add(entity);
		}
	}

	public ObservableList<S> getAll() {
		if (cacheList == null) {
			logger.finer("Initializing cache for model "
					+ this.clazz.getSimpleName());
			cacheList = initialize();

			cacheList.addListener((Change<? extends S> c) -> {
				while (c.next()) {
					if (c.wasPermutated()) {
						logger.finest("Permutated: " + c.getFrom() + " => "
								+ c.getTo());
					} else if (c.wasUpdated()) {
						logger.finest("Updated: " + c.getFrom() + " => "
								+ c.getTo());
					} else {
						if (c.getRemovedSize() > 0)
							logger.finest("Added: " + c.getRemovedSize() + " "
									+ clazz.getSimpleName());
						if (c.getAddedSize() > 0)
							logger.finest("Removed: " + c.getAddedSize() + " "
									+ clazz.getSimpleName());
					}
				}
				saveModel();
			});
		}
		return cacheList;
	}

	private ObservableList<S> initialize() {
		ObservableList<S> result = FXCollections.observableArrayList();
		AppDataSource<S> cs;
		try {
			cs = new AppDataSource<S>(clazz);
			while (cs.next()) {
				S tmp = cs.get();
				result.add(tmp);
			}
			logger.finest("Read " + result.size() + " records from Model "
					+ clazz.getSimpleName());

		} catch (IOException e) {
			logger.warning("Reading data for model " + clazz.getSimpleName()
					+ " failed. First run?");
		}
		return result;
	}

	private void saveModel() {
		logger.finest("Trying to save model: " + this.clazz.getSimpleName());
		try {
			DocumentBuilder docBuilder;
			docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(clazz.getSimpleName()
					.toLowerCase() + "s");
			doc.appendChild(rootElement);

			JAXBContext jc = JAXBContext.newInstance(clazz);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(
					javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			ObservableList<S> list = getAll();
			for (S s : list) {
				marshaller.marshal(s, doc.getFirstChild());
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File file = AppDataSource.getFile(clazz);
			StreamResult result = new StreamResult(file);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
			logger.finer("Saved model " + this.clazz.getSimpleName());

		} catch (ParserConfigurationException | JAXBException
				| TransformerConfigurationException e) {
			e.printStackTrace();
			logger.severe("This should not happen :-/");
			Platform.exit();
		} catch (TransformerException e) {
			e.printStackTrace();
			logger.severe("Could not transform XML for model "
					+ clazz.getSimpleName() + ". Model data broken?");
			Platform.exit();
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Could not write storage file for  model "
					+ clazz.getSimpleName()
					+ " to disk. Check disk space and access rights");

		}

	}
}
