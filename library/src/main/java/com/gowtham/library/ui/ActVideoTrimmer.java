package com.gowtham.library.ui;

import static com.gowtham.library.utils.VideoResKt.fromDisplayName;
import static com.gowtham.library.utils.VideoResKt.getVideoResNames;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.effect.Presentation;
import androidx.media3.transformer.Composition;
import androidx.media3.transformer.DefaultEncoderFactory;
import androidx.media3.transformer.EditedMediaItem;
import androidx.media3.transformer.Effects;
import androidx.media3.transformer.ExportException;
import androidx.media3.transformer.ExportResult;
import androidx.media3.transformer.ProgressHolder;
import androidx.media3.transformer.Transformer;
import androidx.media3.transformer.VideoEncoderSettings;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.gowtham.library.R;
import com.gowtham.library.ui.seekbar.widgets.CrystalRangeSeekbar;
import com.gowtham.library.ui.seekbar.widgets.CrystalSeekbar;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.CustomProgressView;
import com.gowtham.library.utils.FileUtilKt;
import com.gowtham.library.utils.LocaleHelper;
import com.gowtham.library.utils.LogMessage;
import com.gowtham.library.utils.TrimVideo;
import com.gowtham.library.utils.TrimVideoOptions;
import com.gowtham.library.utils.TrimmerUtils;
import com.gowtham.library.utils.VideoRes;
import com.gowtham.library.utils.ViewUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;


@UnstableApi public class ActVideoTrimmer extends LocalizationActivity {

    private static final int PER_REQ_CODE = 115;
    private StyledPlayerView playerView;
    private ExoPlayer videoPlayer;

    private ImageView imagePlayPause;

    private ImageView[] imageViews;

    private long totalDuration;

    private Dialog dialog;

    private Uri fileUri;

    private TextView txtStartDuration, txtEndDuration;

    private CrystalRangeSeekbar seekbar;

    private long lastMinValue = 0;

    private long lastMaxValue = 0;

    private MenuItem menuDone;

    private CrystalSeekbar seekbarController;

    private boolean isValidVideo = true, isVideoEnded;

    private android.os.Handler seekHandler;

    private Bundle bundle;

    private ProgressBar progressBar;

    private TrimVideoOptions trimVideoOptions;

    private long currentDuration, lastClickedTime;

    private VideoRes selectedRes;
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
    private CompressOption compressOption;
    private String outputPath;
    private String local;
    private int trimType;
    private long fixedGap, minGap, minFromGap, maxToGap;
    private boolean hidePlayerSeek, isAccurateCut, showFileLocationAlert;
    private CustomProgressView progressView;
    private String fileName;

