package com.navya.rtautoambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class DriverLoginActivity extends AppCompatActivity {
    private static final String TAG = "DriverLoginActivity";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private EditText motp,mphno;
    private Button msendotp,mverifyotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        mphno=(EditText)findViewById(R.id.editTextPhone);
        motp=(EditText)findViewById(R.id.otpdriver);
        msendotp=(Button)findViewById(R.id.driversendotp);
        mverifyotp=(Button)findViewById(R.id.driververifyotp);
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                mVerificationInProgress=false;
                Toast.makeText(DriverLoginActivity.this,"Verification Complete",Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(DriverLoginActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(DriverLoginActivity.this,"Invalid Phone Number",Toast.LENGTH_SHORT).show();
                }
                else if(e instanceof FirebaseTooManyRequestsException){}
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(DriverLoginActivity.this,"Verification code has been send on your number",Toast.LENGTH_SHORT).show();
                mVerificationId=s;
                mResendToken=forceResendingToken;
                mphno.setVisibility(View.GONE);
                msendotp.setVisibility(View.GONE);
                motp.setVisibility(View.VISIBLE);
                mverifyotp.setVisibility(View.VISIBLE);

            }
        };
        msendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(mphno.getText().toString(),60, TimeUnit.SECONDS,DriverLoginActivity.this,mCallbacks);
            }
        });
        mverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,motp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(DriverLoginActivity.this,DriverHome.class));
                            Toast.makeText(DriverLoginActivity.this,"Verification Done",Toast.LENGTH_SHORT).show();
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child("UID");
                            current_user_db.setValue(user_id);
                        }
                        else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(DriverLoginActivity.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}