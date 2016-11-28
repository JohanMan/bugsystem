package com.dc.ddureportreceiver.db;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBConnectionManager {
	
	private static ComboPooledDataSource dataSource = new ComboPooledDataSource(true);     

	static {
		try {
			Properties properties = new Properties();
			properties.load(DBConnectionManager.class.getClassLoader().getResourceAsStream("com/dc/ddureportreceiver/db/c3p0.properties"));
			dataSource.setDriverClass(properties.getProperty("c3p0.driverClass"));
			dataSource.setJdbcUrl(properties.getProperty("c3p0.jdbcUrl"));    
			dataSource.setUser(properties.getProperty("c3p0.user"));    
			dataSource.setPassword(properties.getProperty("c3p0.password"));    
			dataSource.setMaxPoolSize(Integer.valueOf(properties.getProperty("c3p0.maxPoolSize")));    
			dataSource.setMinPoolSize(Integer.valueOf(properties.getProperty("c3p0.minPoolSize")));    
			dataSource.setAcquireIncrement(Integer.valueOf(properties.getProperty("c3p0.acquireIncrement")));    
			dataSource.setInitialPoolSize(Integer.valueOf(properties.getProperty("c3p0.initialPoolSize")));    
			dataSource.setMaxIdleTime(Integer.valueOf(properties.getProperty("c3p0.maxIdleTime")));    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	private DBConnectionManager() {
		
	}
	
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
}
