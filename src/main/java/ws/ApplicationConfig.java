package ws;

import java.util.Set;
import javax.ws.rs.core.Application;


@javax.ws.rs.ApplicationPath("ws")
public class ApplicationConfig extends Application 
{       
    @Override
    public Set<Class<?>> getClasses() 
    {
        Set<Class<?>> resources = new java.util.HashSet<>();
        
        resources.add(org.glassfish.jersey.jackson.JacksonFeature.class);
        
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) 
    {
        resources.add(ws.ContactPersonController.class);
        resources.add(ws.CustomerController.class);    
        resources.add(ws.JacksonJsonProvider.class);
        resources.add(ws.NoteController.class);
    }    
}
