package de.xsrc.palaver.xmpp;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class ConnectionManager extends ConcurrentHashMap<String, XMPPTCPConnection> {
}
