package com.hello.writer.ui.post;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hello.writer.MainActivity;
import com.hello.writer.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class createPost extends Fragment {

    private CreatePostViewModel mViewModel;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference reference;
    private StorageReference ref;
    boolean uploadComplete = false, emptyImg = true;

    public static createPost newInstance() {
        return new createPost();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Random random = new Random();
        int num = random.nextInt(99999);
        String postId = String.valueOf(num);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = database.getReference("posts").child("userId").child(postId);
        ;
        ref = storage.getReference("posts").child("userId").child(postId);
        View v = inflater.inflate(R.layout.fragment_create_post, container, false);

        ImageView imgPicker = v.findViewById(R.id.ImagePicker);
        Button postSubmit = v.findViewById(R.id.postSubmit);

        ActivityResultLauncher<String> image;

        //img pick
        image = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
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
                                                            Toast.makeText(getContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
                                                            uploadComplete = true;
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "You don't select any image", Toast.LENGTH_SHORT).show();
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
            if (emptyImg) {
                Toast.makeText(getContext(), "You don't select any Image", Toast.LENGTH_SHORT).show();
            } else if (uploadComplete) {
                EditText title = v.findViewById(R.id.titlePost);
                EditText postBody = v.findViewById(R.id.postContent);
                String postTitle = title.getText().toString();
                String postContent = postBody.getText().toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                String currentDate = sdf.format(c.getTime());
                reference.child("title").setValue(postTitle);
                reference.child("body").setValue(postContent);
                reference.child("date").setValue(currentDate);
                reference.child("reaction").setValue(0);
                reference.child("id").setValue(postId).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getActivity().onBackPressed();
                    }
                });

            } else {
                Toast.makeText(getContext(), "Please wait, Image uploading...", Toast.LENGTH_SHORT).show();
            }
        });







        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}