package com.fulvmei.android.media.demo.main.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class Media implements Parcelable {
    private String name;
    private String path;
    private String image;
    private String type;
    private String tag;

    public Media() {

    }

    public Media(String name, String path, String type, String tag) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.tag = tag;
    }

    public Media(String name, String path, String image, String type, String tag) {
        this.name = name;
        this.path = path;
        this.image = image;
        this.type = type;
        this.tag = tag;
    }

    protected Media(Parcel in) {
        name = in.readString();
        path = in.readString();
        image = in.readString();
        type = in.readString();
        tag = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(image);
        parcel.writeString(type);
        parcel.writeString(tag);
    }
}
