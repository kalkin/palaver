package de.xsrc.palaver.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.datafx.util.EntityWithId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * This is the lowest part of the storage. It only provides methods to reading
 * list of EntitiesWithId and saving them.
 * 
 * @author kalkin
 *
 */
public class ColdStorage {

	private static final Logger logger = Logger.getLogger(ColdStorage.class
			.getName());

	/**
	 * Reads the model data from xml file
	 * 
	 * @param <T>
	 * 
	 * @param c
	 * @return
	 * @throws IOException
	 */
	public static <T extends EntityWithId<?>> LinkedList<T> get(Class<?> c) {
		logger.finer("Reading XML file for model" + c.getSimpleName());
		LinkedList<T> result = new LinkedList<T>();
		XmlDataSource<T> source;
		try {
			source = new XmlDataSource<T>(c);

			while (source.next()) {
				T tmp = source.get();
				result.add(tmp);
			}

		} catch (IOException e) {
			return result;
		}
		return result;
	}

	/**
	 * Saves a list of models to xml file. It overwrites it's previous content!
	 * 
	 * @param clazz
	 * @param list
	 */
	public static <T extends EntityWithId<?>> void save(Class<T> clazz,
			List<T> list) {
		Service service = new Service() {
			@Override
			protected Task createTask() {
				Task task = new Task() {
					@Override
					protected Object call() throws ParserConfigurationException,
							JAXBException, ClassNotFoundException, InstantiationException,
							IllegalAccessException, ClassCastException, IOException {

						logger.finer("Saving XML file for model" + clazz.getSimpleName());
						Document doc = DocumentBuilderFactory.newInstance()
								.newDocumentBuilder().newDocument();

						// Create wrapper root element to wrap the beans iE
						// Account -> Accounts
						Element rootElement = doc.createElement(clazz.getSimpleName()
								.toLowerCase() + "s");
						Marshaller m = JAXBContext.newInstance(clazz).createMarshaller();
						doc.appendChild(rootElement);
						m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
								Boolean.TRUE);

						for (T t : list) {
							// attach all nodes to the root wrapper
							m.marshal(t, doc.getFirstChild());
						}

						DOMImplementationRegistry registry = DOMImplementationRegistry
								.newInstance();

						DOMImplementationLS impl = (DOMImplementationLS) registry
								.getDOMImplementation("LS");

						LSSerializer writer = impl.createLSSerializer();
						writer.getDomConfig().setParameter("format-pretty-print",
								Boolean.TRUE);
						LSOutput output = impl.createLSOutput();
						File file = Utils.getFile(clazz);
						output.setByteStream(new FileOutputStream(file));
						return writer.write(doc, output);

					}
				};
				return task;
			}
		};
		service.start();

	}
}
