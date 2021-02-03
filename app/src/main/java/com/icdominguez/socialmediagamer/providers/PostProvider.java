package com.icdominguez.socialmediagamer.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.icdominguez.socialmediagamer.models.Post;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("posts");
    }

    public Task<Void> save(Post post) {
        DocumentReference document = mCollection.document();
        String postId = document.getId();
        post.setPostId(postId);
        return mCollection.document().set(post);
    }

    public Query getAll() {
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostByCategoryAndTimestamp(String category) {
        return mCollection.whereEqualTo("category", category).orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostByTitle(String title) {
        return mCollection.orderBy("title").startAt(title).endAt(title + '\uf8ff');
    }

    public Query getPostByUser(String id) {
        return mCollection.whereEqualTo("userId", id);
    }

    public Task<DocumentSnapshot> getPostById(String id) {
        return mCollection.document(id).get();
    }

    public Task<Void> deletePost(String id) {
        return mCollection.document(id).delete();
    }
}
