package com.lakshitasuman.musicstation.musicplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.R;

import com.lakshitasuman.musicstation.musicplayer.ViewAlbumSongsActivity;
import com.lakshitasuman.musicstation.musicplayer.model.AlbumModel;
import com.lakshitasuman.musicstation.musicplayer.utils.TimberUtils;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    List<AlbumModel> albumList;
    Context ctx;

    public AlbumAdapter(Context context, List<AlbumModel> list) {
        ctx = context;
        albumList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_album_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        AlbumModel albumModel = albumList.get(i);
        viewHolder.title.setText(albumModel.getName());
        if (albumModel.getAlbumSongs().size() > 1) {
            viewHolder.artist.setText("("+albumModel.getAlbumSongs().size() + " Songs)");
        } else {
            viewHolder.artist.setText("("+albumModel.getAlbumSongs().size() + " Song)");
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.tempAlbum = MainActivity.finalAlbums.get(i);
                ctx.startActivity(new Intent(ctx, ViewAlbumSongsActivity.class));

            }
        });
        Log.e("album id",albumModel.getAlbumId().toString());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ctx));
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(albumModel.getAlbumId()).toString(), viewHolder.coverImage,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.albums_image)
                        .resetViewBeforeLoading(true)
                        .build(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                       Log.e("aaaa","onLoadingStarted");
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        Log.e("bbbb","onLoadingFailed");

                                           }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Log.e("cccc","onLoadingComplete");
                                            }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        Log.e("dddd","onLoadingCancelled");
                    }
                });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView art;
        TextView artist;
        RelativeLayout bottomHolder;
        LinearLayout layout;
        TextView title;
        ImageView coverImage;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
            artist = (TextView) view.findViewById(R.id.card_artist);
            coverImage = (ImageView) view.findViewById(R.id.coverImage);
            layout = (LinearLayout) view.findViewById(R.id.layout);

        }
    }
}
