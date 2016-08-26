package ca.aeso.evq.server.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.aeso.evq.server.util.Constants;

/**
 * Represents an SQL query 
 * 
 * @author mbodor
 */
public class SqlQuery {

	private String tableName;
	private String tableIdentifier;
	private String joinTableName;
	private String joinTableIdentifier;
	private List selects = new ArrayList();
	private List partitionColumns = new ArrayList();
	private List wheres = new ArrayList();
	private List groupBys = new ArrayList();
	private List orderBys = new ArrayList();
	private SqlQuery innerQuery;
	
	/**
	 * Constructor
	 * @param tableIdentifier the table name shorthand
	 */
	public SqlQuery (String tableIdentifier) {
		this.tableIdentifier = tableIdentifier;
	}
	
	/**
	 * Transform the SQL Query to a String
	 * @return executable SQL
	 */
	public String toString() {
		
		StringBuffer sql = new StringBuffer("");
		
		// select
		sql.append("SELECT ");
		boolean firstSelect = true;
		boolean firstGroupBy = true;
		boolean firstOrderBy = true;
		
		for (Iterator iterator = selects.iterator(); iterator.hasNext();) {
			String[] select = (String[]) iterator.next();
			if (!firstSelect) {
				sql.append(" , ");
			}

			// table identifier
			if (select[0]!=null) {
				sql.append(select[0]);
				sql.append(".");
			}
			
			// column name
			if (select[1]!=null)
				sql.append(select[1]);
			else
				sql.append("null");
				

			// as
			if (select[2]!=null) {
				sql.append(" as ");
				sql.append(select[2]);
			}
				
			firstSelect = false;
		}
		

		// from
		sql.append(" FROM ( ");
		
		if (innerQuery!=null) {
			sql.append(innerQuery.toString());
		} else {
			sql.append(tableName);
		}

		sql.append(" ) ");
		sql.append(tableIdentifier);
		
		// join
		if (joinTableName!=null) {
			sql.append(" , ");
			sql.append(joinTableName);
			sql.append(" ");
			sql.append(joinTableIdentifier);
		}
		
		
		// where
		if (wheres!=null && wheres.size()>0) {
			sql.append(" WHERE 1=1 ");
			for (Iterator iterator = wheres.iterator(); iterator.hasNext();) {
				String whereClause = (String) iterator.next();
				sql.append(" AND ");
				sql.append(whereClause);
			}
		}
		
		// group by
		if (groupBys!=null && groupBys.size()>0) {
			sql.append(" GROUP BY ");
			for (Iterator iterator = groupBys.iterator(); iterator.hasNext();) {
				String groupClause = (String) iterator.next();
				if (!firstGroupBy) {
					sql.append(" , ");
				}
				sql.append(groupClause);
				firstGroupBy = false;
			}
			
		}
		
		// order by
		if (orderBys!=null && orderBys.size()>0) {
			sql.append(" ORDER BY ");
			for (Iterator iterator = orderBys.iterator(); iterator.hasNext();) {
				String orderClause = (String) iterator.next();
				if (!firstOrderBy) {
					sql.append(" , ");
				}
				sql.append(orderClause);
				firstOrderBy = false;
			}
			
		}
		
		return sql.toString();
	}
	
	
	public List getSelects() {
		return selects;
	}

	public void setSelects(List selects) {
		this.selects = selects;
	}

	/**
	 * Add a joined table column to the list of columns selected
	 * @param columnName
	 * @param addGroupBy
	 * @param addOrderBy
	 */
	public void addJoinColumnSelect(String columnName, boolean addGroupBy, boolean addOrderBy) {
		addSelect(joinTableIdentifier, columnName, columnName, addGroupBy, addOrderBy);
	}

	public void addJoinColumnsSelect(List columnNames, String timeInterval, boolean addGroupBy, boolean addOrderBy) {
		for (Iterator iterator = columnNames.iterator(); iterator.hasNext();) {
			String columnName = (String) iterator.next();
			
			
			if (columnName.equals(Constants.CAL_DAY_DATE) || columnName.equals(Constants.CAL_HOUR_ENDING)) {
				if (timeInterval!=null && timeInterval.equals("DA")) { // add these columns to order by for Daily interval only
					addSelect(joinTableIdentifier, columnName, columnName, addGroupBy, true);
				} else {
					addSelect(joinTableIdentifier, columnName, columnName, addGroupBy, false);
				}
			} else {
				addSelect(joinTableIdentifier, columnName, columnName, addGroupBy, addOrderBy);
			}
				
		}
	}
	
