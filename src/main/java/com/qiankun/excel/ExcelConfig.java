package com.qiankun.excel;

import lombok.Data;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelConfig {

    /**
     * 需要改变背景颜色的行
     */
    private List<Integer> rows = new ArrayList<>();

    /**
     * 需要改变字体颜色的单元格
     */
    private List<FontColor> fontColors = new ArrayList<>();

    /**
     * 表格数据开始行  默认第二行开始
     */
    private Integer dataStartRow;

    /**
     * 表格数据结束行  默认最大行
     */
    private Integer dataEndRow;

    /**
     * 设置自适应宽度的列
     */
    private List<Integer> autoWidthCols = new ArrayList<>();

    /**
     * 设置隐藏列
     */
    private List<Integer> hideCols = new ArrayList<>();

    /**
     * 设置居中列 如果为空/默认为所有居中
     */
    private List<Integer> centerCols = new ArrayList<>();

    /**
     * 是否换行
     */
    private Boolean wrapText;

    /**
     * 字体颜色及样式
     */
    @Data
    public static class FontColor {
        /**
         * 需要改变字体颜色的行
         */
        private Integer fontRow;
        /**
         * 需要改变字体颜色的列
         */
        private Integer fontCol;

        /**
         * 字体需要改变成的颜色 1 绿色  2 橙色
         */
        private Short color;

        /**
         * 字体颜色
         */
        private Font font;

        /**
         * 水平对齐样式
         */
        private HorizontalAlignment horizontalAlignment;

        /**
         * 垂直对齐样式
         */
        private VerticalAlignment verticalAlignment;
    }

}
