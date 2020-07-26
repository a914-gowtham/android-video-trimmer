[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/hyperium/hyper/master/LICENSE)
[![](https://jitpack.io/v/a914-gowtham/Android-video-trimmer.svg)](https://jitpack.io/#a914-gowtham/Android-video-trimmer)

# Android-video-trimmer

##### Helps to trim local videos with many customizations on Android applications using exoplayer2 and FFmpeg.

![](https://github.com/a914-gowtham/Android-video-trimmer/blob/master/demo.gif)

## Usage
*For a working implementation, please have a look at the Sample Project*

1. Include the library as local library project.

+ Add the dependency to your app `build.gradle` file
 ```gradle
 dependencies {
    implementation 'com.github.a914-gowtham:Android-video-trimmer:1.0.6'
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
Intent intent=new Intent(this,ActVideoTrimmer.class);
intent.putExtra(TrimmerConstants.TRIM_VIDEO_URI,String.valueOf(videoUri));
intent.putExtra(TrimmerConstants.DESTINATION,"/storage/emulated/0/DCIM/MYFOLDER"); //optional default output path /storage/emulated/0/DOWNLOADS
startActivityForResult(intent,TrimmerConstants.REQ_CODE_VIDEO_TRIMMER);
```
3. Override `onActivityResult` method in your activity to get trim result
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == TrimmerConstants.REQ_CODE_VIDEO_TRIMMER && data != null) {
            Uri uri = Uri.parse(data.getStringExtra(TrimmerConstants.TRIMMED_VIDEO_PATH));
            Log.d(TAG,"Trimmed path:: "+uri);
        }
}
```
## Customization

* Hide Player seekbar
```java
intent.putExtra(TrimmerConstants.HIDE_PLAYER_SEEKBAR,true);
```

### Custom TrimTypes

* TrimType Default
```java
Intent intent=new Intent(this,ActVideoTrimmer.class);
intent.putExtra(TrimmerConstants.TRIM_VIDEO_URI,String.valueOf(videoUri));
intent.putExtra(TrimmerConstants.TRIM_TYPE,0); //optional: it will take by default
startActivityForResult(intent,TrimmerConstants.REQ_CODE_VIDEO_TRIMMER);
```

* TrimType FixedGap(fixed duration trim)
```java
Intent intent=new Intent(this,ActVideoTrimmer.class);
intent.putExtra(TrimmerConstants.TRIM_VIDEO_URI,String.valueOf(videoUri));
intent.putExtra(TrimmerConstants.TRIM_TYPE,1);
intent.putExtra(TrimmerConstants.FIXED_GAP_DURATION,30L); //in secs
startActivityForResult(intent,TrimmerConstants.REQ_CODE_VIDEO_TRIMMER);
```

* TrimType MinDuration
```java
Intent intent=new Intent(this,ActVideoTrimmer.class);
intent.putExtra(TrimmerConstants.TRIM_VIDEO_URI,String.valueOf(videoUri));
intent.putExtra(TrimmerConstants.TRIM_TYPE,2);
intent.putExtra(TrimmerConstants.MIN_GAP_DURATION,30L); //in secs
startActivityForResult(intent,TrimmerConstants.REQ_CODE_VIDEO_TRIMMER);
```

* TrimType Min-Max Duration
```java
Intent intent=new Intent(this,ActVideoTrimmer.class);
intent.putExtra(TrimmerConstants.TRIM_VIDEO_URI,String.valueOf(videoUri));
intent.putExtra(TrimmerConstants.TRIM_TYPE,3);
intent.putExtra(TrimmerConstants.MIN_FROM_DURATION,30L); //in secs
intent.putExtra(TrimmerConstants.MAX_TO_DURATION,30L); //in secs
startActivityForResult(intent,TrimmerConstants.REQ_CODE_VIDEO_TRIMMER);
```

# Compatibility
  
  * Library - Android Kitkat 4.4+ (API 19)
  * Sample - Android Kitkat 4.4+ (API 19)
  
## Collaboration
There are many ways of improving and adding more features, so feel free to collaborate with ideas, issues and/or pull requests. 
