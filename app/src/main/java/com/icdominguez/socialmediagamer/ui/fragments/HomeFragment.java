package com.icdominguez.socialmediagamer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.TranslationDemoActivity;
import com.icdominguez.socialmediagamer.adapters.PostAdapter;
import com.icdominguez.socialmediagamer.models.Post;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.PostProvider;
import com.icdominguez.socialmediagamer.ui.activities.MainActivity;
import com.icdominguez.socialmediagamer.ui.activities.PostActivity;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{

    View mView;
    FloatingActionButton mFab;
    Toolbar mToolbar;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostAdapter mPostAdapter;
    PostAdapter mPostAdapterSearch;
    MaterialSearchBar mSearchBar;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mFab = mView.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPost();
                Intent i = new Intent(getContext(), TranslationDemoActivity.class);
                startActivity(i);
            }
        });

        mAuthProvider = new AuthProvider();

        mToolbar = mView.findViewById(R.id.toolbar);
        mRecyclerView = mView.findViewById(R.id.recyclerViewHome);

        mPostProvider = new PostProvider();
        mSearchBar = mView.findViewById(R.id.searchBar);
        mSearchBar.setOnSearchActionListener(this);

        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.itemLogout) {
                    logout();
                }
                return true;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
        if(mPostAdapterSearch != null) {
            mPostAdapterSearch.stopListening();
        }
    }

    private void getAllPost() {
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        mPostAdapter = new PostAdapter(options, getContext());
        mPostAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void searchByTitle(String title) {
        Query query = mPostProvider.getPostByTitle(title);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        mPostAdapterSearch = new PostAdapter(options, getContext());
        mPostAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostAdapterSearch);
        mPostAdapterSearch.startListening();
    }
    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled) {
            getAllPost();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByTitle(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void onStart() {
        super.onStart();
        getAllPost();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPostAdapter.getListener() != null) {
            mPostAdapter.getListener().remove();
        }
    }
}