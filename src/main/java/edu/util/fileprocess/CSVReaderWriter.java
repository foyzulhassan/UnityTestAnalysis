package edu.util.fileprocess;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.PerfFixData;
import com.unity.entity.TestData;
import com.unity.entity.TestMethodData;

public class CSVReaderWriter {

	public void writeListBean(List<PerfFixData> fixdata, String csvfilepath)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		try {

			// Creating writer class to generate
			// csv file
			FileWriter writer = new FileWriter(csvfilepath);

			// // Create Mapping Strategy to arrange the
			// // column name in order
			// ColumnPositionMappingStrategy mappingStrategy=
			// new ColumnPositionMappingStrategy();
			// mappingStrategy.setType(PerfFixData.class);
			//
			// // Arrange column name as provided in below array.
			// String[] columns = new String[]
			// { "projName","projGitUrl","fixCommitID", "fixCommitMsg",
			// "patchPath","srcFileChangeCount","assetChangeCount" };
			// mappingStrategy.setColumnMapping(columns);
			//
			//
			// // Createing StatefulBeanToCsv object
			// StatefulBeanToCsvBuilder<PerfFixData> builder=
			// new StatefulBeanToCsvBuilder(writer);
			// StatefulBeanToCsv beanWriter =
			// builder.withMappingStrategy(mappingStrategy).build();
			//
			// // Write list to StatefulBeanToCsv object
			// beanWriter.write(fixdata);

			// // closing the writer object
			// writer.close();

			StatefulBeanToCsvBuilder<PerfFixData> builder = new StatefulBeanToCsvBuilder<PerfFixData>(writer);
			StatefulBeanToCsv<PerfFixData> beanWriter = builder.build();

			beanWriter.write(fixdata);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public <T> void writeListBean(List<T> fixdata, String csvfilepath,Class neededClass)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		try {

			// Creating writer class to generate
			// csv file
			FileWriter writer = new FileWriter(csvfilepath);

			// // Create Mapping Strategy to arrange the
			// // column name in order
			// ColumnPositionMappingStrategy mappingStrategy=
			// new ColumnPositionMappingStrategy();
			// mappingStrategy.setType(PerfFixData.class);
			//
			// // Arrange column name as provided in below array.
			// String[] columns = new String[]
			// { "projName","projGitUrl","fixCommitID", "fixCommitMsg",
			// "patchPath","srcFileChangeCount","assetChangeCount" };
			// mappingStrategy.setColumnMapping(columns);
			//
			//
			// // Createing StatefulBeanToCsv object
			// StatefulBeanToCsvBuilder<PerfFixData> builder=
			// new StatefulBeanToCsvBuilder(writer);
			// StatefulBeanToCsv beanWriter =
			// builder.withMappingStrategy(mappingStrategy).build();
			//
			// // Write list to StatefulBeanToCsv object
			// beanWriter.write(fixdata);

			// // closing the writer object
			// writer.close();

			StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder<T>(writer);
			StatefulBeanToCsv<T> beanWriter = builder.build();

			beanWriter.write(fixdata);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public List<PerfFixData> getListBeanFromCSV(String strpath) throws Exception {

		List<PerfFixData> data = null;

		Path path = Paths.get(strpath);

		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			HeaderColumnNameMappingStrategy<PerfFixData> strategy = new HeaderColumnNameMappingStrategy<>();
			strategy.setType(PerfFixData.class);

			CsvToBean csvToBean = new CsvToBeanBuilder(br).withType(PerfFixData.class).withMappingStrategy(strategy)
					.withIgnoreLeadingWhiteSpace(true).build();

			data = csvToBean.parse();

		}

		return data;
	}
	
	public <T> List<T> getListBeanFromCSV(String strpath,Class neededClass) throws Exception {

		List<T> data = null;

		Path path = Paths.get(strpath);

		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
			strategy.setType(neededClass);

			CsvToBean csvToBean = new CsvToBeanBuilder(br).withType(PerfFixData.class).withMappingStrategy(strategy)
					.withIgnoreLeadingWhiteSpace(true).build();

			data = csvToBean.parse();

		}

		return data;
	}
	
