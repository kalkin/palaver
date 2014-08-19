package de.xsrc.palaver;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.datafx.crud.CrudException;
import org.datafx.crud.CrudService;
import org.datafx.util.EntityWithId;
import org.datafx.util.QueryParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.xsrc.palaver.utils.AppDataSource;

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

	public ObservableList<S> getAll() throws CrudException {
		if (cacheList == null) {
			try {
				@SuppressWarnings("unchecked")
				AppDataSource<S> cs = new AppDataSource<S>(clazz);
				cacheList = FXCollections.observableArrayList();
				while (cs.next()) {
					cacheList.add(cs.get());
				}
				cacheList.addListener(new ListChangeListener() {
					// TODO rework this ugly spaghety save code
					public void onChanged(Change c) {
						try {
							saveModel();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
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

	private void saveModel() throws TransformerFactoryConfigurationError,
			IOException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
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
			transformer.transform(source, result);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Could not write storage");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CrudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CHange");
	}

}
