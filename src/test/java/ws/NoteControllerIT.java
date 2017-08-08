package ws;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import model.Customer;
import model.Note;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class NoteControllerIT 
{
    private final String baseUri = "http://localhost:8080/CRM_WS/ws/note";

    private final String pathDst = "D:\\CRM\\Customers\\";
    private final String pathSrc = "D:\\CRM\\Customers\\testSrc\\";
                
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
        
        Note n = new Note();
        n.setId(id);  
        n.setCid(customer);
        n.setMemo("testMemo");
        n.setCreatedBy("testUser");
        n.setEntryDate(Calendar.getInstance().getTime());
        n.setAttachment("test.pdf");
        
        System.out.println("create note");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.post(Entity.entity(n, MediaType.APPLICATION_XML));
        System.out.println(response.toString());
                
        return id;
    }
    
    private void deleteTestData(int id)
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/" + id);
        System.out.println("delete note");
        Invocation.Builder invocationBuilder = webTarget.request();
        Response response = invocationBuilder.delete();
        System.out.println(response.getStatus());
    }
    
    private Note getNote(int id)
    {
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/" + id);
        System.out.println("get note");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        return invocationBuilder.get(Note.class);
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
    public void testGetNote()
    {
        int id = getNewId();
        insertTestData(id);
        Note n = getNote(id);
        
        assertNotEquals(null, n);
        
        deleteTestData(id);
    }

    @Test
    public void testGetAllNotesForCid()
    {
        int id1 = getNewId();
        insertTestData(id1);
        int id2 = getNewId();
        insertTestData(id2);
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/cid/" + customer.getCid());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        List<Note> list = invocationBuilder.get(new GenericType<List<Note>>(){});
        
        assertTrue(list.size() >= 2);
        
        deleteTestData(id1);
        deleteTestData(id2);
    }

    @Test
    public void testGetAllNotes()
    {
        int id1 = getNewId();
        insertTestData(id1);
        int id2 = getNewId();
        insertTestData(id2);
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        List<Note> list = invocationBuilder.get(new GenericType<List<Note>>(){});
        
        assertTrue(list.size() >= 2);
        
        deleteTestData(id1);
        deleteTestData(id2);
    }

    @Test
    public void testCreateNote()
    {
        int id = getNewId();
        insertTestData(id);
        Note n = getNote(id);
        int _id = n.getId();
        int expResult = id;
        
        assertEquals(expResult, _id);
        
        deleteTestData(id);
    }

    @Test
    public void testEditNote()
    {
        int id = getNewId();
        insertTestData(id);
        Note n = getNote(id);
        n.setMemo("testUpdate");
        
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.put(Entity.entity(n, MediaType.APPLICATION_XML));
        System.out.println(response.toString());
        
        n = getNote(id);
        
        assertEquals("testUpdate", n.getMemo());
        
        deleteTestData(id);     
    }

    @Test
    public void testDeleteNote()
    {
        int id = getNewId();
        insertTestData(id);
        deleteTestData(id);
        Note n = getNote(id);
        
        assertEquals(null, n);
    }

    @Test
    public void testGetNewId()
    {
        int id1 = getNewId();
        insertTestData(id1);
        
        int id2 = getNewId();
        deleteTestData(id1);
        
        assertTrue(id1 > 0);
        assertTrue(id2 == (id1 + 1));         
    }

    @Test
    public void testUploadFile()
    {                
        // delete file at destination if exists
        File file = new File(pathDst + customer.getCid() + "\\test.pdf");
        if (file.exists())
        {
            file.delete();
        }
        
        file = new File(pathSrc + "test.pdf");
        
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/file");
        webTarget = webTarget.queryParam("fileName", "test.pdf");
        webTarget = webTarget.queryParam("cid", customer.getCid());
        
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_OCTET_STREAM);
        Response response = invocationBuilder.put(Entity.entity(file, MediaType.APPLICATION_OCTET_STREAM));
        
        System.out.println(response.getStatus());
        file = new File(pathDst + customer.getCid() + "\\test.pdf");
        
        assertTrue(file.exists());
        
        // clean up: delete file at destination and folder
        file = new File(pathDst + customer.getCid() + "\\test.pdf");
        if (file.exists())
        {
            file.delete();
        }
        file = new File(pathDst + customer.getCid());
        if (file.exists())
        {
            file.delete();
        }
    }
    
    @Test
    public void testDownloadFile() throws Exception
    {        
        int id = getNewId();
        insertTestData(id);
        
        // create test resource at server-side
        File srvFile = new File(pathDst + customer.getCid());
        srvFile.mkdirs();
        srvFile = new File(pathDst + customer.getCid() + "\\test.pdf");
        File srcFile = new File(pathSrc + "test.pdf");
        Files.copy(srcFile.toPath(), srvFile.toPath());
        
        WebTarget webTarget = ClientBuilder.newClient().target(baseUri + "/file/" + id);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_OCTET_STREAM);
        Response response = invocationBuilder.get();
        File file = response.readEntity(File.class);
        System.out.println("file: " + file);
        MultivaluedMap<String, Object> map = response.getHeaders();
        String fileName = (String)map.get("fileName").get(0);
        System.out.println(fileName);
        File dstFile = new File(pathDst + "testDownload\\" + fileName);
        if (dstFile.exists())
        {
            dstFile.delete();
        }
        try
        {
            Files.copy(file.toPath(), dstFile.toPath());
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        
        assertTrue(file != null);
        
        // clean up: delete test resource at server-side
        srvFile.delete();
        srvFile = new File(pathDst + customer.getCid());
        srvFile.delete();
        
        deleteTestData(id);
    }
    
}
