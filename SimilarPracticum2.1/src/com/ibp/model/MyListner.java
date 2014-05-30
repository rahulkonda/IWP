package com.ibp.model;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Application Lifecycle Listener implementation class MyListner
 *
 */
@WebListener
public class MyListner implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public MyListner() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    //DBConnection db = new DBConnection();
    GraphDatabaseService graphUpdateMessage;
    public void contextInitialized(ServletContextEvent arg0) {
        //db.getConnection();
    	try
       	{
    		System.out.println("in side my listerner try");
    		final String DB_PATH = "/var/lib/tomcat7/webapps/SimilarPracticum/WEB-INF/Neo4j_db";
            //final String DB_PATH = "C:\\Users\\shalini\\Contacts\\Desktop\\testingSimilar\\Neo4j_db";
            //final String DB_PATH = "C:\\Users\\Rahul Konda\\Desktop\\db";
    		
            System.out.println("dp:"+DB_PATH);
    		graphUpdateMessage = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    		System.out.println("Created Database Connection from MyListener..<pluginDB>");
       	}
       	catch(Exception e) {
       		e.printStackTrace();
       	}
        ServletContext context = arg0.getServletContext();
        context.setAttribute("db", graphUpdateMessage);
        
        MysqlListener sqlListener=new MysqlListener();
        Connection sqlConnection=sqlListener.getMysqlConnection();
        System.out.println("Mysql con obj:"+sqlConnection);
        context.setAttribute("sqlDBConnection", sqlConnection);
        
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    	System.out.println("Database shutdown from mylistener...<Mylistener>");
    	graphUpdateMessage.shutdown();
    }
	
}
