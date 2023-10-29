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
public class SongSongRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder> {
  public Long currentId;
  private Context context;
  private List<SongEntity> data;

  public SongSongRecyclerViewAdapter(Context context, List<SongEntity> data) {
    this.context = context;
    this.data = data;
  }

  @NonNull
  @Override
  public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = View.inflate(context, R.layout.item_current_playlist, null);
    return new RecyclerViewViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewViewHolder holder, int i) {
    holder.onBind(i,data.get(i), currentId);

  }

  @Override
  public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void updateData(List<SongEntity> data) {
    this.data = data;
    notifyDataSetChanged();
  }

  public void setCurrentId(Long songId) {
    this.currentId = songId;
  }
}