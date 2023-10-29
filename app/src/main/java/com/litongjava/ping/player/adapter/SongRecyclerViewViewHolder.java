package com.litongjava.ping.player.adapter;

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


public class SongRecyclerViewViewHolder extends RecyclerView.ViewHolder {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  private ShapeTextView vvTitle;
  private ShapeTextView vvArtist;
  private ImageView ivDelete;

  public SongRecyclerViewViewHolder(@NonNull View itemView) {
    super(itemView);
    vvTitle = itemView.findViewById(R.id.itemVvTitle);
    log.info("vvTitle:{}", vvTitle);
    vvArtist = itemView.findViewById(R.id.itemVvTitle);
    ivDelete = itemView.findViewById(R.id.itemIvDelete);
  }

  public void onBind(SongEntity e) {

    String fileName = e.getFileName();
    log.info("filename:{}", fileName);
    vvTitle.setText("filename");
    vvArtist.setText("artist");
    ivDelete.setOnClickListener((View v) -> {
      Aop.get(AudioPlayer.class).delete(e);
    });
  }
}