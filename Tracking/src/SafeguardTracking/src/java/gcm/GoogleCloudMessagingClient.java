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
    private final String password = "AIzaSyA0YRZQ6ht3k-Mq08SVp2FRR_mEmszkvmI";
        
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
        
        if(!xmppClient.connection.isConnected()) {
         
            gcmClient = null;
            getGoogleCloudMessagingClient();
        }
       
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
    
    public void sendPhotoRequest(String gcmId, String token, long requestId) {
        
        String messageId = xmppClient.getRandomMessageId();
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("token", token);
        payload.put("operation", Operation.PHOTO_REQUEST.toString());
        payload.put("requestId", requestId + "");
        payload.put("EmbeddedMessageId", messageId);
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        xmppClient.send(createJsonMessage(gcmId, messageId, payload, collapseKey,
            timeToLive, delayWhileIdle));
    }
    
    public void sendCallRequest(String gcmId, String token, String phoneNumber) {
        
        String messageId = xmppClient.getRandomMessageId();
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("token", token);
        payload.put("operation", Operation.CALL_REQUEST.toString());
        payload.put("phoneNumber", phoneNumber);
        payload.put("EmbeddedMessageId", messageId);
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        xmppClient.send(createJsonMessage(gcmId, messageId, payload, collapseKey,
            timeToLive, delayWhileIdle));
    }
    
    public void sendPokeRequest(String gcmId, String token, long requestId) {
        
        String messageId = xmppClient.getRandomMessageId();
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("token", token);
        payload.put("operation", Operation.POKE_REQUEST.toString());
        payload.put("requestId", requestId + "");
        payload.put("EmbeddedMessageId", messageId);
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        xmppClient.send(createJsonMessage(gcmId, messageId, payload, collapseKey,
            timeToLive, delayWhileIdle));
    }
}
