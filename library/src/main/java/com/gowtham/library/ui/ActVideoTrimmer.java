package com.gowtham.library.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.gowtham.library.R;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.CustomProgressView;
import com.gowtham.library.utils.FileUtils;
import com.gowtham.library.utils.LogMessage;
import com.gowtham.library.utils.TrimVideo;
import com.gowtham.library.utils.TrimVideoOptions;
import com.gowtham.library.utils.TrimmerUtils;

import java.io.File;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;


public class ActVideoTrimmer extends AppCompatActivity {

    private PlayerView playerView;

    private static final int PER_REQ_CODE = 115;

    private SimpleExoPlayer videoPlayer;

    private ImageView imagePlayPause;

    private ImageView[] imageViews;

    private long totalDuration;

    private Dialog dialog;

    private Uri uri;

    private TextView txtStartDuration, txtEndDuration;

    private CrystalRangeSeekbar seekbar;

    private long lastMinValue = 0;

    private long lastMaxValue = 0;

    private MenuItem menuDone;

    private CrystalSeekbar seekbarController;

    private boolean isValidVideo = true, isVideoEnded;

    private Handler seekHandler;

    private long currentDuration,lastClickedTime;

    private CompressOption compressOption;

    private String outputPath, destinationPath;

    private int trimType;

    private long fixedGap, minGap, minFromGap, maxToGap;

    private boolean hidePlayerSeek, isAccurateCut;

