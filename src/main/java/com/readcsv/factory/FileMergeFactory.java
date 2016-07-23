package com.readcsv.factory;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.readcsv.fileinterface.FileInterface;
import com.readcsv.filetype.CsvMerge;
import com.readcsv.util.CommonUtil;
import com.readcsv.util.GlobalConstants;

/**
 * @author aseema
 *
 */

public class FileMergeFactory
{

	/**
	 * @param type
	 * @param inputFolderPath
	 * @param outputDirPath
	 * @return Instance of FileInterface based on Type of file passed
	 * @throws Exception
	 * 
	 */
	public static FileInterface getFileInstance( String type, String inputFolderPath, String outputDirPath ) throws Exception
	{

		switch( type )
		{
		case GlobalConstants.CSV:
			if( !CommonUtil.isNullOrBlank( inputFolderPath ) && !CommonUtil.isNullOrBlank( outputDirPath ) && isDir( Paths.get( inputFolderPath ) ) && isDir( Paths.get( outputDirPath ) ) )
			{
				if( !inputFolderPath.endsWith( "/" ) || !outputDirPath.endsWith( "/" ) )

					throw new Exception( "Make sure Your Input Or OutPut Path Ends with /" );
				return new CsvMerge( GlobalConstants.DELIMITOR, inputFolderPath, outputDirPath );
			}
			else
			{

				throw new Exception( "Incorrect Input or Output path" );
			}

		default:
			throw new Exception( "Incorrect File Type Only Csv Supported" );

		}

	}

	/**
	 * @param path-Filepath
	 * @return Whether given path is valid or not
	 */
	private static Boolean isDir( Path path )
	{
		if( path == null || !Files.exists( path ) )
			return false;
		else
			return Files.isDirectory( path );
	}

}
