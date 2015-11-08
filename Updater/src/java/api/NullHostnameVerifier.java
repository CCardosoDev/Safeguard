package api;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 *
 * @author claudia
 */
public class NullHostnameVerifier implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
