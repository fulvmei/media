package com.fulvmei.android.media.demo.main.bean;

import java.util.ArrayList;
import java.util.List;

public class MediaGroup {

    public String name;
    public ArrayList<Media> mediaList;

    public MediaGroup() {

    }

    public MediaGroup(String name, ArrayList<Media> mediaList) {
        this.name = name;
        this.mediaList = mediaList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMediaList(ArrayList<Media> mediaList) {
        this.mediaList = mediaList;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }
}
