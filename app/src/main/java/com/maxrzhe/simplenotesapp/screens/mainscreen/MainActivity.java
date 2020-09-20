package com.maxrzhe.simplenotesapp.screens.mainscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxrzhe.simplenotesapp.screens.LoginActivity;
import com.maxrzhe.simplenotesapp.screens.ProfileActivity;
import com.maxrzhe.simplenotesapp.R;
import com.maxrzhe.simplenotesapp.pojo.Note;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "MAIN_LOG";
    private RecyclerView recyclerView;
    private String currentUserId;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rv_main_notes);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        FloatingActionButton fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(v ->
//                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//                startActivity(new Intent(this, ExamplesActivity.class))
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
        mainViewModel.createNote(note);
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
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            goToLoginActivity();
//            return;
        } else {
            currentUserId = currentUser.getUid();
        }

//        firebaseAuth.getCurrentUser().getIdToken(true)
//                .addOnSuccessListener(getTokenResult -> {
//                    Log.d(TAG, "onCreate: token " + getTokenResult.getToken());
//                });
    }
}