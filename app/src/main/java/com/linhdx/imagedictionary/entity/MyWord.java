package com.linhdx.imagedictionary.entity;

import com.orm.SugarRecord;

/**
 * Created by shine on 22/12/2016.
 */

public class MyWord extends SugarRecord {
    private String keyword;
    private String phonetic;
    private String summary;
    private String mean;
    private String note;
    private byte[] image;

    public MyWord() {
    }

    public MyWord(String keyword, String phonetic, String summary, String mean, String note, byte[] image) {
        this.keyword = keyword;
        this.phonetic = phonetic;
        this.summary = summary;
        this.mean = mean;
        this.note = note;
        this.image = image;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public String getSummary() {
        return summary;
    }

    public String getMean() {
        return mean;
    }

    public String getNote() {
        return note;
    }

    public byte[] getImage() {
        return image;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
