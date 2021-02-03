package com.icdominguez.socialmediagamer.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.models.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageProvider {

    CollectionReference mCollection;

    public MessageProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("messages");
    }
    public Task<Void> create(Message message) {
        DocumentReference document = mCollection.document();
        message.setMessageId(document.getId());
        return document.set(message);
    }

    public Query getMessageByChat(String chatId) {
        return mCollection.whereEqualTo("chatId", chatId).orderBy("timestamp", Query.Direction.ASCENDING);
    }

    public Query getMessageByChatAndSender(String chatId, String senderId) {
        return mCollection.whereEqualTo("chatId", chatId).whereEqualTo("idSender", senderId).whereEqualTo("viewed", false);
    }

    public Query getLastThreeMessageByChatAndSender(String chatId, String senderId) {
        return mCollection
                .whereEqualTo("chatId", chatId)
                .whereEqualTo("idSender", senderId)
                .whereEqualTo("viewed", false).orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
    }

    public Query getLastMessage(String chatId) {
        return mCollection.whereEqualTo("chatId", chatId).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    public Query getLastMessageSender(String chatId, String senderId) {
        return mCollection.whereEqualTo("chatId", chatId).whereEqualTo("idSender",senderId).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    public Task<Void> updateViewed(String documentId, boolean state) {
        Map<String, Object> map = new HashMap<>();
        map.put("viewed", state);
        return mCollection.document(documentId).update(map);
    }
}
