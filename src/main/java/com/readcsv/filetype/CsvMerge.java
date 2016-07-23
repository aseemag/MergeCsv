package com.readcsv.filetype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.readcsv.fileinterface.FileInterface;
import com.readcsv.util.CommonUtil;
import com.readcsv.util.FileComparator;
import com.readcsv.util.GlobalConstants;

public class CsvMerge implements FileInterface
{
	private char delimiter;
	private String folderPath;
	private String outputDirPath;
	private Map< String, List< String >> output = new HashMap< String, List< String >>();
	private List< String > columnNames = new ArrayList< String >();
	private final static Logger logger = LoggerFactory.getLogger( CsvMerge.class );

	/**
	 * @param delimiter
	 * @param folderPath
	 * @param outputDirPath
	 */
	public CsvMerge( char delimiter, String folderPath, String outputDirPath )
	{
		super();
		this.delimiter = delimiter;
		this.folderPath = folderPath;
		this.outputDirPath = outputDirPath;
	}

	/* (non-Javadoc)
	 * @see com.readcsv.fileinterface.FileInterface#merge()
	 */
	@Override
	public void merge() throws Exception
	{
		File inputDir = new File( folderPath );

		List<File> listOfFiles = getListOfFiles( GlobalConstants.TYPE_CSV, inputDir );

		logger.info( "No of Files" + listOfFiles.size()+" in inputDir "+folderPath);

		if( CommonUtil.isNull( listOfFiles ) || listOfFiles.size() <= 1 )
		{
			throw new Exception( "Atleast 2 Csv files needed for merge in "+folderPath+" dir" );
		}
		Map< String, String > file1 = converFileToMap( listOfFiles.get( 0 ) );
		Map< String, String > file2 = converFileToMap( listOfFiles.get( 1 ));
		output =  getCommonEmails( file1, file2 );
		
		logger.info( "CsvMerge.merge()--filePath" + listOfFiles.get( 0 ).getPath() + "second path" + listOfFiles.get( 1 ).getPath() );
		if( listOfFiles.size() == 2 )
		{
			logger.info( "CsvMerge.merge() only 2 files so final output" + output );
		}
		else
		{       
			
			Iterator< Entry< String, List< String >>> iter = output.entrySet().iterator();
			while( iter.hasNext() )
			{
				Entry< String, List< String >> common = iter.next();
				collectCommonEmails( listOfFiles, common.getKey(), output, iter );
			}

			logger.info( "CsvMerge.merge() MultipleFiles final output" + output );
			logger.info( "CsvMerge.merge() MultipleFiles final Columns" + columnNames );
			writeResultToCsv( output );
		}
	}

	/**
	 * @param output
	 * Writes Output to a csv file
	 */
	private void writeResultToCsv( Map< String, List< String >> output )
	{

		try
		{
			Files.deleteIfExists( Paths.get( outputDirPath + GlobalConstants.FILE_NAME ) );
		}
		catch( IOException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try
		{
			// use FileWriter constructor that specifies open for
			// appending
			CsvWriter csvOutput = new CsvWriter( new FileWriter( outputDirPath + GlobalConstants.FILE_NAME, true ), ',' );
			csvOutput.write( "email" );
			for( String column : columnNames )
			{
				csvOutput.write( column );
			}
			csvOutput.endRecord();

			for( Entry< String, List< String >> finalResult : output.entrySet() )
			{
				csvOutput.write( finalResult.getKey() );
				for( String value : finalResult.getValue() )
				{
					csvOutput.write( value );
				}
				csvOutput.endRecord();
			}
			logger.info( "Merged File result.csv created in " + outputDirPath );
			csvOutput.close();
			
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param listOfFiles
	 * @param key
	 * @param output
	 * @param iter
	 * Finds if emails exists in remaining files
	 */
	private void collectCommonEmails( List<File> listOfFiles, String key, Map< String, List< String >> output, Iterator< Entry< String, List< String >>> iter )
	{
		for( int i = 2; i < listOfFiles.size(); i++ )
		{
			boolean isPresent = false;
			try
			{
				CsvReader read = new CsvReader( listOfFiles.get( i ).getPath(), delimiter );

				read.readHeaders();

				String columnName = read.getHeader( 1 );

				while( read.readRecord() )
				{

					if( read.get( "email" ).equals( key ) )
					{
						if( !columnNames.contains( columnName ) )
							columnNames.add( columnName );
						isPresent = true;
						List< String > listValues = output.get( key );
						listValues.add( read.get( columnName ) );
						output.put( key, listValues );
						break;
					}

				}
				read.close();
				if( !isPresent )
				{
					logger.info( "CsvMerge.collectCommonEmails()--no email" + key + " found in " + listOfFiles.get( i ) );
					iter.remove();

					break;

				}

			}
			catch( FileNotFoundException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param smallfile
	 * @param largeFile
	 * @return an intermediate output of common emails in first 2 files
	 */
	private Map< String, List< String >> getCommonEmails( Map< String, String > smallfile, Map< String, String > largeFile )
	{

		for( Map.Entry< String, String > entry : smallfile.entrySet() )
		{
			if( largeFile.containsKey( entry.getKey() ) )
			{
                           
				List< String > values = new ArrayList< String >();
				values.add( entry.getValue() );
				values.add( largeFile.get( entry.getKey() ) );
				output.put( entry.getKey(), values );

			}
		}

		logger.info( "CsvMerge.getCommonEmails()---" + output );
		return output;
	}

	/**
	 * @param file
	 * @return Converts a given Csv file in hashmap
	 */
	private Map< String, String > converFileToMap( File file )
	{
		Map< String, String > fileData = new HashMap< String, String >();
		try
		{
			CsvReader read = new CsvReader( file.getPath(), delimiter );

			read.readHeaders();

			String columnName = read.getHeader( 1 );
			columnNames.add( columnName );

			while( read.readRecord() )
			{
                               if(!fileData.containsKey( read.get( "email" ) ))
				fileData.put( read.get( "email" ), read.get( columnName ) );

			}

			read.close();

		}
		catch( FileNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileData;
	}

	/**
	 * @param type
	 * @param inputDir
	 * @return a list of files in directory sorted based on size
	 */
	private List< File > getListOfFiles( final String type, File inputDir )
	{
		File[] files = inputDir.listFiles( new FilenameFilter()
		{

			@Override
			public boolean accept( File dir, String name )
			{

				if( name.toLowerCase().endsWith( type ) )
				{
					return true;
				}
				return false;
			}
		} );
		List<File>listOfFiles=Arrays.asList(files );
	        Collections.sort( listOfFiles, new FileComparator() );
		logger.info( "Sorted List of Files" +listOfFiles);
		return listOfFiles;

	}

}
