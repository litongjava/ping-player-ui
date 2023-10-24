package com.litongjava.ping.player.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.blankj.utilcode.util.SizeUtils;
import com.litongjava.ping.player.ui.R;
import com.litongjava.ping.player.utils.ImageUtils;

public class AlbumCoverView extends View {
  private static final long TIME_UPDATE = 50L;
  private static final float DISC_ROTATION_INCREASE = 0.5f;
  private static final float NEEDLE_ROTATION_PLAY = 0.0f;
  private static final float NEEDLE_ROTATION_PAUSE = -25.0f;

  private static final int TOP_LINE_HEIGHT = SizeUtils.dp2px(1f);
  private static final int COVER_BORDER_WIDTH = SizeUtils.dp2px(1f);

  private final Handler mainHandler = new Handler(Looper.getMainLooper());

  private final Drawable topLine;
  private final Drawable coverBorder;

  private Bitmap discBitmap;
  private final Matrix discMatrix = new Matrix();
  private final Point discStartPoint = new Point();
  private final Point discCenterPoint = new Point();
  private float discRotation = 0.0f;

  private Bitmap needleBitmap;
  private final Matrix needleMatrix = new Matrix();
  private final Point needleStartPoint = new Point();
  private final Point needleCenterPoint = new Point();
  private float needleRotation = NEEDLE_ROTATION_PLAY;

  private Bitmap coverBitmap;
  private final Matrix coverMatrix = new Matrix();
  private final Point coverStartPoint = new Point();
  private final Point coverCenterPoint = new Point();
  private int coverSize;

  private final ValueAnimator playAnimator;
  private final ValueAnimator pauseAnimator;

  private boolean isPlaying = false;

  public AlbumCoverView(Context context) {
    this(context, null);
  }

  public AlbumCoverView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AlbumCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    topLine = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_playing_cover_top_line, null);
    coverBorder = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_playing_cover_border, null);

    discBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_playing_disc);
    needleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_playing_needle);

    playAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PAUSE, NEEDLE_ROTATION_PLAY);
    playAnimator.setDuration(300);
    playAnimator.addUpdateListener(animation -> {
      needleRotation = (float) animation.getAnimatedValue();
      invalidate();
    });

    pauseAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PLAY, NEEDLE_ROTATION_PAUSE);
    pauseAnimator.setDuration(300);
    pauseAnimator.addUpdateListener(animation -> {
      needleRotation = (float) animation.getAnimatedValue();
      invalidate();
    });
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (w > 0 && h > 0) {
      initSize();
    }
  }

  private void initSize() {
    int unit = Math.min(getWidth(), getHeight()) / 8;

    discBitmap = ImageUtils.resizeImage(discBitmap, unit * 6, unit * 6);
    int discOffsetY = needleBitmap.getHeight() / 5;
    discStartPoint.set((getWidth() - discBitmap.getWidth()) / 2, discOffsetY);
    discCenterPoint.set(getWidth() / 2, discBitmap.getHeight() / 2 + discOffsetY);

    needleBitmap = ImageUtils.resizeImage(needleBitmap, unit * 2, unit * 3);
    needleStartPoint.set(getWidth() / 2 - needleBitmap.getWidth() / 6, -needleBitmap.getWidth() / 6);
    needleCenterPoint.set(discCenterPoint.x, 0);

    coverSize = unit * 4;
    coverStartPoint.set((getWidth() - coverSize) / 2, discOffsetY + (discBitmap.getHeight() - coverSize) / 2);
    coverCenterPoint.set(discCenterPoint.x, discCenterPoint.y);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    // 1.绘制顶部虚线
    topLine.setBounds(0, 0, getWidth(), TOP_LINE_HEIGHT);
    topLine.draw(canvas);

    // 2.绘制封面
    if (coverBitmap != null) {
      coverMatrix.setRotate(discRotation, coverCenterPoint.x, coverCenterPoint.y);
      coverMatrix.preTranslate(coverStartPoint.x, coverStartPoint.y);
      coverMatrix.preScale((float) coverSize / coverBitmap.getWidth(), (float) coverSize / coverBitmap.getHeight());
      canvas.drawBitmap(coverBitmap, coverMatrix, null);
    }

    // 3.绘制黑胶唱片外侧半透明边框
    coverBorder.setBounds(discStartPoint.x - COVER_BORDER_WIDTH, discStartPoint.y - COVER_BORDER_WIDTH,
      discStartPoint.x + discBitmap.getWidth() + COVER_BORDER_WIDTH,
      discStartPoint.y + discBitmap.getHeight() + COVER_BORDER_WIDTH);
    coverBorder.draw(canvas);

    // 4.绘制黑胶
    discMatrix.setRotate(discRotation, discCenterPoint.x, discCenterPoint.y);
    discMatrix.preTranslate(discStartPoint.x, discStartPoint.y);
    canvas.drawBitmap(discBitmap, discMatrix, null);

    // 5.绘制指针
    needleMatrix.setRotate(needleRotation, needleCenterPoint.x, needleCenterPoint.y);
    needleMatrix.preTranslate(needleStartPoint.x, needleStartPoint.y);
    canvas.drawBitmap(needleBitmap, needleMatrix, null);
  }

  public void initNeedle(boolean isPlaying) {
    needleRotation = isPlaying ? NEEDLE_ROTATION_PLAY : NEEDLE_ROTATION_PAUSE;
    invalidate();
  }

  public void setCoverBitmap(Bitmap bitmap) {
    coverBitmap = bitmap;
    discRotation = 0.0f;
    invalidate();
  }

  public void start() {
    if (isPlaying) {
      return;
    }
    isPlaying = true;
    mainHandler.post(rotationRunnable);
    playAnimator.start();
  }

  public void pause() {
    if (!isPlaying) {
      return;
    }
    isPlaying = false;
    mainHandler.removeCallbacks(rotationRunnable);
    pauseAnimator.start();
  }

  private final Runnable rotationRunnable = new Runnable() {
    @Override
    public void run() {
      if (isPlaying) {
        discRotation += DISC_ROTATION_INCREASE;
        if (discRotation >= 360) {
          discRotation = 0f;
        }
        invalidate();
      }
      mainHandler.postDelayed(this, TIME_UPDATE);
    }
  };
}