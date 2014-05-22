package com.ibp.model;

public class UserNode
{
	//  private String category;  //user category
	private String userID;     //user ID 
	private String name;     //
	private Long lastSeenMessageTime;//last seen message time
	private long connectedToUrlId;
	private String hasUrl;
	private String status;   //added to know whether the user is active or not
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public UserNode(String userID, String name, long connectedToUrlId) {
		super();
		setUserID(userID);
		setName(name);
		setConnectedToUrlId(connectedToUrlId);
	}

	public UserNode() {

	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getLastSeenMessageTime() {
		return lastSeenMessageTime;
	}

	public void setLastSeenMessageTime(Long lastSeenMessageTime) {
		this.lastSeenMessageTime = lastSeenMessageTime;
	}

	public long getConnectedToUrlId() {
		return connectedToUrlId;
	}

	public void setConnectedToUrlId(long connectedToUrlId) {
		this.connectedToUrlId = connectedToUrlId;
	}

	public String getHasUrl() {
		return hasUrl;
	}

	public void setHasUrl(String hasUrl) {
		this.hasUrl = hasUrl;
	}
	
	
}