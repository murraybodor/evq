package ca.aeso.evq.server.service;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Transformable
 * Allows query results to be transformed in different ways
 * 
 * @author mbodor
 */
public interface Transformable {

    /**
     * Set the service to be used for any other resources required
     * @param service
     */
    public void setService(QueryService service);

    /**
	 * Transform operation
	 * @return the number of things transformed
	 */
    public int transform();
    
    /**
     * Get the unique identifier of the transformed object
     * @return the id
     */
    public long getTransformedIdentifier();
    
    /**
     * Stream the results of the transformation into an output stream
     * @param out the outputstream to write to
     * @throws IOException
     */
    public void streamResult(OutputStream out) throws IOException;
    
    /**
     * Get the type of the transformed result
     * @return the transformed result type
     */
    public String getResultType();
    
}
