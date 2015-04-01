package org.iiinews.android.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
	private static final String port = "3306";
	private static final String db = "iii_news_db";
	private static final String username = "root";
	
	public static Connection getConnection (String databaseHost, String password) throws SQLException, ClassNotFoundException{
		 Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://"+ databaseHost + ":" + port + "/" + db + "?allowMultiQueries=true&characterEncoding=UTF-8";
		Connection connection =  DriverManager.getConnection(url, username, password);
		return connection;
	}
	
}
