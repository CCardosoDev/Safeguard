/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package device;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author luis
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(device.RegistrationWS.class);
        resources.add(location.LocationWS.class);
        resources.add(locking.LockingWSResource.class);
        resources.add(photo.PhotoWS.class);
        resources.add(poking.PokingWS.class);
        resources.add(telephony.TelephonyWS.class);
    }
    
}
