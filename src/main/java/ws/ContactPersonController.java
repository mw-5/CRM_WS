package ws;

import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.ContactPerson;
import model.Customer;

@Stateless
@Path("contactPerson")
public class ContactPersonController 
{
    @PersistenceContext(unitName = "CRM_WSPU")
    private EntityManager em;
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ContactPerson getContactPerson(@PathParam("id") Integer id)
    {
        return em.find(ContactPerson.class, id);
    }
    
    @GET
    @Path("cid/{cid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<ContactPerson> getContactPersons(@PathParam("cid") Integer cid)
    {
        Customer customer = em.find(Customer.class, cid);
        em.refresh(customer);
        return customer.getContactPersonsCollection();
    }        
            
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ContactPerson> getContactPersons()
    {      
        return em.createNamedQuery("ContactPerson.findAll").getResultList();
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createContactPerson(ContactPerson c)
    {
        Integer id = (Integer)em.createQuery("SELECT max(c.id) FROM ContactPerson c").getSingleResult();
        id++;
        c.setId(id);
        em.persist(c);
        return Response.ok(id).build();
    }
    
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response editContactPerson(ContactPerson cp)
    {
        em.merge(cp);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("{id}")
    public void deleteContactPerson(@PathParam("id") Integer id)
    {
        ContactPerson c = em.find(ContactPerson.class, id);
        em.remove(em.merge(c));
    }
    
    @GET
    @Path("newId")
    public int getNewId()
    {
        Integer id = (Integer)em.createQuery("SELECT max(c.id) FROM ContactPerson c").getSingleResult();
        return ++id;
    }        
}
