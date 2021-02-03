package com.icdominguez.socialmediagamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.models.Comment;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.squareup.picasso.Picasso;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;

    public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context context) {
        super(options);
        this.context = context;

        mUserProvider = new UserProvider();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String commentId = document.getId();
        String userId = document.getString("userId");

        holder.textViewComment.setText(comment.getComment());
        getUserInfo(userId, holder);
    }

    private void getUserInfo(String userId, final ViewHolder holder) {
        mUserProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if(imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.imageViewComment);
                            }
                        }
                    }
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewComment;
        ImageView imageViewComment;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewComment = view.findViewById(R.id.textViewComment);
            imageViewComment = view.findViewById(R.id.circleImageCover);
            viewHolder = view;
        }
    }
}
