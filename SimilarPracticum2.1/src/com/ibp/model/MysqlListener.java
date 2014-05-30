package com.ibp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlListener {

	private  String db_driver;
	private   String db_url;
	private   String db_username;
	private   String db_password;

	public Connection getMysqlConnection() {
		try {
			String dbName = "pluginDB";

			db_username = "root";
			db_password = "root";

			db_driver = "com.mysql.jdbc.Driver";
			db_url = "jdbc:mysql://localhost:3306/" + dbName;

			System.out.println("Database Driver :"+db_driver+ " Database URL :"+db_url+"uname:"+dbName);
			//STEP 2: Register JDBC driver
			Class.forName(db_driver);

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			Connection conn = DriverManager.getConnection(db_url,db_username,db_password);
			return conn;
		} catch (SQLException |ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
