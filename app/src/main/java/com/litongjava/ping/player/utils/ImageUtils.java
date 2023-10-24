package com.litongjava.ping.player.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.litongjava.ping.player.model.CommonResult;
import com.litongjava.ping.player.ui.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 图像工具类
 */
public class ImageUtils {

  private ImageUtils() {
    // private constructor to prevent instantiation
  }

  /**
   * 将图片放大或缩小到指定尺寸
   */
  public static Bitmap resizeImage(Bitmap source, int dstWidth, int dstHeight) {
    if (source.getWidth() == dstWidth && source.getHeight() == dstHeight) {
      return source;
    } else {
      return Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true);
    }
  }

  public static void loadCover(ImageView imageView, Object url, int corners) {
    if (corners > 0) {
      // 圆角和 CenterCrop 不兼容，需同时设置
      Glide.with(imageView.getContext())
        .load(url)
        .placeholder(R.drawable.ic_default_cover)
        .error(R.drawable.ic_default_cover)
        .transform(new CenterCrop(), new RoundedCorners(corners))
        .into(imageView);
    } else {
      Glide.with(imageView.getContext())
        .load(url)
        .placeholder(R.drawable.ic_default_cover)
        .error(R.drawable.ic_default_cover)
        .into(imageView);
    }
  }

  public static void loadBitmap(Object url, final ImageLoadCallback callback) {
    Glide.with(Utils.getApp())
      .asBitmap()
      .load(url)
      .into(new CustomTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
          callback.onResult(CommonResult.success(resource));
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
          super.onLoadFailed(errorDrawable);
          callback.onResult(CommonResult.fail());
        }
      });
  }

  public static void loadBitmap(String url, final ImageLoadCallback callback) {
    loadBitmap((Object) url, callback);
  }

  public static CommonResult<Bitmap> loadBitmap(String url) {
    final Object lock = new Object();
    final List<CommonResult<Bitmap>> resultList = new ArrayList<>(1);

    loadBitmap((Object) url, new ImageLoadCallback() {
      @Override
      public void onResult(CommonResult<Bitmap> result) {
        synchronized (lock) {
          resultList.add(result);
          lock.notifyAll();
        }
      }
    });

    synchronized (lock) {
      while (resultList.isEmpty()) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
          return CommonResult.fail(-1, "Interrupted Exception");
        }
      }
      return resultList.get(0);
    }
  }


}
