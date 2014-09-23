package com.basava.robot.libs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Basavaraj M
 *
 */
public class FileUtils 
{

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static Properties loadFileIntoProperties(String file)
	{
		Properties props = new Properties();
		try {
			props.load(new FileReader(new File(file)));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file : " + file);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not load file : " + file);
			e.printStackTrace();
		}
		return props;
	}
	
	/**
	 * Loads the properties file into properties, with added functionality of loading the files itself for values of the properties mentioned in 
	 * the file
	 * 
	 * i.e. if file=/home/hello.properties which contains
	 *         key1=val1,,val2,,file::test.properties,val3
	 *         key2=a,,b,,c,,file::test.properties
	 *         and test.properties contains - 
	 *         x
	 *         y
	 *         z..
	 *         
	 *         then effecive properties returned will be -
	 *         key1=val1,,val2,,x,,y,,z,,val3
	 *         key2=a,,b,,c,,x,,y,,z
	 *         
	 * @param file
	 * @param fileValueIndicator
	 * @param valueSeparator
	 * @return
	 */
	public static Properties loadProperties(String file, String fileValueIndicator, String valueSeparator )
	{
		Properties props = FileUtils.loadFileIntoProperties(file);
		Set<Entry<Object, Object>> entrySet = props.entrySet();
		for ( Entry<Object, Object> entry : entrySet )
		{
  			String value = (String) entry.getValue();
			if (  value.contains(fileValueIndicator) )
			{
				// the value might have multiple file urls, load each of them append, and remove these file urls on the go
				String[] allValues = value.split(valueSeparator);
				for ( String each : allValues )
				{
					if ( each.contains(fileValueIndicator))
					{
						String fileToBeLoaded = each.split(fileValueIndicator)[1];
						String newValue = getValueListFormatted(fileToBeLoaded,valueSeparator);
						entry.setValue( value.replace(each, newValue));
					}
				}
			}
		}
		return props;
	}
	
	/*
	 * Reads a file filled with values separated by new line characters into 
	 * a string value separated by VALUE SEPARATOR - ,,.
	 * It ignores the lines starting with '#'
	 */
	private static String getValueListFormatted(String fileToBeLoaded, String valueSeparator)
	{
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(new File( fileToBeLoaded.trim()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		DataInputStream ds = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(ds));
		Pattern p;            
		Matcher m;
		String strLine;
		String inputText = "";
		try {
			while (  (strLine = br.readLine()) != null )
			{
				if ( ! strLine.startsWith("#") )
				{
					inputText = inputText + strLine + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// p = Pattern.compile("(?m)$^|[\\n]+\\z");
		p = Pattern.compile("\n");
		m = p.matcher(inputText);
		String str = m.replaceAll(valueSeparator);
		return str;
	}
	
	
	/**
	 * Reads the @file and returns the content as string buffer
	 * @param file
	 * @return
	 */
	public static StringBuffer getFileContent(String file)
	{
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		String line = null ;
		try {
			while (  ( line = reader.readLine()) != null )
			{
				buffer.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	public static void main(String[] args) {
		char comma = ',';
		System.out.println((int)comma);
	}
}
