package com.litongjava.ping.player.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.annotation.FindViewByIdLayout;
import com.litongjava.android.view.inject.annotation.OnClick;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.aop.AopManager;
import com.litongjava.ping.player.ui.fragment.PlayerFragment;
import com.litongjava.wechat.android.ui.adapter.FragmentViewPager2Adapter;
import com.litongjava.wechat.android.ui.callback.ViewPager2OnPageChangeCallback;
import com.litongjava.wechat.android.ui.fragment.EmptyFragment;
import com.litongjava.wechat.android.ui.services.ViewPage2Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@FindViewByIdLayout(R.layout.activity_view_pager2_wechat_actvity)
public class ViewPager2WechatActvity extends AppCompatActivity {

  private Logger log = LoggerFactory.getLogger(this.getClass());
  @FindViewById(R.id.viewPager2)
  private ViewPager2 viewPager2;

  @FindViewById(R.id.bottom_weixin_linearLayout)
  private LinearLayout bottom_weixin_linearLayout;
  @FindViewById(R.id.bottom_contact_list_linearLayout)
  private LinearLayout bottom_contact_list_linearLayout;
  @FindViewById(R.id.bottom_find_linearLayout)
  private LinearLayout bottom_find_linearLayout;
  @FindViewById(R.id.bottom_profile_linearLayout)
  private LinearLayout bottom_profile_linearLayout;

  @FindViewById(R.id.bottom_weixin_imageView)
  private ImageView bottom_weixin_imageView;
  @FindViewById(R.id.bottom_contact_list_imageView)
  private ImageView bottom_contact_list_imageView;
  @FindViewById(R.id.bottom_find_imageView)
  private ImageView bottom_find_imageView;
  @FindViewById(R.id.bottom_profile_imageView)
  private ImageView bottom_profile_imageView;

  @FindViewById(R.id.bottom_weixin_textView)
  private TextView bottom_weixin_textView;
  @FindViewById(R.id.bottom_contact_list_textView)
  private TextView bottom_contact_list_textView;
  @FindViewById(R.id.bottom_find_textView)
  private TextView bottom_find_textView;
  @FindViewById(R.id.bottom_profile_textView)
  private TextView bottom_profile_textView;

  List<ImageView> imageViews = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ViewInjectUtils.injectActivity(this, this);
    initView();
    initPager();
  }

  private void initView() {
    //设置为默认是选中状态
    bottom_weixin_imageView.setSelected(true);
  }

  private void initPager() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Lifecycle lifecycle = getLifecycle();
    List<Fragment> fragments = getFragments();
    List<ImageView> imageViews = getImageViews();

    ViewPage2Service viewPage2Services = new ViewPage2Service(fragmentManager, lifecycle, viewPager2, fragments, imageViews);
    AopManager.me().addSingletonObject(viewPage2Services);

  }

  private List<ImageView> getImageViews() {
    if (imageViews == null) {
      imageViews = new ArrayList<>();
      imageViews.add(bottom_weixin_imageView);
      imageViews.add(bottom_contact_list_imageView);
      imageViews.add(bottom_find_imageView);
      imageViews.add(bottom_profile_imageView);
    }
    return imageViews;
  }

  private List<Fragment> getFragments() {
    EmptyFragment fragment01 = EmptyFragment.newInstance("微信");
    EmptyFragment fragment02 = EmptyFragment.newInstance("通讯录");

    Fragment fragment03 = PlayerFragment.newInstance("Player");
    EmptyFragment fragment04 = EmptyFragment.newInstance("我的");

    List<Fragment> fragments = new ArrayList<>(4);
    fragments.add(fragment01);
    fragments.add(fragment02);
    fragments.add(fragment03);
    fragments.add(fragment04);
    return fragments;
  }

  @OnClick(R.id.bottom_weixin_imageView)
  public void bottom_weixin_imageView_OnClick(View view) {
    Aop.get(ViewPage2Service.class).chageFragment(0);
  }

  @OnClick(R.id.bottom_contact_list_imageView)
  public void bottom_contact_list_textView_OnClick(View view) {
    Aop.get(ViewPage2Service.class).chageFragment(1);
  }

  @OnClick(R.id.bottom_find_imageView)
  public void bottom_find_textView_OnClick(View view) {
    Aop.get(ViewPage2Service.class).chageFragment(2);
  }

  @OnClick(R.id.bottom_profile_imageView)
  public void bottom_profile_textView_OnClick(View view) {
    Aop.get(ViewPage2Service.class).chageFragment(3);
  }
}