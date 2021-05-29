package com.gowtham.library.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.gowtham.library.ui.ActVideoTrimmer;

public class TrimVideo {

    public static final String TRIM_VIDEO_OPTION = "trim_video_option",
            TRIM_VIDEO_URI = "trim_video_uri",TRIMMED_VIDEO_PATH="trimmed_video_path";

    public static ActivityBuilder activity(String uri) {
        return new ActivityBuilder(uri);
    }

    public static String getTrimmedVideoPath(Intent intent){
        return intent.getStringExtra(TRIMMED_VIDEO_PATH);
    }

    public static final class ActivityBuilder {

        @Nullable
        private final String videoUri;

        private final TrimVideoOptions options;

        public ActivityBuilder(@Nullable String videoUri) {
            this.videoUri = videoUri;
            options = new TrimVideoOptions();
            options.trimType=TrimType.DEFAULT;
        }

        public ActivityBuilder setTrimType(final TrimType trimType) {
            options.trimType = trimType;
            return this;
        }

        public ActivityBuilder setLocal(@NonNull final String local) {
            options.local = local;
            return this;
        }

        public ActivityBuilder setHideSeekBar(final boolean hide) {
            options.hideSeekBar = hide;
            return this;
        }

        public ActivityBuilder setCompressOption(final CompressOption compressOption) {
            options.compressOption = compressOption;
            return this;
        }

        public ActivityBuilder setFileName(@NonNull final String fileName) {
            options.fileName = fileName;
            return this;
        }

        public ActivityBuilder showFileLocationAlert() {
            options.showFileLocationAlert = true;
            return this;
        }

        public ActivityBuilder setAccurateCut(final boolean accurate) {
            options.accurateCut = accurate;
            return this;
        }

        public ActivityBuilder setMinDuration(final long minDuration) {
            options.minDuration = minDuration;
            return this;
        }

        public ActivityBuilder setFixedDuration(final long fixedDuration) {
            options.fixedDuration = fixedDuration;
            return this;
        }

        public ActivityBuilder setMinToMax(long min, long max) {
            options.minToMax = new long[]{min, max};
            return this;
        }

        public ActivityBuilder setTitle(@NonNull String title) {
            options.title = title;
            return this;
        }

        public void start(Activity activity,
                          ActivityResultLauncher<Intent> launcher) {
            validate();
            launcher.launch(getIntent(activity));
        }

        public void start(Fragment fragment,ActivityResultLauncher<Intent> launcher) {
            validate();
            launcher.launch(getIntent(fragment.getActivity()));
        }

        private void validate() {
            if (videoUri == null)
                throw new NullPointerException("VideoUri cannot be null.");
            if (videoUri.isEmpty())
                throw new IllegalArgumentException("VideoUri cannot be empty");
            if (options.trimType == null)
                throw new NullPointerException("TrimType cannot be null");
            if (options.minDuration < 0)
                throw new IllegalArgumentException("Cannot set min duration to a number < 1");
            if (options.fixedDuration < 0)
                throw new IllegalArgumentException("Cannot set fixed duration to a number < 1");
            if (options.trimType==TrimType.MIN_MAX_DURATION && options.minToMax==null)
                throw new IllegalArgumentException("Used trim type is TrimType.MIN_MAX_DURATION." +
                        "Give the min and max duration");
            if (options.minToMax != null){
                if ((options.minToMax[0] < 0 || options.minToMax[1] < 0))
                    throw new IllegalArgumentException("Cannot set min to max duration to a number < 1");
                if ((options.minToMax[0] > options.minToMax[1]))
                    throw new IllegalArgumentException("Minimum duration cannot be larger than max duration");
                if ((options.minToMax[0] == options.minToMax[1]))
                    throw new IllegalArgumentException("Minimum duration cannot be same as max duration.Use Fixed duration");
            }
        }

        private Intent getIntent(Activity activity) {
            Intent intent = new Intent(activity,  ActVideoTrimmer.class);
            Gson gson = new Gson();
            Bundle bundle=new Bundle();
            bundle.putString(TRIM_VIDEO_URI, videoUri);
            bundle.putString(TRIM_VIDEO_OPTION, gson.toJson(options));
            intent.putExtras(bundle);
            return intent;
        }
    }


}
