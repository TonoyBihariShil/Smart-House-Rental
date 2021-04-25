package com.smarthouserental.adepter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.smarthouserental.R;

import java.util.ArrayList;

public class PostImageAdepter extends RecyclerView.Adapter<PostImageAdepter.PostImageViewHolder>{

    private ArrayList<String> imageLis;

    public PostImageAdepter(ArrayList<String> imageLis) {
        this.imageLis = imageLis;
    }

    @NonNull
    @Override
    public PostImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_post_add_image,parent,false);
        return new PostImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostImageViewHolder holder, int position) {
        Bitmap bitmap = BitmapFactory.decodeFile(imageLis.get(position));
        holder.image.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return imageLis.size();
    }

    class PostImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        PostImageViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.list_post_add_image);
        }
    }
}
