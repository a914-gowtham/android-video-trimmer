package com.gowtham.library.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    public static String getRealPath(Context context, Uri uri) {
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            return new File(uri.getPath()).getAbsolutePath();
        }
        String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
        File file = new File(context.getExternalCacheDir(), "File_" + System.currentTimeMillis() + "." + type);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }
                fileOutputStream.flush();
            } catch (Exception ignored) {
                // Do nothing
            }
        } catch (Exception ignored) {
            // Do nothing
        }
        return file.getAbsolutePath();
    }
}
