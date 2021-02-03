package com.icdominguez.socialmediagamer.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.adapters.PostAdapter;
import com.icdominguez.socialmediagamer.models.Post;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.PostProvider;
import com.icdominguez.socialmediagamer.utils.ViewedMessageHelper;

public class FilterActivity extends AppCompatActivity {

    String mExtraCategory;
    TextView mTextViewNumberFilter;

    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    PostAdapter mPostAdapter;

    RecyclerView mRecyclerView;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        findViews();
        instantiateProviders();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(FilterActivity.this, 2));

        mExtraCategory = getIntent().getStringExtra("category");
        Toast.makeText(this, "La categoria seleccionada es : " + mExtraCategory, Toast.LENGTH_SHORT).show();
    }

    private void instantiateProviders() {
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }

    private void findViews() {
        mRecyclerView = findViewById(R.id.recyclerViewFilter);
        mToolbar = findViewById(R.id.toolbar);
        mTextViewNumberFilter = findViewById(R.id.textViewNumberFilter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        mPostAdapter = new PostAdapter(options, FilterActivity.this,  mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();

        ViewedMessageHelper.updateOnline(true, FilterActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();

        ViewedMessageHelper.updateOnline(true, FilterActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}