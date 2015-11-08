/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gcm;

import static gcm.SmackCcsClient.createJsonMessage;
import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.XMPPException;
import utils.Operation;

/**
 *
 * @author luis
 */
public class GoogleCloudMessagingClient {
    
    private SmackCcsClient xmppClient = null;
    private static GoogleCloudMessagingClient gcmClient = null;
    private final String userName = "873747395138" + "@gcm.googleapis.com";
    private final String password = "AIzaSyBQl6E10lfGdyJEDnHigthMr7OJDQ3IiGc";
        
    private GoogleCloudMessagingClient() {
        
        xmppClient = new SmackCcsClient();

        try {
          xmppClient.connect(userName, password);
        } catch (XMPPException e) {
          e.printStackTrace();
        }
    }
    
    public static GoogleCloudMessagingClient getGoogleCloudMessagingClient() {
        
        if(gcmClient == null) {
            
            gcmClient = new GoogleCloudMessagingClient();
        }
        
        return gcmClient;
    }
    
    public void sendToken(String gcmId, String token) {
        
        String messageId = xmppClient.getRandomMessageId();
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("operation", Operation.TOKEN_OFFER.toString());
        payload.put("token", token);
        payload.put("EmbeddedMessageId", messageId);
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        xmppClient.send(createJsonMessage(gcmId, messageId, payload, collapseKey,
            timeToLive, delayWhileIdle));
    }
    
    public void sendLock(String gcmId, String token) {
        
        String messageId = xmppClient.getRandomMessageId();
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("token", token);
        payload.put("operation", Operation.LOCK.toString());
        payload.put("EmbeddedMessageId", messageId);
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        xmppClient.send(createJsonMessage(gcmId, messageId, payload, collapseKey,
            timeToLive, delayWhileIdle));
    }
    
    public void sendUnlock(String gcmId, String token) {
        
        String messageId = xmppClient.getRandomMessageId();
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("token", token);
        payload.put("operation", Operation.UNLOCK.toString());
        payload.put("EmbeddedMessageId", messageId);
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        xmppClient.send(createJsonMessage(gcmId, messageId, payload, collapseKey,
            timeToLive, delayWhileIdle));
    }
}
