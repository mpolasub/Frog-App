package com.example.blogfrog.Data;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogfrog.Model.Blog;
import com.example.blogfrog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
//        PFP
        String pfpUrl = null;

        holder.title.setText(blog.getTitle());
        holder.desc.setText(blog.getDesc());

        holder.mood.setImageResource(R.mipmap.happy_mood);

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());
        holder.timestamp.setText("Posted " + formattedDate);

        imageUrl = blog.getImage();
//        PFP
        pfpUrl = blog.getPfp();

        Picasso.get().load(imageUrl).into(holder.image);
//PFP
        Picasso.get().load(pfpUrl).into(holder.pfp);
        holder.userName.setText(blog.getUserName());
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
        public TextView userName;
        public ImageView pfp;
        public ImageView mood;
        String userid;
        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;

            title = (TextView) view.findViewById(R.id.postTitleList);
            desc = (TextView) view.findViewById(R.id.postTextList);
            image = (ImageView) view.findViewById(R.id.postImageList);
            timestamp = (TextView) view.findViewById(R.id.timestampList);
            userName = (TextView) view.findViewById(R.id.postUsername);
            mood = (ImageView) view.findViewById(R.id.postMood);

//            pfp
            pfp = (ImageView) view.findViewById(R.id.pfpIV);

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
