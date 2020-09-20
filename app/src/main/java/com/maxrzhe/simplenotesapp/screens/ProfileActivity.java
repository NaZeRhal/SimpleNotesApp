package com.maxrzhe.simplenotesapp.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.maxrzhe.simplenotesapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextInputEditText textInputEditText;
    private Button updateButton;
    private ProgressBar profileProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        circleImageView = findViewById(R.id.civ_profile_image);
        textInputEditText = findViewById(R.id.tiet_profileName);
        updateButton = findViewById(R.id.btn_profile_update);
        profileProgressBar = findViewById(R.id.phone_button);
    }

    public void updateProfile(View view) {

    }
}