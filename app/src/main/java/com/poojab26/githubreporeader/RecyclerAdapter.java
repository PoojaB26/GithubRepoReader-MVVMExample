package com.poojab26.githubreporeader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by poojab26 on 30-May-18.
 */public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Repo> repos;
    private Context context;

    public  RecyclerAdapter(List repos) {
        this.repos = repos;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        //Set our CardView here
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.ViewHolder viewHolder, final  int i) {

        viewHolder.tvName.setText(repos.get(i).getName());
        viewHolder.tvId.setText("Id: " +String.valueOf(repos.get(i).getId()));

        viewHolder.tvOwnerLogin.setText("Owner: " +repos.get(i).getOwner().getLogin());
        viewHolder.tvOwnerUrl.setText(repos.get(i).getOwner().getAvatarUrl());

        //Download image with Picasso
        Picasso.with(context)
                .load(repos.get(i).getOwner().getAvatarUrl())
                .resize(500, 500)
                .centerCrop()
                .into(viewHolder.ivOwner);

    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName,tvUrl,tvId, tvOwnerLogin, tvOwnerUrl;
        private ImageView ivOwner;

        public ViewHolder(View view) {
            super(view);

            tvName = (TextView)view.findViewById(R.id.textView_name);
            tvId = (TextView)view.findViewById(R.id.textView_id);
            tvUrl = (TextView)view.findViewById(R.id.textView_url);
            tvOwnerLogin = (TextView)view.findViewById(R.id.textView_Owner);
            tvOwnerUrl = (TextView)view.findViewById(R.id.textView_OwnerUrl);
            ivOwner = (ImageView) view.findViewById(R.id.imageView_Owner);

        }
    }
}