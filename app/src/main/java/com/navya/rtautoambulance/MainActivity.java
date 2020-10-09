package com.navya.rtautoambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(user!=null){
            Intent intent=new Intent(this,ShakeService.class);
            startService(intent);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("Users").child("Riders").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Intent intent = new Intent(MainActivity.this, RiderHomeActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            db.child("Users").child("Drivers").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Intent intent = new Intent(MainActivity.this, DriverHome.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });}


        else {
            Intent intent = new Intent(MainActivity.this, LogAsActivity.class);
            startActivity(intent);
            finish();
            return;
        }
}}