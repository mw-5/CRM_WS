package ws;
 

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Provider
public class JacksonJsonProvider implements ContextResolver<ObjectMapper> 
{	
    private static final ObjectMapper mapper = new ObjectMapper();
    
    static 
    {
      mapper.setSerializationInclusion(Include.NON_EMPTY);
      mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);      
    }
     
    @Override
    public ObjectMapper getContext(Class<?> type) 
    {
        return mapper;
    } 
}