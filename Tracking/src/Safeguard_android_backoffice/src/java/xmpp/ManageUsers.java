/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmpp;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import utils.Globals;
import utils.RESTHandler;

/**
 * REST Web Service
 *
 * @author luis
 */
@Path("XMPP")
public class ManageUsers {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ManageUsers
     */
    public ManageUsers() {
    }

    /**
     * Retrieves representation of an instance of xmpp.ManageUsers
     * @return an instance of java.lang.String
     */
    @Path("createXmppServiceUser/{username}/{password}")
    @GET
    @Produces("application/xml")
    public String createXmppServiceUser(@PathParam("username") String username,
                                            @PathParam("password") String password) {
        
        String secret = Globals.XMPP_SERVER_SECRET;
        String result;
        
        result = RESTHandler.getHTML("http://" + Globals.XMPP_SERVER_ADDRESS +
                                        ":9090/plugins/userService/userservice?" + 
                                        "type=add&secret=" + secret + "&" + 
                                        "username=" + username + "&" +
                                        "password=" + password);
        
        return result;
    }

    
    @Path("deleteXmppServiceUser/{username}")
    @GET
    @Produces("application/xml")
    public String deleteXmppServiceUser(@PathParam("username") String username) {
        
        String secret = Globals.XMPP_SERVER_SECRET;
        String result;
        
        result = RESTHandler.getHTML("http://" + Globals.XMPP_SERVER_ADDRESS +
                                        ":9090/plugins/userService/userservice?" + 
                                        "type=delete&secret=" + secret + "&" + 
                                        "username=" + username);
        
        return result;
    }
    
    /**
     * PUT method for updating or creating an instance of ManageUsers
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
