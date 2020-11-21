package com.navya.soonapp;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragmentActivity extends Fragment  {
    View view;
    private EditText dsEnterName,dsEnterPhone,dsEnterEmergencyNo1,dsEmergencyNo2,dsVehicleNo;
    private Button dsUpdateButton;
    private FirebaseUser user;
    private DatabaseReference ref;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.activity_profile_fragment,container,false);

        user= FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();
        ref= FirebaseDatabase.getInstance().getReference();

         dsEnterName=(EditText)rootView.findViewById(R.id.dsEnterName);
         dsEnterPhone=(EditText)rootView.findViewById(R.id.dsEnterPhone);
        dsEnterEmergencyNo1=(EditText)rootView.findViewById(R.id.dsEmergencyNo1);
        dsEmergencyNo2=(EditText)rootView.findViewById(R.id.dsEmergencyNo2);
         dsVehicleNo=(EditText)rootView.findViewById(R.id.dsVehicleNo);
        ref.child("Users").child("Riders").child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue().toString();
                String phone = snapshot.child("Phoneno").getValue().toString();
                String emergencyno1 = snapshot.child("EmergencyContact1").getValue().toString();
                String emergencyno2 = snapshot.child("EmergencyContact2").getValue().toString();
                String vehicleno = snapshot.child("VehicleNo").getValue().toString();

                dsEnterName.setText(name);
                dsEnterPhone.setText(phone);
                dsEnterEmergencyNo1.setText(emergencyno1);
                dsEmergencyNo2.setText(emergencyno2);
                dsVehicleNo.setText(vehicleno);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dsUpdateButton=(Button)rootView.findViewById(R.id.dsUpdateButton);
        dsUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference editref=FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(userID);
                editref.child("Name").setValue(dsEnterName.getText().toString());
                editref.child("Phoneno").setValue(dsEnterPhone.getText().toString());
                editref.child("EmergencyContact1").setValue(dsEnterEmergencyNo1.getText().toString());
                editref.child("EmergencyContact2").setValue(dsEmergencyNo2.getText().toString());
                editref.child("VehicleNo").setValue(dsVehicleNo.getText().toString());
                Toast.makeText(getActivity(),"Updated Successfully",Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
}}