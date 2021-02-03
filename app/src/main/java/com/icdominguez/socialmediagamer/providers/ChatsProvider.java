package com.icdominguez.socialmediagamer.providers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.models.Chat;

import java.util.ArrayList;

public class ChatsProvider {
    CollectionReference mCollection;
    public ChatsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("chats");
    }

    public void create(Chat chat) {
        mCollection.document(chat.getUserId1() + chat.getUserId2()).set(chat);
    }

    public Query getAll(String userId) {
        return mCollection.whereArrayContains("usersIds", userId);
    }

    public Query getChatByUser1AndUser2(String userId, String userId2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(userId + userId2);
        ids.add(userId2 + userId);
        return mCollection.whereIn("chatId", ids);
    }
}
