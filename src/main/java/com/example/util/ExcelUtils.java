package com.example.util;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    public static final String NULL = "_NULL_";
    public static final String FORMULA = "_FORMULA_";
    public static final String UNKNOWN = "_UNKNOWN_";


    public static void readExcel(String filePath, int sheetIndex, Consumer<List<String>> rowDataHandler) {
        try (Workbook sheets = WorkbookFactory.create(new File(filePath))) {
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            int startIndex = sheet.getFirstRowNum();
            int lastIndex = sheet.getLastRowNum();
            for (int rowIndex = startIndex; rowIndex < lastIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                short startColumnIndex = row.getFirstCellNum();
                short lastColumnIndex = row.getLastCellNum();
                rowDataHandler.accept(IntStream.range(startColumnIndex, lastColumnIndex + 1)
                        .mapToObj(index -> getCellValue(row.getCell(index))).collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> readExcel(String filePath, int sheetIndex, Function<List<String>, List<String>> rowDataHandler) {
        try (Workbook sheets = WorkbookFactory.create(new File(filePath))) {
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            int startIndex = sheet.getFirstRowNum();
            int lastIndex = sheet.getLastRowNum();
            List<List<String>> dataList = new ArrayList<>();
            for (int rowIndex = startIndex; rowIndex < lastIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                short startColumnIndex = row.getFirstCellNum();
                short lastColumnIndex = row.getLastCellNum();
                List<String> list = rowDataHandler.apply(IntStream.range(startColumnIndex, lastColumnIndex + 1).mapToObj(index -> getCellValue(row.getCell(index))).collect(Collectors.toList()));
                dataList.add(list);
            }
            return dataList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> readExcel(String filePath, int sheetIndex) {
        try (Workbook sheets = WorkbookFactory.create(new File(filePath))) {
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            int startIndex = sheet.getFirstRowNum();
            int lastIndex = sheet.getLastRowNum();
            List<List<String>> dataList = new ArrayList<>();
            for (int rowIndex = startIndex; rowIndex < lastIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                short startColumnIndex = row.getFirstCellNum();
                short lastColumnIndex = row.getLastCellNum();
                List<String> list = IntStream.range(startColumnIndex, lastColumnIndex + 1).mapToObj(index -> getCellValue(row.getCell(index))).collect(Collectors.toList());
                dataList.add(list);
            }
            return dataList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> readExcel(String filePath, String sheetName) {
        try (Workbook sheets = WorkbookFactory.create(new File(filePath))) {
            int sheetIndex = -1;
            for (int i = 0; i < sheets.getNumberOfSheets(); i++) {
                if (sheetName.equals(sheets.getSheetName(i))) sheetIndex = i;
            }
            if (sheetIndex == -1) throw new RuntimeException("不存在的sheetName");
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            int startIndex = sheet.getFirstRowNum();
            int lastIndex = sheet.getLastRowNum();
            List<List<String>> dataList = new ArrayList<>();
            for (int rowIndex = startIndex; rowIndex < lastIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                short startColumnIndex = row.getFirstCellNum();
                short lastColumnIndex = row.getLastCellNum();
                List<String> list = IntStream.range(startColumnIndex, lastColumnIndex + 1).mapToObj(index -> getCellValue(row.getCell(index))).collect(Collectors.toList());
                dataList.add(list);
            }
            return dataList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readExcelSheetsName(String filePath) {
        try (Workbook sheets = WorkbookFactory.create(new File(filePath))) {
            List<String> sheetsName = new ArrayList<>();
            for (int i = 0; i < sheets.getNumberOfSheets(); i++) {
                sheetsName.add(sheets.getSheetName(i));
            }
            return sheetsName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * readExcelHead By line num
     *
     * @param filePath   excel file path
     * @param sheetIndex from 0 to max
     * @param rowIndex   from 0 to max
     * @return line data
     */
    public static List<String> readExcelHead(String filePath, int sheetIndex, int rowIndex) {
        try (Workbook sheets = WorkbookFactory.create(new File(filePath))) {
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            Row row = sheet.getRow(rowIndex);
            short startColumnIndex = row.getFirstCellNum();
            short lastColumnIndex = row.getLastCellNum();
            return IntStream.range(startColumnIndex, lastColumnIndex + 1).mapToObj(index -> getCellValue(row.getCell(index))).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCellValue(Cell cell) {
        if (null == cell) return NULL;
        return switch (cell.getCellType()) {
            case NUMERIC ->
                    DateUtil.isCellDateFormatted(cell) ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(DateUtil.getLocalDateTime(cell.getNumericCellValue())) : String.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue();
            case FORMULA -> FORMULA;
            case BLANK -> "";
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case ERROR -> String.valueOf(cell.getErrorCellValue());
            default -> UNKNOWN;
        };
    }

    public static void main(String[] args) {
        String iphoneExcel = System.getProperty("user.dir") + File.separator + "src/main/java/com/example/util/assets/iphone.xlsx";
        // 消费每行数据
        readExcel(iphoneExcel, 0, list -> {
            System.out.print(list);
            System.out.println();
        });

        // 转换每行数据
        List<List<String>> excelData = readExcel(iphoneExcel, 0, list -> {
            return list.stream().map(data -> data + "_new").collect(Collectors.toList());
        });
        excelData.forEach(System.out::println);

        // 获取全部数据
        List<List<String>> lists = readExcel(iphoneExcel, 0);

        // 获取sheet名称
        readExcelSheetsName(iphoneExcel).forEach(System.out::println);

        // 获取行标题
        System.out.println("===> get excel head:");
        readExcelHead(iphoneExcel, 0, 0).forEach(head -> {
            System.out.print(head + "\t");
        });
    }

}

