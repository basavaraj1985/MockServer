package com.basava.mock.backend.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Basavaraj M
 *
 */
public class HelloServlet extends HttpServlet 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3412712180660753400L;
	
	private String greeting="Hello Basava!";
    public HelloServlet(){}
    public HelloServlet(String greeting)
    {
        this.greeting=greeting;
    }
   
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/xml");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<root>");
        response.getWriter().println("<h1>"+greeting+"</h1>");
        response.getWriter().println("<anything>this is</anything>");
        response.getWriter().println("</root>");
//        response.getWriter().println("session=" + request.getSession(true).getId());
    }
}

