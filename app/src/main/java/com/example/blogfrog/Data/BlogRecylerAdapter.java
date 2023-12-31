package com.example.blogfrog.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogfrog.Model.Blog;
import com.example.blogfrog.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class BlogRecylerAdapter extends RecyclerView.Adapter<BlogRecylerAdapter.ViewHolder> {
    private Context context;
    private List<Blog> blogList;

    public BlogRecylerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogRecylerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogRecylerAdapter.ViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        String imageUrl = null;

        holder.title.setText(blog.getTitle());
        holder.desc.setText(blog.getDesc());

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());
        holder.timestamp.setText(blog.getTimestamp());

        imageUrl = blog.getImage();

        Picasso.get().load(imageUrl).into(holder.image);
    }
    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView desc;
        public TextView timestamp;
        public ImageView image;
        String userid;
        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;

            title = (TextView) view.findViewById(R.id.postTitleList);
            desc = (TextView) view.findViewById(R.id.postTextList);
            image = (ImageView) view.findViewById(R.id.postImageList);
            timestamp = (TextView) view.findViewById(R.id.timestampList);

            userid = null;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to next activity
                }
            });

        }
    }
}
