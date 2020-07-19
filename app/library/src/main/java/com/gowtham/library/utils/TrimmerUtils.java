package com.gowtham.library.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class TrimmerUtils {

    protected static final int PER_REQ_CODE = 115;

    private static boolean checkPermission(Activity context, String... permissions) {
        boolean allPermitted = false;
        for (String permission : permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED);
            if (!allPermitted)
                break;
        }
        if (allPermitted)
            return true;
        ActivityCompat.requestPermissions(context, permissions,
                PER_REQ_CODE);
        return false;
    }

    protected static boolean isPermissionOk(int... results) {
        boolean isAllGranted = true;
        for (int result : results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    public static String getvideoPath(Context context, Uri uri) {
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            cursor.close();

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(uri);
    }


    public static long getVideoDuration(Activity context, Uri videoPath) {
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

}
