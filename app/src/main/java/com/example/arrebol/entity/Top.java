package com.example.arrebol.entity;

/**
 * 热门搜索的entity类
 * @author zc
 * @time 2020/07/20
 */
public class Top {

    //热度排名
    String rank;
    //内容名字
    String name;

    //top的状态
    boolean topStatus;

    public Top(String rank, String name) {
        this.rank = rank;
        this.name = name;
        topStatus = true;
    }

    public Top() { }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTopStatus() {
        return topStatus;
    }

    public void setTopStatus(boolean topStatus) {
        this.topStatus = topStatus;
    }
}
