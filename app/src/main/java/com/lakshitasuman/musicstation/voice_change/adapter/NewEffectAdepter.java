package com.lakshitasuman.musicstation.voice_change.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.radio.service.PauseReason;
import com.lakshitasuman.musicstation.radio.service.PlayerServiceUtil;
import com.lakshitasuman.musicstation.voice_change.models.EffectModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class NewEffectAdepter extends RecyclerView.Adapter<NewEffectAdepter.ViewHolder> {
    List<EffectModel> effectObjectList;
    Context context;
    int selectedPos= -1;
    public OnEffectListener onEffectListener;


    public interface OnEffectListener {
        void onPlayEffect(EffectModel effectObject);

        void onShareEffect(EffectModel effectObject);
    }


   public  NewEffectAdepter(List<EffectModel> effectObjectList, Context mcontext){
       this.effectObjectList=effectObjectList;
       context=mcontext;
   }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.effect_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       EffectModel effectObject=effectObjectList.get(position);
        holder.textView.setText(effectObject.getName());
        AssetManager assetManager = context.getAssets();
        try (
                InputStream inputStream = assetManager.open(effectObject.getImagePath())
        ) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.imageView.setImageBitmap(bitmap);
        } catch (IOException ex) {
            //ignored
        }
        if (selectedPos==position){
            holder.selectedImage.setVisibility(View.VISIBLE);
        }
        else{
            holder.selectedImage.setVisibility(View.GONE);
        }

      /*  if (effectObject.isPlaying()) {
            holder.selectedImage.setVisibility(View.VISIBLE);
        } else {
            holder.selectedImage.setVisibility(View.GONE);
        }*/
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PlayerServiceUtil.isPlaying()){
                    PlayerServiceUtil.pause(PauseReason.NONE);
                }
                if(PlayerActivity.mMediaPlayer != null){
                    if (PlayerActivity.mMediaPlayer.isPlaying()){
                        PlayerActivity.mMediaPlayer.pause();
                    }
                }
                selectedPos=position;
                if (onEffectListener != null) {
                    onEffectListener.onPlayEffect(effectObject);
                }
                notifyDataSetChanged();

            }
        });

    }


    @Override
    public int getItemCount() {
        return effectObjectList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView selectedImage;
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.effect_image);
            this.selectedImage = (ImageView) itemView.findViewById(R.id.selected_image);
            this.textView = (TextView) itemView.findViewById(R.id.effectText);

        }
    }

    public void setOnEffectListener(OnEffectListener onEffectListener2) {
        onEffectListener = onEffectListener2;
    }
}
