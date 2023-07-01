package com.example.musicplayz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MusicItemRecyclerViewAdapter extends RecyclerView.Adapter<MusicItemRecyclerViewAdapter.MyViewHolder> {



    Context context;
    public MusicItemRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MusicItemRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.music_item_list_view, parent, false);

        return new MusicItemRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItemRecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.musicName.setText(Dashboard.musicName.get(position));
//        holder.musicName.setSelected(true);
        holder.playSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Player.class);
                Dashboard.position = position;
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Dashboard.musicName.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView musicName;
        MaterialButton playSongButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            musicName = itemView.findViewById(R.id.music_name);
            playSongButton = itemView.findViewById(R.id.playSong_button);
        }
    }
}
