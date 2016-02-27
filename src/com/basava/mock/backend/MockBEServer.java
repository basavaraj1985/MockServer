package com.basava.mock.backend;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.basava.mock.backend.servlets.HelloServlet;
import com.basava.mock.backend.servlets.MockResponseServlet;
import com.basava.robot.libs.FileUtils;

/**
 * @author Basavaraj M
 *
 */
public class MockBEServer 
{
	public static final String PORT = "port";
	public static final String CONFIG_UPDATE_PERIOD = "updatePeriod";
	
	private static final String CONTEXT_PATH = "contextPath";
	private Properties configurnProperties ;
	private Server server ;
	private ServletContextHandler context;
	private String confignPropertiesFile ;
	
	public Properties getConfigurnProperties() {
		return configurnProperties;
	}

	public ServletContextHandler getContext() {
		return context;
	}

	public void setContext(ServletContextHandler context) {
		this.context = context;
	}

	public Server getServer() {
		return server;
	}
	
	public MockBEServer(String configPropertiesLocation) 
	{
		confignPropertiesFile = configPropertiesLocation;
		configurnProperties = FileUtils.loadFileIntoProperties(configPropertiesLocation);
		setupAndConfigure();
	}

    private void setupAndConfigure() 
    {
    	int port = 4080;
    	String serverContextPath = configurnProperties.getProperty(CONTEXT_PATH, "/");
    	
    	try {
			port = Integer.valueOf(configurnProperties.getProperty(PORT, "4080").trim() );
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println("port key not configured, seeting default - 4080");
			port = 4080;
		}
		
		server = new Server(port);
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(serverContextPath);
        server.setHandler(context);
        addServletHolders();
	}
    
    /**
     * Add a servlet, its a wrapper method over context.
     * Other is get handle over context, by invoking <CODE>getContext().add(...)<CODE>
     * @param svHolder
     * @param pathSpec
     */
    public void addServlet(ServletHolder svHolder, String pathSpec)
    {
    	context.addServlet(svHolder, pathSpec);
    }
    
    private void addServletHolders() 
    {
    	context.addServlet(new ServletHolder(new HelloServlet()),"/hello/*");
//    	context.addServlet(new ServletHolder(new MockResponseServlet(configurnProperties)), "/vitraveldd_api/V1/destination/*");
    	context.addServlet(new ServletHolder(new MockResponseServlet(confignPropertiesFile)), "/test/*");
	}

	public void start()
    {
    	try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public static void main(String[] args) throws Exception
    {
		String configFile = "config.properties";
    	if ( args.length > 0 )
    	{
    		configFile = args[0];
    	}
    	MockBEServer server = new MockBEServer(configFile);
    	server.start();
    }

}
