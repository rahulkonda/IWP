package com.ibp.model;

import java.sql.Connection;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.ibp.model.DBManager.RelTypes;

public class DBUtilities {
	static GraphDatabaseService graph;
	Connection sqlConnection;
	DBManager db;

	public static void main(String[] args) {
		//		graph=new GraphDatabaseFactory().newEmbeddedDatabase("C:\\Users\\Nikhitha\\Desktop\\GraphDB\\DB-graph\\neo4j-community-2.1.0-M01");
		DBUtilities util=new DBUtilities();
		//		util.deleteNode("1");
		//System.out.println("From Core\n"+util.getMessageFromCoreNode("Nikhitha", "258", "www.rahul.com"));
		//util.addMessagesToCoreNode("Rahul", "25", "www.rahul.com", "message2");
		//util.addMessagesToCoreNode("Rahul", "25", "www.rahul.com", "message1");
		//util.addMessagesToCoreNode("Rahul", "25", "www.rahul.com", "message3");
		System.out.println("From Core\n"+util.getMessageFromCoreNode("Rahul", "25", "www.rahul.com"));
		//util.addMessageToURLNode("nikhitha", "245", "www.nikhithagoodgirl24.com", "REDDYNEW");
		//System.out.println("--"+util.getMessageFromURLNode("nikhitha", "245", "www.nikhithagoodgirl24.com"));
		/*System.out.print("Messages:");
		System.out.println("--"+util.getMessageFromURLNode("Rahul", "1", "www.test19.com"));
		System.out.println("msg2=======================================");
		util.addMessageToURLNode("Rahul", "1", "www.test19.com", "msitmsg2");
		System.out.print("Messages:");
		System.out.println("--"+util.getMessageFromURLNode("Rahul", "1", "www.test11.com"));*/
		//	util.deleteUser("245");
		System.out.println("--"+util.getMessageFromURLNode("abc", "111", "www.abc.com"));
	}

	public DBUtilities(GraphDatabaseService graph,Connection con){
		DBUtilities.graph=graph;
		sqlConnection=con;
		db= new DBManager(graph,sqlConnection);
	}

	public DBUtilities() {
		graph=new GraphDatabaseFactory().newEmbeddedDatabase("C:\\Users\\shalini\\Contacts\\Desktop\\testingSimilar\\Neo4j_db");
		db= new DBManager(graph,sqlConnection);
	}

