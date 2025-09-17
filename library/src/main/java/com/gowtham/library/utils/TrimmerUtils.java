package com.gowtham.library.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrimmerUtils {

    public static String formatCSeconds(long timeInSeconds) {
        long hours = timeInSeconds / 3600;
        long secondsLeft = timeInSeconds - hours * 3600;
        long minutes = secondsLeft / 60;
        long seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    public static int getColor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }

    public static long getDuration(Activity context, Uri videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoPath);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            retriever.release();
            return timeInMillisec / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getDurationMillis(Activity context, Uri videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoPath);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            retriever.release();
            return timeInMillisec;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTrimType(TrimType trimType) {
        switch (trimType) {
            case FIXED_DURATION:
                return 1;
            case MIN_DURATION:
                return 2;
            case MIN_MAX_DURATION:
                return 3;
            default:
                return 0;
        }
    }

    public static String getFileExtension(Context context, Uri uri) {
        try {
            String extension;
            if (uri.getScheme()!=null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
            } else
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            return (extension == null || extension.isEmpty()) ? ".mp4" : extension;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mp4";
    }


    public static Bitmap getFrameBySec(Activity context, Uri videoPath, long millies) {
        try {
            String formatted = millies + "000000";
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoPath);
            Bitmap bitmap = retriever.getFrameAtTime(Long.parseLong(formatted));
            retriever.release();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getFrameRate(Activity context, Uri videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context,videoPath);
            int frameRate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));
            retriever.release();
            return frameRate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 30;
    }

    public static int getBitRate(Activity context, Uri videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoPath);
            int bitRate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            retriever.release();
            return bitRate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 15;
    }


    public static int[] getVideoWidthHeight(Activity context, Uri videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context,videoPath);
            int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            retriever.release();
            return new int[]{width,height};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVideoRotation(Activity context, Uri videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context,videoPath);
            int rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            retriever.release();
            return rotation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatSeconds(long timeInSeconds) {
        long hours = timeInSeconds / 3600;
        long secondsLeft = timeInSeconds - hours * 3600;
        long minutes = secondsLeft / 60;
        long seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10 && hours != 0) {
            formattedTime += "0";
            formattedTime += hours + ":";
        }

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    public static String getLimitedTimeFormatted(long secs){
            long hours = secs / 3600;
            long secondsLeft = secs - hours * 3600;
            long minutes = secondsLeft / 60;
            long seconds = secondsLeft - minutes * 60;
            String time;
            if (hours!=0){
                time=hours+" Hrs "+(minutes!=0 ? minutes+" Mins " : "")+
                        (seconds!=0 ? seconds+" Secs " : "");
            }else if (minutes!=0)
                time=minutes+" Mins "+(seconds!=0 ? seconds+" Secs ":"");
            else
                time=seconds+" Secs ";
            LogMessage.v(time);
            return time;
    }

    public static String clearNull(String value) {
        return value == null ? "" : value.trim();
    }

    public static boolean hasSpecialChar(String value){
        value=clearNull(value);
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        return m.find();
    }
}
