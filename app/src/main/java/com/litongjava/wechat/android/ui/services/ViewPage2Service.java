package com.litongjava.wechat.android.ui.services;

import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.litongjava.wechat.android.ui.adapter.FragmentViewPager2Adapter;
import com.litongjava.wechat.android.ui.callback.ViewPager2OnPageChangeCallback;

import java.util.List;

public class ViewPage2Service {
  private FragmentManager fragmentManager;
  private Lifecycle lifecycle;
  private ViewPager2 viewPager2;
  private List<Fragment> fragments;
  private List<ImageView> imageViews;

  public ViewPage2Service(FragmentManager fragmentManager, Lifecycle lifecycle, ViewPager2 viewPager2,
                          List<Fragment> fragments, List<ImageView> imageViews) {
    this.fragmentManager = fragmentManager;
    this.lifecycle = lifecycle;
    this.viewPager2 = viewPager2;
    this.fragments = fragments;
    this.imageViews = imageViews;
    initPager();


  }

  private void initPager() {
    FragmentViewPager2Adapter adapter = new FragmentViewPager2Adapter(fragmentManager, lifecycle, fragments);
    viewPager2.setAdapter(adapter);

    ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2OnPageChangeCallback(imageViews);
    viewPager2.registerOnPageChangeCallback(changeCallback);
  }

  public void chageFragment(int index) {
    //fragment
    viewPager2.setCurrentItem(index);
    //修改按钮状态
    for (int i = 0; i < imageViews.size(); i++) {
      ImageView imageView = imageViews.get(i);
      if (index == i) {
        imageView.setSelected(true);
      } else {
        imageView.setSelected(false);
      }
    }
  }
}
