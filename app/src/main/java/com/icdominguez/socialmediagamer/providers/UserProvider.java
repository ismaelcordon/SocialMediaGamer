package com.icdominguez.socialmediagamer.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.icdominguez.socialmediagamer.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProvider {
    private CollectionReference mCollection;

    public UserProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("users");
    }

    public Task<DocumentSnapshot> getUser(String id) {
        return mCollection.document(id).get();
    }
    public DocumentReference getUserRealtime(String id) {
        return mCollection.document(id);
    }

    public Task<Void> createUser(User user) {
        return mCollection.document(user.getUserId()).set(user);
    }

    public Task<Void> updateUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("imageProfile",user.getImageProfile());
        map.put("imageCover",user.getImageCover());
        map.put("phone",user.getPhone());
        map.put("timestamp", new Date().getTime());
        return mCollection.document(user.getUserId()).update(map);
    }

    public Task<Void> updateUserOnline(String userId, boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());
        return mCollection.document(userId).update(map);
    }


}