	public void writeListBeanToFile(List<TestData> fixdata, String csvfilepath)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		try {

			// Creating writer class to generate
			// csv file
			FileWriter writer = new FileWriter(csvfilepath);

			// // Create Mapping Strategy to arrange the
			// // column name in order
			// ColumnPositionMappingStrategy mappingStrategy=
			// new ColumnPositionMappingStrategy();
			// mappingStrategy.setType(PerfFixData.class);
			//
			// // Arrange column name as provided in below array.
			// String[] columns = new String[]
			// { "projName","projGitUrl","fixCommitID", "fixCommitMsg",
			// "patchPath","srcFileChangeCount","assetChangeCount" };
			// mappingStrategy.setColumnMapping(columns);
			//
			//
			// // Createing StatefulBeanToCsv object
			// StatefulBeanToCsvBuilder<PerfFixData> builder=
			// new StatefulBeanToCsvBuilder(writer);
			// StatefulBeanToCsv beanWriter =
			// builder.withMappingStrategy(mappingStrategy).build();
			//
			// // Write list to StatefulBeanToCsv object
			// beanWriter.write(fixdata);

			// // closing the writer object
			// writer.close();

			StatefulBeanToCsvBuilder<TestData> builder = new StatefulBeanToCsvBuilder<TestData>(writer);
			StatefulBeanToCsv<TestData> beanWriter = builder.build();

			beanWriter.write(fixdata);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void newwriteListBeanToFile(List<TestData> fixdata, String csvfilepath)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		try {

			// Creating writer class to generate
			// csv file
			//FileWriter writer = new FileWriter(csvfilepath);
			CustomMappingStrategy<TestData> mappingStrategy = new CustomMappingStrategy<>();
	        mappingStrategy.setType(TestData.class);
			
			 Writer writer = new FileWriter(csvfilepath);
		        StatefulBeanToCsv<TestData> beanToCsv = new StatefulBeanToCsvBuilder<TestData>(writer)
		                .withMappingStrategy(mappingStrategy).withSeparator(',').build();
		        beanToCsv.write(fixdata);
		        writer.close();

			// // Create Mapping Strategy to arrange the
			// // column name in order
			// ColumnPositionMappingStrategy mappingStrategy=
			// new ColumnPositionMappingStrategy();
			// mappingStrategy.setType(PerfFixData.class);
			//
			// // Arrange column name as provided in below array.
			// String[] columns = new String[]
			// { "projName","projGitUrl","fixCommitID", "fixCommitMsg",
			// "patchPath","srcFileChangeCount","assetChangeCount" };
			// mappingStrategy.setColumnMapping(columns);
			//
			//
			// // Createing StatefulBeanToCsv object
			// StatefulBeanToCsvBuilder<PerfFixData> builder=
			// new StatefulBeanToCsvBuilder(writer);
			// StatefulBeanToCsv beanWriter =
			// builder.withMappingStrategy(mappingStrategy).build();
			//
			// // Write list to StatefulBeanToCsv object
			// beanWriter.write(fixdata);

			// // closing the writer object
			// writer.close();

//			StatefulBeanToCsvBuilder<TestData> builder = new StatefulBeanToCsvBuilder<TestData>(writer);
//			StatefulBeanToCsv<TestData> beanWriter = builder.build();
//
//			beanWriter.write(fixdata);
//			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void newwriteTestMethodListBeanToFile(List<TestMethodData> fixdata, String csvfilepath)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		try {

			// Creating writer class to generate
			// csv file
			//FileWriter writer = new FileWriter(csvfilepath);
			CustomMappingStrategy<TestMethodData> mappingStrategy = new CustomMappingStrategy<>();
	        mappingStrategy.setType(TestMethodData.class);
			
			 Writer writer = new FileWriter(csvfilepath);
		        StatefulBeanToCsv<TestMethodData> beanToCsv = new StatefulBeanToCsvBuilder<TestMethodData>(writer)
		                .withMappingStrategy(mappingStrategy).withSeparator(',').build();
		        beanToCsv.write(fixdata);
		        writer.close();

			// // Create Mapping Strategy to arrange the
			// // column name in order
			// ColumnPositionMappingStrategy mappingStrategy=
			// new ColumnPositionMappingStrategy();
			// mappingStrategy.setType(PerfFixData.class);
			//
			// // Arrange column name as provided in below array.
			// String[] columns = new String[]
			// { "projName","projGitUrl","fixCommitID", "fixCommitMsg",
			// "patchPath","srcFileChangeCount","assetChangeCount" };
			// mappingStrategy.setColumnMapping(columns);
			//
			//
			// // Createing StatefulBeanToCsv object
			// StatefulBeanToCsvBuilder<PerfFixData> builder=
			// new StatefulBeanToCsvBuilder(writer);
			// StatefulBeanToCsv beanWriter =
			// builder.withMappingStrategy(mappingStrategy).build();
			//
			// // Write list to StatefulBeanToCsv object
			// beanWriter.write(fixdata);

			// // closing the writer object
			// writer.close();

//			StatefulBeanToCsvBuilder<TestData> builder = new StatefulBeanToCsvBuilder<TestData>(writer);
//			StatefulBeanToCsv<TestData> beanWriter = builder.build();
//
//			beanWriter.write(fixdata);
//			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public <T> void writeBeanToFile(List<T> fixdata, String csvfilepath)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		try {

			// Creating writer class to generate
			// csv file
			FileWriter writer = new FileWriter(csvfilepath);

			// // Create Mapping Strategy to arrange the
			// // column name in order
			// ColumnPositionMappingStrategy mappingStrategy=
			// new ColumnPositionMappingStrategy();
			// mappingStrategy.setType(PerfFixData.class);
			//
			// // Arrange column name as provided in below array.
			// String[] columns = new String[]
			// { "projName","projGitUrl","fixCommitID", "fixCommitMsg",
			// "patchPath","srcFileChangeCount","assetChangeCount" };
			// mappingStrategy.setColumnMapping(columns);
			//
			//
			// // Createing StatefulBeanToCsv object
			// StatefulBeanToCsvBuilder<PerfFixData> builder=
			// new StatefulBeanToCsvBuilder(writer);
			// StatefulBeanToCsv beanWriter =
			// builder.withMappingStrategy(mappingStrategy).build();
			//
			// // Write list to StatefulBeanToCsv object
			// beanWriter.write(fixdata);

			// // closing the writer object
			// writer.close();

			StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder<T>(writer);
			StatefulBeanToCsv<T> beanWriter = builder.build();

			beanWriter.write(fixdata);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
