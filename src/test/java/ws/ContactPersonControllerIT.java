package ws;

import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.ContactPerson;
import model.Customer;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class ContactPersonControllerIT 
{
    // server has to run for tests to pass
    private final String baseUri = "http://localhost:8080/CRM_WS/ws/contactPerson";              
    
    private Customer customer = null;
    
    @Before
    public void createTestCustomer()
    {
        int cid = TestCustomer.getNewCid();
        TestCustomer.insertTestCustomer(cid);
        customer = TestCustomer.getCustomer(cid);
    }
    
    @After
    public void deleteTestCustomer()
    {
        TestCustomer.deleteTestCustomer(customer.getCid());
        customer = null;
    }
    
    private int insertTestData(int id)
    {                
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(baseUri);
        
        ContactPerson c = new ContactPerson();
        c.setId(id);  
        c.setCid(customer);
        c.setForename("testForename");
        c.setSurname("testSurname");
        
        System.out.println("create contact person");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.post(Entity.entity(c, MediaType.APPLICATION_XML));
        System.out.println(response.toString());
                
        return id;
    }
    
    private void deleteTestData(int id)
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/" + id);
        System.out.println("delete contact person");
        Invocation.Builder invocationBuilder = webTarget.request();
        Response response = invocationBuilder.delete();
        System.out.println(response.getStatus());
    }
    
    private ContactPerson getContactPerson(int id)
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/" + id);
        System.out.println("get contact person");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        return invocationBuilder.get(ContactPerson.class);
    }
    
    private int getNewId()
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/newId");
        System.out.println("get new id");
        Invocation.Builder invocationBuilder = webTarget.request();
        Response response = invocationBuilder.get();
        return response.readEntity(Integer.class);
    }
    
    @Test
    public void testNewId()
    {
        int id1 = getNewId();
        insertTestData(id1);
        
        int id2 = getNewId();
        deleteTestData(id1);
        
        assertTrue(id1 > 0);
        assertTrue(id2 == (id1 + 1));                
    }
    
    @Test
    public void testCreateContactPerson()
    {
        int id = getNewId();
        insertTestData(id);
        ContactPerson c = getContactPerson(id);
        int _id = c.getId();
        int expResult = id;
        
        assertEquals(expResult, _id);
        
        deleteTestData(id);
    }
    
    @Test
    public void testDeleteContactPerson()
    {
        int id = getNewId();
        insertTestData(id);
        deleteTestData(id);
        ContactPerson c = getContactPerson(id);
        
        assertEquals(null, c);
    }
    
    @Test
    public void testUpdateContactPerson()
    {
        int id = getNewId();
        insertTestData(id);
        ContactPerson cp = getContactPerson(id);
        cp.setForename("testUpdate");
        
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.put(Entity.entity(cp, MediaType.APPLICATION_XML));
        System.out.println(response.toString());
        
        cp = getContactPerson(id);
        
        assertEquals("testUpdate", cp.getForename());
        
        deleteTestData(id);        
    }
    
    @Test
    public void testGetContactPerson()
    {
        int id = getNewId();
        insertTestData(id);
        ContactPerson cp = getContactPerson(id);
        
        assertNotEquals(null, cp);
        
        deleteTestData(id);
    }
    
    @Test
    public void testGetAllContactPersons()
    {
        int id1 = getNewId();
        insertTestData(id1);
        int id2 = getNewId();
        insertTestData(id2);
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        List<ContactPerson> list = invocationBuilder.get(new GenericType<List<ContactPerson>>(){});
        
        assertTrue(list.size() >= 2);
        
        deleteTestData(id1);
        deleteTestData(id2);
    }    
    
    @Test
    public void testGetAllContactPersonsForCid()
    {
        int id1 = getNewId();
        insertTestData(id1);
        int id2 = getNewId();
        insertTestData(id2);
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/cid/" + customer.getCid());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        List<ContactPerson> list = invocationBuilder.get(new GenericType<List<ContactPerson>>(){});
        
        assertTrue(list.size() >= 2);
        
        deleteTestData(id1);
        deleteTestData(id2);
    }
    
}
