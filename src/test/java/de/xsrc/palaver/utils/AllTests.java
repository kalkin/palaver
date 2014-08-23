package de.xsrc.palaver.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlRootElement;

import org.datafx.util.EntityWithId;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.xsrc.palaver.model.AbstractModel;

@RunWith(Suite.class)
@SuiteClasses({ ColdStorageTest.class, StorageTest.class, de.xsrc.palaver.model.AbstractModelTest.class })
public class AllTests {
	@XmlRootElement
	static class TestModel extends AbstractModel implements
			EntityWithId<String> {

		private static final long serialVersionUID = 1L;

		private StringProperty jid;

		private StringProperty password;

		public TestModel() {
			this(null, null);
		}

		public TestModel(String jid, String password) {
			this.jid = new SimpleStringProperty(jid);
			this.password = new SimpleStringProperty(password);
		}

		public String getId() {
			return getJid();
		}

		public void setJid(String jid) {
			this.jid.set(jid);
		}

		public String getPassword() {
			return password.get();
		}

		public void setPassword(String password) {
			this.password.set(password);
		}

		public String getJid() {
			return jid.get();
		}

		public String toString() {
			return jid.get();

		}
	}

}
