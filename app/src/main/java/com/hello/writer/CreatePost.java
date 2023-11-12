package com.hello.writer;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hello.writer.ui.post.CreatePostViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreatePost extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference reference;
    private StorageReference ref;
    private Context context = CreatePost.this;
    boolean uploadComplete = false, emptyImg = true;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Random random = new Random();
        int post = random.nextInt(999999);
        postId = String.valueOf(post);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = database.getReference("posts").child(postId);
        ref = storage.getReference("posts").child(postId);
        ImageView imgPicker = findViewById(R.id.ImagePicker);
        Button postSubmit = findViewById(R.id.postSubmit);

        ActivityResultLauncher<String> image;

        //img pick
        image = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    imgPicker.setImageURI(result);
                    emptyImg = false;
                    ref.putFile(result)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            reference.child("image").setValue(uri.toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(context, "Image Upload Successful", Toast.LENGTH_SHORT).show();
                                                            DatabaseReference userRef = database.getReference("users")
                                                                    .child("userId")
                                                                    .child("postId");
                                                            userRef.child("post"+postId).setValue(postId);
                                                            uploadComplete = true;
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                }else {
                    Toast.makeText(context, "You don't select any image", Toast.LENGTH_SHORT).show();
                    imgPicker.setImageResource(R.drawable.upload_image);
                    uploadComplete = false;
                }

            }
        });
        imgPicker.setOnClickListener(view -> {
            image.launch("image/*");
        });
        //end image pick

        postSubmit.setOnClickListener(view -> {
            if (emptyImg){
                Toast.makeText(context, "You don't select any Image", Toast.LENGTH_SHORT).show();
            }else if (uploadComplete){
                EditText title = findViewById(R.id.titlePost);
                EditText postBody = findViewById(R.id.postContent);
                String postTitle = title.getText().toString();
                String postContent = postBody.getText().toString();

                //check
                if (TextUtils.isEmpty(postTitle)){
                    Toast.makeText(context, "Set a post title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(postContent)){
                    Toast.makeText(context, "Write Your post", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String currentDate = sdf.format(c.getTime());
                reference.child("title").setValue(postTitle);
                reference.child("body").setValue(postContent);
                reference.child("date").setValue(currentDate);
                reference.child("reaction").setValue("0");
                reference.child("timestamp").setValue(ServerValue.TIMESTAMP);
                reference.child("status").setValue("approved");
                reference.child("id").setValue(postId);
                reference.child("userId").setValue("user").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CreatePost.super.onBackPressed();
                    }
                });

            }else {
                Toast.makeText(context, "Please wait, Image uploading...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}