package com.gowtham.library.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

object FileUtilsKt {

    @JvmStatic
    fun getFileDataFromUriId(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val id = uri.path?.split(":")?.last() ?: return null
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