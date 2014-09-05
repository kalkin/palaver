package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.xmpp.task.JoinMucTask;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class MucManager {

	private static final Logger logger = Logger
					.getLogger(MucManager.class.getName());

	private final ObservableList<Palaver> openPalavers;
	private ConcurrentHashMap<Palaver, MultiUserChat> joinedMucMap;

	private static final class InstanceHolder {
		static final MucManager INSTANCE = new MucManager();
	}

	private MucManager() {
		joinedMucMap = new ConcurrentHashMap<>();

		openPalavers = PalaverModel.getInstance().getOpenPalavers();
		openPalavers.addListener((ListChangeListener<Palaver>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					joinMucs(change.getAddedSubList());
				} else if (change.wasRemoved()) {
				}
			}
		});
	}

	private void joinMucs(List<? extends Palaver> list) {
		ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
		list.forEach(palaver -> {
							if (palaver.isConference()) {
								JoinMucTask task = new JoinMucTask(palaver);
								Platform.runLater(() -> task.setOnSucceeded(event -> joinedMucMap.put(palaver, (MultiUserChat) task.getValue())));

								executor.submit(task);
							}
						}
		);
	}

	public void disconnect(String accountJid) {
		joinedMucMap.forEach((palaver, multiUserChat) -> {
			if (palaver.getAccount().equals(accountJid)) {
				joinedMucMap.remove(palaver);
			}
		});
	}

	public void reconnect(String accountJid) {

		List<Palaver> mucsToRejoin = openPalavers.parallelStream().filter(palaver -> palaver.isConference() && palaver.getAccount().equals(accountJid)).collect(Collectors.toList());
		logger.info(String.format("Rejoining the mucs %s", mucsToRejoin.toString()));
		joinMucs(mucsToRejoin);
	}

	public static MucManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public MultiUserChat getMuc(Palaver palaver) {
		System.out.println(joinedMucMap);
		return joinedMucMap.get(palaver);
	}

}
