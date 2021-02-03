package com.icdominguez.socialmediagamer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.adapters.MyPostsAdapter;
import com.icdominguez.socialmediagamer.models.Post;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.PostProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.icdominguez.socialmediagamer.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    LinearLayout mLinearLayotEditProfile;
    TextView mEditTextUsername, mEditTextEmail, mEditTextPhone, mEditTextPostNumber, mTextViewPostExists;

    ImageView mImageViewCover;
    CircleImageView mCircleImageViewProfile;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    String mExtraUserId;

    MyPostsAdapter mMyPostsAdapter;
    RecyclerView mRecyclerViewMyPosts;

    Toolbar mToolbar;
    FloatingActionButton mFabChat;

    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mLinearLayotEditProfile = findViewById(R.id.linearLayoutEditProfile);

        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextUsername = findViewById(R.id.editTextUsername);
        mEditTextPhone = findViewById(R.id.editTextPhone);
        mEditTextPostNumber = findViewById(R.id.editTextPostNumber);

        mImageViewCover = findViewById(R.id.circleImageViewCover);
        mCircleImageViewProfile = findViewById(R.id.circleImageViewProfile);

        mTextViewPostExists = findViewById(R.id.textViewPostExist);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mExtraUserId = getIntent().getStringExtra("userId");

        mRecyclerViewMyPosts =findViewById(R.id.recyclerViewMyPosts);

        // Comprobamos que el usuario que ve el perfil no es el mismo que está usando la app
        if(mAuthProvider.getUid().equals(mExtraUserId)) {
            mFabChat.setEnabled(false);
        }
        mFabChat = findViewById(R.id.fabChat);
        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerViewMyPosts.setLayoutManager(linearLayoutManager);

        getUser();
        getPostNumber();
        checkIfExistPost();
    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
        intent.putExtra("userId", mAuthProvider.getUid());
        intent.putExtra("userId2", mExtraUserId);
        startActivity(intent);
    }

    private void checkIfExistPost() {
        mListener = mPostProvider.getPostByUser(mExtraUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int numberPost = value.size();
                    if(numberPost > 0) {
                        mTextViewPostExists.setText("Publicaciones");
                    } else {
                        mTextViewPostExists.setText("Publicaciones");
                    }
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mExtraUserId);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        mMyPostsAdapter = new MyPostsAdapter(options, UserProfileActivity.this);
        mRecyclerViewMyPosts.setAdapter(mMyPostsAdapter);
        mMyPostsAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, UserProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, UserProfileActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMyPostsAdapter.stopListening();
    }

    private void getUser() {
        mUserProvider.getUser(mExtraUserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("email")) {
                        String email = documentSnapshot.getString("email");
                        mEditTextEmail.setText(email);
                    }
                    if(documentSnapshot.contains("phone")) {
                        String phone = documentSnapshot.getString("phone");
                        mEditTextPhone.setText(phone);
                    }
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        mEditTextUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null) {
                            Picasso.with(UserProfileActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                        }
                    }
                    if(documentSnapshot.contains("image_cover")) {
                        String imageCover = documentSnapshot.getString("image_cover");
                        if(imageCover != null) {
                            Picasso.with(UserProfileActivity.this).load(imageCover).into(mImageViewCover);
                        }
                    }

                }
            }
        });
    }

    private void getPostNumber() {
        mPostProvider.getPostByUser(mExtraUserId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                mEditTextPostNumber.setText(String.valueOf(numberPost));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListener.remove();
    }
}