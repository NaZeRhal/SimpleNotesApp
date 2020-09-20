package com.maxrzhe.simplenotesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.protobuf.Field;
import com.maxrzhe.simplenotesapp.pojo.Note;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamplesActivity extends AppCompatActivity {

    private static final String TAG = "EXAM_LOG";
    private Button createButton;
    private Button readButton;
    private Button deleteButton;
    private Button updateButton;
    private Button allButton;
    private Button allRealtimeButton;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examples);

        createButton = findViewById(R.id.btn_create);
        readButton = findViewById(R.id.btn_read);
        updateButton = findViewById(R.id.btn_update);
        deleteButton = findViewById(R.id.btn_delete);
        allButton = findViewById(R.id.btn_all_docs);
        allRealtimeButton = findViewById(R.id.btn_all_realtime);

        firestore = FirebaseFirestore.getInstance();
    }

    public void createDoc(View view) {

        Note note = new Note(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Buy new shoes", new Timestamp(new Date()), false);

        firestore.collection("notes").add(note)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "createDoc: task was successful");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "createDoc: task failed");
                });
    }

    public void readDoc(View view) {
        firestore.collection("notes").document("9jR31uxr29uVmJ8Hdu1K").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        if (documentSnapshot.getData() != null) {
                            Note note = documentSnapshot.toObject(Note.class);
                            Log.d(TAG, "readDoc: note -> " + note);
                        }
                    } else {
                        Log.d(TAG, "readDoc: document doesn't exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "readDoc: error while reading -> ", e);
                });
    }

    public void updateDoc(View view) {
        //update
//        firestore.collection("notes").document("iuZkVnXf0MWCLjU5WVhq")
//                .update("text", "Cook tasty pie")
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "updateDoc: updated successfully");
//                })
//                .addOnFailureListener(e -> {
//                    Log.d(TAG, "updateDoc: error -> ", e );
//                });

        //merge

        Map<String, Object> map = new HashMap<>();
        map.put("text", "Cook something else if you want");
        map.put("isCompleted", false);
        firestore.collection("notes").document("iuZkVnXf0MWCLjU5WVhq")
                .set(map, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "updateDoc: updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "updateDoc: error -> ", e);
                });
    }

    public void deleteDoc(View view) {
//        firestore.collection("notes").document("eDpHrdDbR2rCMrpJ4GIL")
//                .delete()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Log.d(TAG, "deleteDoc: deleted successfully");
//                    } else {
//                        Log.d(TAG, "deleteDoc: deleting failed");
//                    }
//                });
        firestore.collection("notes").whereEqualTo("isCompleted", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {

                        WriteBatch writeBatch = firestore.batch();

                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            writeBatch.delete(document.getReference());
                        }
                        writeBatch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "deleteDoc: deleted successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "deleteDoc: deleting failed");
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "deleteDoc: deleting failed");
                });
    }

    public void readAllDocs(View view) {
        firestore.collection("notes")
                .orderBy("created", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Note> notes = queryDocumentSnapshots.toObjects(Note.class);
                        for (Note note : notes) {
                            Log.d(TAG, "readAllDocs: note -> " + note);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "readAllDocs: error -> ", e);
                });
    }

    public void readAllRealTime(View view) {
        firestore.collection("notes")
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.d(TAG, "readAllRealTime: error while reading -> ", error);
                        return;
                    }
                    if (documentSnapshot != null && !documentSnapshot.isEmpty()) {
                        List<DocumentChange> documentChanges = documentSnapshot.getDocumentChanges();
                        Log.d(TAG, "------------------------");
                        for (DocumentChange documentChange : documentChanges) {
                            Note note = documentChange.getDocument().toObject(Note.class);
                            Log.d(TAG, "readAllRealTime: note -> " + note);
                        }
                    } else {
                        Log.d(TAG, "readDoc: query snapshot was null or empty ");
                    }
                });
    }
}