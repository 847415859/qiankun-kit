package com.qiankun.excel.watermark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.InputStream;

public class SystemLoadFont {
    private static Logger logger = LoggerFactory.getLogger(SystemLoadFont.class);
    /**
     * 本地读取方法
     * @param path 文件路径
     * @param style 字体样式
     * @param fontSize 字体大小
     * @return
     */
    public static java.awt.Font styleFont(String path,int style,float fontSize) {

        return SystemLoadFont.loadStyleFont(path,style,fontSize);
    }
    /**
     *
     * @param fontFileName 外部字体名
     * @param style 字体样式
     * @param fontSize 字体大小
     * @return
     */
    public static Font loadStyleFont(String fontFileName, int style, float fontSize) {
        try{
            InputStream in = SystemLoadFont.class.getClassLoader().getResourceAsStream(fontFileName);
            Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, in);
            Font dynamicFontPt =  dynamicFont.deriveFont(style,fontSize);
            in.close();
            return dynamicFontPt;
        }catch(Exception e) {//异常处理
            logger.info("读取字体文件失败");
            return new java.awt.Font("宋体", Font.BOLD, 70);
        }
    }
}