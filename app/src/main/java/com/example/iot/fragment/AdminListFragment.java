package com.example.iot.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot.R;
import com.example.iot.adapter.adpteruser;
import com.example.iot.models.UserInfo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class AdminListFragment extends Fragment implements adpteruser.OnItemClickListener {

    RecyclerView recyclerView;
    adpteruser adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myCollection ;
    //  = db.collection("event");
    UserInfo mymodal ;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_list, container, false);

        //  Log.d(TAG, "idmymodal =" + mymodal.getId(.to) );
        recyclerView = view.findViewById(R.id.subrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myCollection = db.collection("users");
        Query query = myCollection.orderBy("fullName", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UserInfo> options = new FirestoreRecyclerOptions.Builder<UserInfo>()

                .setQuery(query, UserInfo.class)
                .build();

        adapter = new adpteruser(options,this::onItemClick);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        return view;
    }

    @Override
    public void onItemClick(UserInfo model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("show data of user")
                .setPositiveButton("oximeter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mooveToFragment(new HomeFragment(),model.getId());

                    }
                })
                .setNegativeButton("pressure", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mooveToFragment(new PressureFragment(),model.getId());


                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();




    }

    void mooveToFragment(Fragment fragment,String path){


        Bundle args = new Bundle();
        args.putString("path",path);
        fragment.setArguments(args);

        // Replace the current fragment with the ItemDetailsFragment

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, fragment)
                .addToBackStack(null)
                .commit();
    }
}