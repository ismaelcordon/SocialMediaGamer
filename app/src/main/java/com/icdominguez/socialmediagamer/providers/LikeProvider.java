package com.icdominguez.socialmediagamer.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.models.Like;

public class LikeProvider {

    CollectionReference mCollection;

    public LikeProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("likes");
    }

    public Task<Void> create(Like like) {
        DocumentReference document = mCollection.document();
        String likeId = document.getId();
        like.setLikeId(likeId);
        return mCollection.document().set(like);
    }

    public Query getLikeByPostAndUser(String postId, String userId) {
        return mCollection.whereEqualTo("postId", postId).whereEqualTo("userId", userId);
    }
    public Query getLikesByPost(String postId) {
        return mCollection.whereEqualTo("postId", postId);
    }

    public Task<Void> deleteLike(String likeId) {
        return mCollection.document(likeId).delete();
    }
}
