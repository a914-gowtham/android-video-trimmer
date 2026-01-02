package com.gowtham.library.utils

import android.app.Activity
import android.net.Uri
import android.util.Log
import kotlin.math.min

enum class VideoRes(val displayName: String, val res: Int) {
    LOWER_SD("<360p", -1), SD_360("360p", 360),
    SD("480p", 480), HD("720p", 720), FULL_HD("1080p", 1080)
}

fun fromDisplayName(name: String): VideoRes? =
    VideoRes.values().firstOrNull { it.displayName == name }



fun getVideoResNames(context: Activity, inputUri: Uri): List<String> {

    val wh = TrimmerUtils.getVideoRes(context, inputUri)
    val inputRes = TrimmerUtils.classifyResolution(wh!!.first!!, wh.second!!)
    return if (inputRes== VideoRes.LOWER_SD){
        arrayListOf(inputRes.displayName)
    }else{
        val list= arrayListOf<String>()
        for (res in VideoRes.values()){
            if (res.ordinal<= inputRes.ordinal && res!= VideoRes.LOWER_SD){
                list.add(res.displayName)
            }
        }
        return list.reversed()
    }
}

fun getDownScaleRatio(
    context: Activity,
    fileUri: Uri,
    targetRes: VideoRes
): Float {
    val widthHeightPair = TrimmerUtils.getVideoRes(context, fileUri)

    val resolution = min(widthHeightPair.first, widthHeightPair.second)
    Log.e("TAG", "trimVideo: resolution: "+resolution);
    Log.e("TAG", "trimVideo: targetRes: "+targetRes.res);
    if (resolution== targetRes.res || targetRes== VideoRes.LOWER_SD){
        return 1f
    }else{
        val value= resolution-targetRes.res.toFloat()
        return (value/ resolution).coerceIn(0f, 1f)
    }
}


//
//input      |   upper bound   |  lower bound
//4k video         1080p           360p   -> 1080, 720, 480, 360
//1080p            1080p           360p   -> 1080, 720, 480, 360
//720p             720p            360p   -> 720, 480, 360
//480p             480p            360p   -> 480, 360
//<=360p          given-res        -   -> no
//
//
//720p>1080p<    video res        360p approx   -> given-res, 2/
//480p>720p<     video res        360p approx   -> given-res, 480, 360
//360p>480p<     video res        360p approx  -> given-res, 360

