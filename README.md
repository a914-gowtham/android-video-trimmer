[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/hyperium/hyper/master/LICENSE)
[![](https://jitpack.io/v/a914-gowtham/Android-video-trimmer.svg)](https://jitpack.io/#a914-gowtham/Android-video-trimmer)

# Android-video-trimmer

##### Helps to trim local videos with many customizations on Android applications using exoplayer2 and FFmpeg [Demo app](https://play.google.com/store/apps/details?id=com.gowtham.videotrimmerdemo&hl=en_IN). 

![](https://github.com/a914-gowtham/Android-video-trimmer/blob/master/demo.gif)

## How to use
*For a working implementation, please have a look at the Sample Project*

1. Include the library as local library project.

+ Add the dependency to your app `build.gradle` file
 ```gradle
 dependencies {
    implementation 'com.github.a914-gowtham:Android-video-trimmer:1.4.0'
 }
 ```
 + Add to project's root `build.gradle` file:
```gradle
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```
2. Add the code for opening Trim Activity.
```java
TrimVideo.activity(String.valueOf(videoUri))
//        .setCompressOption(new CompressOption()) //empty constructor for default compress option
          .setDestination("/storage/emulated/0/DCIM/TESTFOLDER")  //default output path /storage/emulated/0/DOWNLOADS
          .start(this);
```
3. Override `onActivityResult` method in your activity to get trim result
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
            Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data));
            Log.d(TAG,"Trimmed path:: "+uri);
        }
}
```
## Customization

#### Video Compress:
```java
.setCompressOption(new CompressOption(frameRate,bitRate,width,height))  //pass empty constructor for default compressoption
```
  * `FrameRate` Recommeded frameRate is 30
  * `BitRate`   Bitrate Can be between 150k to 1000k or 1M to 10M.Lower bitrate can reduce the quality and size of the video.
               Use 1M for better quality output 
  * `Width` Width of the video output video. 
  * `Height` Height of the video output video.Use `TrimmerUtils.getVideoWidthHeight(this,Uri.parse(videoUri));` method to get the width and height of the video
   1. No need to use accurateCut while using video compressOption
   2. Video compressing process will take more time
```java 
.setCompressOption(new CompressOption(30,"1M",460,320))
//You could divide the width and height by 2. when try to compress a large resolution videos ex:Taken from camera
/*int[] wAndh=TrimmerUtils.getVideoWidthHeight(this,Uri.parse(videoUri));
    int width=wAndh[0];
    int height=wAndh[1];
    if(wAndh[0]>800){
      width/=2;
      width/=2;
     .setCompressOption(new CompressOption(30,"1M",width,height))   
     }else
     .setCompressOption(new CompressOption(30,"400k",width,height))   
   */
```
   
#### Video Trim Accuracy:
```java
.setAccurateCut(true) //default value is false 
```
   1. AccurateCut **false** makes video trimming faster and less accuracy(approx. 1-3secs) 
   2. AccurateCut **true** makes video trimming slower and high accuracy

#### Hide Player Seekbar:
```java
.setHideSeekBar(true) //default value is false 
```

### Custom TrimTypes

#### TrimType Default:
```java
TrimVideo.activity(videoUri)
          .start(this);
```

#### TrimType Fixed Duration:
```java
TrimVideo.activity(videoUri)
          .setTrimType(TrimType.FIXED_DURATION)
          .setFixedDuration(30) //seconds
          .start(this);
```

#### TrimType Minimum Duration:
```java
TrimVideo.activity(videoUri)
          .setTrimType(TrimType.MIN_DURATION)
          .setMinDuration(30) //seconds
          .start(this);
```

#### TrimType Min-Max Duration:
```java
TrimVideo.activity(videoUri)
          .setTrimType(TrimType.MIN_MAX_DURATION)
          .setMinToMax(10, 30)  //seconds
          .start(this);
```

## Proguard Rules
```pro
-dontwarn com.gowtham.library**
-keep class com.gowtham.library** { *; }
-keep interface com.gowtham.library** { *; }
```

## Compatibility
  
  * Library - Android Kitkat 4.4+ (API 19)
  * Sample - Android Kitkat 4.4+ (API 19)
  
## ChangeLog
### Version 1.4.2
  * Toolbar title softcoded 
  
### Version 1.4.1
  * Can be started from fragment
  * Added Custom fileName for the output video
  
### Version 1.4.0
  * FFmpegMedia lib changed into Mobile-ffmpeg 
### Version 1.0.9
  * Library size reduced 
  * Library size : 12mb(release build)
  * Constants class name changed into TrimmerConstants
  * Added TrimmerUtils class which has getVideoDuration() method and some more VideoUtils methods

### Version 1.0.5
  * Fixed issue on Android Q.
  * Library size : 26mb(debug build).
  * Small UI updates.
  * Couple new things to configure.

### Version 1.0.0
  * Initial Build
  
## Thanks to 
[TanerSener](https://github.com/tanersener/mobile-ffmpeg)

## Support 
Show your support by giving a star to this repository.that's how i know the usage of this library so this can keep me motivated to make improvements 
  
## Collaboration
There are many ways of improving and adding more features, so feel free to collaborate with ideas, issues and/or pull requests. 
