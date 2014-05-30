package com.ibp.model;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

public class StatisticsSchema {
	private static Cluster cluster;
	private static Session session;
	private static String ip="localhost";
	
	static{
		cluster = Cluster.builder().addContactPoint(ip).build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n",  metadata.getClusterName());
		/*for ( Host host : metadata.getAllHosts() ) {
		System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(), host.getRack());
		}*/
		session = cluster.connect();
		
		session.execute("CREATE KEYSPACE IF NOT EXISTS stats WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};");
		
		session.execute(
				 "CREATE TABLE IF NOT EXISTS stats.categoryStats (" +
				 "category varchar(50) PRIMARY KEY," + 
				 "onlineUsers int," + 
				 "totalUsers int," + 
				 ");");
		
		session.execute(
				 "CREATE TABLE IF NOT EXISTS stats.urlStats (" +
				 "url varchar(50) PRIMARY KEY," + "datetime timestamp PRIMARY KEY," +
				 "0-1 int," +  "1-2 int," +  "2-3 int," + " 3-4 int," +  "4-5 int," +  "5-6 int," +  "6-7 int," +  "7-8 int," +  "8-9 int," +  "9-10 int," +  "10-11 int," +
				 "11-12 int," +  "12-13 int," +  "13-14 int," + " 14-15 int," +  "15-16 int," +  "16-17 int," +  "17-18 int," +  "18-19 int," +  "19-20 int," +  "20-21 int," +  
				 "21-22 int," + "22-23 int, "+ "23-24 int, "+
				");");
		
		session.execute(
				 "CREATE TABLE IF NOT EXISTS stats.UserDetails (" +
				 "uid varchar(50) PRIMARY KEY," +
				 "fullname varchar(80),"+
				 "dob timestamp, "+
				 "gender varchar(9),"+
				 "imageId varchar(80),"+
				 "interests varchar(250),"+ 
				 ");");
		
		session.close();
	}
	
	
	
}
