package com.maxrzhe.simplenotesapp.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.maxrzhe.simplenotesapp.pojo.Note;

public class FirebaseRepository implements Repository<Note> {

    private static final String TAG = "FBR_LOG";
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionRef;


    public FirebaseRepository() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionRef = firebaseFirestore.collection("notes");
    }

    @Override
    public void create(Note note) {
        collectionRef.add(note)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "new note created");
                    } else {
                        Log.d(TAG, "creation failed -> ", task.getException());
                    }
                });
    }

    @Override
    public void read(Note note) {

    }

    @Override
    public void update(Note note) {

    }

    @Override
    public void delete(Note note) {

    }

    @Override
    public void readAll() {

    }

    @Override
    public void readAllInRealTime() {

    }
}
