package com.readcsv.util;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator< File >
{

	@Override
	public int compare( File o1, File o2 )
	{
		// TODO Auto-generated method stub
		return o1.length() < o2.length()?-1:o1.length()>o2.length()?1:0;
	}

}
