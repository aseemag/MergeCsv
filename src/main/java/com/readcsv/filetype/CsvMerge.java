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
import com.readcsv.util.GlobalConstants;

public class CsvMerge implements FileInterface {
	private char delimiter;
	private String folderPath;
	private String outputDirPath;
	private Map<String, List<String>> output = new HashMap<String, List<String>>();
	private List<String> columnNames = new ArrayList<String>();
	private final static Logger logger=LoggerFactory.getLogger(CsvMerge.class);

	public CsvMerge(char delimiter, String folderPath, String outputDirPath) {
		super();
		this.delimiter = delimiter;
		this.folderPath = folderPath;
		this.outputDirPath = outputDirPath;
	}

	@Override
	public void merge() throws Exception {
		File inputDir = new File(folderPath);

		File[] listOfFiles = getListOfFiles(GlobalConstants.TYPE_CSV, inputDir);

		logger.debug("CsvMerge.merge()--length of file"
				+ listOfFiles.length);

		if (CommonUtil.isNull(listOfFiles) || listOfFiles.length <= 1) {
			throw new Exception("Atleast 2 Csv files needed for merge in dir");
		}
		Map<String, String> file1 = converFileToMap(listOfFiles[0]);
		Map<String, String> file2 = converFileToMap(listOfFiles[1]);
		output = file1.size() <= file2.size() ? getCommonEmails(file1, file2)
				: getCommonEmails(file2, file1);
		// logger.debug("CsvMerge.merge()"+file1);
		logger.debug("CsvMerge.merge()--filePath"
				+ listOfFiles[0].getPath() + "second path"
				+ listOfFiles[1].getPath());
		if (listOfFiles.length == 2) {
			logger.debug("CsvMerge.merge() only 2 files so final output"
					+ output);
		} else {
			Iterator<Entry<String, List<String>>> iter = output.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<String, List<String>> common = iter.next();
				collectCommonEmails(listOfFiles, common.getKey(), output, iter);
			}

			logger.debug("CsvMerge.merge() MultipleFiles final output"
					+ output);
			logger.debug("CsvMerge.merge() MultipleFiles final Columns"
					+ columnNames);
			writeResultToCsv(output);
		}
	}

	private void writeResultToCsv(Map<String, List<String>> output) {

		try {
			Files.deleteIfExists(Paths.get(outputDirPath
					+ GlobalConstants.FILE_NAME));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputDirPath
					+ GlobalConstants.FILE_NAME, true), ',');
			csvOutput.write("email");
			for (String column : columnNames) {
				csvOutput.write(column);
			}
			csvOutput.endRecord();

			for (Entry<String, List<String>> finalResult : output.entrySet()) {
				csvOutput.write(finalResult.getKey());
				for (String value : finalResult.getValue()) {
					csvOutput.write(value);
				}
				csvOutput.endRecord();
			}

			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void collectCommonEmails(File[] listOfFiles, String key,
			Map<String, List<String>> output,
			Iterator<Entry<String, List<String>>> iter) {
		for (int i = 2; i < listOfFiles.length; i++) {
			boolean isPresent = false;
			try {
				CsvReader read = new CsvReader(listOfFiles[i].getPath(),
						delimiter);

				read.readHeaders();

				String columnName = read.getHeader(1);

				while (read.readRecord()) {

					if (read.get("email").equals(key)) {
						if (!columnNames.contains(columnName))
							columnNames.add(columnName);
						isPresent = true;
						List<String> listValues = output.get(key);
						listValues.add(read.get(columnName));
						output.put(key, listValues);
					}

				}
				read.close();
				if (!isPresent) {
					System.out
							.println("CsvMerge.collectCommonEmails()--no email"
									+ key + " found in " + listOfFiles[i]);
					iter.remove();

					break;

				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private Map<String, List<String>> getCommonEmails(
			Map<String, String> smallfile, Map<String, String> largeFile) {

		for (Map.Entry<String, String> entry : smallfile.entrySet()) {
			if (largeFile.containsKey(entry.getKey())) {

				List<String> values = new ArrayList<String>();
				values.add(entry.getValue());
				values.add(largeFile.get(entry.getKey()));
				output.put(entry.getKey(), values);

			}
		}

		logger.debug("CsvMerge.getCommonEmails()---" + output);
		return output;
	}

	private Map<String, String> converFileToMap(File file) {
		Map<String, String> fileData = new HashMap<String, String>();
		try {
			CsvReader read = new CsvReader(file.getPath(), delimiter);

			read.readHeaders();

			String columnName = read.getHeader(1);
			columnNames.add(columnName);

			while (read.readRecord()) {

				fileData.put(read.get("email"), read.get(columnName));

			}

			read.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileData;
	}

	private File[] getListOfFiles(final String type, File inputDir) {
		File[] files=inputDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				
				if (name.toLowerCase().endsWith(type)) {
					return true;
				}
				return false;
			}
		});
       //  List<File> filesList=new ArrayList<File>(Arrays.asList(files));
		return files;
   
	}

}
