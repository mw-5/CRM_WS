package ws;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Customer;


public class TestCustomer 
{
    private static final String baseUri = "http://localhost:8080/CRM_WS/ws/customer";    
    
    public static int insertTestCustomer(int cid)
    {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(baseUri);
        
        Customer c = new Customer();
        c.setCid(cid);
        c.setCity("testCity");
        c.setCompany("testCompany");
        
        System.out.println("create customer");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.post(Entity.entity(c, MediaType.APPLICATION_XML));
        System.out.println(response.toString());
                
        return cid;
    }
    
    public static void deleteTestCustomer(int cid)
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/" + cid);
        System.out.println("delete customer");
        Invocation.Builder invocationBuilder = webTarget.request();
        Response response = invocationBuilder.delete();
        System.out.println(response.getStatus());
    }
    
    public static Customer getCustomer(int cid)
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/" + cid);
        System.out.println("get customer");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        return invocationBuilder.get(Customer.class);
    }
    
    public static int getNewCid()
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/newId");
        System.out.println("get new id");
        Invocation.Builder invocationBuilder = webTarget.request();
        Response response = invocationBuilder.get();
        return response.readEntity(Integer.class);
    }
}
