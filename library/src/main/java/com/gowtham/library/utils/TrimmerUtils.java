package com.gowtham.library.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;

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

    public static long getVideoDuration(Activity context, Uri videoUri) {
        try {
            String videoPath = FileUtils.getPath(context,videoUri);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(videoPath));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            retriever.release();
            return timeInMillisec / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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


}
