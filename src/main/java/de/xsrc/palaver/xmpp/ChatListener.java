package de.xsrc.palaver.xmpp;

import java.util.logging.Logger;

import javafx.application.Platform;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.Utils;

public class ChatListener implements ChatManagerListener {

	private Account account;

	private static final Logger logger = Logger.getLogger(ChatListener.class
			.getName());

	public ChatListener(Account account) {
		this.account = account;
	}

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		if (!createdLocally) {
			// Listen for new incoming chats
			String id = account.getId() + ":"
					+ StringUtils.parseBareAddress(chat.getParticipant());
			try {
				// if a new chat this should fail
				Palaver p = Utils.getStorage(Palaver.class).getById(id);
				chat.addMessageListener(new MsgListener(account.getJid()));
				ChatUtils.getChat(p);
				logger.finest("Retrieved palaver: " + p);
			} catch (Exception e) {
				String recipent = StringUtils.parseBareAddress(chat
						.getParticipant());
				Palaver p = new Palaver();
				p.setAccount(account.getJid());
				p.setRecipient(recipent);
				logger.finer("Created new palaver" + p);
				Platform.runLater(() -> {
					Utils.getStorage(Palaver.class).save(p);
				});
			}
		}
	}

}
