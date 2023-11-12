package com.hello.writer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    ArrayList<DataReturn> list;

    public PostAdapter(Context context, ArrayList<DataReturn> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_content_view, parent,false);
        //return new postAdapter.ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {

        DataReturn dataReturn = list.get(position);
        holder.title.setText(dataReturn.getTitle());
        holder.content.setText(dataReturn.getBody());
        String id = dataReturn.getId();
        /*Picasso.with(context)
                .load(dataReturn.getImage()).resize(300,300).centerCrop()
                .error(R.drawable.upload_image)
                .into(holder.img);*/
        Glide.with(context).load(dataReturn.getImage())
                .thumbnail(1).centerCrop()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView img; TextView title,content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.postImageView);
            title = itemView.findViewById(R.id.post_view_title);
            content = itemView.findViewById(R.id.post_view_content);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            DataReturn dataReturn = list.get(position);
            String id = dataReturn.getId();
        }
    }
}
