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


public class PressureFragment extends Fragment {
    private Switch SwichOnOf;
    private TextView t1;
    private ValueEventListener valueEventListener;
    Boolean value =false;
    String uid;
    String path;


    public PressureFragment() {


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pressure, container, false);
        SwichOnOf =view.findViewById(R.id.switch2);
        t1 =view.findViewById(R.id.t3);



        try{
            path =getArguments().getString("path");
            if (path != null){
                uid = path ;

            }
        }
        catch (Exception e){

        }
        uid = admin.firebaseAuth.getCurrentUser().getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(admin.firebaseAuth.getCurrentUser().getUid()).child("pressure");
        database.child("activeData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve the boolean value from the snapshot

                try{
                    value = dataSnapshot.getValue(Boolean.class);
                    if(value == true){
                        t1.setVisibility(View.VISIBLE);
                    }
                    else{t1.setVisibility(View.INVISIBLE);}

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

                    database.child("activeData").setValue(true);

                } else {
                    t1.setVisibility(View.INVISIBLE);

                    database.child("activeData").setValue(false);
                }
            }
        });

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the double value from the dataSnapshot
                Double value = dataSnapshot.getValue(Double.class);

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
        database.child("Datapressure").addValueEventListener(valueEventListener);
        return view;
    }
}