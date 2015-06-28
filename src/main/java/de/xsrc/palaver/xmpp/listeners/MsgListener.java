package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.utils.Notifications;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

public class MsgListener implements PacketListener {
    private static final Logger logger = Logger.getLogger(MsgListener.class
            .getName());

    private Account account;

    public MsgListener(Account account) {
        this.account = account;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if (packet instanceof Message) {
            Message message = (Message) packet;
            String body = message.getBody();
            logger.finest(message.toString());

            if (body != null && body.length() >= 0) {
                HistoryEntry historyEntry = new HistoryEntry();
                historyEntry.setBody(message.getBody());

                String fromJid = XmppStringUtils.parseBareJid(message.getFrom());
                String toJid = XmppStringUtils.parseBareJid(message.getTo());

                if (fromJid == null || fromJid.equals(account.getJid())) {
                    // Messages send by us
                    historyEntry.setFrom(account.getJid());
                    saveEntry(account.getJid(), toJid, historyEntry);
                } else if (toJid.equals(account.getJid())) {
                    // Messages sent to us
                    historyEntry.setFrom(fromJid);
                    saveEntry(account.getJid(), fromJid, historyEntry);
                    Notifications.notify(XmppStringUtils.parseLocalpart(fromJid), body);
                } else {
                    logger.severe("Server is sending garbage? " + message.toString());
                }
//                TODO Fix Carbon Handling. The MessageListener needs an XmppConnectionObject to check if the current Connection has carbon enabled
//			} else if (CarbonManager.getInstanceFor(CONNECTION).getCarbonsEnabled()) {
//				handleCarbon(message);
            } else {
                logger.warning(String.format("Strange Message %s", message.toString()));
            }

        }
    }

    private void saveEntry(String account, String recipient, HistoryEntry historyEntry) {
        Palaver palaver = PalaverModel.getInstance().getById(account, recipient);
        if (palaver == null) {
            logger.fine(String.format("Creating new palaver %s -> %s", account, recipient));
            palaver = PalaverModel.getInstance().openPalaver(account, XmppStringUtils.parseBareJid(recipient), false);
        }
        palaver.history.addEntry(historyEntry);
        if (!account.equals(historyEntry.getFrom())) {
            palaver.setUnread(true);
        }
        palaver.setClosed(false);

    }

//    private void handleCarbon(Message message) throws SmackException.NotConnectedException {
//        CarbonExtension extension = CarbonManager.getCarbon(message);
//        if (extension.getDirection() == CarbonExtension.Direction.sent) {
//            logger.finer(String.format("Found Carbon Message %s", message.toString()));
//            Stanza stanza = extension.getForwarded().getForwardedPacket();
//            processPacket(stanza);
//        }
//    }


}
