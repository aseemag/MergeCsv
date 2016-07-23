package main;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.readcsv.factory.FileMergeFactory;
import com.readcsv.fileinterface.FileInterface;

public class Csv
{
	private final static Logger logger = LoggerFactory.getLogger( Csv.class );
	public static void main( String args[] )
	{
		
		try
		{
			
			FileInterface csv = FileMergeFactory.getFileInstance( "csv", "/home/aseema/work/", "/home/aseema/" );
			logger.debug( "Csv.main()" + csv );
			long startTime = System.currentTimeMillis();
			csv.merge();
			long endtime = System.currentTimeMillis();
			logger.debug( "Csv.main()Total time taken=" + ( endtime - startTime ) );

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
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
