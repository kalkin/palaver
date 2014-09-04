package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.xmpp.task.JoinMucTask;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.logging.Logger;

/**
 * Created by user on 04.09.14.
 */
public class PalaverConnectionListener implements ConnectionListener {

	private static final Logger logger = Logger.getLogger(PalaverConnectionListener.class.getName());

	@Override
	public void connected(XMPPConnection connection) {
		logger.info("Connected account " + connection.getServiceName());
	}

	@Override
	public void authenticated(XMPPConnection connection) {
		logger.fine(String.format("Authenticated to %s", connection.getUser()));
		ListProperty<Palaver> data = ApplicationContext.getInstance().getRegisteredObject(PalaverProvider.class).getData();

		ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
		data.stream().filter(palaver -> palaver.isOpen())
						.filter(palaver -> palaver.isConference())
						.forEach(palaver -> Platform.runLater(() -> {
															executor.submit(
																			new JoinMucTask(palaver, connection)
															);
														}
										)
						);
	}

	@Override
	public void connectionClosed() {
		logger.info("Connection closed");
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		logger.warning(String.format("Connection closed with error %s", e.getMessage()));
	}

	@Override
	public void reconnectingIn(int seconds) {
		logger.fine(String.format("Reconnecting in %d", seconds));
	}

	@Override
	public void reconnectionSuccessful() {
		logger.info("Reconnection successful");
	}

	@Override
	public void reconnectionFailed(Exception e) {
		logger.warning(String.format("Reconnection failed with error %s", e.getMessage()));
	}


}
