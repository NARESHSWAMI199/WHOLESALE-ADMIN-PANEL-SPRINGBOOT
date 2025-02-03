package com.sales.utils;

import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class ReadExcel {

    int totalColumns = 7;

    @Autowired
    Logger logger;

    public Map getExcelDataInJsonFormat(MultipartFile excelFile) throws IOException {

        Map result = new HashMap();
        List<String> columnsList = new ArrayList<>();
        try {
            DataFormatter formatter = new DataFormatter();
            logger.info("Excel file reading....");
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile.getInputStream());

            int totalSheets = workbook.getNumberOfSheets();

            /** for visit worksheet */
            for (int sheetNumber = 0; sheetNumber < totalSheets; sheetNumber++) {
                XSSFSheet worksheet = workbook.getSheetAt(sheetNumber);
                /** for visit rows */
                for (int rowNumber = 0; rowNumber < worksheet.getPhysicalNumberOfRows(); rowNumber++) {
                    XSSFRow row = worksheet.getRow(rowNumber);
                    /** for visit columns */
                    for (int colNumber = 0; colNumber < totalColumns; colNumber++) {
                        XSSFCell column = row.getCell(colNumber);
                        if (rowNumber == 0) {
                            /** column the first row */
                            columnsList.add(column.getStringCellValue());
                            result.put(column.getStringCellValue().toUpperCase(), new ArrayList<>());
                        } else {
                            String columnName = columnsList.get(colNumber);
                            List updatedList = (List) result.get(columnName);
                            String val = formatter.formatCellValue(column);
                            updatedList.add(val);
                            result.put(columnName, updatedList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Excel file reading END.... ");
        return result;
    }

}
