package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.ConnectionListener;
import de.xsrc.palaver.xmpp.RosterManager;
import javafx.collections.MapChangeListener;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 29.06.15.
 */
public class RosterSynchronisationListener implements ConnectionListener {

    private final RosterManager rosterManager;

    public RosterSynchronisationListener(RosterManager rosterManager) {
        this.rosterManager = rosterManager;
    }

    @Override
    public void onChanged(MapChangeListener.Change<? extends String, ? extends Connection> c) {
        if (c.wasAdded()) {
            rosterManager.registerConnection(c.getValueAdded());
        }
    }
}
