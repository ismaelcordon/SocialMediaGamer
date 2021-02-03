package com.icdominguez.socialmediagamer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.models.Like;
import com.icdominguez.socialmediagamer.models.Post;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.LikeProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.icdominguez.socialmediagamer.ui.activities.PostDetailActivity;
import com.icdominguez.socialmediagamer.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    LikeProvider mLikeProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumberFilter;
    ListenerRegistration mListener;

    public PostAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;

        mUserProvider = new UserProvider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();
    }

    public PostAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView) {
        super(options);
        this.context = context;

        mUserProvider = new UserProvider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();

        mTextViewNumberFilter = textView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);

        final String postId = document.getId();

        if(mTextViewNumberFilter != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(toString().valueOf(numberFilter));
        }

        holder.textViewTitle.setText(post.getTitle().toUpperCase());
        holder.textViewDescription.setText(post.getDescription());

        if(document.contains("timestamp")) {
            long timestamp = document.getLong("timestamp");
            String relativeTime = RelativeTime.getTimeAgo(timestamp, context);
            holder.textViewPostTimestamp.setText(relativeTime);
        }

        if(post.getImage1() != null) {
            Picasso.with(context).load(post.getImage1()).into(holder.imageViewPost);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostDetailActivity.class);
                i.putExtra("id", postId);
                context.startActivity(i);
            }
        });

        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setUserId(mAuthProvider.getUid());
                like.setPostId(postId);
                like.setTimestamp(new Date().getTime());
                like(like, holder);
            }
        });

        getUserInfo(post.getUserId(), holder);
        getNumberLikesByPost(postId, holder);
        checkIfLikeExists(postId, mAuthProvider.getUid(), holder);
    }

    private void like(final Like like, final ViewHolder holder) {
        mLikeProvider.getLikeByPostAndUser(like.getPostId(),mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0) {
                    String likeId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_grey);
                    mLikeProvider.deleteLike(likeId);
                } else {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                    mLikeProvider.create(like);
                }
            }
        });
    }

    private void checkIfLikeExists(String postId, String userId, final ViewHolder holder) {
        mLikeProvider.getLikeByPostAndUser(postId, userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0) {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                } else {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_grey);
                }
            }
        });
    }

    private void getNumberLikesByPost(String postId, final ViewHolder holder) {
        mListener = mLikeProvider.getLikesByPost(postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int numberLikes = value.size();
                    holder.textViewLikes.setText(String.valueOf(numberLikes));
                }
            }
        });
    }

    private void getUserInfo(String userId, final ViewHolder holder) {
        mUserProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    holder.textViewUsername.setText("By: " + username.toUpperCase());
                }
            }
        });
    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewUsername;
        TextView textViewLikes;
        TextView textViewPostTimestamp;
        ImageView imageViewPost;
        ImageView imageViewLike;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewUsername = view.findViewById(R.id.textViewUsernamePostCard);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            textViewPostTimestamp = view.findViewById(R.id.textViewPostTimestamp);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            viewHolder = view;
        }
    }
}
