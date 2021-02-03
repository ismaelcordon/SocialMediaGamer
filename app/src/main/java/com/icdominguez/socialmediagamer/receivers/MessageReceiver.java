package com.icdominguez.socialmediagamer.receivers;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.icdominguez.socialmediagamer.models.FCMBody;
import com.icdominguez.socialmediagamer.models.FCMResponse;
import com.icdominguez.socialmediagamer.models.Message;
import com.icdominguez.socialmediagamer.providers.MessageProvider;
import com.icdominguez.socialmediagamer.providers.TokenProvider;
import com.icdominguez.socialmediagamer.retrofit.NotificationProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icdominguez.socialmediagamer.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class MessageReceiver extends BroadcastReceiver {

    String mExtraSenderId;
    String mExtraReceiverId;
    String mExtraChatId;
    String mExtraUsernameSender;
    String mExtraUsernameReceiver;
    String mExtraImageSender;
    String mExtraImageReceiver;
    int mExtraNotificationId;

    TokenProvider mTokenProvider;
    NotificationProvider mNotificationProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        mExtraSenderId = intent.getExtras().getString("senderId");
        mExtraReceiverId = intent.getExtras().getString("receiverId");
        mExtraChatId = intent.getExtras().getString("chatId");
        mExtraNotificationId = intent.getExtras().getInt("notificationId");
        mExtraImageSender = intent.getExtras().getString("imageSender");
        mExtraImageReceiver = intent.getExtras().getString("imageReceiver");
        mExtraUsernameSender = intent.getExtras().getString("usernameSender");
        mExtraUsernameReceiver = intent.getExtras().getString("usernameReceiver");

        mTokenProvider = new TokenProvider();
        mNotificationProvider = new NotificationProvider();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mExtraNotificationId);

        String message = getMessageText(intent).toString();

        sendMessage(message);
    }

    private void sendMessage(String messageText) {
        final Message message = new Message();
        message.setChatId(mExtraChatId);
        message.setIdSender(mExtraSenderId);
        message.setIdReceiver(mExtraReceiverId);
        message.setTimestamp(new Date().getTime());
        message.setViewed(false);
        message.setChatId(mExtraChatId);
        message.setMessage(messageText);

        final MessageProvider messageProvider = new MessageProvider();

        messageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    getToken(message);
                }
            }
        });
    }

    private void getToken(final Message message) {
        mTokenProvider.getToken(mExtraSenderId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Gson gson = new Gson();

                        ArrayList<Message> messagesArray = new ArrayList<>();
                        messagesArray.add(message);

                        String messages = gson.toJson(messagesArray);
                        sendNotification(token, messages, message);
                    }
                }
            }
        });
    }

    private void sendNotification(final String token, String messages, Message message) {
        final Map<String, String> data = new HashMap<>();
        data.put("title", "NUEVO MENSAJE");
        data.put("body", message.getMessage());
        data.put("notificationId", String.valueOf(mExtraNotificationId));
        data.put("usernameSender", mExtraUsernameReceiver);
        data.put("usernameReceiver", mExtraUsernameReceiver);
        data.put("imageSender", mExtraImageSender);
        data.put("imageReceiver", mExtraImageReceiver);
        data.put("messages", messages);
        data.put("senderId", message.getIdSender());
        data.put("receiverId", message.getIdReceiver());
        data.put("chatId", message.getChatId());

        FCMBody body = new FCMBody(token, "high", "4500s", data);
        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) { }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d("ERROR", "El error fue: " + t.getMessage());
            }
        });
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if(remoteInput != null) {
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }
        return null;
    }
}
