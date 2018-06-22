package com.capraraedefrancescosoft.progettomobidev.widgets;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.capraraedefrancescosoft.progettomobidev.R;

import java.io.IOException;

/**
 * Created by Ianfire on 26/06/2016.
 */
public class VideoPlayerView extends TextureView implements TextureView.SurfaceTextureListener, View.OnClickListener {

    private Uri videoUri;
    private MediaPlayer mp;
    private Surface surface;
    private float mVideoHeight;
    private float mVideoWidth;
    private View playView;

    public VideoPlayerView(Context context) {
        super(context);
        this.setOnClickListener(this);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnClickListener(this);
    }

    public void loadVideo(Uri videoUri) {
        this.playView = ((View) getParent()).findViewById(R.id.play_video);
        this.videoUri = videoUri;

        if (this.isAvailable()) {
            prepareVideo(getSurfaceTexture());
        }

        setSurfaceTextureListener(this);
    }


    public void loadAndStartVideo(Uri videoUri) {
        loadVideo(videoUri);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        prepareVideo(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void prepareVideo(SurfaceTexture t) {
        if (this.mp == null)
            this.mp = new MediaPlayer();

        mp.reset();
        surface = new Surface(t);

        mp.setSurface(surface);

        try {
            mp.setLooping(false);
            mp.setDataSource(this.getContext(), videoUri);
            mp.prepareAsync();

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.seekTo(1);
                    calculateVideoSize(videoUri);
                    ViewGroup.LayoutParams layoutParams = VideoPlayerView.this.getLayoutParams();
                    layoutParams.width = (int) mVideoWidth;
                    layoutParams.height = (int) mVideoHeight;
                    VideoPlayerView.this.setLayoutParams(layoutParams);
                }
            });

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (playView != null)
                        playView.setVisibility(VISIBLE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void calculateVideoSize(Uri videoUri) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(getContext(), videoUri);
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);

        float videoH = Float.parseFloat(height);
        float videoW = Float.parseFloat(width);

        // scala il video per fittare l'altezza di un element
        View parent = (View) getParent();
        mVideoHeight = parent.getHeight();

        // la lunghezza del video viene scalata in proporzione all'altezza
        float scaling = mVideoHeight / videoH;
        mVideoWidth = videoW * scaling;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    public synchronized boolean startPlay() {
        if (mp != null)
            if (!mp.isPlaying()) {
                mp.start();
                if (playView != null)
                    playView.setVisibility(View.INVISIBLE);
                return true;
            }

        return false;
    }

    public synchronized void pausePlay() {
        if (mp != null) {
            mp.pause();
            if (playView != null)
                playView.setVisibility(View.VISIBLE);

        }
    }

    public synchronized void stopPlay() {
        if (mp != null) {
            mp.stop();
            if (playView != null)
                playView.setVisibility(View.VISIBLE);
        }
    }

    public void changePlayState() {
        if (mp != null) {
            if (mp.isPlaying()) {
                pausePlay();
            } else {
                startPlay();
            }
        }

    }

    public void onClick(View v) {
        changePlayState();
    }
}