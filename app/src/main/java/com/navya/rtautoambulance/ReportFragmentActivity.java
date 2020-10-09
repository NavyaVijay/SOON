package com.navya.rtautoambulance;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



public class ReportFragmentActivity extends Fragment {
   /* View view;
    private static final String TAG="ReportFragmentActivity";
    DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_report_fragment,container,false);
        Query query=rootRef.child("Riders").orderByChild("Phoneno").equalTo("+919400653559");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String key=ds.getKey();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG,error.getMessage());
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
        return view;
    }*/
}