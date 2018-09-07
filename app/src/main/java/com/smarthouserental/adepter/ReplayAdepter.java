package com.smarthouserental.adepter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smarthouserental.R;
import com.smarthouserental.model.Replay;
import java.util.ArrayList;

public class ReplayAdepter extends RecyclerView.Adapter<ReplayAdepter.ReplayViewHolder>{
    private ArrayList<Replay> replayList;
    private Activity activity;

    public ReplayAdepter(ArrayList<Replay> replayList, Activity activity) {
        this.replayList = replayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ReplayAdepter.ReplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_replay_item,parent,false);
        return new ReplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplayViewHolder holder, int position) {
        Replay replay = replayList.get(position);
        holder.replayText.setText(replay.getName()+" : "+replay.getReplay());
    }

    @Override
    public int getItemCount() {
        return replayList.size();
    }

    class ReplayViewHolder extends RecyclerView.ViewHolder {
        private TextView replayText;
        public ReplayViewHolder(View itemView) {
            super(itemView);

            replayText = itemView.findViewById(R.id.replay_text);

        }
    }
}
