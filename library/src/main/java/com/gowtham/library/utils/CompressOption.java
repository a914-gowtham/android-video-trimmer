package com.gowtham.library.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class CompressOption implements Parcelable {

    private int frameRate=30;

    private String bitRate="0k";

    private int width=0;

    private int height=0;

    public CompressOption() {
    }

    public CompressOption(int frameRate, String bitRate, int width, int height) {
        this.frameRate = frameRate;
        this.bitRate = bitRate;
        this.width = width;
        this.height = height;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public String getBitRate() {
        return bitRate;
    }

    public static Creator<CompressOption> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.frameRate);
        dest.writeString(this.bitRate);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected CompressOption(Parcel in) {
        this.frameRate = in.readInt();
        this.bitRate = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<CompressOption> CREATOR = new Creator<CompressOption>() {
        @Override
        public CompressOption createFromParcel(Parcel source) {
            return new CompressOption(source);
        }

        @Override
        public CompressOption[] newArray(int size) {
            return new CompressOption[size];
        }
    };
}
