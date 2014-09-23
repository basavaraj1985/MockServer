package com.basava.mock.backend.servlets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.basava.mock.backend.MockBEServer;
import com.basava.robot.libs.FileUtils;

/**
 * @author Basavaraj M
 *
 */
public class MockResponseServlet extends HttpServlet  
{
	
	public static final String CITYGUIDE_RESP = "cityGuideResponse";
	public static final String SUPPORT_REGEX = "regex::";
	public static boolean sleep = false;

	/**
	 * Generated serial id 
	 */
	private static final long serialVersionUID = -1119871127587995253L;
	private String configPropertiesFile;
	private Properties confignProperties ;

	private boolean isFirstRequest = true;
	public static final int DEFAULT_CONFIGURATION_UPDATE_INTERVAL_MS = 5 * 1000 ;
	
	public MockResponseServlet(String confFile) 
	{
		this.configPropertiesFile = confFile ;
		confignProperties = FileUtils.loadFileIntoProperties(configPropertiesFile);
	}

	/**
	 * Config is loaded periodically, thus enabling dynamic configuration and editing of response files.
	 * For configuration change/response file change there is no need to restart the server.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException 
	{
		if ( isFirstRequest )
		{
			isFirstRequest = false;
			
			Thread configUpdaterThread = new Thread( new Runnable(){
				@Override
				public void run()
				{
					while ( true )
					{
						synchronized (confignProperties)
						{
							confignProperties = FileUtils.loadFileIntoProperties(configPropertiesFile);
							System.out.println("Loaded new configuration!");
						}
						try
						{
							String updatePeriod = confignProperties.getProperty(MockBEServer.CONFIG_UPDATE_PERIOD, String.valueOf(DEFAULT_CONFIGURATION_UPDATE_INTERVAL_MS) ).trim();
							int sleepPeriod = DEFAULT_CONFIGURATION_UPDATE_INTERVAL_MS;
							try
							{
								sleepPeriod = Integer.valueOf(updatePeriod);
							} catch (Exception e)
							{
								sleepPeriod = DEFAULT_CONFIGURATION_UPDATE_INTERVAL_MS;
							}
							Thread.sleep(sleepPeriod);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			configUpdaterThread.setDaemon(true);
			configUpdaterThread.setName("ConfigurationUpdateThread");
			configUpdaterThread.start();
		}
		String requestURI = req.getRequestURI();
		String requestURL = req.getRequestURL().toString();
		response.setContentType("text/xml");
		response.setStatus(HttpServletResponse.SC_OK);
		if ( ! formResponse(response, req) ) 
		{
			response.getWriter().println("<root>");
			response.getWriter().println("<reqURI>" + requestURI + "</reqURI>");
			response.getWriter().println("<reqURL>" + requestURL + "</reqURL>");
			response.getWriter().println("<reason>couldnt form response</reason>");
			response.getWriter().println("</root>");
		}
		if ( requestURI.contains("sleep"))
		{
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if ( sleep )
		{
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sleep = false;
		}
		if ( requestURI.contains("nextSlp"))
		{
			sleep = true;
		}
		System.out.println(".");
	}
	
	private boolean formResponse(HttpServletResponse response, HttpServletRequest req) throws IOException 
	{
		String requestURI = req.getRequestURI();
		String url = req.getRequestURL().toString();
		String fileToLoadAndSetAsResponse = null;
		
		Set<Object> keySet = confignProperties.keySet();
		
		Iterator<Object> iterator = keySet.iterator();
		while ( iterator.hasNext() )
		{
			String next = (String) iterator.next();
			if ( requestURI.contains(next) && ! next.contains(SUPPORT_REGEX))
			{
				fileToLoadAndSetAsResponse = confignProperties.getProperty(next);
				break;
			}
			
			if ( next.startsWith(SUPPORT_REGEX))
			{
				String actualExpr = next.split(SUPPORT_REGEX)[1];
				Pattern pattern = null;
				Matcher matcher = null;
				try
				{
					pattern = Pattern.compile(actualExpr);
					matcher = pattern.matcher(requestURI);
					if ( matcher.matches() )
					{
						fileToLoadAndSetAsResponse = confignProperties.getProperty(next);
						break;
					}
				} catch (Exception e) {
					System.err.println("Error in Regex configured! - " + actualExpr );
				}
			}
		}
		
		if ( fileToLoadAndSetAsResponse == null && requestURI.contains("vitraveldd_api/V1/destination"))
		{
			fileToLoadAndSetAsResponse = confignProperties.getProperty(CITYGUIDE_RESP);
		}
		else if ( fileToLoadAndSetAsResponse == null )
		{
			System.out.println("Request : " + req.getRequestURI() );
			System.err.println("Could not select response file!");
			response.setContentType("text/xml");
	        response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("<root>");
	        response.getWriter().println("<h1>Could not map the request URI to a response file!</h1>");
	        response.getWriter().println("</root>");
	        return false;
		}
		
		String[] splits = fileToLoadAndSetAsResponse.split("::");
		if ( splits != null && splits.length > 1 )
		{
			response.setContentType(splits[0]);
			fileToLoadAndSetAsResponse = splits[1];
		}
		
		if ( fileToLoadAndSetAsResponse != null )
		{
			System.out.println("Request : " + req.getRequestURI() );
			System.out.println("Response file selected : " + fileToLoadAndSetAsResponse);
			StringBuffer buffer = FileUtils.getFileContent(fileToLoadAndSetAsResponse);
			response.getWriter().println(buffer.toString());
			return true;
		}
		return false;
	}
}
