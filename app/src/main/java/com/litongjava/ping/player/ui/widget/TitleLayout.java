package com.litongjava.ping.player.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.litongjava.ping.player.ui.R;

public class TitleLayout extends LinearLayout {
  public TitleLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleLayout);
    boolean justShowStatusBar = ta.getBoolean(R.styleable.TitleLayout_tlJustShowStatusBar, false);
    // 根据justShowStatusBar的值进行操作
    ta.recycle();
  }
}
