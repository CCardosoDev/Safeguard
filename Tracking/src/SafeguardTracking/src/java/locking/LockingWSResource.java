/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package locking;

import gcm.GoogleCloudMessagingClient;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import utils.DatabaseQueries;

/**
 * REST Web Service
 *
 * @author luis
 */
@Path("locking")
public class LockingWSResource {

    @Context
    private UriInfo context;
    
    private GoogleCloudMessagingClient gcmClient;

    /**
     * Creates a new instance of LockingWSResource
     */
    public LockingWSResource() {
    }

    
    @Path("lock/{token}/{androidId}")
    @GET
    @Produces("application/xml")
    public String lockDevice(@PathParam("token") String token,
                                @PathParam("androidId") String androidId) {
        
        /* Verifica se existe um Device com o androidId fornecido */
        if(!DatabaseQueries.checkIfEntryExistsByAndroidId(androidId)) {
            
            return "<status>error</status>";
        }
        
        /* Envia mensagem de lock ao Device */
        String gcmId;
        
        gcmId = DatabaseQueries.getGcmIdByAndroidId(androidId);
        
        if(gcmId == null) {
            
            return "<status>error</status>";
        }
        
        gcmClient = GoogleCloudMessagingClient.getGoogleCloudMessagingClient();
        
        gcmClient.sendLock(gcmId, token);
        
        return "<status>ok</status>";
    }

    @Path("unlock/{token}/{androidId}")
    @GET
    @Produces("application/xml")
    public String unlockDevice(@PathParam("token") String token,
                                @PathParam("androidId") String androidId) {
        
        /* Verifica se existe um Device com o androidId fornecido */
        if(!DatabaseQueries.checkIfEntryExistsByAndroidId(androidId)) {
            
            return "<status>error</status>";
        }
        
        /* Envia mensagem de lock ao Device */
        String gcmId;
        
        gcmId = DatabaseQueries.getGcmIdByAndroidId(androidId);
        
        if(gcmId == null) {
            
            return "<status>error</status>";
        }
        
        gcmClient = GoogleCloudMessagingClient.getGoogleCloudMessagingClient();
        
        gcmClient.sendUnlock(gcmId, token);
        
        return "<status>ok</status>";
    }
    
}