	public int getOnlineUsersForCoreNode(Node coreNode){
		int users;
		try ( Transaction tx = graph.beginTx() ){
			 users=(int) coreNode.getProperty("onlineUsers");
			 tx.success();
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		System.out.println("users : "+users);
		return users;
	}

	public void deActivateUser(String uId,String uname,String url)// should be verified by konda rahul
	{
		UserNode userNode=new UserNode();
		userNode.setName(uname);
		userNode.setUserID(uId);
		userNode.setHasUrl(url);
		Node userNodeInDB=db.getUserNode(userNode); 
		if(userNodeInDB != null){
			userNodeInDB.setProperty("status","false");
		}
		try(Transaction tx1=graph.beginTx()){
			Node urlNode=graph.getNodeById(userNode.getConnectedToUrlId());
			long coreNodeId=(long)urlNode.getProperty("connectedToCoreNode");
			Node coreNode=graph.getNodeById(coreNodeId);
			int users=(int) coreNode.getProperty("onlineUsers");
			coreNode.setProperty("onlineUsers",users-1);
			//onlineUsers
		}
	}

	@SuppressWarnings("unused")
	public int getOnlineUsersURL(Node urlNodeInDB){

		int n=0;
		try(Transaction tx=graph.beginTx()){
			Label label = DynamicLabel.label("LabelUserNode");
			System.out.println("url Node :"+urlNodeInDB.getProperty("value"));
			System.out.println("url Node :"+urlNodeInDB.getProperty("connectedToMessageNodeUrl"));
			System.out.println("url Node :"+urlNodeInDB.getProperty("connectedToCoreNode"));

			System.out.println("url node id : "+urlNodeInDB.getId());
			ResourceIterable<Node> ncol= graph.findNodesByLabelAndProperty(label,"connectedToUrl",urlNodeInDB.getId());
			//		ResourceIterable<Node> ncol= graph.findNodesByLabelAndProperty(label,"status","true");

			/////////////////////////
			//		Node usernode=graph.getNodeById()
			//////////////////////////
			ResourceIterator<Node> nodeIterator=ncol.iterator();

			while(nodeIterator.hasNext()){
				System.out.println("while");
				Node userNodeInDB;
				userNodeInDB=nodeIterator.next();
				System.out.println("status : "+userNodeInDB.getProperty("status"));
				//		System.out.println("connectedToUrlId in count : "+userNodeInDB.getProperty("connectedToUrlId"));
				if(userNodeInDB.getProperty("status").equals("true")){
					//System.out.println("true");
					n++;
				}
			}
			tx.success();
		}
		System.out.println("number of online users : "+n);
		return n;
	}

	public String getMessageFromCoreNode(String uname,String uId,String url)
	{
		Node msgNodeInDB=null;
		UserNode userNode=new UserNode();
		userNode.setName(uname);
		userNode.setUserID(uId);
		userNode.setHasUrl(url);
		Node userNodeInDB=db.getUserNode(userNode);//get usernode returns the node from the db or null
		String messagesToSend="";
		UrlNode urlNode=new UrlNode(url);

		Node urlNodeInDB=db.getUrlNode(urlNode);
		Node coreNodeInDB;
		if(urlNodeInDB==null)
		{

			System.out.println("----------------------------new url node");

			String msgs="";
			urlNode.setConnectedToCoreNodeId(url,graph, sqlConnection);      //+++++++++++++++calls meher method internally
			MessageNodeCore mnc= new MessageNodeCore("");
			long connectedToMessageNodeUrlId=db.createMessageNodeCore(mnc).getId();

			urlNode.setConnectedToMessageNodeUrlId(connectedToMessageNodeUrlId);

			urlNodeInDB=db.createUrlNode(urlNode);

			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime(System.currentTimeMillis());
			userNode.setStatus("true");
			updateUserInterests(userNode,urlNode);
			
			userNodeInDB=db.createUserNode(userNode);

			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);


			try(Transaction trans1=graph.beginTx()){

				long coreNodeId=(long)urlNodeInDB.getProperty("connectedToCoreNode");

				coreNodeInDB=graph.getNodeById(coreNodeId);

				System.out.println("connected to core node------------------>"+coreNodeInDB.getProperty("value"));

				long msgNodeId=(long)coreNodeInDB.getProperty("connectedMessageNodeCore");
				msgNodeInDB=graph.getNodeById(msgNodeId);
				msgs=(String) msgNodeInDB.getProperty("value");
				trans1.success();
			}
			String[] msgWithTime=msgs.split(",");

			for (int i = 0; i < msgWithTime.length && i<15; i++) {
				messagesToSend+=msgWithTime[i]+"[(:-,-:)]";
			}
			/*try(Transaction trans3=graph.beginTx()){
				userNodeInDB.setProperty("lastSeenMessageTime", System.currentTimeMillis());
			}*/


			int n=getOnlineUsersForCoreNode(coreNodeInDB);
			messagesToSend=messagesToSend+"[(:-,;,-:)]"+n;
			System.out.println("message to send:"+messagesToSend);
			return messagesToSend;
		}

		if(userNodeInDB == null)
		{

			System.out.println("new user");

			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime(System.currentTimeMillis());
			userNode.setStatus("true");

			userNodeInDB=db.createUserNode(userNode);

			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);

			String msgs;

			try(Transaction trans1=graph.beginTx()){

				long coreNodeId=(long)urlNodeInDB.getProperty("connectedToCoreNode");

				coreNodeInDB=graph.getNodeById(coreNodeId);

				System.out.println("core node value for new user:-------------->"+coreNodeInDB.getProperty("value"));

				long msgNodeId=(long)coreNodeInDB.getProperty("connectedMessageNodeCore");

				msgNodeInDB=graph.getNodeById(msgNodeId);

				msgs=(String) msgNodeInDB.getProperty("value");

				System.out.println("msgs:"+msgs);

				int users=(int) coreNodeInDB.getProperty("onlineUsers");
				coreNodeInDB.setProperty("onlineUsers",users+1);
				
				trans1.success();
			}
			String[] msgWithTime=msgs.split(",");
			////////////////////////////////////////////////////handle below
			for (int i = 0; i < msgWithTime.length && i<15; i++) {
				messagesToSend+=msgWithTime[i]+"[(:-,-:)]";
			}
			try(Transaction trans3=graph.beginTx()){
				userNodeInDB.setProperty("lastSeenMessageTime", System.currentTimeMillis());
				trans3.success();
			}
			int n=getOnlineUsersForCoreNode(coreNodeInDB);
			messagesToSend=messagesToSend+"[(:-,;,-:)]"+n;
			System.out.println("message to send:"+messagesToSend);
			return messagesToSend;
		}

