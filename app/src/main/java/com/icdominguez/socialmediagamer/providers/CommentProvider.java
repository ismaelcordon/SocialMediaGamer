package com.icdominguez.socialmediagamer.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.models.Comment;

public class CommentProvider {

    CollectionReference mCollection;

    public CommentProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("comments");
    }

    public Task<Void> create(Comment comment) {
        DocumentReference document = mCollection.document();
        String commentId = document.getId();
        comment.setCommentId(commentId);
        return mCollection.document().set(comment);
    }

    public Query getCommentByPost(String postId) {
        return mCollection.whereEqualTo("postId", postId);
    }
}
