package com.example.arrebol.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 历史搜索的实体类
 * @author zc
 * @time 2020/07/21
 */
public class History extends LitePalSupport {
    String name;

    //历史的类型 1：小说 2：动漫 3：影视
    int type;

    public History(){ }

    public History(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
