package com.maxrzhe.simplenotesapp.screens.mainscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.maxrzhe.simplenotesapp.adapters.NoteFirestoreRecyclerAdapter;
import com.maxrzhe.simplenotesapp.screens.LoginActivity;
import com.maxrzhe.simplenotesapp.screens.ProfileActivity;
import com.maxrzhe.simplenotesapp.R;
import com.maxrzhe.simplenotesapp.pojo.Note;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "MAIN_LOG";
    private String currentUserId;

    private RecyclerView recyclerView;
    private NoteFirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rv_main_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabEdit = findViewById(R.id.fab_edit);
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
                    Log.d(TAG, "showAlertDialog: " + editText.getText());
                    addNoteToDatabase(editText.getText().toString());
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
                .whereEqualTo("userId", userId);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        adapter = new NoteFirestoreRecyclerAdapter(options);
        recyclerView.setAdapter(adapter);

        adapter.startListening();
    }
}