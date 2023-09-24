package com.qiankun.excel;

import com.qiankun.common.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class QiankunExcel {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    int startRow = 0, startCol = 0;
    Integer rows = null, cols = null;
    boolean merge = true;
    BiConsumer<Workbook, Cell> afterCell;

    protected QiankunExcel() {}

    protected void solveStart() {

    }

    protected void solve(Integer rows, Integer cols) {

    }

    public boolean merge() {
        return merge;
    }

    public QiankunExcel setAfterCell(BiConsumer<Workbook, Cell> afterCell) {
        this.afterCell = afterCell;
        return this;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public Integer getRows() {
        return rows;
    }

    public Integer getCols() {
        return cols;
    }

    protected void travel(Consumer<QiankunCell> visitor) {

    }


    public void writeTo(Workbook wb, Sheet sheet) {
        writeTo(wb, sheet, null, null);
    }

    public void writeTo(Workbook wb, Sheet sheet, CellStyle cellStyle) {
        writeTo(wb, sheet, cellStyle, null);
    }

    public void writeTo(Workbook wb, Sheet sheet, CellStyle cellStyle,CellStyle headerCellStyle) {
        writeTo(wb, sheet, cellStyle, headerCellStyle,null);
    }

    public void writeTo(Workbook wb, Sheet sheet, CellStyle cellStyle, CellStyle headerCellStyle,  ExcelConfig excelConfig) {
        QiankunExcel mme = this;
        if(cellStyle == null){
            cellStyle = defaultCellStyle(wb,excelConfig);
        }
        if(headerCellStyle == null) {
            headerCellStyle = defaultHeaderCellStyle(wb,excelConfig);
        }

        // 下面两个样式是防止合并单元格 OA预览拆开合并单元格，导致页面混乱问题
        CellStyle noStyle = wb.createCellStyle();
        noStyle.setBorderLeft(BorderStyle.NONE);
        noStyle.setBorderRight(BorderStyle.NONE);
        noStyle.setBorderBottom(BorderStyle.NONE);
        noStyle.setBorderTop(BorderStyle.NONE);
        noStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        noStyle.setWrapText(Objects.isNull(excelConfig) || Objects.isNull(excelConfig.getWrapText()) || excelConfig.getWrapText());
        noStyle.setAlignment(HorizontalAlignment.CENTER);


        CellStyle headerNoStyle = wb.createCellStyle();
        headerNoStyle.setBorderLeft(BorderStyle.NONE);
        headerNoStyle.setBorderRight(BorderStyle.NONE);
        headerNoStyle.setBorderBottom(BorderStyle.NONE);
        headerNoStyle.setBorderTop(BorderStyle.NONE);
        headerNoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerNoStyle.setWrapText(Objects.isNull(excelConfig) || Objects.isNull(excelConfig.getWrapText()) || excelConfig.getWrapText());
        headerNoStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short)14);
        headerNoStyle.setFont(headerFont);

        mme.travel(mmc -> {
            if(mmc.getRows() == null || mmc.getCols() == null) {
                throw new RuntimeException(String.format("存在未确定行数或者列数的单元格: %s", mmc.getValue()));
            }
        });
        mme.solveStart();

        for(int r = 0; r < mme.getRows(); r ++) {
            Row row = sheet.createRow(r);
            CellStyle finalCellStyle = cellStyle;
            CellStyle finalHeaderCellStyle = headerCellStyle;
            mme.travel(mmc -> {
                if(mmc.getStartRow() == row.getRowNum()) {
                    Cell cell = row.createCell(mmc.getStartCol());
                    if (mmc.apply == null && !mmc.isHeader) {
                        cell.setCellStyle(finalCellStyle);
                    }else if(Objects.equals(mmc.apply,1) && !mmc.isHeader){
                        cell.setCellStyle(noStyle);
                    }else if(Objects.equals(mmc.apply,0) && !mmc.isHeader){
                        cell.setCellStyle(headerNoStyle);
                    // 标题样式
                    }else if(mmc.isHeader){
                        cell.setCellStyle(finalHeaderCellStyle);
                    }
                    if(mmc.getValue() == null) {
                        cell.setCellValue("");
                    } else {
                        Object v = mmc.getValue();
                        if (v instanceof String && excelConfig != null && ((String) v).contains("${")) {
                            v = EvalAnalysisUtils.eval(v.toString(), excelConfig);
                        }
                        if (v instanceof Date) {
                            cell.setCellValue(DateUtils.dateStr((Date) v));
                        } else {
                            cell.setCellValue(String.valueOf(v));
                        }
                    }
                    if (mmc.afterCell != null) {
                        mmc.afterCell.accept(wb, cell);
                    }
                }
            });
        }

        CellStyle finalCellStyle1 = cellStyle;
        mme.travel(mmc -> {
            if (mmc.getRows() > 1 || mmc.getCols() > 1) {
                CellRangeAddress cra = new CellRangeAddress(mmc.getStartRow(), mmc.getStartRow() + mmc.getRows() - 1, mmc.getStartCol(), mmc.getStartCol() + mmc.getCols() - 1);
                if (mmc.merge) {
                    sheet.addMergedRegion(cra);
                    if (finalCellStyle1 != null) {
                        RegionUtil.setBorderBottom(finalCellStyle1.getBorderBottom(), cra, sheet);
                        RegionUtil.setBorderLeft(finalCellStyle1.getBorderLeft(), cra, sheet);
                        RegionUtil.setBorderRight(finalCellStyle1.getBorderRight(), cra, sheet);
                        RegionUtil.setBorderTop(finalCellStyle1.getBorderTop(), cra, sheet);
                    }
                } else if (finalCellStyle1 != null) {
                    for (int row = mmc.getStartRow(); row < mmc.getStartRow() + mmc.getRows(); row++) {
                        for (int col = mmc.getStartCol(); col < mmc.getStartCol() + mmc.getCols(); col++) {
                            Row sheetRow = sheet.getRow(row);
                            if (sheetRow == null) {
                                sheetRow = sheet.createRow(row);
                            }
                            Cell cell = sheetRow.getCell(col);
                            if (cell == null) {
                                cell = sheetRow.createCell(col);
                                cell.setCellValue("");
                            }
                            cell.setCellStyle(Objects.isNull(mmc.apply) ? finalCellStyle1 : noStyle);
                        }
                    }
                }
            }
        });

        if (Objects.nonNull(excelConfig)) {
            //----指定列自适应宽度
            if (excelConfig.getDataStartRow() == null) {
                excelConfig.setDataStartRow(Math.min(sheet.getFirstRowNum() , sheet.getLastRowNum()));
            }
            if (excelConfig.getDataEndRow() == null) {
                excelConfig.setDataEndRow(sheet.getLastRowNum());
            }
            if (excelConfig.getDataStartRow() != null && excelConfig.getDataStartRow() >= 0 && excelConfig.getDataEndRow() != null && excelConfig.getDataEndRow() > 0) {
                //---在设置行范围内对指定列设置自适应宽度
                int[] widthArray = new int[mme.getCols()];
                for (int row = excelConfig.getDataStartRow(); row < excelConfig.getDataEndRow(); row++) {
                    Row sheetRow = sheet.getRow(row);
                    for (int col = 0; col < mme.getCols(); col++) {
                        if (excelConfig.getAutoWidthCols().contains(col)) {
                            Cell cell = sheetRow.getCell(col);
                            int length = 0;
                            try {
                                length = cell.getStringCellValue().getBytes("GBK").length;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (widthArray[col] < length) {
                                widthArray[col] = length;
                            }
                        } else {
                            widthArray[col] = -1;
                        }
                    }
                }

                Arrays.stream(widthArray).forEach(x -> logger.error("++++++++++++++++++{}", x));

                for (int col = 0; col < widthArray.length; col++) {
                    if (widthArray[col] > 0) {
                        // cell 最大列宽为为 255 * 256
                        sheet.setColumnWidth(col, (Math.min(widthArray[col] + 6, 255) * 256));
                    } else {
                        try {
                            sheet.setColumnWidth(col, ("一二三四五六".getBytes("GBK").length + 2 ) * 256);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            // 设置隐藏列
            List<Integer> hideCols = excelConfig.getHideCols();
            if (!CollectionUtils.isEmpty(hideCols)) {
                for (Integer column : hideCols) {
                    sheet.setColumnHidden(column, true);
                }
            }

            // 设置居中列
            // if(!CollectionUtils.isEmpty(excelConfig.getCenterCols())){
            //     int lastRowNum = sheet.getLastRowNum();
            //     for (int i = excelConfig.getDataStartRow(); i < Math.min(lastRowNum,excelConfig.getDataStartRow()); i++) {
            //         Row row = sheet.getRow(i);
            //         for (Integer centerCol : excelConfig.getCenterCols()) {
            //             Cell cell = row.getCell(centerCol);
            //             CellStyle centerCellStyle = cell.getCellStyle();
            //             centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            //             cell.setCellStyle(centerCellStyle);
            //         }
            //     }
            // }

            // 设置字体颜色
            if (!CollectionUtils.isEmpty(excelConfig.getFontColors())) {
                List<ExcelConfig.FontColor> fontColors = excelConfig.getFontColors();
                Map<String, ExcelConfig.FontColor> fontColorMap = fontColors.stream().collect(Collectors.toMap((item -> String.join("_", item.getFontRow().toString(), item.getFontCol().toString())), Function.identity(), (f1, f2) -> f2));
                mme.travel(mmc -> {
                    Integer startRow = mmc.getStartRow();
                    Integer startCol = mmc.getStartCol();
                    String key = String.join("_", startRow.toString(), startCol.toString());
                    if(fontColorMap.containsKey(key)){
                        ExcelConfig.FontColor fontColor = fontColorMap.get(key);
                        CellStyle colorCellStyle = wb.createCellStyle();
                        colorCellStyle.setAlignment(Objects.isNull(fontColor.getHorizontalAlignment()) ? HorizontalAlignment.CENTER: fontColor.getHorizontalAlignment());
                        colorCellStyle.setVerticalAlignment(Objects.isNull(fontColor.getVerticalAlignment()) ? VerticalAlignment.CENTER : fontColor.getVerticalAlignment());
                        colorCellStyle.setWrapText(Objects.isNull(excelConfig) || Objects.isNull(excelConfig.getWrapText()) || excelConfig.getWrapText());
                        colorCellStyle.setFont(fontColor.getFont());
                        colorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        colorCellStyle.setFillForegroundColor(fontColor.getColor());
                        colorCellStyle.setBorderBottom(BorderStyle.THIN);
                        colorCellStyle.setBorderLeft(BorderStyle.THIN);
                        colorCellStyle.setBorderRight(BorderStyle.THIN);
                        colorCellStyle.setBorderTop(BorderStyle.THIN);

                        // 如果是合并的单元格
                        for (int row = mmc.getStartRow(); row < mmc.getStartRow() + mmc.getRows(); row++) {
                            Row sheetRow = sheet.getRow(row);
                            if (sheetRow == null) {
                                sheetRow = sheet.createRow(row);
                            }
                            for (int col = mmc.getStartCol(); col < mmc.getStartCol() + mmc.getCols(); col++) {
                                Cell cell = sheetRow.getCell(col);
                                if (cell == null) {
                                    cell = sheetRow.createCell(col);
                                    cell.setCellValue("");
                                }
                                cell.setCellStyle(colorCellStyle);
                            }
                        }

                    }
                });
            }
        }

    }

    /**
     * 设置默认列宽
     * @param sheet
     */
    private void setDefaultColumnWidth(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        Integer maxCols = 0;
        for (int rowNum = 0; rowNum < lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            short lastCellNum = row.getLastCellNum();
            if(lastCellNum > maxCols){
                maxCols = (int) lastCellNum;
            }
        }

        for (Integer col = 0; col < maxCols; col++) {
            sheet.setColumnWidth(col,50);
        }
    }

    /**
     * 默认表头样式
     * @param wb
     * @return
     */
    private CellStyle defaultHeaderCellStyle(Workbook wb,ExcelConfig excelConfig) {
        CellStyle defaultHeaderCellStyle = wb.createCellStyle();
        // 居中
        defaultHeaderCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        if(Objects.isNull(excelConfig) || CollectionUtils.isEmpty(excelConfig.getCenterCols())) {
            defaultHeaderCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }else {
            defaultHeaderCellStyle.setAlignment(HorizontalAlignment.LEFT);
        }
        defaultHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
        defaultHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
        defaultHeaderCellStyle.setBorderRight(BorderStyle.THIN);
        defaultHeaderCellStyle.setBorderTop(BorderStyle.THIN);
        Font defaultHeaderFont = wb.createFont();
        // defaultHeaderFont.setBold(true);
        defaultHeaderFont.setFontHeightInPoints((short) 13);
        // 淡灰色背景
        defaultHeaderCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        defaultHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        defaultHeaderCellStyle.setFont(defaultHeaderFont);
        defaultHeaderCellStyle.setWrapText(Objects.isNull(excelConfig) || Objects.isNull(excelConfig.getWrapText()) || excelConfig.getWrapText());
        return defaultHeaderCellStyle;
    }

    /**
     * 默认样式
     * @param wb
     * @return
     */
    private CellStyle defaultCellStyle(Workbook wb,ExcelConfig excelConfig) {
        CellStyle cellStyle = wb.createCellStyle();
        // 居中
        if(Objects.isNull(excelConfig) || CollectionUtils.isEmpty(excelConfig.getCenterCols())) {
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
        }else {
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
        }
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setWrapText(Objects.isNull(excelConfig) || Objects.isNull(excelConfig.getWrapText()) || excelConfig.getWrapText());
        return cellStyle;
    }

}
