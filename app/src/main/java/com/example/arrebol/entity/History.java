package com.example.arrebol.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class History extends LitePalSupport {
    String name;

    public History(){ }
    public History(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
