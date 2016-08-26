package ca.aeso.evq.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Customized SuggestOracle to examine the starting characters of the item List.
 * Also takes a filter string to narrow the results
 * 
 * @author mbodor
 *
 */
public class StartsWithSuggestOracle extends SuggestOracle {

	ArrayList items = new ArrayList();

	public StartsWithSuggestOracle()
	{
	}

	/**
	 * Wrapper class that's used for each matching Suggestion returned.
	 */
	public static class StartsWithSuggestion implements Suggestion,	IsSerializable
	{
		private String _value;
		private String _displayString;

		/**
		 * Constructor used by RPC.
		 */
		public StartsWithSuggestion() { }

		/**
		 * Constructor for <code>StartsWithSuggestion</code>.
		 */
		public StartsWithSuggestion(String value, String
				displayString)
		{
			_value = value;
			_displayString = displayString;
		}

		public String getDisplayString()
		{
			return _displayString;
		}

		public Object getValue()
		{
			return _value;
		}
		
		public String getReplacementString()
		{
			return _value;
		}
		
	}

	/**
	 * Implementation of the sole abstract method in parent SuggestOracle.
	 * Determines which strings match the query string, populates a Response
	 * (which contains a List<StartsWithSuggestion>) and passes it to the
	 * supplied Callback
	 */
	public void requestSuggestions(Request request, Callback callback)
	{
		requestSuggestions(request, null, callback);
	}

	public void requestSuggestions(Request request, String filter, Callback callback)
	{
		String queryStr = request.getQuery();
		
		final List/* <StartsWithSuggestion */ suggestions =	computeItemsFor(queryStr, filter, request.getLimit());
		Response response = new Response(suggestions);
		callback.onSuggestionsReady(request, response);
	}
	
	/**
	 * Decides which strings match the supplied query; in this case, if the
	 * string starts with the query (case-insensitive)
	 */
	private List computeItemsFor(String query, String filter, int limit)
	{
		ArrayList filteredItems = new ArrayList();
		
		// first get all filtered items
		for (int i = 0; i < items.size(); i++) {
			String itemStr = (String)items.get(i);

			if (filter!=null && !filter.equals("")) {
				if (contains(itemStr.toLowerCase(), filter.toLowerCase())) {
					filteredItems.add(itemStr);
				}
			} else {
				filteredItems.add(itemStr);
			}
		}
		
		ArrayList/* <StartsWithSuggestion> */ matches = new ArrayList();

		// now get query matches
		for (int i = 0; i < filteredItems.size() && matches.size() < limit; i++) {
			String itemStr = (String)filteredItems.get(i);
			
			if (query!=null && query.length()>0) {
				if (itemStr.toLowerCase().startsWith(query)) {
					matches.add(new StartsWithSuggestion(itemStr, itemStr));
				}
			} else {
				// query is empty, add everything to the list of matches
				matches.add(new StartsWithSuggestion(itemStr, itemStr));
			}
		}
		return matches;
	}

	private boolean contains(String original, String filter) {
		if (filter.length() == 0) {
			return true;
		}
		if (filter.length() > original.length()) {
			return false;
		}
		for (int i = 0; i < original.length() - filter.length() + 1; i++) {
			if (original.charAt(i) == filter.charAt(0)) {
				boolean matches = true;
				for (int j = 0; j < filter.length(); j++) {
					if (original.charAt(i + j) != filter.charAt(j)) {
						matches = false;
						break;
					}
				}
				if (matches) {
					return true;
				}
			}
		}
		return false;
	}
	

	public void add(String suggestion)
	{
		items.add(suggestion);
	}

	public void addAll(Collection collection)
	{
		items.addAll(collection);
	}

	/**
	 * Specifies that the display string we're returning in the
	 * StartsWithSuggestion is HTML
	 */
	public boolean isDisplayStringHTML() {
		return true;
	}

} 	


