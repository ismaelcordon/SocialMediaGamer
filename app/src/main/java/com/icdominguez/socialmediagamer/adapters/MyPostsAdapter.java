package com.icdominguez.socialmediagamer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.models.Post;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.LikeProvider;
import com.icdominguez.socialmediagamer.providers.PostProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.icdominguez.socialmediagamer.ui.activities.PostDetailActivity;
import com.icdominguez.socialmediagamer.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post, MyPostsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    LikeProvider mLikeProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    public MyPostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;

        mUserProvider = new UserProvider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String mRelativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), context);

        holder.mTextViewRelativeTimePost.setText(mRelativeTime);
        holder.mTextViewTitlePost.setText(post.getTitle().toUpperCase());

        if(post.getUserId().equals(mAuthProvider.getUid())) {
            holder.mImageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.mImageViewDelete.setVisibility(View.GONE);
        }

        if(post.getImage1() != null) {
            Picasso.with(context).load(post.getImage1()).into(holder.mCircleImagePost);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostDetailActivity.class);
                i.putExtra("id", postId);
                context.startActivity(i);
            }
        });

        holder.mImageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDelete(postId);
            }
        });
    }

    private void showConfirmDelete(final String postId) {
        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar publicación")
                .setMessage("¿Estas seguro de realizar esta acción?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void deletePost(String postId) {
        mPostProvider.deletePost(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(context, "El post se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No se pudo eliminar el post", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewTitlePost;
        TextView mTextViewRelativeTimePost;
        CircleImageView mCircleImagePost;
        ImageView mImageViewDelete;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            mTextViewTitlePost = view.findViewById(R.id.textViewTitlePost);
            mTextViewRelativeTimePost = view.findViewById(R.id.textViewRelativeTimePost);
            mCircleImagePost = view.findViewById(R.id.circleImageMypost);
            mImageViewDelete = view.findViewById(R.id.imageViewDelete);
            viewHolder = view;
        }
    }
}
