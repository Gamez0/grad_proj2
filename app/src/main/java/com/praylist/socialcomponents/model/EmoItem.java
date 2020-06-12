package com.praylist.socialcomponents.model;

import androidx.annotation.Keep;

@Keep
public class EmoItem {
    // emotion Item
    String name;
    String subName;
    public EmoItem(){

    }
    public EmoItem(String name, String subName){
        this.name=name;
        this.subName=subName;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }
    @Override
    public String toString() {
        return "emoData{" +
                "name='" + name + '\'' +
                ", subName='" + subName + '\'' +
                '}';
    }
}