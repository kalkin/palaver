package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.RosterEntriesImporter;
import javafx.collections.MapChangeListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.rosterstore.DirectoryRosterStore;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.File;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 29.06.15.
 */
public class ConnectionEstablishedListener implements MapChangeListener<Account, XMPPTCPConnection> {

    private static final String WORKING_DIRECTORY = Utils.getConfigDirectory() + "/roster/";
    private final ContactModel contactModel;

    public ConnectionEstablishedListener(ContactModel contactModel) {
        this.contactModel = contactModel;
    }

    @Override
    public void onChanged(MapChangeListener.Change<? extends Account, ? extends XMPPTCPConnection> c) {
        if (c.wasAdded()) {
            final XMPPConnection connection = c.getValueAdded();
            final Account account = c.getKey();
            final Roster roster = Roster.getInstanceFor(connection);
            final String jid = account.getJid();
            setupRosterStore(WORKING_DIRECTORY + jid, roster);
            setupRosterEntriesSynchronisation(account, roster, contactModel);

        }
    }

    /**
     * Initializes or opens a {@link DirectoryRosterStore} for storing roster entries in a set of files as specified by
     * RFC 6121 and setups the given {@link Roster} to use it.
     *
     * @param path path to directory where data is/should be stored
     * @return
     */
    public static Roster setupRosterStore(String path, Roster roster) {
        final File dir = new File(path);
        final DirectoryRosterStore rosterStore;
        if (!dir.exists()) {
            dir.mkdirs();
            rosterStore =  DirectoryRosterStore.init(dir);
        } else {
            rosterStore = DirectoryRosterStore.open(dir);
        }
        roster.setRosterStore(rosterStore);
        return roster;
    }

    /**
     *
     * @param account
     * @param roster
     * @param contactModel
     */
    public static void setupRosterEntriesSynchronisation(Account account, Roster roster, ContactModel contactModel) {
        final RosterEntriesImporter rosterEntriesImporter = new RosterEntriesImporter(account, contactModel);
        final PalaverRosterListener palaverRosterListener = new PalaverRosterListener(account, contactModel, roster);
        contactModel.registerRoster(account, roster);
        roster.getEntriesAndAddListener(palaverRosterListener, rosterEntriesImporter);
    }

}
