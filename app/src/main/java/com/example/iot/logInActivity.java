package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot.ref.admin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class logInActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;
    private FirebaseAuth auth;
    TextView forgotPassword;
    private Spinner spinner;
    private TextView label;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

         Date currentTime = Calendar.getInstance().getTime();
         String expiryDateString = "21-05-2023"; // upcoming date
           DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

          try {
               Date expiryDate = formatter.parse(expiryDateString);
               if(System.currentTimeMillis() < expiryDate.getTime()){

                   setContentView(R.layout.activity_log_in);


            }
             }catch (Exception e){}










        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
        auth=FirebaseAuth.getInstance();


        // Create an ArrayAdapter to populate the Spinner with the choices
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.choices, android.R.layout.simple_spinner_item);

        // Specify the layout to use for the Spinner's dropdown list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter as the Spinner's adapter


        // Set the OnItemSelectedListener to listen for Spinner selection changes





        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = loginEmail.getText().toString().trim();
                String pass = loginPassword.getText().toString().trim();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email,pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(logInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        if(email.equals("admin@admin.mail")){
                                            admin.check = true;
                                        }else {
                                            admin.check =false;
                                        }

                                        startActivity(new Intent(logInActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(logInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        loginPassword.setError("Empty fields are not allowed");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Empty fields are not allowed");
                } else {
                    loginEmail.setError("Please enter correct email");
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logInActivity.this, RegisterActivity.class));




            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}