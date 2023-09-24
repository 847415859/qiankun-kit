package com.qiankun.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: 文件转化为压缩包
 * @Date : 2023/09/24 13:47
 * @Auther : tiankun
 */
public class ZipFileUtils {

    static final Logger logger = LoggerFactory.getLogger(ZipFileUtils.class);

    public static void urlToZipUtils(List<String> urls , HttpServletResponse response , String zipFileName, Map<String,String> fileNames) {
        try {
            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(outputBuffer);

            for (String url : urls) {

                URL urlObj = new URL(url);
                InputStream urlInputStream = urlObj.openStream();

                ZipEntry zipEntry = new ZipEntry(fileNames.get(url));

                zos.putNextEntry(zipEntry);

                byte[] buf = new byte[2 * 1024];
                int len;
                while ((len = urlInputStream.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                urlInputStream.close();
            }

            zos.close();

            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipFileName, "UTF-8"));

            response.getOutputStream().write(outputBuffer.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    public static void urlToZipUtils(List<DownloadFile> files , HttpServletResponse response , String zipFileName) {
        try(ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(outputBuffer);) {
            // 防止文件名重复
            if(CollectionUtils.isNotEmpty(files)){
                Map<String, List<DownloadFile>> filenameMap = files.stream().collect(Collectors.groupingBy(DownloadFile::getName));
                filenameMap.forEach((filename, fileList) -> {
                    if(fileList.size() > 1){
                        for (int i = 1; i < fileList.size(); i++) {
                            DownloadFile downloadFile = fileList.get(i);
                            downloadFile.setName(downloadFile.getName()+"-"+i);
                        }
                    }
                });
            }

            for (DownloadFile file : files) {

                URL urlObj = new URL(file.getUrl());
                InputStream urlInputStream = urlObj.openStream();
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);
                byte[] buf = new byte[2 * 1024];
                int len;
                while ((len = urlInputStream.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                urlInputStream.close();
            }
            zos.close();
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipFileName, "UTF-8"));
            response.getOutputStream().write(outputBuffer.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static ByteArrayOutputStream urlToZipToByteArrayOutputStream(List<DownloadFile> files) {
        try(ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(outputBuffer);) {
            // 防止文件名重复
            if(CollectionUtils.isNotEmpty(files)){
                Map<String, List<DownloadFile>> filenameMap = files.stream().collect(Collectors.groupingBy(DownloadFile::getName));
                filenameMap.forEach((filename, fileList) -> {
                    if(fileList.size() > 1){
                        for (int i = 1; i < fileList.size(); i++) {
                            DownloadFile downloadFile = fileList.get(i);
                            String extension = FilenameUtils.getExtension(downloadFile.getName());
                            downloadFile.setName(downloadFile.getName().substring(0,downloadFile.getName().lastIndexOf("."))+"-"+i+"."+extension);
                        }
                    }
                });
            }
            for (DownloadFile file : files) {

                URL urlObj = new URL(file.getUrl());
                InputStream urlInputStream = urlObj.openStream();
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);
                byte[] buf = new byte[1024 * 1024];
                int len;
                while ((len = urlInputStream.read(buf)) != -1) {
                    logger.debug("读取到的字节长度：{}",len);
                    zos.write(buf, 0, len);
                }
                urlInputStream.close();
            }
            logger.debug("字节流中读取的总大小：{}",outputBuffer.size());
            zos.close();
            return outputBuffer;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }
}
