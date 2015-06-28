package de.xsrc.palaver.xmpp;

public class MucManager {

//	private static final Logger logger = Logger
//					.getLogger(MucManager.class.getName());
//
//	private final ObservableList<Palaver> openPalavers;
//	private ConcurrentHashMap<Palaver, MultiUserChat> joinedMucMap;
//
//	private MucManager() {
//		joinedMucMap = new ConcurrentHashMap<>();
//
//		openPalavers = PalaverModel.getInstance().getOpenPalavers();
//		openPalavers.addListener((ListChangeListener<Palaver>) change -> {
//			while (change.next()) {
//				if (change.wasAdded()) {
//					joinMucs(change.getAddedSubList());
//				} else if (change.wasRemoved()) {
//					ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
//					change.getRemoved().parallelStream().filter(palaver -> palaver.isConference()).forEach(palaver -> {
//						executor.submit(() -> {
//							try {
//								BookmarkManager bm = BookmarkManager.getBookmarkManager(ConnectionManager.getConnection(palaver.getAccount()));
//								bm.getBookmarkedConferences().parallelStream().filter(bookmarkedConference -> bookmarkedConference.getJid().equals(palaver.getRecipient()) && bookmarkedConference.isAutoJoin()).forEach(bookmarkedConference -> {
//									try {
//										bm.addBookmarkedConference(XmppStringUtils.parseLocalpart(palaver.getRecipient()), palaver.getRecipient(), false, XmppStringUtils.parseLocalpart(palaver.getAccount()), null);
//									} catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
//										logger.warning(String.format("Could not remove autjoin on %s", palaver.toString()));
//									}
//								});
//							} catch (XMPPException | SmackException e) {
//								logger.warning(String.format("Could not get Bookmark manager on %s", palaver.toString()));
//							}
//						});
//					});
//				}
//			}
//		});
//	}
//
//	public static MucManager getInstance() {
//		return InstanceHolder.INSTANCE;
//	}
//
//	private void joinMucs(List<? extends Palaver> list) {
//		ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
//		list.forEach(palaver -> {
//							if (palaver.isConference()) {
//								JoinMucTask task = new JoinMucTask(palaver);
//								Platform.runLater(() -> task.setOnSucceeded(event -> joinedMucMap.put(palaver, (MultiUserChat) task.getValue())));
//
//								executor.submit(task);
//							}
//						}
//		);
//	}
//
//	public void disconnect(String accountJid) {
//		joinedMucMap.forEach((palaver, multiUserChat) -> {
//			if (palaver.getAccount().equals(accountJid)) {
//				joinedMucMap.remove(palaver);
//			}
//		});
//	}
//
//	public void reconnect(String accountJid) {
//
//		List<Palaver> mucsToRejoin = openPalavers.parallelStream().filter(palaver -> palaver.isConference() && palaver.getAccount().equals(accountJid)).collect(Collectors.toList());
//		logger.info(String.format("Rejoining the mucs %s", mucsToRejoin.toString()));
//		joinMucs(mucsToRejoin);
//	}
//
//	public MultiUserChat getMuc(Palaver palaver) {
//		return joinedMucMap.get(palaver);
//	}
//
//	private static final class InstanceHolder {
//		static final MucManager INSTANCE = new MucManager();
//	}

}
