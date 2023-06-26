package com.gowtham.library.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File

object FileUtilKt {

    @JvmStatic
    fun getValidatedFileUri(context: Context, uri: Uri): String? {
        val actualUri = getActualFileUri(context, uri)
        return if (actualUri != null && File(actualUri).canRead())
            actualUri
        else {
            FileCacheHandler.putFileInCache(context, context.contentResolver.openInputStream(uri))
        }
    }

    @JvmStatic
    fun getActualFileUri(context: Context, uri: Uri): String? {
        try {
            val actualPath = getFileUriFromContentProvider(context, uri)
            return if (actualPath != null) {
                actualPath
            } else {
                val id = uri.path?.split(":")?.last()
                id?.let {
                    val realUri = getFileUriFromContentProviderBySplit(context, id)
                    realUri
                }
            }

        } catch (e: Exception) {
            LogMessage.e(Log.getStackTraceString(e));
        }
        return null
    }

    private fun getFileUriFromContentProvider(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Video.Media.DATA)
            cursor = context.contentResolver.query(uri, proj, null, null, null)
            if (cursor != null) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(columnIndex)
            }
            return null
        } finally {
            cursor?.close()
        }
    }

    private fun getFileUriFromContentProviderBySplit(context: Context, id: String): String? {
        var cursor: Cursor? = null
        try {
            val queryUri = MediaStore.Files.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
            )
            cursor = context.contentResolver.query(
                queryUri,
                projection,
                "(${MediaStore.Files.FileColumns._ID} IN (?))",
                arrayOf(id),
                null
            )
            if (cursor != null) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                while (cursor.moveToNext()) {
                    return cursor.getString(columnIndex)
                }
            }
            return null
        } finally {
            cursor?.close()
        }
    }

}