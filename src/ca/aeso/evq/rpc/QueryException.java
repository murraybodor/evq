package ca.aeso.evq.rpc;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * QueryException
 * Represents an exception in Query processing
 * 
 * @author mbodor
 */
public class QueryException extends SerializableException {

	private static final long serialVersionUID = 3713131258099607589L;
	
	String message;
	
	public QueryException () {
	}

	public QueryException (String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
