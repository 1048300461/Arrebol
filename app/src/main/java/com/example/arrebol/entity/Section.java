package com.example.arrebol.entity;

import java.io.Serializable;

/**
 * 动漫电影章节的实体类
 * @author zc
 * @time 2020/07/23
 */
public class Section implements Serializable {
    //当前的集数
    private String currentSection;
    private String m3u8Url;
    private String onlineUrl;
    private String download;

    public String getCurrentSection() {
        return currentSection;
    }

    public void setCurrentSection(String currentSection) {
        this.currentSection = currentSection;
    }

    public String getM3u8Url() {
        return m3u8Url;
    }

    public void setM3u8Url(String m3u8Url) {
        this.m3u8Url = m3u8Url;
    }

    public String getOnlineUrl() {
        return onlineUrl;
    }

    public void setOnlineUrl(String onlineUrl) {
        this.onlineUrl = onlineUrl;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
}
