package com.sales.utils;

import com.sales.admin.repositories.ItemHbRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class WriteExcelUtil {

    private final Logger logger = LoggerFactory.getLogger(WriteExcelUtil.class);

    @Value("${excel.export.absolute}")
    String excelExportAbsolutePath;

    @Value("${excel.notUpdated.absolute}")
    String getExcelNotUpdateItemsFolderPath;

    public String createExcelSheet(Map<String, List<Object>> data, int totalRow, List<String> headers,String folderName) throws IOException {
        try(XSSFWorkbook workbook = new XSSFWorkbook()) {
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
            int rowNo = 1;
            for (int index = 0; index < totalRow; index++) {
                Row row = sheet.createRow(rowNo);
                for (int colNo = 0; colNo < headers.size(); colNo++) {
                    String headerName = headers.get(colNo).replace("-","");
                    if(headerName.equals("TOKEN")) headerName = "SLUG";
                    Cell cell = row.createCell(colNo);
                    String value = String.valueOf(data.get(headerName).get(index));
                    if (value.equals("N") && headerName.equals("LABEL")) {
                        value = "New";
                    } else if (value.equals("O") && headerName.equals("LABEL")) {
                        value = "Old";
                    }
                    else if (value.equals("A") && headerName.equals("STATUS")) {
                        value = "Active";
                    }else if (value.equals("D") && headerName.equals("STATUS")) {
                        value = "Deactive";
                    }
                    else if (value.equals("Y") && headerName.equals("INSTOCK")) {
                        value = "Yes";
                    }else if (value.equals("N") && headerName.equals("INSTOCK")) {
                        value = "No";
                    }
                    else if (headerName.equals("CREATEDAT") || headerName.equals("UPDATEDAT")) {
                        value = Utils.getMillisToDate(((Double)Double.parseDouble(value)).longValue());
                    }
                    cell.setCellValue(value);
                    cell.setCellStyle(style);
                }
                rowNo++;
            }

            Path absoluteExcelPath = Paths.get(excelExportAbsolutePath);
            Path resolve = absoluteExcelPath.resolve(folderName).normalize();
            File file = new File(String.valueOf(resolve.toAbsolutePath()));

            if(!file.exists()) {
                boolean created = file.mkdirs();
                if(created) logger.info("The file is created at : {}",file.getAbsolutePath());
            }
            String path = file.getAbsolutePath();
            String fileLocation = path +File.separator + "temp.xlsx";
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            return fileLocation;
        }
    }

    public String writeNotUpdatedItemsExcel(List<ItemHbRepository.ItemUpdateError> data, String[] headers, String folderName) throws IOException {

        try (XSSFWorkbook workbook =new XSSFWorkbook()) {
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
            for (int cellNo = 0; cellNo < headers.length; cellNo++) {
                Cell headerCell = header.createCell(cellNo);
                headerCell.setCellValue(headers[cellNo]);
                headerCell.setCellStyle(headerStyle);
            }
            int rowNo = 1;
            for (int index = 0; index < data.size(); index++) {
                Map<String, Object> itemDetail = data.get(index).getItemRowDetail();
                Row row = sheet.createRow(rowNo);
                for (int colNo = 0; colNo < headers.length; colNo++) {
                    String headerName = headers[colNo];
                    Cell cell = row.createCell(colNo);
                    String value = String.valueOf(itemDetail.get(headerName));
                    if (headerName.equals("REASON")) value = data.get(index).getErrorMessage();
                    cell.setCellValue(value);
                    cell.setCellStyle(style);
                }
                rowNo++;
            }

            File file = new File(getExcelNotUpdateItemsFolderPath + folderName);
            if (!file.exists()) file.mkdirs();
            String path = file.getAbsolutePath();
            String fileName = "temp.xlsx";
            String fileLocation = path + File.separator + fileName;
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            logger.info("The excel file location of not updated items : {}", Utils.sanitizeForLog(fileLocation));
            return fileName;
        }
    }


}
