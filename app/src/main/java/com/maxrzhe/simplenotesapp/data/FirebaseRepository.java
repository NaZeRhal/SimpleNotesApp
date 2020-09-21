package com.maxrzhe.simplenotesapp.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.maxrzhe.simplenotesapp.pojo.Note;

import java.util.List;

public class FirebaseRepository implements Repository<Note> {

    private static final String TAG = "FBR_LOG";
    private CollectionReference collectionRef;


    public FirebaseRepository() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionRef = firebaseFirestore.collection("notes");
    }

    @Override
    public LiveData<Note> create(Note note) {
        MutableLiveData<Note> noteMutableLiveData = new MutableLiveData<>();
        collectionRef.add(note)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "new note created");
                        noteMutableLiveData.setValue(note);
                    } else {
                        Log.d(TAG, "creation failed -> ", task.getException());
                    }
                });
        return noteMutableLiveData;
    }

    @Override
    public LiveData<Note> read(Note note) {
        return null;
    }

    @Override
    public void update(Note note) {

    }

    @Override
    public void delete(Note note) {

    }

    @Override
    public LiveData<List<Note>> readAll() {
        MutableLiveData<List<Note>> noteListMutableLiveData = new MutableLiveData<>();
        collectionRef.orderBy("created", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Note> notes = queryDocumentSnapshots.toObjects(Note.class);
                        noteListMutableLiveData.setValue(notes);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "readAllDocs: error -> ", e);
                });
        return noteListMutableLiveData;
    }

    @Override
    public LiveData<List<Note>> readAllInRealTime(String currentUserId) {
        MutableLiveData<List<Note>> noteListMutableLiveData = new MutableLiveData<>();
        collectionRef.whereEqualTo("userId", currentUserId).addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.d(TAG, "readAllRealTime: error while reading -> ", error);
                return;
            }
            if (documentSnapshot != null && !documentSnapshot.isEmpty()) {
                List<Note> notes = documentSnapshot.toObjects(Note.class);
                noteListMutableLiveData.setValue(notes);
//                List<DocumentChange> documentChanges = documentSnapshot.getDocumentChanges();
//                Log.d(TAG, "------------------------");
//                for (DocumentChange documentChange : documentChanges) {
//                    Note note = documentChange.getDocument().toObject(Note.class);
//                    Log.d(TAG, "readAllRealTime: note -> " + note);
//                }
            } else {
                Log.d(TAG, "readDoc: query snapshot was null or empty ");
            }
        });
        return noteListMutableLiveData;
    }
}
