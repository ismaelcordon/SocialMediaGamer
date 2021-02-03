package com.icdominguez.socialmediagamer.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.adapters.MessageAdapter;
import com.icdominguez.socialmediagamer.models.Chat;
import com.icdominguez.socialmediagamer.models.FCMBody;
import com.icdominguez.socialmediagamer.models.FCMResponse;
import com.icdominguez.socialmediagamer.models.Message;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.ChatsProvider;
import com.icdominguez.socialmediagamer.providers.MessageProvider;
import com.icdominguez.socialmediagamer.providers.TokenProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.icdominguez.socialmediagamer.retrofit.NotificationProvider;
import com.icdominguez.socialmediagamer.utils.RelativeTime;
import com.icdominguez.socialmediagamer.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraUserId1;
    String mExtraUserId2;
    String mExtraChatId;

    long mNotificationIdChat;

    CircleImageView mCircleImageViewProfile;
    EditText mEditTextMessage;
    ImageView mImageViewSendMessage;
    TextView mTextViewUsername;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;
    RecyclerView mRecyclerViewMessage;

    ChatsProvider mChatProvider;
    MessageProvider mMessageProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    NotificationProvider mNotificationProvider;

    MessageAdapter mMessageAdapter;

    LinearLayoutManager mLinearLayoutManager;
    TokenProvider mTokenProvider;

    View mActionBarView;
    ListenerRegistration mListener;

    String mMyUsername;
    String mUsernameChat;
    String mImageReceiver = "";
    String mImageSender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        instantiateProviders();
        findViews();
        events();

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        // Mostrar la pantalla en la parte de abajo
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(mLinearLayoutManager);

        getExtras();

        showCustomToolbar(R.layout.custom_chat_toolbar);
        getMyUserInfo();

        checkIfChatExist();
    }

    private void findViews() {
        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        mRecyclerViewMessage = findViewById(R.id.recyclerViewMessage);
    }

    private void events() {
        mImageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void getExtras() {
        mExtraUserId1 = getIntent().getStringExtra("userId");
        mExtraUserId2 = getIntent().getStringExtra("userId2");
        mExtraChatId = getIntent().getStringExtra("chatId");
    }

    private void instantiateProviders() {
        mChatProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setUserId1(mExtraUserId1);
        chat.setUserId2(mExtraUserId2);
        chat.setWriting(false);
        chat.setChatId(mExtraUserId1 + mExtraUserId2);
        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setNotificationId(n);
        mNotificationIdChat = n;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraUserId1);
        ids.add(mExtraUserId2);

        chat.setUsersIds(ids);
        chat.setTimestamp(new Date().getTime());
        mChatProvider.create(chat);
        mExtraChatId = chat.getChatId();
        getMessageChat();
    }

    private void checkIfChatExist(){
        mChatProvider.getChatByUser1AndUser2(mExtraUserId1, mExtraUserId2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if(size == 0) {
                    createChat();
                }
                else {
                    mExtraChatId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mNotificationIdChat = queryDocumentSnapshots.getDocuments().get(0).getLong("notificationId");
                    getMessageChat();
                    updateViewed();
                }
            }
        });
    }

    private void updateViewed() {
        String senderId = "";

        if(mAuthProvider.getUid().equals(mExtraUserId1)) {
            senderId = mExtraUserId2;
        } else {
            senderId = mExtraUserId1;
        }
        mMessageProvider.getMessageByChatAndSender(mExtraChatId, senderId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    mMessageProvider.updateViewed(document.getId(), true);
                }
            }
        });
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);

        mCircleImageViewProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUsername);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserInfo();

    }

    private void getUserInfo() {
        String userId = "";
        if(mAuthProvider.getUid().equals(mExtraUserId1)) {
            userId = mExtraUserId2;
        } else {
            userId = mExtraUserId1;
        }
        mListener = mUserProvider.getUserRealtime(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        mUsernameChat = documentSnapshot.getString("username");
                        mTextViewUsername.setText(mUsernameChat);
                    }
                    if(documentSnapshot.contains("online")) {
                        boolean online = documentSnapshot.getBoolean("online");
                        if(online){
                            mTextViewRelativeTime.setText("En línea");
                        } else if(documentSnapshot.contains("lastConnect")) {
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            mTextViewRelativeTime.setText(relativeTime);
                        }
                    }
                    if(documentSnapshot.contains("imageProfile")) {
                        mImageReceiver = documentSnapshot.getString("imageProfile");
                        if(mImageReceiver != null) {
                            if(!mImageReceiver.equals("")) {
                                Picasso.with(ChatActivity.this).load(mImageReceiver).into(mCircleImageViewProfile);
                            }
                        }
                    }
                }
            }
        });
    }

    private void sendMessage() {
        String textMessage = mEditTextMessage.getText().toString();
        if(!textMessage.isEmpty()) {
            final Message message = new Message();
            message.setChatId(mExtraChatId);
            if(mAuthProvider.getUid().equals(mExtraUserId1)) {
                message.setIdSender(mExtraUserId1);
                message.setIdReceiver(mExtraUserId2);
            } else {
                message.setIdSender(mExtraUserId2);
                message.setIdReceiver(mExtraUserId1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setChatId(mExtraChatId);
            message.setMessage(textMessage);
            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mEditTextMessage.setText("");
                        mMessageAdapter.notifyDataSetChanged();
                        getToken(message);
                        Toast.makeText(ChatActivity.this, "El mensaje se creó correctametne", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "El mensaje no se pudo crear", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getMessageChat() {
        Query query = mMessageProvider.getMessageByChat(mExtraChatId);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();
        mMessageAdapter = new MessageAdapter(options, ChatActivity.this);
        mRecyclerViewMessage.setAdapter(mMessageAdapter);
        mMessageAdapter.startListening();
        mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numberMessages = mMessageAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(lastMessagePosition == -1 || (positionStart >= (numberMessages -1) && lastMessagePosition == (positionStart - 1))) {
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void getToken(final Message message) {

        String userdId = "";
        if(mAuthProvider.getUid().equals(mExtraUserId1)) {
            userdId = mExtraUserId2;
        } else {
            userdId = mExtraUserId1;
        }

        mTokenProvider.getToken(userdId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        getLastThreeMessage(message, token);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLastThreeMessage(final Message message, final String token) {
        mMessageProvider.getLastThreeMessageByChatAndSender(mExtraChatId, mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Message> messagesArrayList = new ArrayList<>();

                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    if(d.exists()) {
                        Message message = d.toObject(Message.class);
                        messagesArrayList.add(message);
                    }
                }

                if(messagesArrayList.size() > 0) {
                    messagesArrayList.add(message);
                }

                Collections.reverse(messagesArrayList);

                Gson gson = new Gson();
                String messages = gson.toJson(messagesArrayList);
                sendNotification(token, messages, message);
            }
        });
    }

    private void sendNotification(final String token, String messages, Message message) {
        final Map<String, String> data = new HashMap<>();
        data.put("title", "NUEVO MENSAJE");
        data.put("body", message.getMessage());
        data.put("notificationId", String.valueOf(mNotificationIdChat));
        data.put("usernameSender", mMyUsername.toUpperCase());
        data.put("usernameReceiver", mUsernameChat.toUpperCase());
        data.put("messages", messages);
        data.put("senderId", message.getIdSender());
        data.put("receiverId", message.getIdReceiver());
        data.put("chatId", message.getChatId());

        if (mImageSender == null) {
            mImageSender = "IMAGEN_NO_VALIDA";
        }
        if (mImageReceiver == null) {
            mImageReceiver = "IMAGEN_NO_VALIDA";
        }

        data.put("imageSender", mImageSender);
        data.put("imageReceiver", mImageReceiver);

        String idSender = "";
        if (mAuthProvider.getUid().equals(mExtraUserId1)) {
            idSender = mExtraUserId2;
        }
        else {
            idSender = mExtraUserId1;
        }

        mMessageProvider.getLastMessageSender(mExtraChatId, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String lastMessage = "";
                if (size > 0) {
                    lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    data.put("lastMessage", lastMessage);
                }
                FCMBody body = new FCMBody(token, "high", "4500s", data);
                mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() == 1) {
                                //Toast.makeText(ChatActivity.this, "La notificacion se envio correcatemente", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ChatActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(ChatActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void getMyUserInfo() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        mMyUsername = documentSnapshot.getString("username");
                    }
                    if(documentSnapshot.contains("imageProfile")) {
                        mImageSender = documentSnapshot.getString("imageProfile");
                    }
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        if (mMessageAdapter != null) {
            mMessageAdapter.startListening();
        }
        ViewedMessageHelper.updateOnline(true, ChatActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null) {
            mListener.remove();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }
}