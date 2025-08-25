package com.gowtham.library.utils;

public class CompressOption {


    public CompressOption() {
    }

    private int bitRate= 0;

    private float compressionScale=0;

    public CompressOption(int bitRate, float compressionScaleForResolution) {
        this.bitRate = bitRate;
        this.compressionScale = compressionScaleForResolution;
    }

    private VideoRes videoRes= null;

    public CompressOption(VideoRes videoRes) {
        this.videoRes = videoRes;
    }


    private boolean isUserSelection;

    public CompressOption(boolean isUserSelection) {
        this.isUserSelection = isUserSelection;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public float getCompressionScale() {
        return compressionScale;
    }

    public void setCompressionScale(float compressionScale) {
        this.compressionScale = compressionScale;
    }

    public int getBitRate() {
        return bitRate;
    }

    public VideoRes getVideoRes() {
        return videoRes;
    }

    public void setVideoRes(VideoRes videoRes) {
        this.videoRes = videoRes;
    }
}
