package main;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.readcsv.factory.FileMergeFactory;
import com.readcsv.fileinterface.FileInterface;

public class Csv {
	public static void main(String args[]) {
		try {
			/*
			 * CsvReader read=new CsvReader("/home/aseema/work/test1.csv", ',');
			 * 
			 * read.readHeaders();
			 * System.out.println("Csv.main()"+read.getHeader(1)); while
			 * (read.readRecord()) { String productID = read.get("email");
			 * String productName = read.get("value");
			 * 
			 * 
			 * 
			 * // perform program logic here System.out.println(productID + ":"
			 * + productName);
			 * 
			 * } File f=new File("/home/aseema/work/test1.csv"); String
			 * s=FileUtils.readFileToString(f);
			 * System.out.println("Csv.main()"+s.charAt(1));
			 * 
			 * read.close();
			 */
			FileInterface csv = FileMergeFactory.getFileInstance("csv",
					"/home/aseema/work/abc/", "/home/aseema/");
			System.out.println("Csv.main()" + csv);
			long startTime = System.currentTimeMillis();
			csv.merge();
			long endtime = System.currentTimeMillis();
			System.out.println("Csv.main()Total time taken="
					+ (endtime - startTime));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
