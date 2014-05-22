package com.ibp.controller;

//import GraphDB;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Servlet implementation class SendMessage
 */
@WebServlet("/SendMessage")
public class SendMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	GraphDatabaseService graph;
	ExecutionEngine engine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendMessage() {
		super();
		// TODO Auto-generated constructor stub

	}
	public void init() throws ServletException {
		System.out.println("init called from sendMessage");	
		graph= (GraphDatabaseService) this.getServletContext().getAttribute("db");
	}
	


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String url=request.getParameter("url");
		String uid=request.getParameter("uid");
		String uname=request.getParameter("uname");
		String message=request.getParameter("message");
		String type=request.getParameter("type");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
