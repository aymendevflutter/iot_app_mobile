package com.example.iot.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.iot.R;
import com.example.iot.ref.admin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {
    private Switch SwichOnOf;
    private TextView t1,t2;
    private ValueEventListener valueEventListener;
    String  path  ;
    String uid;




    public HomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SwichOnOf =view.findViewById(R.id.switch1);
        t1 =view.findViewById(R.id.t1);
        t2 = view.findViewById(R.id.t2);
        uid = admin.firebaseAuth.getCurrentUser().getUid();

        try{
            path =getArguments().getString("path");
        if (path != null){
            uid = path ;

        }
        }
        catch (Exception e){

        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(uid);


        database.child("ActiveOxi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve the boolean value from the snapshot
                Boolean value= false;
                try{
                    value = dataSnapshot.getValue(Boolean.class);
                    if(value == true){
                        t1.setVisibility(View.VISIBLE);
                        t2.setVisibility(View.VISIBLE);
                    }
                    else{t1.setVisibility(View.INVISIBLE);
                        t2.setVisibility(View.INVISIBLE);}
                } catch (Exception e){

                }


                // Set the switch based on the retrieved boolean value

                if (value != null) {
                    SwichOnOf.setChecked(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
        SwichOnOf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    t1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    database.child("ActiveOxi").setValue(true);

                } else {
                    t1.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                    database.child("ActiveOxi").setValue(false);
                }
            }
        });
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the double value from the dataSnapshot
                Double value =0.0;
                try{ value = dataSnapshot.getValue(Double.class);}
                catch (Exception e){}


                if (value != null) {
                    // Set the double value to the TextView
                    t1.setText(String.valueOf(value));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        };
        database.child("dataoximeter").addValueEventListener(valueEventListener);




        return  view;
    }







}