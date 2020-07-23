package com.example.arrebol.entity;

/**
 * 小说，漫画的章节的实体类
 * @author zc
 * @time 2020/07/23
 */
public class Chapter {

    String currentChapter;
    String url;

    public Chapter(){}

    public Chapter(String currentChapter, String url){
        this.currentChapter = currentChapter;
        this.url = url;
    }

    public String getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(String currentChapter) {
        this.currentChapter = currentChapter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