    private TextView resChangeSpinner, txtFileSize;
    private boolean isCompressionEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.act_video_trimmer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bundle = getIntent().getExtras();
        Gson gson = new Gson();
        String videoOption = bundle.getString(TrimVideo.TRIM_VIDEO_OPTION);
        trimVideoOptions = gson.fromJson(videoOption, TrimVideoOptions.class);
        toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.colorWhite));
        setUpToolBar(getSupportActionBar(), trimVideoOptions.title);
        toolbar.setNavigationOnClickListener(v -> finish());
        progressView = new CustomProgressView(this);
        View viewThumbnails= findViewById(R.id.view_thumbnails);
        View viewTimer= findViewById(R.id.view_timer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        View rootView= findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets topInsets= insets.getInsets(WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.statusBars());
                Insets btmInsets= insets.getInsets(WindowInsetsCompat.Type.navigationBars());

                int left= topInsets.left!=0 ? topInsets.left : btmInsets.left;
                int right= topInsets.right!=0 ? topInsets.right : btmInsets.right;
                rootView.setPadding(left, 0, right, 0);
                toolbar.setPadding(toolbar.getPaddingLeft(), topInsets.top, toolbar.getPaddingRight(),
                        toolbar.getPaddingBottom());

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewThumbnails.getLayoutParams();
                params.bottomMargin =btmInsets.bottom + ViewUtil.dpToPx(80);
                viewThumbnails.setLayoutParams(params);

                ViewGroup.MarginLayoutParams endDurationLayoutParams = (ViewGroup.MarginLayoutParams) viewTimer.getLayoutParams();
                endDurationLayoutParams.bottomMargin =btmInsets.bottom + ViewUtil.dpToPx(58);
                viewTimer.setLayoutParams(endDurationLayoutParams);
                return insets;
            }
        });
    }

    @Override
    protected void attachBaseContext(@NotNull Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
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
        progressBar = findViewById(R.id.progress_circular);
        ImageView imageOne = findViewById(R.id.image_one);
        ImageView imageTwo = findViewById(R.id.image_two);
        ImageView imageThree = findViewById(R.id.image_three);
        ImageView imageFour = findViewById(R.id.image_four);
        ImageView imageFive = findViewById(R.id.image_five);
        ImageView imageSix = findViewById(R.id.image_six);
        ImageView imageSeven = findViewById(R.id.image_seven);
        ImageView imageEight = findViewById(R.id.image_eight);
        ImageView imageNine = findViewById(R.id.image_nine);
        ImageView imageTen = findViewById(R.id.image_ten);
        resChangeSpinner= findViewById(R.id.txt_change_res);
        txtFileSize= findViewById(R.id.txt_file_size);

        View viewThumbnails = findViewById(R.id.view_thumbnails);

        ViewUtil.systemGestureExclusionRects(findViewById(R.id.root_view), viewThumbnails);
        imageViews = new ImageView[]{imageOne, imageTwo, imageThree,
                imageFour, imageFive, imageSix, imageSeven, imageEight, imageNine, imageTen};
        seekHandler = new Handler();
        initPlayer();

        fileUri = Uri.parse(bundle.getString(TrimVideo.TRIM_VIDEO_URI));
        isCompressionEnabled= bundle.getBoolean(TrimVideo.ENABLE_COMPRESSION, true);

        if(!isCompressionEnabled){
            resChangeSpinner.setVisibility(View.GONE);
        }

        String selectVideoRes = savedInstanceState!=null ? savedInstanceState.getString("selectedRes") : "";
        if (checkStoragePermission())
            setDataInView(selectVideoRes);
    }

    private double bitsToMbs(long bits) {
        return bits / 8.0 / 1024.0 / 1024;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedRes", selectedRes.getDisplayName());
    }

    private void setUpToolBar(ActionBar actionBar, String title) {
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title != null ? title : getString(R.string.txt_edt_video));
        } catch (Exception e) {
            Log.e("TAG", "initPlayer: ", e);
        }
    }

    /**
     * SettingUp exoplayer
     **/
    private void initPlayer() {
        try {
            videoPlayer = new ExoPlayer.Builder(this).build();
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            playerView.setPlayer(videoPlayer);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build();
            videoPlayer.setAudioAttributes(audioAttributes, true);
        } catch (Exception e) {
            Log.e("TAG", "initPlayer: ", e);
        }
    }

    private void setDataInView(String selectVideoRes) {
        try {
            setSelectedVideoRes(selectVideoRes);

            Runnable fileUriRunnable = () -> {
                runOnUiThread(() -> {
                    LogMessage.v("VideoPath:: fileUri: " + fileUri);
                    progressBar.setVisibility(View.GONE);
                    totalDuration = TrimmerUtils.getDuration(ActVideoTrimmer.this, fileUri);
                    imagePlayPause.setOnClickListener(v ->
                            onVideoClicked());
                    Objects.requireNonNull(playerView.getVideoSurfaceView()).setOnClickListener(v ->
                            onVideoClicked());
                    initTrimData();
                    buildMediaSource();
                    loadThumbnails();
                    setUpSeekBar();
                    setUpResChanger();
                });
            };
            Executors.newSingleThreadExecutor().execute(fileUriRunnable);
        } catch (Exception e) {
            Log.e("VideoTrimmer", "", e);
        }
    }

    private void setSelectedVideoRes(String selectVideoRes) {
        try {
            Pair<Integer, Integer> wh= TrimmerUtils.getVideoRes(this, fileUri);
            selectedRes= selectVideoRes!=null && !selectVideoRes.isEmpty() ?
                    fromDisplayName(selectVideoRes) : TrimmerUtils.classifyResolution(wh.first, wh.second);
            resChangeSpinner.setText(selectedRes.getDisplayName());
        } catch (Exception e) {
            Log.e("VideoTrimmer", "", e);
        }
    }

    private void setUpResChanger() {
        resChangeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResolutionMenu(resChangeSpinner);
            }
        });
    }

    private void showResolutionMenu(TextView anchorView) {

        Context wrapperContext = new ContextThemeWrapper(ActVideoTrimmer.this, R.style.AppTheme_PopupMenu);
        PopupMenu popupMenu = new PopupMenu(wrapperContext, anchorView);
        Log.e("TAG", "showResolutionMenu: "+popupMenu.getGravity());


        List<String> resolutions= getVideoResNames(this, fileUri);
        for (String resolution : resolutions) {
            popupMenu.getMenu().add(resolution);
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            Log.e("TAG", "showResolutionMenu: "+menuItem.getTitle().toString());
            selectedRes = fromDisplayName(menuItem.getTitle().toString());

            anchorView.setText(selectedRes.getDisplayName());
            return true;
        });
        popupMenu.show();
    }

    private void initTrimData() {
        try {
            assert trimVideoOptions != null;
            trimType = TrimmerUtils.getTrimType(trimVideoOptions.trimType);
            fileName = trimVideoOptions.fileName;
            hidePlayerSeek = trimVideoOptions.hideSeekBar;
            local = trimVideoOptions.local;
            compressOption = trimVideoOptions.compressOption;
            showFileLocationAlert = trimVideoOptions.showFileLocationAlert;
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
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        setLanguage(new Locale(local != null ? local : "en"));
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

    private void buildMediaSource() {
        try {
            DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(fileUri));
            videoPlayer.addMediaSource(mediaSource);
            videoPlayer.prepare();
            videoPlayer.setPlayWhenReady(true);
            videoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                    imagePlayPause.setVisibility(playWhenReady ? View.GONE :
                            View.VISIBLE);
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state) {
                        case Player.STATE_ENDED:
                            LogMessage.v("onPlayerStateChanged: Video ended.");
                            imagePlayPause.setVisibility(View.VISIBLE);
                            isVideoEnded = true;
                            break;
                        case Player.STATE_READY:
                            isVideoEnded = false;
                            imagePlayPause.setVisibility(View.GONE);
                            startProgress();
                            LogMessage.v("onPlayerStateChanged: Ready to play.");
                            break;
                        default:
                            break;
                        case Player.STATE_BUFFERING:
                            LogMessage.v("onPlayerStateChanged: STATE_BUFFERING.");
                            break;
                        case Player.STATE_IDLE:
                            LogMessage.v("onPlayerStateChanged: STATE_IDLE.");
                            break;
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  loading thumbnails
     * */
    private void loadThumbnails() {
        try {
            long diff = totalDuration / imageViews.length;
            int sec = 1;
            for (ImageView img : imageViews) {
                long interval = (diff * sec) * 1000000;
                RequestOptions options = new RequestOptions().frame(interval);
                Glide.with(this)
                        .load(fileUri)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(300))
                        .into(img);
                if (sec < totalDuration)
                    sec++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpSeekBar() {
        seekbar.setVisibility(View.VISIBLE);
        txtStartDuration.setVisibility(View.VISIBLE);
        txtEndDuration.setVisibility(View.VISIBLE);

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
    }

    /**
     * will be called whenever seekBar range changes
     * it checks max duration is exceed or not.
     * and disabling and enabling done menuItem
     *
     * @param minVal left thumb value of seekBar
     * @param maxVal right thumb value of seekBar
     */
    private void setDoneColor(long minVal, long maxVal) {
        try {
            if (menuDone == null)
                return;
            //changed value is less than maxDuration
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
                setDataInView("");
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
        try {
            if (videoPlayer != null)
                videoPlayer.release();
            if (progressView != null && progressView.isShowing())
                progressView.dismiss();
            File f = new File(getCacheDir(), "temp_video_file");
            if (f.exists()) {
                f.delete();
            }
            stopRepeatingTask();
            transformer.cancel();
        } catch (Exception e) {
            LogMessage.e(Log.getStackTraceString(e));
        }
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
            //preventing multiple clicks
            if (SystemClock.elapsedRealtime() - lastClickedTime < 800)
                return true;
            lastClickedTime = SystemClock.elapsedRealtime();
            trimVideo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @UnstableApi private void trimVideo() {
        if (isValidVideo) {
            //not exceed given maxDuration if has given
            outputPath = getFileName();
            LogMessage.v("outputPath::" + outputPath + new File(outputPath).exists());
            LogMessage.v("sourcePath::" + fileUri);
            videoPlayer.setPlayWhenReady(false);
            showProcessingDialog();

            androidx.media3.common.MediaItem mediaItem =
                    new androidx.media3.common.MediaItem.Builder()
                            .setUri(fileUri)
                            .setClippingConfiguration(
                                    new androidx.media3.common.MediaItem.ClippingConfiguration.Builder()
                                            .setStartPositionMs(lastMinValue * 1000)
                                            .setEndPositionMs(lastMaxValue * 1000)
                                            .build())
                            .build();

            Pair<Integer, Integer> videoRes = TrimmerUtils.getVideoRes(this, fileUri);

            int width= videoRes.first;
            int height= videoRes.second;
            EditedMediaItem editedMediaItem = new EditedMediaItem.Builder(mediaItem)
                    .setEffects(new Effects(ImmutableList.of(),
                            ImmutableList.of(Presentation.createForWidthAndHeight(width, height,
                                    Presentation.LAYOUT_SCALE_TO_FIT))))
                    .build();


            long bitRate= TrimmerUtils.getBitRate(this, fileUri);

            if(isCompressionEnabled){
                HashMap<VideoRes, Long> videoResMap= TrimmerUtils.getResBitRate(this, fileUri);
                Long compressionBitRate= videoResMap.get(selectedRes);
                bitRate= compressionBitRate!=null ? compressionBitRate : bitRate;
            }
            transformer =
                    new Transformer.Builder(this)
                            .addListener(transformerListener)
                            .setEncoderFactory(
                                    new DefaultEncoderFactory.Builder(this)
                                            .setRequestedVideoEncoderSettings(
                                                    new VideoEncoderSettings.Builder()
                                                            .setBitrate(Math.toIntExact(bitRate))
                                                            .build()
                                            ).build())
                            .build();

            transformer.getProgress(new ProgressHolder());
            transformer.start(editedMediaItem, outputPath);

            ProgressHolder progressHolder = new ProgressHolder();

            Handler mainHandler = new Handler(Looper.getMainLooper());
            if(progressRunnable!=null){
                mainHandler.removeCallbacks(progressRunnable);
            }
            progressRunnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        @Transformer.ProgressState int progressState = transformer.getProgress(progressHolder);
                        if (progressState == Transformer.PROGRESS_STATE_AVAILABLE) {
                            String progress = getString(R.string.txt_trimming_video)+ " "+progressHolder.progress+"%"; // This is the percentage (0-100)
                            if (txtProgress!=null){
                                txtProgress.setText(progress);
                            }
                        }
                        if (progressState != Transformer.PROGRESS_STATE_NOT_STARTED) {
                            mainHandler.postDelayed(this, 300); // Repeat after 500ms
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "run: ", e);
                    }
                }
            };
            mainHandler.post(progressRunnable); // Start the periodic update
        } else
            Toast.makeText(this, getString(R.string.txt_smaller) + " " + TrimmerUtils.getLimitedTimeFormatted(maxToGap), Toast.LENGTH_SHORT).show();
    }

    Transformer transformer;
    Runnable progressRunnable;
    Transformer.Listener transformerListener =
            new Transformer.Listener() {
                @Override
                public void onCompleted(Composition composition, ExportResult result) {
                    dialog.dismiss();
                    if (showFileLocationAlert) showLocationAlert();
                    else {
                        Intent intent = new Intent();
                        intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                @Override
                public void onError(Composition composition, ExportResult result,
                                    ExportException exception) {
                    if (dialog.isShowing()) dialog.dismiss();
                    Log.e("ActVideoTrimmer:: ", "Composition onError: "+composition);
                    Log.e("ActVideoTrimmer:: ", "ExportResult onError: "+result);
                    Log.e("ActVideoTrimmer:: ", "ExportException onError: ",exception);
                    runOnUiThread(() -> Toast.makeText(ActVideoTrimmer.this, "Failed to trim", Toast.LENGTH_SHORT).show());
                }
            };

    private String getFileName() {
        String path = getExternalFilesDir("TrimmedVideo").getPath();
        Calendar calender = Calendar.getInstance();
        String fileDateTime = calender.get(Calendar.YEAR) + "_" +
                calender.get(Calendar.MONTH) + "_" +
                calender.get(Calendar.DAY_OF_MONTH) + "_" +
                calender.get(Calendar.HOUR_OF_DAY) + "_" +
                calender.get(Calendar.MINUTE) + "_" +
                calender.get(Calendar.SECOND);
        String fName = "trimmed_video_";
        if (fileName != null && !fileName.isEmpty())
            fName = fileName;
        String extension = null;
        String mimeType = getContentResolver().getType(fileUri);

        if (mimeType != null) {
            extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        File newFile = new File(path + File.separator +
                (fName) + fileDateTime + "." + extension);
        return String.valueOf(newFile);
    }

    private void showLocationAlert() {
        // dialog to ask user to open file location in file manager or not
        AlertDialog openFileLocationDialog = new AlertDialog.Builder(ActVideoTrimmer.this).create();
        openFileLocationDialog.setTitle(getString(R.string.open_file_location));
        openFileLocationDialog.setCancelable(true);

        // when user click yes
        openFileLocationDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialogInterface, i) -> {
            // open file location
            Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uriFile = Uri.parse(outputPath);
            chooser.addCategory(Intent.CATEGORY_OPENABLE);
            chooser.setDataAndType(uriFile, "*/*");
            startActivity(chooser);
        });

        // when user click no and finish current activity
        openFileLocationDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialogInterface, i) -> openFileLocationDialog.dismiss());

        // when user click no and finish current activity
        openFileLocationDialog.setOnDismissListener(dialogInterface -> {
            Intent intent = new Intent();
            intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath);
            setResult(RESULT_OK, intent);
            finish();
        });
        openFileLocationDialog.show();
    }

    private TextView txtProgress;
    private void showProcessingDialog() {
        try {
            dialog = new Dialog(this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_convert);
            TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
            txtProgress = dialog.findViewById(R.id.txt_process);

            dialog.setCancelable(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtCancel.setOnClickListener(v -> {
                dialog.dismiss();
                transformer.cancel();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkStoragePermission() {
        String uri= FileUtilKt.getActualFileUri(this, fileUri);

        if(uri!=null && new File(uri).canRead()){
            Log.e("VideoTrimmer", "checkStoragePermission: has no permission");
            // might have used photo picker or file picker. therefore have read access without permission.
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                            == PackageManager.PERMISSION_GRANTED;
            if (hasPermission) {
                return true;
            } else {
                return checkPermission(
                        Manifest.permission.READ_MEDIA_VIDEO);
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            return checkPermission(
                    Manifest.permission.READ_MEDIA_VIDEO);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        } else
            return checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

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

}
