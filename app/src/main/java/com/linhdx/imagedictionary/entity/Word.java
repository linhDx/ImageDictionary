package com.linhdx.imagedictionary.entity;

/**
 * Created by shine on 14/12/2016.
 */

public class Word {
    private String keyword;
    private String phonetic;
    private String summary;
    private String mean;

    public Word(String keyword, String phonetic, String summary, String mean) {
        this.keyword = keyword;
        this.phonetic = phonetic;
        this.summary = summary;
        this.mean = mean;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }
}
