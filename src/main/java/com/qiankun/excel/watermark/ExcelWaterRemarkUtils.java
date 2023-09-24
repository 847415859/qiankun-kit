package com.qiankun.excel.watermark;



import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;


public class ExcelWaterRemarkUtils {
    public static final String inFilePath = "E:\\360MoveData\\Users\\84741\\Desktop\\综合评议表模板.xlsx";

    // 宽度
    private static final int WIDTH = 600;
    // 高度
    private static final int HEIGHT = 200;
    // 水印透明度
    private static float alpha = 0.2f;
    // 水印横向位置
    private static int positionWidth = 100;
    // 水印纵向位置
    private static int positionHeight = 40;
    // 水印文字字体
    private static Font font = new Font("宋体", Font.BOLD ,70);
    // 水印文字颜色
    private static Color color = new Color(230, 230, 230);

    /*
     * 为Excel打上水印工具函数 请自行确保参数值，以保证水印图片之间不会覆盖。 在计算水印的位置的时候，并没有考虑到单元格合并的情况，请注意
     *
     * @param wb
     *            Excel Workbook
     * @param sheet
     *            需要打水印的Excel
     * @param waterRemarkPath
     *            水印地址，classPath，目前只支持png格式的图片，
     *            因为非png格式的图片打到Excel上后可能会有图片变红的问题，且不容易做出透明效果。
     *            同时请注意传入的地址格式，应该为类似："\\excelTemplate\\test.png"
     * @param startXCol
     *            水印起始列
     * @param startYRow
     *            水印起始行
     * @param betweenXCol
     *            水印横向之间间隔多少列
     * @param betweenYRow
     *            水印纵向之间间隔多少行
     * @param XCount
     *            横向共有水印多少个
     * @param YCount
     *            纵向共有水印多少个
     * @param waterRemarkWidth
     *            水印图片宽度为多少列
     * @param waterRemarkHeight
     *            水印图片高度为多少行
     * @throws IOException
     */
    public static void putWaterRemarkToExcel(Workbook wb, Sheet sheet, BufferedImage bufferImg, int startXCol,
                                             int startYRow, int betweenXCol, int betweenYRow, int XCount, int YCount, int waterRemarkWidth,
                                             int waterRemarkHeight) throws IOException {
        // 加载图片
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        if (null == bufferImg) {
            throw new RuntimeException("向Excel上面打印水印，读取水印图片失败(2)。");
        }
        ImageIO.write(bufferImg, "png", byteArrayOut);
        // 开始打水印
        Drawing drawing = sheet.createDrawingPatriarch();
        // 按照共需打印多少行水印进行循环
        for (int yCount = 0; yCount < YCount; yCount++) {
            // 按照每行需要打印多少个水印进行循环
            for (int xCount = 0; xCount < XCount; xCount++) {
                // 创建水印图片位置
                int xIndexInteger = startXCol + (xCount * waterRemarkWidth) + (xCount * betweenXCol);
                int yIndexInteger = startYRow + (yCount * waterRemarkHeight) + (yCount * betweenYRow);
                /*
                 * 参数定义： 第一个参数是（x轴的开始节点）； 第二个参数是（是y轴的开始节点）； 第三个参数是（是x轴的结束节点）；
                 * 第四个参数是（是y轴的结束节点）； 第五个参数是（是从Excel的第几列开始插入图片，从0开始计数）；
                 * 第六个参数是（是从excel的第几行开始插入图片，从0开始计数）； 第七个参数是（图片宽度，共多少列）；
                 * 第8个参数是（图片高度，共多少行）；
                 */
                ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, xIndexInteger,
                        yIndexInteger, xIndexInteger + waterRemarkWidth, yIndexInteger + waterRemarkHeight);
                Picture pic = drawing.createPicture(anchor,
                        wb.addPicture(byteArrayOut.toByteArray(), Workbook.PICTURE_TYPE_PNG));
                pic.resize();
            }
        }
    }

    /**
     * 创建背景透明图片
     *
     * @param os
     * @param text
     * @param fontPath
     * @throws IOException
     */
    public static BufferedImage createTransparentImage(OutputStream os, String text,String fontPath) throws IOException {
        // 创建空白图片
        BufferedImage image = new BufferedImage(
                WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        // 获取图片画笔
        Graphics2D g = image.createGraphics();
        // 设置背景透明度
        image = g.getDeviceConfiguration().createCompatibleImage(WIDTH, HEIGHT, Transparency.TRANSLUCENT);
        g.dispose();
        g = image.createGraphics();
        // 设置对线段的锯齿状边缘处理
        // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 文字处理
        AttributedString ats = new AttributedString(text);

        // 需要注意,如果生产环境部署在docker 或者是 是linux服务器,很有可能会引起中文水印乱码 成了方块的情况,这时候你就需要自己导入字体放到项目路径.然后读取.下面是字体相关的工具类
        Font  font1 = SystemLoadFont.styleFont(fontPath,Font.BOLD,70);
        // Font  font1 = font;
        ats.addAttribute(TextAttribute.FONT, font1, 0, text.length());
        AttributedCharacterIterator iter = ats.getIterator();
        // 水印旋转
        g.rotate(Math.toRadians(-15), (double) image.getWidth() / 2, (double) image.getHeight() / 2);
        // 设置水印文字颜色
        g.setColor(color);
        // 设置水印文字Font
        g.setFont(font1);
        // alpha = 0.3f;
        // 设置水印文字透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 设置水印文字透明度结束
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
        // 水印位置
        g.drawString(iter, positionHeight, positionWidth);
        // 释放资源
        g.dispose();
        return image;
    }


    public static void main(String[] args) {

        try {
            //读取excel文件
            Workbook wb =null;
            if (inFilePath.endsWith("xls")) {
                wb = new HSSFWorkbook(new FileInputStream(inFilePath));
            }else if (inFilePath.endsWith("xlsx")){
                wb = new XSSFWorkbook(new FileInputStream(inFilePath));
            }
            //获取excel sheet个数
            int sheets = wb.getNumberOfSheets();
            //循环sheet给每个sheet添加水印
            for (int i = 0; i < sheets; i++) {
                Sheet sheet = wb.getSheetAt(i);
                //获取excel实际所占行
                int row = sheet.getFirstRowNum() + sheet.getLastRowNum();
                //获取excel实际所占列
                Row row1 = sheet.getRow(sheet.getFirstRowNum());
                if (row1==null){
                    continue;
                }
                int cell = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum() + 1;
                //根据行与列计算实际所需多少水印
                ExcelWaterRemarkUtils.putWaterRemarkToExcel(wb, sheet, null, 0, 0, 15, 15, cell / 15 + 1, row / 15 + 1, 0, 0);

                sheet.protectSheet("xxx");
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                wb.write(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
            wb.close();
            byte[] content = os.toByteArray();
            // Excel文件生成后存储的位置。
            File file1 = new File(inFilePath);
            OutputStream fos = null;
            try {
                fos = new FileOutputStream(file1);
                fos.write(content);
                os.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

