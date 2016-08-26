package ca.aeso.evq.common;

import java.util.HashMap;

/**
 * Represents a type of Date query
 * @author mbodor
 */
public class DateQueryType {

	public static HashMap DATE_QUERY_TYPES = new HashMap();

	private String value;
	private String description;
	
	private DateQueryType(String value, String desc) {
		this.value = value;
		this.description = desc;
		DATE_QUERY_TYPES.put(value, desc);
	}
	
	public static final DateQueryType DATE_QUERY_TYPE_SPECIFIC = new DateQueryType("SP", "Specific");
	public static final DateQueryType DATE_QUERY_TYPE_CALENDAR = new DateQueryType("CA", "Calendar");
	public static final DateQueryType DATE_QUERY_TYPE_TWO_SEASONS = new DateQueryType("TW", "Two Seasons");
	public static final DateQueryType DATE_QUERY_TYPE_FOUR_SEASONS = new DateQueryType("FO", "Four Seasons");

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean equals(String aQueryType) {
		if (aQueryType.equals(this.getValue()))
			return true;
		else
			return false;
	}
}
