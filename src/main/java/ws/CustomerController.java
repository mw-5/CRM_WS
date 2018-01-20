package ws;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Customer;
import org.slf4j.*;


@Stateless
@Path("customer")
public class CustomerController 
{
    Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
    @PersistenceContext(unitName = "CRM_WSPU")
    private EntityManager em;
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Customer getCustomer(@PathParam("id") Integer id)
    {
        return em.find(Customer.class, id);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Customer> getCustomers()
    {        
        // works independent of existence of named query
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Customer.class));
        return em.createQuery(cq).getResultList();
        // return em.createNamedQuery("Customer.findAll").getResultList();
    }
        
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createCustomer(Customer c)
    {
        System.err.println("create customer calles");
        logger.trace("create customer called");  // other severity levels e.g. are: .debug() or .info()
        Integer cid = (Integer)em.createQuery("SELECT max(c.cid) FROM Customer c").getSingleResult();
        cid++;
        c.setCid(cid);
        em.persist(c);
        return Response.ok(cid).build();
    }
    
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response editCustomer(Customer c)
    {
        System.err.println("editCustomer() called");
        logger.trace("editCustomer() called");
        em.merge(c);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("{id}")
    public void deleteCustomer(@PathParam("id") Integer id)
    {
        Customer c = em.find(Customer.class, id);
        em.remove(em.merge(c));
    }
    
    @GET
    @Path("newId")
    public int getNewId()
    {
        Integer cid = (Integer)em.createQuery("SELECT max(c.cid) FROM Customer c").getSingleResult();
        return ++cid;
    }        
}
