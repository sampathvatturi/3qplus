package com.spgon.a3flowers.model;

public class VideoItem {

    private final String id;
    private final String videoLink;
    private final String title;

    public VideoItem(String id, String videoLink, String title) {
        this.id=id;
        this.videoLink = videoLink;
        this.title = title;
    }

    public String getId() {
        return id;
    }
    public String getVideoLink() {
        return videoLink;
    }

    public String getTitle() {
        return title;
    }
}