	public void addColumnSelect(String columnName, boolean addGroupBy, boolean addOrderBy) {
		addSelect(tableIdentifier, columnName, columnName, addGroupBy, addOrderBy);
	}

	public void addColumnSelects(List columns, boolean addGroupBy, boolean addOrderBy) {
		for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			String columnName = (String) iterator.next();
			addSelect(tableIdentifier, columnName, columnName, addGroupBy, addOrderBy);
		}
	}
	
	/**
	 * Add a column to the selected columns list
	 * @param tablePrefix
	 * @param columnName
	 * @param columnAs
	 * @param addGroupBy
	 * @param addOrderBy
	 */
	public void addSelect(String tablePrefix, String columnName, String columnAs, boolean addGroupBy, boolean addOrderBy) {

		String[] select = new String[] {null, null, null};
		
		if (tablePrefix!=null) {
			select[0] = tablePrefix;
		}
		
		select[1] = columnName;
		
		if (columnAs!=null) {
			select[2] = columnAs;
		}
		
		this.selects.add(select);
		
		if (columnName!=null) {
			if (addGroupBy)
				this.addGroupBy(tablePrefix + "." + columnName);
			
			if (addOrderBy)
				this.addOrderBy(tablePrefix + "." + columnName);
		}
		
	}

	/**
	 * Add a non-column select clause
	 * @param selectClause
	 * @param columnAs
	 */
	public void addOtherSelect(String selectClause, String columnAs) {
		addSelect(null, selectClause, columnAs, false, false);
	}
	
	public void addOtherSelect(String[] selectColumnAsArray) {
		addOtherSelect(selectColumnAsArray[0], selectColumnAsArray[1]);
	}

	public void addOtherSelects(List otherSelects) {
		for (Iterator iterator = otherSelects.iterator(); iterator.hasNext();) {
			String[] otherSelect = (String[]) iterator.next();
			addOtherSelect(otherSelect);
		}
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setInnerQuery(SqlQuery ql) {
		this.innerQuery = ql;
	}
	
	public List getWheres() {
		return wheres;
	}

	public void setWheres(List wheres) {
		this.wheres = wheres;
	}

	public void addWhere(String whereClause) {
		this.wheres.add(whereClause);
	}

	public List getGroupBys() {
		return groupBys;
	}

	public void setGroupBys(List groupBys) {
		this.groupBys = groupBys;
	}
	
	public void addGroupBy(String groupByClause) {
		this.groupBys.add(groupByClause);
	}

	public List getOrderBys() {
		return orderBys;
	}

	public void setOrderBys(List orderBys) {
		this.orderBys = orderBys;
	}

	public void addOrderBy(String orderByClause) {
		this.orderBys.add(orderByClause);
	}

	public String getTableIdentifier() {
		return tableIdentifier;
	}


	public void setTableIdentifier(String tableIdentifier) {
		this.tableIdentifier = tableIdentifier;
	}


	public SqlQuery getInnerQuery() {
		return innerQuery;
	}


	public String getJoinTableName() {
		return joinTableName;
	}


	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}


	public String getJoinTableIdentifier() {
		return joinTableIdentifier;
	}


	public void setJoinTableIdentifier(String joinTableIdentifier) {
		this.joinTableIdentifier = joinTableIdentifier;
	}

	public List getPartitionColumns() {
		return partitionColumns;
	}

	public void setPartitionColumns(List partitionColumns) {
		this.partitionColumns = partitionColumns;
	}

	public void addPartitionColumn(String columnName) {
		this.partitionColumns.add(columnName);
	}

	public void addPartitionColumns(List columnNames) {
		this.partitionColumns.addAll(columnNames);
	}
	
	/**
	 * Transform the list of partition columns to a String 
	 * @return
	 */
	public String getPartitionColumnString() {
		StringBuffer sql = new StringBuffer();
		
		if (partitionColumns!=null) {
			for (Iterator iterator = partitionColumns.iterator(); iterator.hasNext();) {
				sql.append(" " + tableIdentifier + ".");
				sql.append((String) iterator.next());
				if (iterator.hasNext()) {
					sql.append(", ");
				}
			}
		}
		
		return sql.toString();
	}
	
}
