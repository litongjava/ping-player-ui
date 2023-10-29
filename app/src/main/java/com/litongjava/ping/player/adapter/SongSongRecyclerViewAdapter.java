package com.litongjava.ping.player.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.ui.R;

import java.util.List;

/**
 * @author Ping E Lee
 * @email itonglinux@qq.com
 * @date 2022/1/16
 */
public class SongSongRecyclerViewAdapter extends RecyclerView.Adapter<SongRecyclerViewViewHolder> {
  private Context context;
  private List<SongEntity> data;

  public SongSongRecyclerViewAdapter(Context context, List<SongEntity> data) {
    this.context = context;
    this.data = data;
  }

  @NonNull
  @Override
  public SongRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = View.inflate(context, R.layout.item_current_playlist, null);
    return new SongRecyclerViewViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull SongRecyclerViewViewHolder holder, int i) {
    holder.onBind(data.get(i));

  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }
}