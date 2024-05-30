package com.example.util;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * <dependency>
 * <groupId>org.apache.poi</groupId>
 * <artifactId>poi</artifactId>
 * <version>5.2.5</version>
 * </dependency>
 * <dependency>
 * <groupId>org.apache.poi</groupId>
 * <artifactId>poi-ooxml</artifactId>
 * <version>5.2.5</version>
 * </dependency>
 */
public class ExcelUtils {

    /**
     * 读取Excel文件的每一行数据，自动兼容xls和xlsx格式。
     *
     * @param filePath    文件路径
     * @param sheetIndex  工作表索引
     * @param rowConsumer 每行数据的处理器
     */
    public static void readExcel(String filePath, int sheetIndex, Consumer<Row> rowConsumer) {
        try (InputStream is = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            for (Row row : sheet) {
                rowConsumer.accept(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入数据到Excel文件，自动处理xls和xlsx格式。
     *
     * @param filePath  文件路径
     * @param sheetName 工作表名称
     * @param data      数据列表，每个数组代表一行数据
     */
    public static void writeExcel(String filePath, String sheetName, Iterable<String[]> data) {
        try (Workbook workbook = filePath.endsWith(".xls") ? new HSSFWorkbook() : new XSSFWorkbook();
             OutputStream os = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet(sheetName);
            int rowIndex = 0;
            for (String[] rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(rowData[i]);
                }
            }
            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据模板Excel文件写入数据，自动处理xls和xlsx格式。
     *
     * @param templatePath 模板文件路径
     * @param outputPath   输出文件路径
     * @param sheetName    工作表名称
     * @param data         数据列表，每个数组代表一行数据，数组第一位是行号，第二位是列号
     */
    public static void writeExcelUsingTemplate(String templatePath, String outputPath, String sheetName, List<int[]> data) {
        try (InputStream is = new FileInputStream(templatePath);
             Workbook workbook = WorkbookFactory.create(is);
             OutputStream os = new FileOutputStream(outputPath)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }
            Sheet finalSheet = sheet;
            data.forEach(rowData -> {
                int rowIndex = rowData[0];
                int columnIndex = rowData[1];
                String cellValue = String.valueOf(rowData[2]);

                Row row = finalSheet.getRow(rowIndex);
                if (row == null) {
                    row = finalSheet.createRow(rowIndex);
                }
                Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(cellValue);
            });

            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用SXSSF写入大型Excel文件。
     *
     * @param filePath  文件路径
     * @param sheetName 工作表名称
     * @param data      数据列表，每个数组代表一行数据
     */
    public static void writeLargeExcel(String filePath, String sheetName, List<String[]> data) {
        // 在内存中保留100行数据，超过这个数量的数据将被刷新到磁盘
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100); // 保留100行在内存中
             FileOutputStream out = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet(sheetName);
            int rowIndex = 0;
            for (String[] rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(rowData[i]);
                }
            }
            workbook.write(out);
            // 清除在写文件过程中保存的临时文件
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用SAX解析器读取大型Excel文件。
     *
     * @param filePath    文件路径
     * @param sheetIndex  工作表索引
     * @param rowConsumer 处理每行数据的消费者接口
     */
    public static void readLargeExcel(String filePath, int sheetIndex, Consumer<String[]> rowConsumer) {
        try (OPCPackage pkg = OPCPackage.open(filePath)) {
            XSSFReader reader = new XSSFReader(pkg);
            SharedStringsTable sst = (SharedStringsTable) reader.getSharedStringsTable();

            XMLReader parser = XMLReaderFactory.createXMLReader();
            XSSFSheetXMLHandler handler = new XSSFSheetXMLHandler(
                    reader.getStylesTable(), sst, new SheetToCSV(rowConsumer), false);

            InputStream sheet = reader.getSheet("rId" + (sheetIndex + 1));
            parser.setContentHandler(handler);
            parser.parse(new InputSource(sheet));
            sheet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SheetToCSV implements XSSFSheetXMLHandler.SheetContentsHandler {
        private Consumer<String[]> rowConsumer;
        private String[] values = new String[10]; // Adjust the size as per your need

        public SheetToCSV(Consumer<String[]> rowConsumer) {
            this.rowConsumer = rowConsumer;
        }

        public void startRow(int rowNum) {
        }

        public void endRow(int rowNum) {
            rowConsumer.accept(values.clone());
            values = new String[10]; // Reset array
        }

        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            int idx = cellReference.charAt(0) - 'A'; // Assumes single-digit columns
            values[idx] = formattedValue;
        }
    }

    /**
     * 根据模板Excel文件和表头来写入数据。
     *
     * @param templatePath 模板文件路径
     * @param outputPath   输出文件路径
     * @param sheetName    工作表名称
     * @param headerRowNum 表头所在的行号（从0开始计数）
     * @param data         数据列表，每个Map代表一行数据，键为表头名称，值为单元格值
     */
    public static void writeExcelWithHeader(String templatePath, String outputPath, String sheetName, int headerRowNum, List<Map<String, String>> data) {
        try (InputStream is = new FileInputStream(templatePath);
             Workbook workbook = WorkbookFactory.create(is);
             OutputStream os = new FileOutputStream(outputPath)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            // 读取表头，并建立表头名称到列号的映射
            Row headerRow = sheet.getRow(headerRowNum);
            Map<String, Integer> headerMap = new HashMap<>();
            headerRow.forEach(cell -> headerMap.put(cell.getStringCellValue(), cell.getColumnIndex()));

            // 根据表头映射填写数据
            int rowIndex = headerRowNum + 1;
            for (Map<String, String> rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                headerMap.forEach((key, colIndex) -> {
                    Cell cell = row.createCell(colIndex);
                    String value = rowData.getOrDefault(key, "");
                    cell.setCellValue(value);
                });
            }

            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String projectDir = System.getProperty("user.dir");
        String excelFile4Read = projectDir + File.separator + "src/main/java/com/example/util/assets/score.xlsx";
        readExcel(excelFile4Read, 0, line -> {
            IntStream.range(line.getFirstCellNum(), line.getLastCellNum() + 1).forEach(i ->
                    System.out.print(line.getCell(i))
            );
            System.out.println();
        });
    }
}