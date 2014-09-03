package de.xsrc.palaver.xmpp.task;

import org.datafx.concurrent.DataFxTask;
import org.jivesoftware.smack.XMPPConnection;

public class DisconnectTask extends DataFxTask{

	private XMPPConnection connection;

	public DisconnectTask(XMPPConnection connection){
		this.connection = connection;
	}
	@Override
	protected Boolean call() throws Exception {
		connection.disconnect();
		return true;
	}
}
