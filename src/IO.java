import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IO {

	private static IO instance = new IO();

	public static IO getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}

	public List<Pair> readXML(File file) {
		// TODO Auto-generated method stub
		List<Pair> list = new ArrayList<>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document document = dbf.newDocumentBuilder().parse(file);
			// new InputSource(new StringReader(read(file).toString())));
			NodeList nodeList = document.getElementsByTagName("string");
			for (int x = 0, size = nodeList.getLength(); x < size; x++) {
				// System.out.println();
				Pair pair = new Pair();
				Node node = nodeList.item(x);
				pair.first = node.getAttributes().getNamedItem("name").getNodeValue();
				pair.second = "";
				// System.out.println();
				for (int i = 0; i < node.getChildNodes().getLength(); i++) {
					Node node2 = node.getChildNodes().item(i);
					if (node2.getNodeValue() != null)
						pair.second += node2.getNodeValue();
					else if (node2.getNodeName() != null)
						pair.second += String.format("<%s>%s</%s>", node2.getNodeName(), node2.getTextContent(),
								node2.getNodeName());

					// System.out.println(node2.getNodeName() + " " +
					// node2.getTextContent() + " " + node2.getNodeType());
				}
				// pair.second.repla

				// System.out.println(pair.first + " " + pair.second);
				list.add(pair);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Pair> readString(File file) {
		// TODO Auto-generated method stub
		List<Pair> list = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder builder = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("\"")) {
					builder = new StringBuilder();
				}
				if (builder != null) {
					builder.append(line);

					if (line.endsWith(";")) {
						String[] split = builder.toString().split("=", 2);
						System.out.println("pass : " + split.length);
						if (split.length >= 2) {
							Pair pair = new Pair();
							String name = split[0].trim();
							String value = split[1].trim();
							pair.first = name.substring(1, name.length() - 1);
							pair.second = value.substring(1, value.length() - 1);
							System.out.printf("first : %s , second : %s\n", pair.first, pair.second);
							list.add(pair);

						}
					}
				}
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public StringBuilder read(File file) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(builder.toString());
		return builder;
	}

	public void write(File file, TreeMap<String, String[]> map) {
		// TODO Auto-generated method stub
		try {
			PrintWriter writer = new PrintWriter(file);

			while (!map.isEmpty()) {
				Entry<String, String[]> entry = map.pollFirstEntry();
				writer.print(entry.getKey());

				for (String s : entry.getValue()) {
					writer.print("," + (s == null ? "" : s));
				}

				writer.println();
			}

			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public File writeAsExcel(File file, TreeMap<String, String[]> map) {
		// Blank workbook
		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("Employee Data");

		// Iterate over data and write to sheet
		Set<String> keyset = map.keySet();

		int rownum = 0;
		for (String key : keyset) {
			// create a row of excelsheet
			Row row = sheet.createRow(rownum++);

			// get object array of prerticuler key
			String[] objArr = map.get(key);

			int cellnum = 0;
			sheet.setColumnWidth(cellnum, 5000);
			Cell cell = row.createCell(cellnum++);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(HSSFColor.AQUA.index);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setAlignment(HorizontalAlignment.CENTER);

			XSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 12);
			font.setBold(true);
			style.setFont(font);
			style.setWrapText(true);

			cell.setCellValue(key);
			cell.setCellStyle(style);
			for (String obj : objArr) {
				sheet.setColumnWidth(cellnum, 10000);
				cell = row.createCell(cellnum++);

				cell.setCellValue(obj);

				if (rownum == 1)
					cell.setCellStyle(style);

			}
		}
		try {
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
			System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	public TreeMap<String, String[]> readExcel(File selectedFile) {

		System.out.println("===============READ EXCEL=====================");
		TreeMap<String, String[]> map = new TreeMap<>();
		try {
			FileInputStream fis = new FileInputStream(selectedFile);

			XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				String key = null;
				if (cellIterator.hasNext())
					key = cellIterator.next().getStringCellValue();

				System.out.println(key);

				List<String> list = new ArrayList<>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					list.add(cell.getStringCellValue());
				}
				map.put(key, list.toArray(new String[list.size()]));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return map;

	}

	public void writeXML(File selectedFolder, TreeMap<String, String[]> map) {

		System.out.println("===============WRITE XML======================");
		// TODO Auto-generated method stub
		Entry<String, String[]> entry = map.pollFirstEntry();
		while (!".".equals(entry.getKey()))
			entry = map.pollFirstEntry();
		String[] folderNames = entry.getValue();
		PrintWriter[] writers = new PrintWriter[entry.getValue().length];
		for (int i = 0; i < writers.length; i++) {
			System.out.println(folderNames[i]);
			String folderName = folderNames[i].toLowerCase();
			folderName = "values" + (folderName.contains("default") ? "" : "-" + folderName);
			File folder = new File(selectedFolder, folderName);
			if (!folder.exists())
				folder.mkdirs();
			try {
				writers[i] = new PrintWriter(new File(folder, "strings.xml"));
				writers[i].println("<resources>");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("===============WRITE XML=2====================");

		while (!map.isEmpty()) {
			entry = map.pollFirstEntry();
			System.out.print(entry.getKey() + ": ");
			String[] values = entry.getValue();
			if (values != null) {

				boolean translateable = values.length == writers.length;
				for (String value : values)
					if (value == null)
						translateable = false;
					else if (value.trim().length() == 0)
						translateable = false;

				for (int i = 0; i < writers.length && i < values.length; i++) {
					if (values[i] == null)
						continue;
					else if (values[i].length() == 0)
						continue;

					System.out.print(values[i] + " , ");
					writers[i].print(String.format("\t<string name=\"%s\" formatted=\"false\"", entry.getKey()));
					if (!translateable)
						writers[i].print(" translatable=\"false\"");
					writers[i].print(">");
					writers[i].print(StringEscapeUtils.escapeXml11(values[i].replaceAll("\n", "\\\n"))
							.replaceAll("(&lt;b&gt;)", "<b>").replaceAll("(&lt;/b&gt;)", "</b>")
							.replaceAll("(&lt;i&gt;)", "<i>").replaceAll("(&lt;/i&gt;)", "</i>")
							.replaceAll("(&lt;u&gt;)", "<u>").replaceAll("(&lt;/u&gt;)", "</u>")
							.replaceAll("(&apos;)", "\\\\'").replaceAll("\\\\\\\\'", "\\\\'"));
					writers[i].println("</string>");
				}
				System.out.println();
			}
		}
		for (int i = 0; i < writers.length; i++) {
			writers[i].println("</resources>");
			writers[i].close();
		}
	}

	public void writeString(File selectedFolder, TreeMap<String, String[]> map) {

		System.out.println("===============WRITE XML======================");
		// TODO Auto-generated method stub
		Entry<String, String[]> entry = map.pollFirstEntry();
		while (!".".equals(entry.getKey()))
			entry = map.pollFirstEntry();
		String[] folderNames = entry.getValue();
		PrintWriter[] writers = new PrintWriter[entry.getValue().length];

		for (int i = 0; i < writers.length; i++) {
			System.out.println(folderNames[i]);
			String folderName = folderNames[i].toLowerCase();
			folderName = (folderName.contains("default") ? "Base" : folderName)+ ".lproj";

			File folder = new File(selectedFolder, folderName );
			if (!folder.exists())
				folder.mkdirs();
			try {

				writers[i] = new PrintWriter(new File(folder, "Localizable.strings"));
				writers[i].println("/* \n" + "  Localizable.strings\n" + "  Lansuma\n" + "\n"
						+ "  Created by Muhammad Norzariman on 08/02/2018.\n"
						+ "  Copyright © 2018 Dattel. All rights reserved.\n" + "*/");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("===============WRITE XML=2====================");

		while (!map.isEmpty()) {
			entry = map.pollFirstEntry();
			System.out.print(entry.getKey() + ": ");
			String[] values = entry.getValue();
			if (values != null) {

				boolean translateable = values.length == writers.length;
				for (String value : values)
					if (value == null)
						translateable = false;
					else if (value.trim().length() == 0)
						translateable = false;

				for (int i = 0; i < writers.length && i < values.length; i++) {
					if (values[i] == null)
						continue;
					else if (values[i].length() == 0)
						continue;

					System.out.print(values[i] + " , ");
					writers[i]
							.println(
									String.format(
											"\"%s\" = \"%s\";", entry
													.getKey(),
											StringEscapeUtils
													.escapeXml11(
															values[i].replaceAll("\n", "\\\n").replaceAll("\r", "\\\n"))
													.replaceAll("(&lt;b&gt;)", "<b>").replaceAll("(&lt;/b&gt;)", "</b>")
													.replaceAll("(&lt;i&gt;)", "<i>").replaceAll("(&lt;/i&gt;)", "</i>")
													.replaceAll("(&lt;u&gt;)", "<u>").replaceAll("(&lt;/u&gt;)", "</u>")
													.replaceAll("(&apos;)", "\\\\'").replaceAll("\\\\\\\\'", "\\\\'")));
				}
				System.out.println();
			}
		}
		for (int i = 0; i < writers.length; i++) {
			writers[i].close();
		}
	}

}
