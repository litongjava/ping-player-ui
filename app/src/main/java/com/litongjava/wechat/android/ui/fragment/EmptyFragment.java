package com.litongjava.wechat.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.ping.player.ui.R;


public class EmptyFragment extends Fragment {

  private static final String ARG_TEXT = "text_content";
  private String textContent;

  private View rootView = null;

  @FindViewById(R.id.textView01)
  private TextView textView01;

  public static EmptyFragment newInstance(String text) {
    EmptyFragment fragment = new EmptyFragment();
    Bundle args = new Bundle();
    args.putString(ARG_TEXT, text);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      textContent = getArguments().getString(ARG_TEXT);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (rootView == null) {
      rootView = inflater.inflate(R.layout.empty_fragment, container, false);
    }
    ViewInjectUtils.injectViewAndOnClick(rootView, this);
    initView();
    return rootView;
  }

  private void initView() {
    textView01.setText(textContent);
  }
}