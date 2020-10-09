package com.navya.rtautoambulance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RiderProfileActivity extends AppCompatActivity {
    private EditText name,emcontact1,emcontact2,vehno;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_profile);
        name=(EditText)findViewById(R.id.name);
        emcontact1=(EditText)findViewById(R.id.emcontact1);
        emcontact2=(EditText)findViewById(R.id.emcontact2);
        vehno=(EditText)findViewById(R.id.vehno);
        save=(Button)findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String user_id=user.getUid();
                DatabaseReference current_user_name = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id).child("Name");
                current_user_name.setValue(name.getText().toString());
                DatabaseReference current_user_emcontact1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id).child("EmergencyContact1");
                current_user_emcontact1.setValue(emcontact1.getText().toString());
                DatabaseReference current_user_emcontact2 = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id).child("EmergencyContact2");
                current_user_emcontact2.setValue(emcontact2.getText().toString());
                DatabaseReference current_user_vehno = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id).child("VehicleNo");
                current_user_vehno.setValue(vehno.getText().toString());
                startActivity(new Intent(RiderProfileActivity.this, RiderHomeActivity.class));
                finish();
                return;
            }
        });
    }
}