    private CustomProgressView progressView;

    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video_trimmer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpToolBar(getSupportActionBar(), "Edit Video");
        toolbar.setNavigationOnClickListener(v -> finish());
        progressView = new CustomProgressView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        playerView = findViewById(R.id.player_view_lib);
        imagePlayPause = findViewById(R.id.image_play_pause);
        seekbar = findViewById(R.id.range_seek_bar);
        txtStartDuration = findViewById(R.id.txt_start_duration);
        txtEndDuration = findViewById(R.id.txt_end_duration);
        seekbarController = findViewById(R.id.seekbar_controller);
        ImageView imageOne = findViewById(R.id.image_one);
        ImageView imageTwo = findViewById(R.id.image_two);
        ImageView imageThree = findViewById(R.id.image_three);
        ImageView imageFour = findViewById(R.id.image_four);
        ImageView imageFive = findViewById(R.id.image_five);
        ImageView imageSix = findViewById(R.id.image_six);
        ImageView imageSeven = findViewById(R.id.image_seven);
        ImageView imageEight = findViewById(R.id.image_eight);
        imageViews = new ImageView[]{imageOne, imageTwo, imageThree,
                imageFour, imageFive, imageSix, imageSeven, imageEight};
        seekHandler = new Handler();
        initPlayer();
        if (checkStoragePermission())
            setDataInView();
    }

    private void setUpToolBar(ActionBar actionBar, String title) {
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayer() {
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector =
                    new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            videoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            playerView.requestFocus();
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            playerView.setPlayer(videoPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataInView() {
        try {
            uri = Uri.parse(getIntent().getStringExtra(TrimVideo.TRIM_VIDEO_URI));
            uri = Uri.parse(FileUtils.getPath(this, uri));
            LogMessage.v("VideoUri:: " + uri);
            totalDuration = TrimmerUtils.getDuration(this, uri);
            imagePlayPause.setOnClickListener(v ->
                    onVideoClicked());
            playerView.getVideoSurfaceView().setOnClickListener(v ->
                    onVideoClicked());
            validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validate() {
        try {
            TrimVideoOptions trimVideoOptions = getIntent().getParcelableExtra(TrimVideo.TRIM_VIDEO_OPTION);
            assert trimVideoOptions != null;
            trimType = TrimmerUtils.getTrimType(trimVideoOptions.trimType);
            destinationPath = trimVideoOptions.destination;
            hidePlayerSeek = trimVideoOptions.hideSeekBar;
            isAccurateCut = trimVideoOptions.accurateCut;
            compressOption = trimVideoOptions.compressOption;
            fixedGap = trimVideoOptions.fixedDuration;
            fixedGap = fixedGap != 0 ? fixedGap : totalDuration;
            minGap = trimVideoOptions.minDuration;
            minGap = minGap != 0 ? minGap : totalDuration;
            if (trimType == 3) {
                minFromGap = trimVideoOptions.minToMax[0];
                maxToGap = trimVideoOptions.minToMax[1];
                minFromGap = minFromGap != 0 ? minFromGap : totalDuration;
                maxToGap = maxToGap != 0 ? maxToGap : totalDuration;
            }
            if (destinationPath != null) {
                File outputDir = new File(destinationPath);
                outputDir.mkdirs();
                destinationPath = String.valueOf(outputDir);
                if (!outputDir.isDirectory())
                    throw new IllegalArgumentException("Destination file path error" + " " + destinationPath);
            }
            buildMediaSource(uri);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void onVideoClicked() {
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue);
                videoPlayer.setPlayWhenReady(true);
                return;
            }
            if ((currentDuration - lastMaxValue) > 0)
                seekTo(lastMinValue);
            videoPlayer.setPlayWhenReady(!videoPlayer.getPlayWhenReady());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seekTo(long sec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(sec * 1000);
    }

    private void buildMediaSource(Uri mUri) {
        try {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mUri);
            videoPlayer.prepare(videoSource);
            videoPlayer.setPlayWhenReady(true);
            videoPlayer.addListener(new Player.DefaultEventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_ENDED:
                            LogMessage.v("onPlayerStateChanged: Video ended.");
                            imagePlayPause.setVisibility(View.VISIBLE);
                            isVideoEnded = true;
                            break;
                        case Player.STATE_READY:
                            isVideoEnded = false;
                            startProgress();
                            imagePlayPause.setVisibility(videoPlayer.getPlayWhenReady() ? View.GONE :
                                    View.VISIBLE);
                            LogMessage.v("onPlayerStateChanged: Ready to play.");
                            break;
                        default:
                            break;
                    }
                }
            });
            setImageBitmaps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageBitmaps() {
        try {
            long diff = totalDuration / 8;
            new Handler().postDelayed(() -> {
                int index = 1;
                for (ImageView img : imageViews) {
                    img.setImageBitmap(TrimmerUtils.getFrameBySec(ActVideoTrimmer.this, uri, diff * index));
                    index++;
                }
                seekbar.setVisibility(View.VISIBLE);
                txtStartDuration.setVisibility(View.VISIBLE);
                txtEndDuration.setVisibility(View.VISIBLE);
            }, 1000);

            seekbarController.setMaxValue(totalDuration).apply();
            seekbar.setMaxValue(totalDuration).apply();
            seekbar.setMaxStartValue((float) totalDuration).apply();
            if (trimType == 1) {
                seekbar.setFixGap(fixedGap).apply();
                lastMaxValue = totalDuration;
            } else if (trimType == 2) {
                seekbar.setMaxStartValue((float) minGap);
                seekbar.setGap(minGap).apply();
                lastMaxValue = totalDuration;
            } else if (trimType == 3) {
                seekbar.setMaxStartValue((float) maxToGap);
                seekbar.setGap(minFromGap).apply();
                lastMaxValue = maxToGap;
            } else {
                seekbar.setGap(2).apply();
                lastMaxValue = totalDuration;
            }
            if (hidePlayerSeek)
                seekbarController.setVisibility(View.GONE);

            seekbar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
                if (!hidePlayerSeek)
                    seekbarController.setVisibility(View.VISIBLE);
            });

            seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                long minVal = (long) minValue;
                long maxVal = (long) maxValue;
                if (lastMinValue != minVal) {
                    seekTo((long) minValue);
                    if (!hidePlayerSeek)
                        seekbarController.setVisibility(View.INVISIBLE);
                }
                lastMinValue = minVal;
                lastMaxValue = maxVal;
                txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal));
                txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal));
                if (trimType == 3)
                    setDoneColor(minVal, maxVal);
            });

            seekbarController.setOnSeekbarFinalValueListener(value -> {
                long value1 = (long) value;
                if (value1 < lastMaxValue && value1 > lastMinValue) {
                    seekTo(value1);
                    return;
                }
                if (value1 > lastMaxValue)
                    seekbarController.setMinStartValue((int) lastMaxValue).apply();
                else if (value1 < lastMinValue) {
                    seekbarController.setMinStartValue((int) lastMinValue).apply();
                    if (videoPlayer.getPlayWhenReady())
                        seekTo(lastMinValue);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDoneColor(long minVal, long maxVal) {
        try {
            if (menuDone == null)
                return;
            if ((maxVal - minVal) <= maxToGap) {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorWhite)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = true;
            } else {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorWhiteLt)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PER_REQ_CODE) {
            if (isPermissionOk(grantResults))
                setDataInView();
            else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null)
            videoPlayer.release();
        if (progressView != null && progressView.isShowing())
            progressView.dismiss();
        stopRepeatingTask();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuDone = menu.findItem(R.id.action_done);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (SystemClock.elapsedRealtime() - lastClickedTime < 800)
                return true;
            lastClickedTime = SystemClock.elapsedRealtime();
            validateVideo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateVideo() {
        if (isValidVideo) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "";
            if (destinationPath != null)
                path = destinationPath;
            int fileNo = 0;
            String fileName = "trimmed_video_";
            File newFile = new File(path + File.separator +
                    (fileName + fileNo) + "." + TrimmerUtils.getFileExtension(this, uri));
            while (newFile.exists()) {
                fileNo++;
                newFile = new File(path + File.separator +
                        (fileName + fileNo) + "." + TrimmerUtils.getFileExtension(this, uri));
            }
            outputPath = String.valueOf(newFile);
            LogMessage.v("outputPath::" + outputPath);
            LogMessage.v("sourcePath::" + uri);
            videoPlayer.setPlayWhenReady(false);
            showProcessingDialog();
            String[] complexCommand;
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(String.valueOf(uri));
            String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            int w = TrimmerUtils.clearNull(width).isEmpty() ? 0 : Integer.parseInt(width);
            int h = Integer.parseInt(height);
            if (compressOption != null) {
                if (w >= 800) {
                    w = w / 2;
                    h = Integer.parseInt(height) / 2;
                    complexCommand = new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                            "-i", String.valueOf(uri),
                            "-s", w + "x" + h, "-r", String.valueOf(compressOption.getFrameRate()),
                            "-vcodec", "mpeg4", "-b:v",
                            compressOption.getBitRate() + "M", "-b:a", "48000", "-ac", "2", "-ar", "22050",
                            "-t",
                            TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath};
                } else {
                    complexCommand = new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                            "-i", String.valueOf(uri), "-s", w + "x" + h, "-r",
                            String.valueOf(compressOption.getFrameRate()), "-vcodec", "mpeg4", "-b:v",
                            "350K", "-b:a", "48000", "-ac", "2", "-ar", "22050",
                            "-t",
                            TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath};
                }
            } else if (isAccurateCut)
                complexCommand = getAccurateBinary();
            else {
                complexCommand = new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue) + ".00",
                        "-t",
                        TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue) + ".00", "-noaccurate_seek"
                        , "-i", String.valueOf(uri),
                        "-codec", "copy", "-avoid_negative_ts", "1", outputPath};
            }
            execFFmpegBinary(complexCommand, true);
        } else
            Toast.makeText(this, getString(R.string.txt_smaller) + " " + TrimmerUtils.getLimitedTimeFormatted(maxToGap), Toast.LENGTH_SHORT).show();
    }

    private void execFFmpegBinary(final String[] command, boolean retry) {
        try {
            long executionId = FFmpeg.executeAsync(command, (executionId1, returnCode) -> {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                } else {
                    if (retry) {
                        File newFile = new File(outputPath);
                        if (newFile.exists())
                            newFile.delete();
                        execFFmpegBinary(getAccurateBinary(), false);
                    } else {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        runOnUiThread(() ->
                                Toast.makeText(ActVideoTrimmer.this, "Failed to trim", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getAccurateBinary() {
        return new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue)
                , "-i", String.valueOf(uri), "-t",
                TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                "-async", "1", outputPath};
    }

    private void showProcessingDialog() {
        try {
            dialog = new Dialog(this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_convert);
            TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtCancel.setOnClickListener(v -> {
                dialog.dismiss();
                FFmpeg.cancel();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION);
        } else
            return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private boolean checkPermission(String... permissions) {
        boolean allPermitted = false;
        for (String permission : permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED);
            if (!allPermitted)
                break;
        }
        if (allPermitted)
            return true;
        ActivityCompat.requestPermissions(this, permissions,
                PER_REQ_CODE);
        return false;
    }


    private boolean isPermissionOk(int... results) {
        boolean isAllGranted = true;
        for (int result : results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    void startProgress() {
        updateSeekbar.run();
    }

    void stopRepeatingTask() {
        seekHandler.removeCallbacks(updateSeekbar);
    }

    Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            try {
                currentDuration = videoPlayer.getCurrentPosition() / 1000;
                if (!videoPlayer.getPlayWhenReady())
                    return;
                if (currentDuration <= lastMaxValue)
                    seekbarController.setMinStartValue((int) currentDuration).apply();
                else
                    videoPlayer.setPlayWhenReady(false);
            } finally {
                seekHandler.postDelayed(updateSeekbar, 1000);
            }
        }
    };

}