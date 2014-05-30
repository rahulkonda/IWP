package com.ibp.model;


import java.util.Date;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CategoryWiseStatistics {

	
	public CategoryWiseStatistics(){

	}
	
	Cluster cluster;
	private Session session;
	
	public static void main(String[] args)
	{
		StatisticsSchema start= new StatisticsSchema();
		CategoryWiseStatistics cs=new CategoryWiseStatistics();
		cs.connect("localhost");
		cs.insertCategory("rahul");
		System.out.println("inserted");
		cs.display();
		cs.AddOnlineUsersCategory("rahul");
		System.out.println("added");
		cs.display();
		cs.DecrementOnlineUsersCategory("rahul");
		System.out.println("reduced");
		cs.display();
		cs.close();
	}

	public void insertUrlStats(String url){
		Date date=new Date();
		session.execute("INSERT INTO stats.urlStats VALUES ('" +url+ "','"+date+"',1,1);");
	}

	public void connect(String node) {
		cluster = Cluster.builder().addContactPoint(node).build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n",  metadata.getClusterName());
		/*for ( Host host : metadata.getAllHosts() ) {
		System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(), host.getRack());
		}*/
		session = cluster.connect();
	}

	public void display(){
	//	ResultSet results = session.execute("SELECT * FROM simplex.playlists " + "WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;");
		ResultSet results = session.execute("SELECT * FROM stats.categoryStats;" );
		System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title",
				 "album", "artist","-------------------------------+-----------------------+--------------------"));
				for (Row row : results) {
				 System.out.println(String.format("%-30s\t%-20s\t%-20s",
				 row.getString("category"),
				 row.getInt("onlineUsers"), row.getInt("totalUsers")));
				}
				System.out.println();
	}
	
	public void insertCategory(String category){
		session.execute("INSERT INTO stats.categoryStats(category,onlineUsers,totalUsers) VALUES ('" +category+ "',1,1);");
	}

	public boolean isCategoryExists(String category){
		String cate;
		ResultSet results = session.execute("SELECT * FROM stats.categoryStats;" );
		for (Row row : results) {
			cate=row.getString("category");
			if(cate.equals(category)){
				return true;
			}
		}
		return false;
	}

	/*	category text PRIMARY KEY," + 
	 "onlineUsers int," + 
	 "totalUsers int,
	 */
	public void AddOnlineUsersCategory(String category){
		int online=0,total=0;
		ResultSet results = session.execute("SELECT * FROM stats.categoryStats WHERE category = '"+category+"'");
		for (Row row : results) {
			online=row.getInt("onlineUsers");
			total=row.getInt("totalUsers");
		}
		online=online+1;
		total=total+1;
		session.execute("UPDATE stats.categoryStats set onlineUsers="+online+", totalUsers="+total+" WHERE category = '"+category+"'");
	}

	public void DecrementOnlineUsersCategory(String category){
		int online=0,total=0;
		ResultSet results = session.execute("SELECT * FROM stats.categoryStats WHERE category = '"+category+"'");
		for (Row row : results) {
			online=row.getInt("onlineUsers");
			total=row.getInt("totalUsers");
		}
		online=online-1;
		session.execute("update stats.categoryStats set onlineUsers="+online+" WHERE category = '"+category+"'");
	}

	public void close(){
		cluster.close();
	}
	
}

