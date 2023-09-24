package com.qiankun.excel.watermark;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 添加水印
 * @Date : 2023/09/24 9:46
 * @Auther : tiankun
 */
public class WaterRemakUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 添加水印（图片）
     * @param workbook
     */
    public void addWatermarkByPic(Workbook workbook) {
        try {
            // 加载水印图片
            InputStream inputStream = WaterRemakUtils.class.getClassLoader().getResourceAsStream("excel/waterremark/dingdang_logo_waterremark_250.png");
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (null == bufferedImage) {
                throw new RuntimeException("向Excel上面打印水印，读取水印图片失败(2)。");
            }
            ImageIO.write(bufferedImage, "png", os);
            int pictureIdx = workbook.addPicture(os.toByteArray(), Workbook.PICTURE_TYPE_PNG);
            POIXMLDocumentPart poixmlDocumentPart = (POIXMLDocumentPart) workbook.getAllPictures().get(pictureIdx);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(i);
                PackagePartName ppn = poixmlDocumentPart.getPackagePart().getPartName();
                String relType = XSSFRelation.IMAGES.getRelation();
                //add relation from sheet to the picture data
                PackageRelationship pr = sheet.getPackagePart().addRelationship(ppn, TargetMode.INTERNAL, relType, null);
                //set background picture to sheet
                sheet.getCTWorksheet().addNewPicture().setId(pr.getId());
            }

        } catch (IOException e) {
            logger.error("添加图片水印失败：{}",e);
            e.printStackTrace();
        }
    }

    /**
     * 添加水印（文字）
     * @param workbook
     */
    public void addWatermarkByText(Workbook workbook) {
        try {
            BufferedImage bufferedImage = ExcelWaterRemarkUtils.createTransparentImage(null,"乾坤","excel/font/songti.ttc");//fontPath 字体文件所在路径
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (null == bufferedImage) {
                throw new RuntimeException("向Excel上面打印水印，读取水印图片失败(2)。");
            }
            ImageIO.write(bufferedImage, "png", os);
            int pictureIdx = workbook.addPicture(os.toByteArray(), Workbook.PICTURE_TYPE_PNG);
            POIXMLDocumentPart poixmlDocumentPart = (POIXMLDocumentPart) workbook.getAllPictures().get(pictureIdx);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(i);
                PackagePartName ppn = poixmlDocumentPart.getPackagePart().getPartName();
                String relType = XSSFRelation.IMAGES.getRelation();
                //add relation from sheet to the picture data
                PackageRelationship pr = sheet.getPackagePart().addRelationship(ppn, TargetMode.INTERNAL, relType, null);
                //set background picture to sheet
                sheet.getCTWorksheet().addNewPicture().setId(pr.getId());
            }

        } catch (IOException e) {
            logger.error("添加文字水印失败 {}",e);
            e.printStackTrace();
        }
    }
}
