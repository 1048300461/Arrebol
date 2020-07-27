package com.example.arrebol.entity;

import java.io.Serializable;

/**
 * 小说，漫画的章节的实体类
 * @author zc
 * @time 2020/07/23
 */
public class Chapter implements Serializable {

    String currentChapterName;
    String url;

    Chapter nextChapter;
    Chapter previousChapter;

    public Chapter(){}

    public Chapter(String currentChapterName, String url){
        this.currentChapterName = currentChapterName;
        this.url = url;
    }

    public String getCurrentChapterName() {
        return currentChapterName;
    }

    public void setCurrentChapterName(String currentChapterName) {
        this.currentChapterName = currentChapterName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Chapter getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(Chapter nextChapter) {
        this.nextChapter = nextChapter;
    }

    public Chapter getPreviousChapter() {
        return previousChapter;
    }

    public void setPreviousChapter(Chapter previousChapter) {
        this.previousChapter = previousChapter;
    }

}
