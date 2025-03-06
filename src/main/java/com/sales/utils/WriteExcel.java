package com.sales.utils;

import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class WriteExcel {

    @Autowired
    Logger logger;

    public void writeExcel(Map<String, List<Object>> data, int totalRow, List<String> headers) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Items");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        headerStyle.setFont(font);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        // create excel file's headers
        for (int cellNo = 0; cellNo < headers.size(); cellNo++) {
            Cell headerCell = header.createCell(cellNo);
            headerCell.setCellValue(headers.get(cellNo));
            headerCell.setCellStyle(headerStyle);
        }
        for (int rowNo = 1; rowNo <= totalRow; rowNo++) {
            Row row = sheet.createRow(rowNo);
            for (int colNo = 0; colNo < headers.size(); colNo++) {
                Cell cell = row.createCell(colNo);
                String value = String.valueOf(data.get(headers.get(colNo)).get(rowNo - 1));
                if (value.equals("N") && headers.get(colNo).equals("LABEL")) {
                    value = "New";
                } else if (value.equals("O") && headers.get(colNo).equals("LABEL")) {
                    value = "Old";
                }
                else if (value.equals("A") && headers.get(colNo).equals("STATUS")) {
                    value = "Active";
                }else if (value.equals("D") && headers.get(colNo).equals("STATUS")) {
                    value = "Deactive";
                }
                else if (value.equals("Y") && headers.get(colNo).equals("INSTOCK")) {
                    value = "Yes";
                }else if (value.equals("N") && headers.get(colNo).equals("INSTOCK")) {
                    value = "No";
                }
                else if (headers.get(colNo).equals("CREATEDAT") || headers.get(colNo).equals("UPDATEDAT")) {
                    value = Utils.getMillisToDate(((Double)Double.parseDouble(value)).longValue());
                }
                cell.setCellValue(value);
                cell.setCellStyle(style);
            }
        }

        File currDir = new File("C:/Users/DATA/Downloads/");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length())+File.separator + "temp.xlsx";
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
        logger.info(fileLocation);

    }

}
