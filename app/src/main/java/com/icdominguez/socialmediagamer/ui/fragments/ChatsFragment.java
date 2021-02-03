package com.icdominguez.socialmediagamer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.adapters.ChatAdapter;
import com.icdominguez.socialmediagamer.models.Chat;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.ChatsProvider;


public class ChatsFragment extends Fragment {

    ChatAdapter mChatAdapter;
    RecyclerView mRecyclerView;
    View mView;

    ChatsProvider mChatProvider;
    AuthProvider mAuthProvider;

    Toolbar mToolbar;

    public ChatsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chats, container, false);

        mRecyclerView = mView.findViewById(R.id.recyclerViewChats);
        mToolbar = mView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mChatProvider = new ChatsProvider();
        mAuthProvider = new AuthProvider();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();
        mChatAdapter = new ChatAdapter(options, getContext());
        mRecyclerView.setAdapter(mChatAdapter);
        mChatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChatAdapter.getListener() != null) {
            mChatAdapter.getListener().remove();
        }
        if(mChatAdapter.getListenerLastMessage() != null) {
            mChatAdapter.getListenerLastMessage().remove();
        }
    }
}