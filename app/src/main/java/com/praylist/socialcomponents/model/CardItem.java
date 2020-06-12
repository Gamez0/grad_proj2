package com.praylist.socialcomponents.model;

public class CardItem {
    private int mTextResource;
    private int mTitleResource;
    private int musicNum;
    private int musicResource;

    public CardItem(int title, int text, int num, int music) {
        mTitleResource = title;
        mTextResource = text;
        musicNum = num;
        musicResource = music;
    }
    public int getMusicNum(){ return musicNum; }
    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }
}