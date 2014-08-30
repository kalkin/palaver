package de.xsrc.palaver.xmpp;

import java.util.logging.Logger;

import javafx.application.Platform;

import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.ColdStorage;

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
			String recipent = StringUtils.parseBareAddress(chat.getParticipant());
			String id = account.getId() + ":" + recipent;
			logger.fine("Got new Msg");
			PalaverProvider provider = ApplicationContext.getInstance()
					.getRegisteredObject(PalaverProvider.class);
			Palaver palaver = provider.getById(id);
			if (palaver == null) {
				Palaver p = new Palaver();
				p.setAccount(account.getJid());
				p.setRecipient(recipent);
				Platform.runLater(() -> {
					provider.getData().add(p);
					p.setClosed(false);
					// TODO Find out why write back handler does not handle this.
						ColdStorage.save(Palaver.class, provider.getData());
					});
			} else if (palaver.getClosed()) {
				palaver.setClosed(false);
			}
			chat.addMessageListener(new MsgListener(account.getJid()));
		}
	}
}