		System.out.println("IN getmsgs() ---user and url exists---");
		try(Transaction trans2=graph.beginTx())
		{
			//System.out.println("user and url exists");
			String msgs="";
			long urlNodeId=(long)userNodeInDB.getProperty("connectedToUrl");
			urlNodeInDB=graph.getNodeById(urlNodeId);

			long coreNodeId=(long)urlNodeInDB.getProperty("connectedToCoreNode");
			coreNodeInDB=graph.getNodeById(coreNodeId);

			String status=(String) userNodeInDB.getProperty("status");
			if(status.equalsIgnoreCase("false")){
				userNodeInDB.setProperty(status, "true");
			}
			System.out.println("refering to code node------------------------------>>>>"+coreNodeInDB.getProperty("value"));

			long msgNodeId=(long)coreNodeInDB.getProperty("connectedMessageNodeCore");
			msgNodeInDB=graph.getNodeById(msgNodeId);

			//System.out.println("get messages \t"+msgNodeInDB.getId());
			//	System.out.println("msgs:"+ msgNodeInDB.getProperty("value"));
			msgs=(String) msgNodeInDB.getProperty("value");
			//	trans.success();
			//System.out.println("Msgsss:"+msgs);

			if(!msgs.equals("")){
				String[] msgWithTime=msgs.split(",");
				String[] msgTime;

				for (int i = 0;i<msgWithTime.length ; i++) {
					msgTime=msgWithTime[i].split("::");
					if(msgTime.length==0)
						continue;
					Long messageTime = new Long(msgTime[1]);
					Long lastSeenTime=(Long) userNodeInDB.getProperty("lastSeenMessageTime");
					if(messageTime<lastSeenTime){
						//System.out.println("last seen");
						break;
					}
					messagesToSend+=msgTime[0]+"[(:-,-:)]"+msgTime[1]+"[(:-,-:)]"+msgTime[2]+"[(:-,-:)]"+msgTime[3]+"[(:-,-:)]"+msgTime[4]+"[(:-;-:)]";
				}
			}
			trans2.success();
		}

		try(Transaction trans3=graph.beginTx()){
			userNodeInDB.setProperty("lastSeenMessageTime", System.currentTimeMillis());
			trans3.success();
		}

