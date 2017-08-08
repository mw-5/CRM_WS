package ws;

import java.util.List;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Customer;
import org.junit.Test;
import static org.junit.Assert.*;

public class CustomerControllerIT 
{
    // server has to run for tests to pass
    private final String baseUri = "http://localhost:8080/CRM_WS/ws/customer";    
    
    private int insertTestData(int id)
    {
        return TestCustomer.insertTestCustomer(id);
    }
    
    private void deleteTestData(int id)
    {
        TestCustomer.deleteTestCustomer(id);
    }
    
    private Customer getCustomer(int id)
    {
        return TestCustomer.getCustomer(id);
    }
    
    private int getNewId()
    {
        return TestCustomer.getNewCid();
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
    public void testCreateCustomer()
    {
        int id = getNewId();
        insertTestData(id);
        Customer c = getCustomer(id);
        int cid = c.getCid();
        int expResult = id;
        
        assertEquals(expResult, cid);
        
        deleteTestData(id);
    }
    
    @Test
    public void testDeleteCustomer()
    {
        int id = getNewId();
        insertTestData(id);
        deleteTestData(id);
        Customer c = getCustomer(id);
        
        assertEquals(null, c);
    }
    
    @Test
    public void testGetCustomer()
    {
        int id = getNewId();
        insertTestData(id);
        Customer customer = getCustomer(id);
        
        assertEquals(id, customer.getCid().intValue());
        
        deleteTestData(id);
    }
    
    @Test
    public void testGetAllCustomers()
    {
        int id1 = getNewId();
        insertTestData(id1);
        int id2 = getNewId();
        insertTestData(id2);
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        List<Customer> list = invocationBuilder.get(new GenericType<List<Customer>>(){});
        
        assertTrue(list.size() >= 2);
        
        deleteTestData(id1);
        deleteTestData(id2);
    }    
    
    @Test
    public void testUpdateCustomer()
    {
        int id = getNewId();
        insertTestData(id);
        
        Customer customer = getCustomer(id);
        customer.setCompany("testUpdated");
        
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.put(Entity.entity(customer, MediaType.APPLICATION_XML));
        
        customer = getCustomer(id);
        
        assertEquals("testUpdated", customer.getCompany());
        
        deleteTestData(id);        
    }
    
}
