package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.RosterEntriesImporter;
import javafx.collections.MapChangeListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.rosterstore.DirectoryRosterStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 29.06.15.
 */
public class ConnectionEstablishedListener implements MapChangeListener<Credentials, Connection> {

    private static final String WORKING_DIRECTORY = Utils.getConfigDirectory() + "/roster/";
    private final ContactModel contactModel;

    public ConnectionEstablishedListener(ContactModel contactModel) {
        this.contactModel = contactModel;
    }

    /**
     * Initializes or opens a {@link DirectoryRosterStore} for storing roster entries in a set of files as specified by
     * RFC 6121 and setups the given {@link Roster} to use it.
     *
     * @param path path to directory where data is/should be stored
     */
    private static void setupRosterStore(String path, Roster roster) throws IOException {
        final File dir = new File(path);
        final DirectoryRosterStore rosterStore;
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                rosterStore = DirectoryRosterStore.init(dir);
            } else {
                throw new IOException("Could not create roster directory");
            }
        } else {
            rosterStore = DirectoryRosterStore.open(dir);
        }
        roster.setRosterStore(rosterStore);
    }

    public static void setupRosterEntriesSynchronisation(Credentials credentials, Roster roster, ContactModel contactModel) {
        final RosterEntriesImporter rosterEntriesImporter = new RosterEntriesImporter(credentials, contactModel);
        final PalaverRosterListener palaverRosterListener = new PalaverRosterListener(credentials, contactModel, roster);
        contactModel.registerRoster(credentials, roster);
        roster.getEntriesAndAddListener(palaverRosterListener, rosterEntriesImporter);
    }

    @Override
    public void onChanged(MapChangeListener.Change<? extends Credentials, ? extends Connection> c) {
        if (c.wasAdded()) {
            final XMPPConnection connection = c.getValueAdded().xmpptcpConnection;
            final Credentials credentials = c.getKey();
            final Roster roster = Roster.getInstanceFor(connection);
            final String jid = credentials.getJid();
            try {
                setupRosterStore(WORKING_DIRECTORY + jid, roster);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setupRosterEntriesSynchronisation(credentials, roster, contactModel);

        }
    }

}
