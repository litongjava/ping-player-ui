package com.litongjava.ping.player.utils;

import android.graphics.Bitmap;

import com.litongjava.ping.player.model.CommonResult;

public interface ImageLoadCallback {
  /**
   * This method will be called once the image loading process is completed.
   *
   * @param result The result of the image loading operation. It can be a success with the Bitmap data or a failure.
   */
  void onResult(CommonResult<Bitmap> result);
}
