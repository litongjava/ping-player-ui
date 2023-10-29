package com.litongjava.ping.player.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.annotation.FindViewByIdLayout;
import com.litongjava.android.view.inject.annotation.OnClick;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.adapter.SongSongRecyclerViewAdapter;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.ui.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@FindViewByIdLayout(R.layout.activity_current_play_list)
public class CurrentPlayListActivity extends AppCompatActivity {

  private AudioPlayer audioPlayer = Aop.get(AudioPlayer.class);

  @FindViewById(R.id.currentPlayListRecyclerView)
  private RecyclerView currentPlayListRecyclerView;
  private SongSongRecyclerViewAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ViewInjectUtils.injectActivity(this, this);
    initView();
    initData();
  }


  private void initView() {
    // 获取根视图
    View rootView = findViewById(android.R.id.content);

    // 为根视图设置点击监听器
    rootView.setOnClickListener(v -> {
      onBackPressed();
    });


    adapter = new SongSongRecyclerViewAdapter(this, audioPlayer.getPlaylist().getValue());
    adapter.setCurrentId(audioPlayer.getCurrentSong().getValue().getSongId());
    currentPlayListRecyclerView.setAdapter(adapter);
  }


  @OnClick(R.id.btnClear)
  public void btnClearOnClick(View v) {
    ToastUtils.showLong("清空");
    audioPlayer.clearPlayList();
  }

  private void initData() {
    Aop.get(AudioPlayer.class).getPlaylist().observe(this, new Observer<List<SongEntity>>() {

      @Override
      public void onChanged(List<SongEntity> playlist) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        if (playlist == null) return;
        int size = playlist.size();
        log.info("size:{}",size);
        adapter.updateData(playlist);
      }
    });

    Aop.get(AudioPlayer.class).getCurrentSong().observe(this, new Observer<SongEntity>() {
      @Override
      public void onChanged(SongEntity songEntity) {
        adapter.setCurrentId(songEntity.getSongId());
        adapter.notifyDataSetChanged();
      }
    });
  }


}
