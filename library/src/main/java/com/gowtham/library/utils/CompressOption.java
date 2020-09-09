package com.gowtham.library.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class CompressOption implements Parcelable {

    private int frameRate=30;

    private int bitRate=10;

    public CompressOption() {
    }

    public CompressOption(int frameRate, int bitRate) {
        this.frameRate = frameRate;
        this.bitRate = bitRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.frameRate);
        dest.writeInt(this.bitRate);
    }

    protected CompressOption(Parcel in) {
        this.frameRate = in.readInt();
        this.bitRate = in.readInt();
    }

    public static final Parcelable.Creator<CompressOption> CREATOR = new Parcelable.Creator<CompressOption>() {
        @Override
        public CompressOption createFromParcel(Parcel source) {
            return new CompressOption(source);
        }

        @Override
        public CompressOption[] newArray(int size) {
            return new CompressOption[size];
        }
    };

    public int getFrameRate() {
        return frameRate;
    }

    public int getBitRate() {
        return bitRate;
    }

    public static Creator<CompressOption> getCREATOR() {
        return CREATOR;
    }
}
