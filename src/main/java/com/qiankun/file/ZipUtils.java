package com.qiankun.file;

import com.qiankun.web.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class ZipUtils {

    static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 将对应的文件资源转换为压缩包
     * @param urls          文件地址url集合
     * @param fileNames     压缩到压缩包的文件名称
     * @param response      Http输出流
     * @param zipFileName   压缩包名称
     */
    public static void fileUrlToZipWeb(List<String> urls , Map<String,String> fileNames, String zipFileName,
                                       HttpServletRequest request , HttpServletResponse response) {
        ByteArrayOutputStream outputBuffer = null;
        try {
            outputBuffer = fileUrlToZip(urls, fileNames);
            response.getOutputStream().write(outputBuffer.toByteArray());
            WebUtils.setOctetAttachmentHeader(request,response,zipFileName);
        } catch (Exception e) {
            logger.error("fileUrlToZipWeb error :{}",e);
            e.printStackTrace();
        } finally {
            if(outputBuffer != null){
                try {
                    outputBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将对应的文件资源转换为压缩包  (需要一一对应)
     * @param urls      文件地址url
     * @param fileNames 压缩到压缩包的文件名称
     * @return
     */
    public static ByteArrayOutputStream fileUrlToZip(List<String> urls , Map<String,String> fileNames) {
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(outputBuffer);){
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
            return outputBuffer;
        } catch (Exception e) {
            logger.error("fileUrlToZip error :{}",e);
            throw new RuntimeException(e);
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

    /**
     * 将文件打成一个字节输出流
     * @param files
     * @return
     */
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
