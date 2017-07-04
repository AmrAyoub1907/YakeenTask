package com.amrayoub.yakeentask;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Amr Ayoub on 7/3/2017.
 */

public class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.StoriesViewHolder>{
    ArrayList<TopSories> mystories;
    Context mContext;
    RecycleviewAdapter(ArrayList<TopSories> mystories){
        this.mystories = mystories;
    }
    class StoriesViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView date;
        TextView title;
        ImageView photo;
        public StoriesViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            date = (TextView)itemView.findViewById(R.id.date);
            title = (TextView)itemView.findViewById(R.id.title);
            photo = (ImageView)itemView.findViewById(R.id.photo);
        }
    }
    @Override
    public StoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        mContext = parent.getContext();
        StoriesViewHolder holder = new StoriesViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(StoriesViewHolder holder, int position) {
        holder.date.setText(mystories.get(position).getmPublished_date().substring(0,10));
        holder.title.setText(mystories.get(position).getmTitle());
        Picasso.with(mContext).load(mystories.get(position).getmUrl()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return mystories.size();
    }
}
