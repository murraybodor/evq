package ca.aeso.evq.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * JdbcDaoSupport
 * Ancestor class for DAOs using JDBC through Spring
 * @author mbodor
 */
public class JdbcDaoSupport {

	protected Log logger = LogFactory.getLog(JdbcDaoSupport.class);
	protected JdbcTemplate jdbcTemplate;

	public JdbcDaoSupport() {
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
