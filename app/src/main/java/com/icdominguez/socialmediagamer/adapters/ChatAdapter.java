package com.icdominguez.socialmediagamer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.icdominguez.socialmediagamer.models.Chat;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.ChatsProvider;
import com.icdominguez.socialmediagamer.providers.MessageProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.icdominguez.socialmediagamer.ui.activities.ChatActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatProvider;
    MessageProvider mMessageProvider;

    ListenerRegistration mListener, mListenerLastMessage;

    public ChatAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mChatProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Chat chat) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if(mAuthProvider.getUid().equals(chat.getUserId1())) {
            getUserInfo(chat.getUserId2(), holder);
        } else {
            getUserInfo(chat.getUserId1(), holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goChatActivity(chatId, chat.getUserId1(), chat.getUserId2());
            }
        });

        getLastMessage(chatId, holder.textViewLastMessage);

        String senderId = "";
        if(mAuthProvider.getUid().equals(chat.getUserId1())) {
            senderId = chat.getUserId2();
        } else {
            senderId = chat.getUserId1();
        }
        getNumberUnreadMessages(chatId, senderId, holder.textViewUnreadMessage, holder.frameLayoutUnreadMessages);
    }

    private void goChatActivity(String chatId, String userId, String userId2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("userId", userId);
        intent.putExtra("userId2", userId2);
        context.startActivity(intent);
    }

    private void getUserInfo(String userId, final ViewHolder holder) {
        mUserProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsernameChat.setText(username);
                    }
                    if(documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if(imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.circleImageChat);
                            }
                        }
                    }
                }
            }
        });
    }

    private void getLastMessage(String chatId, final TextView textViewLastMessage) {
        mListenerLastMessage = mMessageProvider.getLastMessage(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int size = value.size();
                    if(size > 0) {
                        String lastMessage = value.getDocuments().get(0).getString("message");
                        textViewLastMessage.setText(lastMessage);
                    }
                }
            }
        });
    }

    private void getNumberUnreadMessages(String chatId, String senderId, final TextView textViewUnreadMessage, final FrameLayout frameLayoutUnreadMessages) {
        mListener = mMessageProvider.getMessageByChatAndSender(chatId, senderId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int size = value.size();
                    if(size > 0) {
                        frameLayoutUnreadMessages.setVisibility(View.VISIBLE);
                        textViewUnreadMessage.setText((String.valueOf(size)));
                    } else {
                        frameLayoutUnreadMessages.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    public ListenerRegistration getListener() {
        return mListener;
    }

    public ListenerRegistration getListenerLastMessage() {
        return mListenerLastMessage;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsernameChat;
        TextView textViewLastMessage;
        TextView textViewUnreadMessage;
        CircleImageView circleImageChat;
        FrameLayout frameLayoutUnreadMessages;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewUsernameChat = view.findViewById(R.id.textViewUsernameChat);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessageChat);
            textViewUnreadMessage = view.findViewById(R.id.textViewUnreadMessages);
            circleImageChat = view.findViewById(R.id.circleImageChat);
            frameLayoutUnreadMessages = view.findViewById(R.id.frameLayoutUnreadMessages);
            viewHolder = view;
        }
    }
}
