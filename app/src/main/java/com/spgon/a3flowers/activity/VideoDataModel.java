package com.spgon.a3flowers.activity;

public class VideoDataModel {
    private String videoTitle;
    private String videoUrl;
    private String question;

    public void setVideoUrl(String videoUrl) {
    }

    public void setVideoTitle(String videoTitle) {
    }

    public void setQuestion(String question) {
    }

    // Constructor, getters, and setters for the model
    // ...

    public String getVideoUrl() {
        return videoTitle + "\n" + question;
    }
}

