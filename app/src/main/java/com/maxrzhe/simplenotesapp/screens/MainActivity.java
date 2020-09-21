package com.maxrzhe.simplenotesapp.screens;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.maxrzhe.simplenotesapp.R;
import com.maxrzhe.simplenotesapp.adapters.NoteFirestoreRecyclerAdapter;
import com.maxrzhe.simplenotesapp.pojo.Note;

import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NoteFirestoreRecyclerAdapter.OnNoteRecyclerListener {

    private static final String TAG = "MAIN_LOG";
    private String currentUserId;

    private RecyclerView recyclerView;
    private NoteFirestoreRecyclerAdapter adapter;

    private FloatingActionButton fabEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rv_main_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(v ->
                showAlertDialog()
        );
    }

    private void showAlertDialog() {
        EditText editText = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Add Note")
                .setView(editText)
                .setPositiveButton("Add", (dialog, which) -> {
                    String text = editText.getText().toString();
                    if (!text.isEmpty()) {
                        addNoteToDatabase(editText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void addNoteToDatabase(String text) {
        Note note = new Note(currentUserId, text, new Timestamp(new Date()), false);
        FirebaseFirestore.getInstance().collection("notes").add(note)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "addNoteToDatabase: note was created successfully");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_profile:
                goToProfile();
                break;
            case R.id.menu_item_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        AuthUI.getInstance().signOut(this);
    }

    private void goToProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            goToLoginActivity();
        } else {
            currentUserId = currentUser.getUid();
            initRecyclerView(currentUserId);
        }
    }

    private void initRecyclerView(String userId) {
        Query query = FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("userId", userId)
                .orderBy("isCompleted", Query.Direction.ASCENDING)
                .orderBy("created", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new NoteFirestoreRecyclerAdapter(options, this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onCheckBoxChanged(boolean isChecked, DocumentSnapshot documentSnapshot) {
        documentSnapshot.getReference().update("isCompleted", isChecked)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onCheckBoxChanged: note was updated");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onCheckBoxChanged: failed", e);
                });
    }

    @Override
    public void onItemClickListener(DocumentSnapshot snapshot) {
        Note note = snapshot.toObject(Note.class);
        EditText editText = new EditText(this);
        if (note != null) {
            editText.setText(note.getText());
            editText.setSelection(note.getText().length());
        }
        new AlertDialog.Builder(this)
                .setTitle("Edit Note")
                .setView(editText)
                .setPositiveButton("Edit", (dialog, which) -> {
                    String text = editText.getText().toString();
                    if (!text.isEmpty() && note != null) {
                        note.setText(editText.getText().toString());
                        editNoteInDatabase(note, snapshot);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onSwipeToDelete(DocumentSnapshot snapshot) {
        DocumentReference documentReference = snapshot.getReference();
        Note note = snapshot.toObject(Note.class);
        documentReference.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onSwipeToDelete: deleted successfully");
                    Snackbar.make(recyclerView, "Note was deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> {
                                if (note != null) {
                                    documentReference.set(note);
                                }
                            })
                            .show();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onSwipeToDelete: deleting failed");
                });

    }

    private void editNoteInDatabase(Note note, DocumentSnapshot snapshot) {
        snapshot.getReference().set(note)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "editNoteInDatabase: note was edited successfully");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "editNoteInDatabase: editing failed -> ", e);
                });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                NoteFirestoreRecyclerAdapter.NoteViewHolder noteViewHolder = (NoteFirestoreRecyclerAdapter.NoteViewHolder) viewHolder;
                noteViewHolder.deleteItem();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c,
                                @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY,
                                int actionState,
                                boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}