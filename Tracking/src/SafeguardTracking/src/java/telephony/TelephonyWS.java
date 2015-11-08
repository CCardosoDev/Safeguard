/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package telephony;

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
@Path("telephony")
public class TelephonyWS {

    @Context
    private UriInfo context;
    
    private GoogleCloudMessagingClient gcmClient;

    /**
     * Creates a new instance of TelephonyWS
     */
    public TelephonyWS() {
    }

    /**
     * Retrieves representation of an instance of telephony.TelephonyWS
     * @param token
     * @param androidId
     * @param phoneNumber
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/request/{token}/{androidId}/{phoneNumber}")
    @Produces("application/xml")
    public String requestCall(@PathParam("token") String token,
            @PathParam("androidId") String androidId,
            @PathParam("phoneNumber") String phoneNumber) {
        
        /* Verifica se existe um Device com o androidId fornecido */
        if(!DatabaseQueries.checkIfEntryExistsByAndroidId(androidId)) {
            
            return "<status>error</status>";
        }
        
        /* Envia mensagem de CALL_REQUEST ao Device */
        String gcmId;
        
        gcmId = DatabaseQueries.getGcmIdByAndroidId(androidId);
        
        if(gcmId == null) {
            
            return "<status>error</status>";
        }
        
        gcmClient = GoogleCloudMessagingClient.getGoogleCloudMessagingClient();
        
        gcmClient.sendCallRequest(gcmId, token, phoneNumber);
        
        return "<status>ok</status>";
    }
}
