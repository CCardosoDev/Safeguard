/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package locking;

import gcm.GoogleCloudMessagingClient;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
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
public class LockingResource {

    @Context
    private UriInfo context;
    
    private GoogleCloudMessagingClient gcmClient;

    /**
     * Creates a new instance of LockingResource
     */
    public LockingResource() {
    }

    /**
     * @Retrieves representation of an instance of locking.LockingResource
     * @return an instance of java.lang.String
     */
    @Path("lock/{token}")
    @GET
    @Produces("application/xml")
    public String lockDevice(@PathParam("token") String token) {
        
        /* Verifica se existe um Device com o token fornecido */
        if(!DatabaseQueries.checkIfEntryExistsByToken(token)) {
            
            return "<status>error</status>";
        }
        
        /* Envia mensagem de lock ao Device */
        String gcmId;
        
        gcmId = DatabaseQueries.getGcmIdByToken(token);
        
        if(gcmId == null) {
            
            return "<status>error</status>";
        }
        
        gcmClient = GoogleCloudMessagingClient.getGoogleCloudMessagingClient();
        
        gcmClient.sendLock(gcmId, token);
        
        return "<status>ok</status>";
    }

    @Path("unlock/{token}")
    @GET
    @Produces("application/xml")
    public String unlockDevice(@PathParam("token") String token) {
        
        /* Verifica se existe um Device com o token fornecido */
        if(!DatabaseQueries.checkIfEntryExistsByToken(token)) {
            
            return "<status>error</status>";
        }
        
        /* Envia mensagem de lock ao Device */
        String gcmId;
        
        gcmId = DatabaseQueries.getGcmIdByToken(token);
        
        if(gcmId == null) {
            
            return "<status>error</status>";
        }
        
        gcmClient = GoogleCloudMessagingClient.getGoogleCloudMessagingClient();
        
        gcmClient.sendUnlock(gcmId, token);
        
        return "<status>ok</status>";
    }
}
