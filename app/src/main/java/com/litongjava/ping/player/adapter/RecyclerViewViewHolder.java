package com.litongjava.ping.player.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.shape.view.ShapeTextView;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.ui.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  private ShapeTextView vvTitle;
  private ShapeTextView vvArtist;
  private ImageView ivDelete;
  private ShapeTextView itemIndex;

  public RecyclerViewViewHolder(@NonNull View itemView) {
    super(itemView);
    itemIndex = itemView.findViewById(R.id.itemIndex);
    vvTitle = itemView.findViewById(R.id.itemVvTitle);
    vvArtist = itemView.findViewById(R.id.itemTvArtist);
    ivDelete = itemView.findViewById(R.id.itemIvDelete);

  }

  public void onBind(int i, SongEntity e, Long currentId) {
    itemIndex.setText(i + 1 + ":");
    String fileName = e.getFileName();
    if (e.getSongId() == currentId) {
      vvTitle.setSelected(true);
      vvArtist.setSelected(true);
      itemIndex.setSelected(true);
    }
    vvTitle.setText(fileName);
    vvArtist.setText(e.getArtist());

    ivDelete.setOnClickListener((View v) -> {
      Aop.get(AudioPlayer.class).delete(e.getSongId());
    });

    super.itemView.setOnClickListener((View) -> {
      Aop.get(AudioPlayer.class).play(e);
    });
  }
}