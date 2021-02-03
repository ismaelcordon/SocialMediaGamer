package com.icdominguez.socialmediagamer.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.adapters.CommentAdapter;
import com.icdominguez.socialmediagamer.adapters.SliderAdapter;
import com.icdominguez.socialmediagamer.models.Comment;
import com.icdominguez.socialmediagamer.models.FCMBody;
import com.icdominguez.socialmediagamer.models.FCMResponse;
import com.icdominguez.socialmediagamer.models.SliderItem;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.CommentProvider;
import com.icdominguez.socialmediagamer.providers.LikeProvider;
import com.icdominguez.socialmediagamer.providers.PostProvider;
import com.icdominguez.socialmediagamer.providers.TokenProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.icdominguez.socialmediagamer.retrofit.NotificationProvider;
import com.icdominguez.socialmediagamer.utils.ViewedMessageHelper;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItem = new ArrayList<>();
    PostProvider mPostProvider;

    String mExtraPostId;
    String mUserId = "";

    Button mButtonShowProfile;
    TextView mTextViewUsername, mTextViewPhone, mTextViewVideogameTitle, mTextViewVideogameDescription, mTextViewVideogameCategory;
    ImageView mImageViewCategory;
    CircleImageView mCircleImagePhotoProfile;
    UserProvider mUserProvider;
    CommentProvider mCommentProvider;
    AuthProvider mAuthProvider;

    FloatingActionButton mFabComment;

    RecyclerView mRecyclerView;
    CommentAdapter mCommentAdapter;
    LikeProvider mLikeProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    Toolbar mToolbar;

    //ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView = findViewById(R.id.imageSlider);
        mButtonShowProfile = findViewById(R.id.buttonShowProfile);

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShowProfile();
            }
        });

        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewVideogameTitle = findViewById(R.id.textViewVideogameTitle);
        mTextViewVideogameDescription = findViewById(R.id.textViewVideogameDescription);
        mTextViewVideogameCategory = findViewById(R.id.textViewVideogameCategory);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFabComment = findViewById(R.id.fabComment);
        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogComment();
            }
        });

        mImageViewCategory = findViewById(R.id.imageViewCategory);
        mCircleImagePhotoProfile = findViewById(R.id.circleImageViewPhotoProfile);

        mRecyclerView = findViewById(R.id.recyclerViewComments);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mCommentProvider = new CommentProvider();
        mAuthProvider = new AuthProvider();
        mLikeProvider = new LikeProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mExtraPostId = getIntent().getStringExtra("id");


        getPost();
        //getNumberLikes();

    }

    /*private void getNumberLikes() {
        mListener = mLikeProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int numberLikes = value.size();
                    if(numberLikes == 1) {
                        mTextViewLikes.setText(numberLikes + " me gusta");
                    }
                    mTextViewLikes.setText(numberLikes + " me gustas");
                }
            }
        });
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        mCommentAdapter.stopListening();
    }

    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("¡COMENTARIO!");
        alert.setMessage("Ingrese tu comentario");

        final EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("Texto");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);

        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
          RelativeLayout.LayoutParams.MATCH_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(layoutParams);
        container.addView(editText);

        alert.setView(container);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString();
                if(!value.isEmpty()) {
                    createComment(value);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Debe ingresar el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.show();

    }

    private void createComment(final String value) {
        Comment currentComment = new Comment();
        currentComment.setComment(value);
        currentComment.setPostId(mExtraPostId);
        currentComment.setUserId(mAuthProvider.getUid());
        currentComment.setTimestamp(new Date().getTime());
        mCommentProvider.create(currentComment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "El comentario se creo correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "Error al crear el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(final String comment) {
        if(mUserId == null) {
            return;
        }

        mTokenProvider.getToken(mUserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO COMENTARIO");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body() != null) {
                                    if(response.body().getSuccess() == 1) {
                                        Toast.makeText(PostDetailActivity.this, "La notificación se envió", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PostDetailActivity.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(PostDetailActivity.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                else {
                    Toast.makeText(PostDetailActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToShowProfile() {
        if(!mUserId.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("userId", mUserId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "El id de usuario aún no se ha cargado",Toast.LENGTH_SHORT).show();
        }

    }

    private void instanceSlider() {
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItem);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        //mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }
    private void getPost() {
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("image1")) {
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem sliderItem = new SliderItem();
                        sliderItem.setImageUrl(image1);
                        mSliderItem.add(sliderItem);
                    }
                    if(documentSnapshot.contains("image2")) {
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem sliderItem = new SliderItem();
                        sliderItem.setImageUrl(image2);
                        mSliderItem.add(sliderItem);
                    }
                    if(documentSnapshot.contains("title")) {
                        String title = documentSnapshot.getString("title");
                        mTextViewVideogameTitle.setText(title.toUpperCase());
                    }
                    if(documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");
                        mTextViewVideogameDescription.setText(description);
                    }
                    if(documentSnapshot.contains("category")) {
                        String category = documentSnapshot.getString("category");
                        mTextViewVideogameCategory.setText(category);

                        if(category.equals("PS4")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_ps4);
                        } else if(category.equals("XBOX")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_xbox);
                        } else if(category.equals("PC")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_pc);
                        } else if(category.equals("NINTENDO")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_nintendo);
                        }
                    }
                    if(documentSnapshot.contains("userId")) {
                        mUserId = documentSnapshot.getString("userId");
                        getUserInfoById(mUserId);
                    }

                    /*if(documentSnapshot.contains("timestamp")) {
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                        mTextViewRelativeTime.setText(relativeTime);
                    }*/

                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfoById(String userId) {
        mUserProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("phone")) {
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if(documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if(imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleImagePhotoProfile);
                            }
                        }
                    }

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mCommentProvider.getCommentByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).build();
        mCommentAdapter = new CommentAdapter(options, PostDetailActivity.this);
        mRecyclerView.setAdapter(mCommentAdapter);
        mCommentAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, PostDetailActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mListener.remove();
    }
}