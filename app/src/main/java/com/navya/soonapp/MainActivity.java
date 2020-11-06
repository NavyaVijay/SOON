package com.navya.soonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userref = db.child("Users").child("Riders").child(user.getUid());
                    userref.addListenerForSingleValueEvent(new ValueEventListener() {
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


                }
                else {
                    Intent intent = new Intent(MainActivity.this, RiderLoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

        Intent intentservice = new Intent(this, ShakeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentservice);
        }
        Intent intents=new Intent(this,ShakeService.class);
        startService(intents);
        /*Intent intentlocservice = new Intent(this, RealtimeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentlocservice);
        }
        Intent intentlocs=new Intent(this,RealtimeService.class);
        startService(intentlocs);

        else{
            Toast.makeText(MainActivity.this,"Else User",Toast.LENGTH_SHORT).show();
        }*/

    }

}