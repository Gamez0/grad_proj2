package com.praylist.socialcomponents.model;

import androidx.annotation.Keep;

@Keep
public class BoardItem {
    String title;
    String writer;
    String date;
    String readComment;

    public BoardItem() {
    }

    public BoardItem(String title, String writer, String date, String readComment) {
        this.title = title;
        this.writer = writer;
        this.date = date;
        this.readComment = readComment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReadComment() {
        return readComment;
    }

    public void setReadComment(String readComment) {
        this.readComment = readComment;
    }

    @Override
    public String toString() {
        return "BoardItem{" +
                "title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", date='" + date + '\'' +
                ", readComment='" + readComment + '\'' +
                '}';
    }
}