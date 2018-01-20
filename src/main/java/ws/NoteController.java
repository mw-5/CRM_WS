package ws;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Customer;
import model.Note;
import org.slf4j.*;


@Stateless
@Path("note")
public class NoteController 
{
    Logger logger = LoggerFactory.getLogger(NoteController.class);
    
    @PersistenceContext(unitName = "CRM_WSPU")
    private EntityManager em;
    
    @Inject
    private ServletContext context;    
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Note getNote(@PathParam("id") Integer id)
    {
        return em.find(Note.class, id);
    }
    
    @GET
    @Path("cid/{cid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})    
    public Collection<Note> getNotes(@PathParam("cid") Integer cid)
    {
        Customer customer = em.find(Customer.class, cid);
        em.refresh(customer);
        return customer.getNotesCollection();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Note> getNotes()
    {
        return em.createNamedQuery("Note.findAll").getResultList();
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createNote(Note n)
    {
        Integer id = (Integer)em.createQuery("SELECT max(n.id) FROM Note n").getSingleResult();
        id++;
        n.setId(id);
        em.persist(n);
        return Response.ok(id).build();
    }
    
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response editNote(Note n)
    {
        em.merge(n);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("{id}")
    public void deleteNote(@PathParam("id") Integer id)
    {
        Note n = em.find(Note.class, id);
        em.remove(em.merge(n));
    }
    
    @GET
    @Path("newId")
    public int getNewId()
    {
        Integer id = (Integer)em.createQuery("SELECT max(n.id) FROM Note n").getSingleResult();
        return ++id;
    }        
            
    private String getCustomersFolder() throws IOException
    {
        Properties config = new Properties();        
        InputStream is = context.getResourceAsStream("/WEB-INF/config.properties");        
        config.load(is);
        String customersFolder = config.getProperty("customersFolder");                
        is.close();        
        return customersFolder;
    }
    
    /**
     * 
     * @param id Expects note id.
     * @return file as stream
     */
    @GET
    @Path("file/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("id") Integer id)
    {       
        String msg = null;
        File file = null;
        try
        {
            Note n = em.find(Note.class, id);
            file = Paths.get(getCustomersFolder(), n.getCid().getCid().toString(), n.getAttachment()).toFile();
        }
        catch(IOException e)
        {
            msg = e.getMessage();
        }
        if (msg == null && file != null)
        {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"") // optional
                    .header("fileName", file.getName())
                    .build();
        }
        else
        {
            return Response.status(404).build();
        }        
    }
    
    @PUT
    @Path("file")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void putFile(File file, @QueryParam("fileName") String fileName, @QueryParam("cid") Integer cid)
    {
        try
        {
            File dstFolder = Paths.get(getCustomersFolder(), cid.toString()).toFile();
            if (!dstFolder.exists())
            {
                dstFolder.mkdir();
            }
            File dstFile = Paths.get(getCustomersFolder(), cid.toString(), fileName).toFile();
            if (dstFile.exists())
            {
                dstFile.delete();
            }
            Files.copy(file.toPath(), dstFile.toPath());            
        }
        catch(IOException e)
        {
            System.out.println("Exception thrown: " + e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }
    
}
