package com.ibp.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDetailsSql {

	Connection conn;
	Statement stmt;
	ResultSet rs;

	public UserDetailsSql(Connection conn) {
		System.out.println("conn is "+conn);
		this.conn=conn;
	}

	public UserNode setUserDetails(UserNode userNode, String uid){
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM USERDETAILS where uId='"+uid+"'";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				userNode.setDob(rs.getString("dob"));
				userNode.setGender(rs.getString("gender"));
				userNode.setImageUrl(rs.getString("imageurl"));
				userNode.setInterests(rs.getString("interests"));
				userNode.setName(rs.getString("uName"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userNode;
	}

	public void inserUserSql(String uid,String fullname, String gender,String dob,String imageurl, String placeLived)
	{
		try {
			System.out.println("stmt:"+stmt);
			stmt=conn.createStatement();
			System.out.println("stmt:"+stmt);
			String interests="interesting";
			String sql="INSERT INTO USERDETAILS VALUES('"+uid+"','"+fullname+"','"+gender+"','"+dob+"','"+imageurl+"','"+placeLived+"','"+interests+"')";			
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isUserExists(String uId) {

		return false;
	}




}
