[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/hyperium/hyper/master/LICENSE)
[![](https://jitpack.io/v/jZAIKO/android-video-trimmer.svg)](https://jitpack.io/#jZAIKO/android-video-trimmer)
[![](https://jitpack.io/v/a914-gowtham/Android-video-trimmer/month.svg)](https://jitpack.io/#a914-gowtham/Android-video-trimmer)

# Android-video-trimmer

##### Helps to trim local videos with many customizations on Android applications using exoplayer2 and FFmpeg [Demo app](https://play.google.com/store/apps/details?id=com.videotrimmer.videotrimmerdemo). 

![](https://github.com/a914-gowtham/Android-video-trimmer/blob/master/demo.gif)

## How to use
*For a working implementation, please have a look at the Sample Project*

1. Include the library as local library project.

+ Add the dependency to your app `build.gradle` file
+ Take a look at light weight version of this library [Android-video-trimmer-litr](https://github.com/a914-gowtham/android-video-trimmer-litr)
 ```gradle
 dependencies {
    implementation 'com.github.a914-gowtham:android-video-trimmer:1.7.0'
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
2. Create a global variable for ActivityResultLauncher

```java
    //Java
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
                    Log.d(TAG, "Trimmed path:: " + uri);
                   
                } else
                    LogMessage.v("videoTrimResultLauncher data is null");
            });
```

```kotlin
    //Kotlin
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { 
    result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK &&
                        result.getData() != null) {
                Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()))
                Log.d(TAG, "Trimmed path:: " + uri)
         }else 
          	LogMessage.v("videoTrimResultLauncher data is null");
    }   
```

3. Add the code for opening Trim Activity.
```java
TrimVideo.activity(String.valueOf(videoUri))
//        .setCompressOption(new CompressOption()) //empty constructor for default compress option
          .setHideSeekBar(true)
          .start(this,startForResult);
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
          .start(this,startForResult);
```

#### TrimType Fixed Duration:
```java
TrimVideo.activity(videoUri)
          .setTrimType(TrimType.FIXED_DURATION)
          .setFixedDuration(30) //seconds
          .start(this,startForResult);
```

#### TrimType Minimum Duration:
```java
TrimVideo.activity(videoUri)
          .setTrimType(TrimType.MIN_DURATION)
          .setMinDuration(30) //seconds
          .start(this,startForResult);
```

#### TrimType Min-Max Duration:
```java
TrimVideo.activity(videoUri)
          .setTrimType(TrimType.MIN_MAX_DURATION)
          .setMinToMax(10, 30)  //seconds
          .start(this,startForResult);
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
### Version 1.6.5
  * Video stretch issue 2 [#46](https://github.com/a914-gowtham/Android-video-trimmer/issues/46)
### Version 1.6.4
  * Video stretch issue [#46](https://github.com/a914-gowtham/Android-video-trimmer/issues/46)
### Version 1.6.3
  * java.lang.NoClassDefFoundError Issue fix [#45](https://github.com/a914-gowtham/Android-video-trimmer/issues/45) that only happens on samsung devices
### Version 1.6.2
  * Arabic localization and custom toolbar title
### Version 1.6.1
  * Compression issue fixed
### Version 1.6.0
  * Ffmpeg-mobile version downgraded to 4.3.1-LTS to rectify release build issue
### Version 1.5.11
  * Write permission and setDestination removed due to the android 11 issues
### Version 1.5.2
  * Thumbnail loading optimized
### Version 1.5.1
  * Controller seekbar bug fixed
### Version 1.5.0
  * Audio focus added
  * Bug fixes on Android 11
  * ui improvements
### Version 1.4.4
  * Exoplayer version updated to 2.12.1
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
Show your support by giving a star to this repository. 
  
## Collaboration
There are many ways of improving and adding more features, so feel free to collaborate with ideas, issues and/or pull requests. 
