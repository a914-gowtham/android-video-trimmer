[![](https://jitpack.io/v/jZAIKO/android-video-trimmer.svg)](https://jitpack.io/#jZAIKO/android-video-trimmer)
[![](https://jitpack.io/v/a914-gowtham/android-video-trimmer.svg)](https://jitpack.io/#a914-gowtham/android-video-trimmer)

### Starting from version 1.8.0 this library is using [media3](https://developer.android.com/media/media3/transformer/transformations) for media transcoding. Previously it was using [ffmpeg](https://github.com/arthenica/ffmpeg-kit?tab=readme-ov-file) which is deprecated now.


# Android-video-trimmer

##### Helps to trim local videos with many customizations on Android applications using exoplayer2 and FFmpeg [Demo app](https://github.com/a914-gowtham/android-video-trimmer/releases) 

![](https://github.com/a914-gowtham/Android-video-trimmer/blob/master/demo.gif)

## How to use
*For a working implementation, please have a look at the Sample Project*

1. Include the library as local library project.

+ Add the dependency to your app `build.gradle` file
 ```gradle
 // replace x.y.z with latest available jitpack version
 dependencies {
    implementation 'com.github.a914-gowtham:android-video-trimmer:x.y.z'
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
          .setHideSeekBar(true)
          .disableCompression()   // enabled by default
          .start(this,startForResult);
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
  
  * Library - Android Nougat 7.0+ (API 24)
  * Sample - Android Kitkat 4.4+ (API 19)

## License

This library is licensed under the MIT License.
  
## Support 
Show your support by giving a star to this repository. 
  
## Collaboration
There are many ways of improving and adding more features, so feel free to collaborate with ideas, issues and/or pull requests. 
