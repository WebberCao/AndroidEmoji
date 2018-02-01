package com.webber.androidemoji.model;


import java.io.Serializable;

public class ItemBean implements Serializable {

    private int type;
    private String content;


    public ItemBean(int type, String content){
        this.type = type;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
