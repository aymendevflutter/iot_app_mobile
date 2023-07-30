package com.example.iot.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.iot.R;
import com.example.iot.ref.admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class SettingsFragment extends Fragment implements  View.OnClickListener{
    private static final int PICK_IMAGE_REQUEST = 123;
    SharedPreferences sharedPreferences;

   private String name,phone,url;
   private  ImageView edit1,edit2,edit3,edit4;
   private EditText nameVeiw ,phomeView , emailView,passwordView;
   private Button ButtonUpdate;
   private ImageView imageView;
   boolean mbooleanName;
   boolean mbooleanImage;

   private   Uri imageUri;
   private   String uid;
   private  String imageurl;
   private FirebaseFirestore db;
  private   FirebaseUser user;
  private TextView navHeaderName;
  private ImageView navHeaderImage;
  private boolean imagecheck = false;


    public SettingsFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        nameVeiw = view.findViewById(R.id.name_edittext);
        phomeView =view.findViewById(R.id.phone_edittext);
        imageView= view.findViewById(R.id.imageViewphoto);
        passwordView= view.findViewById(R.id.password_edittext);
        emailView =view.findViewById(R.id.email_edittext);

        edit1 =view.findViewById(R.id.edit1);
        edit2 =view.findViewById(R.id.edit2);
        edit3 =view.findViewById(R.id.edit3);
        edit4 =view.findViewById(R.id.edit4);
        edit1.setOnClickListener(this);
        edit2.setOnClickListener(this);
        edit3.setOnClickListener(this);
        edit4.setOnClickListener(this);
        ButtonUpdate = view.findViewById(R.id.update);
        ButtonUpdate.setOnClickListener(this);



        nameVeiw.setEnabled(false);
        emailView.setEnabled(false);
        passwordView.setEnabled(false);
        phomeView.setEnabled(false);



       try {
           uid = admin.firebaseAuth.getCurrentUser().getUid();
       }catch(Exception e) {

       }

        db = FirebaseFirestore.getInstance();
         user = admin.firebaseAuth.getCurrentUser();


        setvalFromShared();
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ButtonUpdate.setVisibility(View.VISIBLE);

                selectImage();
            }
        });



        return view;
    }
    void setvalFromShared(){


        url = sharedPreferences.getString("url","nourl");
        Log.d(TAG, "this url :" + url);
        Glide.with(getActivity())
                .load(url)
                .circleCrop()
                .into(imageView);

        nameVeiw.setText(sharedPreferences.getString("name","noname"));
        phomeView.setText(sharedPreferences.getString("phone", "nophone"));
        emailView.setText(sharedPreferences.getString("email","noemail"));


    }


    void SaveData(String key,String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();

    }

    public void uploadImage() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");


            UploadTask uploadTask = imagesRef.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();
                    updateData("url",imageUrl);

                  //  updateuser update = new updateuser(name,phone,url);
              //      updateNavigationHeader();

                } else {
                    Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }}
        public void selectImage() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        }
        @Override
       public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData();
                imagecheck =true;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void updatePassword(String newPassword){



            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Password updated successfully
                                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to update password
                                Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        void  updateEmail(String newEmail){


            user.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email updated successfully
                                Toast.makeText(getActivity(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to update email
                                Toast.makeText(getActivity(), "Failed to update email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        void  updateData(String key, String data){
            Map<String, Object> updates = new HashMap<>();

            updates.put(key, data);
            DocumentReference documentReference=  db.collection("users").document(uid);

            documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Field updated successfully
                        //    Toast.makeText(getActivity(), "Field updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update field
                         //   Toast.makeText(getActivity(), "Failed to update field", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        void Editflied(EditText editText){
            ButtonUpdate.setVisibility(View.VISIBLE);

        editText.setEnabled(true);

        }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit1:
                Editflied(nameVeiw);
                // Handle click for button1
                break;
            case R.id.edit2:
                Editflied(phomeView);

                // Handle click for button2
                break;
            // Add more cases for other views
            case R.id.edit3:
                Editflied(emailView);
                // Handle click for button2
                break;
            case R.id.edit4:
                Editflied(passwordView);
                // Handle click for button2
                break;
            case R.id.update:
                performAsyncOperation();
                // Handle click for button2
                break;

            default:
                break;
        }
    }
   boolean  checkData(EditText editText){
        if(editText.isEnabled() && !editText.getText().equals("")){
            return true;

        }
        return false;

    }
   void update(){
    //    for(int i =0 ; i<3 ; i++){

      //  }
       if(checkData(nameVeiw)){
           String data = nameVeiw.getText().toString();
           updateData("fullName",data );


       }
       if(checkData(phomeView)){
           String phone =phomeView.getText().toString();
           updateData("phone",phone );
         //  SaveToShared("phone",phone);


       }
       if(checkData(emailView)){
           String email =emailView.getText().toString();
           updateEmail( email);
           updateData("email",email);
         //  SaveToShared("email",email);

       }
       if(checkData(passwordView)){
           updatePassword( passwordView.getText().toString());

       }
       if(imagecheck){

           uploadImage();
        //   SaveToShared("url",url);


       }


    }
    void SaveToShared(String key , String value){
       SharedPreferences.Editor editor =    sharedPreferences.edit();
       editor.putString(key,value);
    }

    private void updateNavigationHeader() {
        if (getActivity() != null) {
            NavigationView navigationView = getActivity().findViewById(R.id.nvView);
            View headerView = navigationView.getHeaderView(0);
            navHeaderName = headerView.findViewById(R.id.myname);
            navHeaderImage = headerView.findViewById(R.id.myimage);


            String  names  = sharedPreferences.getString("name","noname");
            String  urls =sharedPreferences.getString("url","nourl");
            navHeaderName.setText(names);
              ImageView headimage = headerView.findViewById(R.id.myimage) ;
            Glide.with(getActivity())
                    .load(urls)
                    .circleCrop()
                    .into(headimage);
        }
    }

    private void performAsyncOperation() {
        // Create a CompletableFuture representing the asynchronous operation
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            // Simulate a long-running operation
            try {
                update();
                ClearPref();

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Return the result of the operation
            return "Async operation completed";
        });

        // Perform other tasks while waiting for the result
        // ...

        // Wait for the result of the asynchronous operation
        try {
            String result = completableFuture.get();

            // Handle the result (e.g., update UI)
         //   Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

            // Recreate the activity
            getActivity().recreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void ClearPref(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}