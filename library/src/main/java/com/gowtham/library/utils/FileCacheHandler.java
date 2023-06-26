package com.gowtham.library.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCacheHandler {

    public static String putFileInCache(Context context, InputStream fis)
    {

        FileOutputStream fos=null;
        try
        {
            File f=new File(context.getCacheDir(), "temp_video_file");
            if(f.exists()){
                f.delete();
            }
            fos=new FileOutputStream(f);
            CopyStream(fis,fos);
            return f.getAbsolutePath();
        }
        catch(Exception e)
        {
            LogMessage.e(Log.getStackTraceString(e));
            return "";
        }
        finally
        {
            try
            {
                fos.flush();
                fos.close();
            } catch (Exception e) {
                LogMessage.e(Log.getStackTraceString(e));
            }
        }
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try {

            byte[] bytes = new byte[buffer_size];
            for (; ; ) {

                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
                os.flush();
            }
        } catch (Exception ex) {
            LogMessage.e(Log.getStackTraceString(ex));
        }
    }
}
