package com.maxrzhe.simplenotesapp.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.maxrzhe.simplenotesapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "PROF_LOG";
    private static final int TAKE_IMAGE_CODE = 1012;
    private CircleImageView circleImageView;
    private TextInputEditText textInputEditText;
    private Button updateButton;
    private ProgressBar profileProgressBar;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        circleImageView = findViewById(R.id.civ_profile_image);

        textInputEditText = findViewById(R.id.tiet_profileName);
        updateButton = findViewById(R.id.btn_profile_update);
        profileProgressBar = findViewById(R.id.pb_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                textInputEditText.setText(user.getDisplayName());
                textInputEditText.setSelection(user.getDisplayName().length());
            }
            if (user.getPhotoUrl() != null) {
                Picasso.get().load(user.getPhotoUrl()).into(circleImageView);
            }
        }
        profileProgressBar.setVisibility(View.GONE);

    }

    public void updateProfile(View view) {
        String name = textInputEditText.getText() != null &&
                !textInputEditText.getText().toString().isEmpty() ? textInputEditText.getText().toString() : null;
        if (name != null) {
            view.setEnabled(false);
            profileProgressBar.setVisibility(View.VISIBLE);
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            user.updateProfile(request)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                        profileProgressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        view.setEnabled(true);
                        profileProgressBar.setVisibility(View.GONE);
                    });
        }
    }

    public void storageDemo(View view) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference rootRef = firebaseStorage.getReference();

        //gs://simplenotesapp-43393.appspot.com/photo__.jpg
//        StorageReference photoRef = rootRef
//                .child("images")
//                .child("profileImages")
//                .child(user.getUid())
//                .child("photo__.jpg");
//
//        Bitmap photoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
//
//        ByteArrayOutputStream boas = new ByteArrayOutputStream();
//        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 20, boas);
//
//        photoRef.putBytes(boas.toByteArray())
//                .addOnSuccessListener(taskSnapshot -> {
//                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "storageDemo: Image uploaded successfully");
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "storageDemo: failed", e);
//                });

//        StorageReference photoRef = rootRef
//                .child("images")
//                .child("profileImages")
//                .child(user.getUid())
//                .child("photo__.jpg");

//        photoRef.getBytes(1024 * 1024)
//                .addOnSuccessListener(bytes -> {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    circleImageView.setImageBitmap(bitmap);
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "storageDemo: downloading failed", e);
//                });
//        photoRef.getDownloadUrl()
//                .addOnSuccessListener(uri -> {
//                    Picasso.get().load(uri).into(circleImageView);
//                });
    }

    public void changeProfileImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        circleImageView.setImageBitmap(bitmap);
                        uploadImageToDatabase(bitmap);
                    }
                } else {
                    Log.d(TAG, "onActivityResult: there was no image");
                }
            } else {
                Log.d(TAG, "onActivityResult: user denied making photo");
            }
        }
    }

    private void uploadImageToDatabase(Bitmap bitmap) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference rootRef = firebaseStorage.getReference();
        StorageReference userPhotoRef = rootRef
                .child("images")
                .child("profileImages")
                .child(user.getUid())
                .child(user.getUid() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);

        userPhotoRef.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "uploadImageToDatabase: user profile image uploaded successfully");
                    getDownLoadUrl(userPhotoRef);
                });
    }

    private void getDownLoadUrl(StorageReference userPhotoRef) {
        userPhotoRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "getDownLoadUrl: " + uri.toString());
                    setUserProfileUrl(uri);
                });
    }

    private void setUserProfileUrl(Uri uri) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Image updated", Toast.LENGTH_SHORT).show();
                });
    }
}