		int n=getOnlineUsersForCoreNode(coreNodeInDB);
		messagesToSend=messagesToSend+"[(:-,;,-:)]"+n;
		System.out.println("message to send:"+messagesToSend);
		return messagesToSend;

	}
	private void updateUserInterests(UserNode userNode, UrlNode urlNode) {
		
		
	}

	public String getMessageFromURLNode(String uname,String uId,String url)
	{

		System.out.println("inside dbutilites uname:"+uname+",uid:"+uId+",url:"+url);

		Node msgNodeInDB=null;
		UserNode userNode=new UserNode();
		userNode.setName(uname);
		userNode.setUserID(uId);
		userNode.setHasUrl(url);
		Node userNodeInDB=db.getUserNode(userNode);
		String messagesToSend="";
		UrlNode urlNode=new UrlNode(url);


		Node urlNodeInDB=db.getUrlNode(urlNode);
		if(urlNodeInDB==null){
			System.out.println("new url");
			urlNode.setConnectedToCoreNodeId(url,graph, sqlConnection);//calls meher method internally
			MessageNodeCore mnc= new MessageNodeCore("");
			long connectedToMessageNodeUrlId=db.createMessageNodeCore(mnc).getId();
			urlNode.setConnectedToMessageNodeUrlId(connectedToMessageNodeUrlId);
			db.createUrlNode(urlNode);

			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime(System.currentTimeMillis());
			userNode.setStatus("true");

			userNodeInDB=db.createUserNode(userNode);

			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);

			return "empty";
		}
		if(userNodeInDB == null){
			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime(System.currentTimeMillis());
			userNode.setStatus("true");
			userNodeInDB=db.createUserNode(userNode);


			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);
			String msgs;

			try(Transaction trans1=graph.beginTx())
			{
				long msgNodeId=(long)urlNodeInDB.getProperty("connectedToMessageNodeUrl");
				msgNodeInDB=graph.getNodeById(msgNodeId);
				msgs=(String) msgNodeInDB.getProperty("value");
				trans1.success();
			}
			String[] msgWithTime=msgs.split(",");

			for (int i = 0; i < msgWithTime.length && i<15; i++) {
				messagesToSend+=msgWithTime[i]+"[(:-,-:)]";
			}

			try(Transaction trans3=graph.beginTx()){
				userNodeInDB.setProperty("lastSeenMessageTime", System.currentTimeMillis());
				trans3.success();
			}
			int n=getOnlineUsersURL(urlNodeInDB);
			messagesToSend=messagesToSend+"[(:-,;,-:)]"+n;
			System.out.println("message to send:"+messagesToSend);
			return messagesToSend;
		}
		try(Transaction trans2=graph.beginTx())
		{

			System.out.println("user and url exists");

			long urlNodeId=(long)userNodeInDB.getProperty("connectedToUrl");
			urlNodeInDB=graph.getNodeById(urlNodeId);

			long msgNodeId=(long)urlNodeInDB.getProperty("connectedToMessageNodeUrl");

			String status=(String) userNodeInDB.getProperty("status");
			if(status.equalsIgnoreCase("false")){
				userNodeInDB.setProperty(status, "true");
			}

			msgNodeInDB=graph.getNodeById(msgNodeId);
			String msgs=(String)msgNodeInDB.getProperty("value");
			//	trans.success();
			System.out.println("Msgsss:"+msgs);
			String[] msgWithTime=msgs.split(",");

			String[] msgTime;
			System.out.println("message with time "+msgWithTime.length);
			for (int i = 0;i<msgWithTime.length; i++) {
				System.out.println("mesg with time-->"+msgWithTime[i]+"---");
				msgTime=msgWithTime[i].split("::");

				if(msgTime.length<2)
					continue;
				System.out.println("length="+msgTime.length+",mesgtime="+msgTime[0]);
				Long messageTime = new Long(msgTime[1]);
				Long lastSeenTime=(Long) userNodeInDB.getProperty("lastSeenMessageTime");
				if(messageTime<lastSeenTime)
					break;
				messagesToSend+=msgTime[0]+"[(:-,-:)]"+msgTime[1]+"[(:-,-:)]"+msgTime[2]+"[(:-,-:)]"+msgTime[3]+"[(:-,-:)]"+msgTime[4]+"[(:-;-:)]";


			}

			System.out.println("message to send in dbutil==>"+messagesToSend);
			trans2.success();
		}

		try(Transaction trans3=graph.beginTx()){
			userNodeInDB.setProperty("lastSeenMessageTime", System.currentTimeMillis());
			trans3.success();
		}
		int n=getOnlineUsersURL(urlNodeInDB);
		messagesToSend=messagesToSend+"[(:-,;,-:)]"+n;
		System.out.println("message to send:"+messagesToSend);
		return messagesToSend;

	}

	public void addMessageToURLNode(String uname,String uId,String url,String message){

		UserNode userNode=new UserNode();
		userNode.setName(uname);
		userNode.setUserID(uId);
		userNode.setHasUrl(url);
		Node userNodeInDB=db.getUserNode(userNode);  //checks for user existence

		UrlNode urlNode=new UrlNode(url);          //check for url existence returns node object if exist or else returns null
		Node urlNodeInDB=db.getUrlNode(urlNode);
		if(urlNodeInDB==null){                                 
			System.out.println("In addMsg  url not exists");

			urlNode.setConnectedToCoreNodeId(url,graph, sqlConnection);   //calls meher method internally   and links to core node

			MessageNodeUrl mnu= new MessageNodeUrl(message+"::"+System.currentTimeMillis()+"::"+uId+"::"+uname+"::"+url);  //attaching a message node to an url and appending the user message

			Node msgNode=db.createMessageNodeUrl(mnu);

			System.out.println("message=:"+msgNode);

			long connectedToMessageNodeUrlId=msgNode.getId();

			//System.out.println("con:"+connectedToMessageNodeUrlId);

			urlNode.setConnectedToMessageNodeUrlId(connectedToMessageNodeUrlId);

			urlNodeInDB=db.createUrlNode(urlNode);

			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime((long) 0);
			userNode.setStatus("true");

			userNodeInDB=db.createUserNode(userNode);

			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);


			return ;
		}
		if(userNodeInDB == null){
			System.out.println("In addMsg user not exists");

			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime((long) 0);
			userNodeInDB=db.createUserNode(userNode);
			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);

			Transaction trans=graph.beginTx();
			Node msgNodeInDB=null;

			try(Transaction tx=graph.beginTx())
			{
				long msgNodeId=(long)urlNodeInDB.getProperty("connectedToMessageNodeUrl");
				msgNodeInDB=graph.getNodeById(msgNodeId);
				String msgs=(String) msgNodeInDB.getProperty("value");
				//System.out.println("prev msgs:"+msgs);
				msgNodeInDB.setProperty("value", message+"::"+System.currentTimeMillis()+"::"+uId+"::"+uname+"::"+url+","+msgs);
				trans.success();
				msgs=(String) msgNodeInDB.getProperty("value");
				tx.success();
			}



			return ;

		}

		Node msgNodeInDB=null;
		String msgs = "";
		try(Transaction tx=graph.beginTx())
		{
			System.out.println("In addMsg user and url exists");
			long urlNodeId=(long)userNodeInDB.getProperty("connectedToUrl");
			urlNodeInDB=graph.getNodeById(urlNodeId);

			long msgNodeId=(long)urlNodeInDB.getProperty("connectedToMessageNodeUrl");
			msgNodeInDB=graph.getNodeById(msgNodeId);

			msgs=(String)msgNodeInDB.getProperty("value");

			//		System.out.println("add msg url \n"+msgs);
			msgNodeInDB.setProperty("value", message+"::"+System.currentTimeMillis()+"::"+uId+"::"+uname+"::"+url+","+msgs);
			System.out.println("properties got set");
			tx.success();
			String msgs1=(String)msgNodeInDB.getProperty("value");
			//System.out.println("inserted as:"+msgs1);
		}
		//		try(Transaction tx1=graph.beginTx())
		//		{
		//			msgNodeInDB.setProperty("value", message+"::"+System.currentTimeMillis()+"::"+uId+"::"+uname+","+msgs);
		//			tx1.success();
		//		}


		System.out.println("message added");

	}

	public void addMessagesToCoreNode(String uname,String uId,String url,String message){
		UserNode userNode=new UserNode();
		userNode.setName(uname);
		userNode.setUserID(uId);
		userNode.setHasUrl(url);
		Node userNodeInDB=db.getUserNode(userNode);

		UrlNode urlNode=new UrlNode(url);
		Node urlNodeInDB=db.getUrlNode(urlNode);
		if(urlNodeInDB==null){
			System.out.println("In addMsg  url not exists");

			urlNode.setConnectedToCoreNodeId(url,graph, sqlConnection);//calls meher method internally
			urlNodeInDB=db.getUrlNode(urlNode);
			urlNodeInDB=db.createUrlNode(urlNode);

			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime((long) 0);
			userNode.setStatus("true");

			userNodeInDB=db.createUserNode(userNode);

			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);


			try(Transaction trans2=graph.beginTx())
			{
				System.out.println(urlNodeInDB);
				long coreNodeId=(long)urlNodeInDB.getProperty("connectedToCoreNode");
				Node coreNodeInDB=graph.getNodeById(coreNodeId);

				long msgNodeId=(long)coreNodeInDB.getProperty("connectedMessageNodeCore");
				Node msgNodeInDB=graph.getNodeById(msgNodeId);
				String msgs=(String) msgNodeInDB.getProperty("value");
				msgs=message+"::"+System.currentTimeMillis()+"::"+uId+"::"+uname+"::"+url+','+msgs;
				msgNodeInDB.setProperty("value",msgs);
				trans2.success();
			}
			return ;
		}
		if(userNodeInDB == null){
			System.out.println("In addMsg user not exists");

			userNode.setConnectedToUrlId(urlNodeInDB.getId());
			userNode.setLastSeenMessageTime((long) 0);
			userNode.setStatus("true");
			userNodeInDB=db.createUserNode(userNode);
			db.createLink(userNodeInDB, urlNodeInDB, RelTypes.toUrlNode);

			Node msgNodeInDB=null;
			try(Transaction trans=graph.beginTx())
			{
				long coreNodeId=(long)urlNodeInDB.getProperty("connectedToCoreNode");
				Node coreNodeInDB=graph.getNodeById(coreNodeId);

				long msgNodeId=(long)coreNodeInDB.getProperty("connectedMessageNodeCore");
				msgNodeInDB=graph.getNodeById(msgNodeId);

				String msgs=(String) msgNodeInDB.getProperty("value");
				msgs=message+"::"+System.currentTimeMillis()+"::"+uId+"::"+uname+"::"+url+','+msgs;
				msgNodeInDB.setProperty("value",msgs);
				trans.success();

			}
			return ;
		}

		System.out.println("In AddMSg---user and url exists:");
		Node msgNodeInDB=null;
		String msgs = "";

		try(Transaction trans1=graph.beginTx()){

			long urlNodeId=(long)userNodeInDB.getProperty("connectedToUrl");
			urlNodeInDB=graph.getNodeById(urlNodeId);
			System.out.println("----->");
			long coreNodeId=(long)urlNodeInDB.getProperty("connectedToCoreNode");
			/////////////////////////////////////////////////see if getstatus of a user is needed
			System.out.println("corenode:"+coreNodeId);


			Node coreNodeInDB=graph.getNodeById(coreNodeId);

			long msgNodeId=(long)coreNodeInDB.getProperty("connectedMessageNodeCore");
			msgNodeInDB=graph.getNodeById(msgNodeId);

			msgs=(String) msgNodeInDB.getProperty("value");

			System.out.println("add: "+msgs);

			msgs=message+"::"+System.currentTimeMillis()+"::"+uId+"::"+uname+"::"+url+','+msgs;

			msgNodeInDB.setProperty("value",msgs);
			//System.out.println("In add message function :  to msg node "+msgNodeInDB.getId()+"\n"+msgNodeInDB.getProperty("value"));
			trans1.success();
		}
	}

}
