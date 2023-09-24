package com.qiankun.excel;

import java.util.function.Consumer;

public class QiankunCell extends QiankunExcel {

    /**
     * 值
     */
    Object value;

    /**
     * 是否是表头
     */
    Boolean isHeader;

    /**
     * 该字段的作用只是为了应付泛微OA在线预览拆解合并单元格显示混乱问题
     */
    Integer apply;

    
    protected QiankunCell(Integer rows, Integer cols, Object value,Integer apply) {
        this.rows = rows;
        this.cols = cols;
        this.value = value;
        this.apply = apply;
        this.isHeader = false;
    }

    protected QiankunCell(Integer rows, Integer cols, Object value,Integer apply,Boolean isHeader) {
        this.rows = rows;
        this.cols = cols;
        this.value = value;
        this.apply = apply;
        this.isHeader = isHeader;
    }


    protected QiankunCell(Integer rows, Integer cols, Object value) {
        this.rows = rows;
        this.cols = cols;
        this.value = value;
        this.isHeader = false;
    }

    protected QiankunCell(Integer rows, Integer cols, Object value,Boolean isHeader) {
        this.rows = rows;
        this.cols = cols;
        this.value = value;
        this.isHeader = isHeader;
    }

    @Override
    protected void solve(Integer rows, Integer cols) {
        if(rows != null) {
            this.rows = rows;
        }
        if(cols != null) {
            this.cols = cols;
        }
    }

    public Object getValue() {
        return value;
    }

    @Override
    protected void travel(Consumer<QiankunCell> visitor) {
        visitor.accept(this);
    }

    /** 固定 1 行 1 列 */
    public static QiankunExcel fixed(Object v) {
        return new QiankunCell(1, 1, v);
    }

    /** 固定 rows 行 1 列 */
    public static QiankunExcel fixedRows(int rows, Object v) {
        return new QiankunCell(rows, 1, v);
    }

    /**
     * 添加标题样式
     * @param rows
     * @param v
     * @param isHeader
     * @return
     */
    public static QiankunExcel fixedRows(int rows, Object v,Boolean isHeader) {
        return new QiankunCell(rows, 1, v,isHeader);
    }
    /** 固定 1 行 cols 列 */
    public static QiankunExcel fixedCols(int cols, Object v) {
        return new QiankunCell(1, cols, v);
    }

    /** 固定 rows 行 cols 列 */
    public static QiankunExcel fixed(int rows, int cols, Object v) {
        return new QiankunCell(rows, cols, v);
    }
    /** 自动扩高，固定1列 */
    public static QiankunExcel dynamicRows(Object v) {
        return new QiankunCell(null, 1, v);
    }

    /** 自动扩高，固定1列 */
    public static QiankunExcel dynamicRows(Object v,Integer apply) {
        return new QiankunCell(null, 1, v,apply);
    }
    /** 自动扩高，固定 cols 列 */
    public static QiankunExcel dynamicRows(int cols, Object v) {
        return new QiankunCell(null, cols, v);
    }

    /** 自动扩高，固定 cols 列 */
    public static QiankunExcel dynamicRowsHeader(int cols, Object v) {
        return new QiankunCell(null, cols, v,true);
    }

    /** 自动扩宽，固定1行 */
    public static QiankunExcel dynamicCols(Object v) {
        return new QiankunCell(1, null, v);
    }

    /** 自动扩宽，固定1行 */
    public static QiankunExcel dynamicCols(Object v,Boolean isHeader) {
        return new QiankunCell(1, null, v,isHeader);
    }

    /** 自动扩宽，固定1行 */
    public static QiankunExcel dynamicCols(Object v,Integer apply) {
        return new QiankunCell(1, null, v,apply);
    }

    /** 自动扩宽，固定1行 */
    public static QiankunExcel dynamicColsNoApply(Object v) {
        return new QiankunCell(1, null, v, ExcelConstant.Apply.defaul);
    }

    public static QiankunExcel dynamicColsNoApply(Object v,Integer apply) {
        return new QiankunCell(1, null, v,apply);
    }

    public static QiankunExcel dynamicColsNoApply(Integer row,Integer apply) {
        return new QiankunCell(row, null, null,apply);
    }

    public static QiankunExcel dynamicColsNoApply(Integer row,Integer apply, Boolean merge) {
        QiankunCell mmc = new QiankunCell(row, null, null, apply);
        mmc.merge = merge;
        return mmc;
    }

    public static QiankunExcel dynamicColsNoApply(Integer row, Object value,Integer apply) {
        return new QiankunCell(row, null, value,apply);
    }

    /** 自动扩高，固定 cols 列 */
    public static QiankunExcel dynamicRows(int cols, Object v, boolean merge) {
        QiankunExcel excel = dynamicRows(cols, v);
        excel.merge = merge;
        return excel;
    }
    /** 自动扩宽，固定rows行 */
    public static QiankunExcel dynamicCols(int rows, Object v) {
        return new QiankunCell(rows, null, v);
    }
    /** 自动扩宽，固定rows行 */
    public static QiankunExcel dynamicCols(int rows, Object v, boolean merge) {
        QiankunExcel excel = dynamicCols(rows, v);
        excel.merge = merge;
        return excel;
    }

    /**
     * 空出几行
     * @param rows
     * @return
     */
    public static QiankunExcel emptyRow(int rows) {
        return dynamicColsNoApply(rows, 1,false);
    }
}
