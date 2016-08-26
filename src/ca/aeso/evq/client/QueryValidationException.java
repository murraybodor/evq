package ca.aeso.evq.client;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * QueryValidationException
 * Generic Exception used in Query processing
 * 
 * @author mbodor
 */
public class QueryValidationException extends SerializableException {

	private static final long serialVersionUID = 3443131258099607586L;
	
    /**
     * @gwt.typeArgs <java.lang.String>
     */
	private List errors;
	
	public static final String NEWLINE = "\r\n";
	
	public QueryValidationException () {
	}

	public QueryValidationException (List errors) {
		this.errors = errors;
	}

	public String getMessage() {
		StringBuffer buf = new StringBuffer("Your query has the following errors:");
		buf.append(NEWLINE);
		buf.append(NEWLINE);

		for (Iterator iterator = errors.iterator(); iterator.hasNext();) {
			String error = (String) iterator.next();
			buf.append(error);
			buf.append(NEWLINE);
		}
		
		return buf.toString();
	}
}
