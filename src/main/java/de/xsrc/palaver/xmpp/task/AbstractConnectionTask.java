package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import org.datafx.concurrent.DataFxTask;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 27.06.15.
 */
public abstract class AbstractConnectionTask<V> extends DataFxTask<V> {

    public static XMPPTCPConnectionConfiguration.Builder getConfigurationBuilder(String serviceName) throws ConnectionFailedException {
        final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        SSLContext context;
        try {
            context = getContext();
            builder.setCustomSSLContext(context);
            builder.setServiceName(serviceName);
            builder.setResource("palaver");
            return builder;
        } catch (SSLException e) {
            throw new ConnectionFailedException(e.getMessage(), e);
        }


    }

    protected static SSLContext getContext() throws SSLException {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext
                    .init(null, getTrustManager(), new java.security.SecureRandom());

            return sslContext;
        } catch (Exception e) {
            throw new SSLException("Error creating context for SSLSocket!", e);
        }
    }

    /**
     * Create a trust manager that trusts all certificates It is not using a
     * particular keyStore
     */
    protected static TrustManager[] getTrustManager() {

        return new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};
    }


}
