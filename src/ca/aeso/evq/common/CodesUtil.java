package ca.aeso.evq.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * CodesUtil
 * A static HashMap wrapper class
 * @author mbodor
 */
public class CodesUtil {

	private static HashMap codeMap = null;
	private static List codeList = null;

	public static void addCode(String value, String desc) {
		if (codeMap==null)
			codeMap = new HashMap();
		
		codeMap.put(value, desc);
	}
	
	public static String getCodeDesc(String value) {

		Object code = codeMap.get(value);
		if (code==null)
			return value;
		else
			return (String)codeMap.get(value);
		
	}

	public static HashMap getCodeMap() {
		return codeMap;
	}

	public static List getCodeList() {
		return codeList;
	}
	
	public static void setCodeMap(HashMap codes) {
		codeMap = codes;
	}

	/**
	 * Resets the code list and map
	 * @param codes
	 */
	public static void setCodes(List codes) {
		codeList = codes;
		
    	if (codeList!=null) {
			codeMap = null;
    		
    		for (Iterator iterator = codeList.iterator(); iterator.hasNext();) {
    			String[] entry = (String[]) iterator.next();
    			addCode(entry[1], entry[0]);
    		}
    	}
		
	}
	
}
