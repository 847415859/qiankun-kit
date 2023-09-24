package com.qiankun.file;

public class DownloadFile {
    private String url;
    private String name;

    public DownloadFile() {
    }

    public DownloadFile(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}