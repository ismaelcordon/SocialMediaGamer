package com.icdominguez.socialmediagamer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.icdominguez.socialmediagamer.ui.activities.EditProfileActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    LinearLayout mLinearLayotEditProfile;
    View mView;
    TextView mEditTextUsername, mEditTextEmail, mEditTextPhone, mEditTextPostNumber, mPostExists;

    ImageView mImageViewCover;
    CircleImageView mCircleImageViewProfile;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    MyPostsAdapter mMyPostsAdapter;

    RecyclerView mRecyclerViewMyPosts;
    ListenerRegistration mListener;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        mLinearLayotEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);
        mLinearLayotEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mEditTextEmail = mView.findViewById(R.id.editTextEmail);
        mEditTextUsername = mView.findViewById(R.id.editTextUsername);
        mEditTextPhone = mView.findViewById(R.id.editTextPhone);
        mEditTextPostNumber = mView.findViewById(R.id.editTextPostNumber);
        mPostExists = mView.findViewById(R.id.textViewPostExist);

        mImageViewCover = mView.findViewById(R.id.circleImageViewCover);
        mCircleImageViewProfile = mView.findViewById(R.id.circleImageViewProfile);

        mRecyclerViewMyPosts = mView.findViewById(R.id.recyclerViewMyPosts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewMyPosts.setLayoutManager(linearLayoutManager);

        getUser();
        getPostNumber();
        checkIfExistPost();

        return mView;
    }

    private void checkIfExistPost() {
        mListener = mPostProvider.getPostByUser(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int numberPost = value.size();
                    if(numberPost > 0) {
                        mPostExists.setText("Publicaciones");
                    } else {
                        mPostExists.setText("Publicaciones");
                    }
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        mMyPostsAdapter = new MyPostsAdapter(options, getContext());
        mRecyclerViewMyPosts.setAdapter(mMyPostsAdapter);
        mMyPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMyPostsAdapter.stopListening();
    }

    private void goToEditProfile() {
        Intent i = new Intent(getContext(), EditProfileActivity.class);
        startActivity(i);
    }

    private void getUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                    if(documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if(imageProfile != null) {
                            Picasso.with(getContext()).load(imageProfile).into(mCircleImageViewProfile);
                        }
                    }
                    if(documentSnapshot.contains("imageCover")) {
                        String imageCover = documentSnapshot.getString("imageCover");
                        if(imageCover != null) {
                            Picasso.with(getContext()).load(imageCover).into(mImageViewCover);
                        }
                    }

                }
            }
        });
    }

    private void getPostNumber() {
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                mEditTextPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener.remove();
    }
}