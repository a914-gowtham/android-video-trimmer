package com.gowtham.library.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class TrimVideoOptions implements Parcelable {

    public String destination;

    public TrimType trimType = TrimType.DEFAULT;

    public long minDuration, fixedDuration;

    public boolean hideSeekBar;

    public boolean accurateCut;

    public long[] minToMax;

    public CompressOption compressOption;

    public TrimVideoOptions() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.destination);
        dest.writeInt(this.trimType == null ? -1 : this.trimType.ordinal());
        dest.writeLong(this.minDuration);
        dest.writeLong(this.fixedDuration);
        dest.writeByte(this.hideSeekBar ? (byte) 1 : (byte) 0);
        dest.writeByte(this.accurateCut ? (byte) 1 : (byte) 0);
        dest.writeLongArray(this.minToMax);
        dest.writeParcelable(this.compressOption, flags);
    }

    protected TrimVideoOptions(Parcel in) {
        this.destination = in.readString();
        int tmpTrimType = in.readInt();
        this.trimType = tmpTrimType == -1 ? null : TrimType.values()[tmpTrimType];
        this.minDuration = in.readLong();
        this.fixedDuration = in.readLong();
        this.hideSeekBar = in.readByte() != 0;
        this.accurateCut = in.readByte() != 0;
        this.minToMax = in.createLongArray();
        this.compressOption = in.readParcelable(CompressOption.class.getClassLoader());
    }

    public static final Creator<TrimVideoOptions> CREATOR = new Creator<TrimVideoOptions>() {
        @Override
        public TrimVideoOptions createFromParcel(Parcel source) {
            return new TrimVideoOptions(source);
        }

        @Override
        public TrimVideoOptions[] newArray(int size) {
            return new TrimVideoOptions[size];
        }
    };
}
