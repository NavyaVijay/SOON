package com.navya.soonapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;


public class ReportFragmentActivity extends Fragment implements View.OnClickListener {
    View view;
    EditText reportVehicle;
    String  es1, es2;
    String key;
    TextView e1, e2;
    Button rButton;
    TextView textVehicle;
    ImageButton imgbtn;
    Bitmap bitmap;
    private static final String TAG = "ReportFragmentActivity";
    int TAKE_IMAGE_CODE=10001;
    public static final String VEHICLE_NO="com.navya.soonapp.VEHICLE_NO";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_report_fragment, container, false);
        reportVehicle = (EditText) view.findViewById(R.id.reportvehno);
        textVehicle=(TextView)view.findViewById(R.id.txtvehicle);
       /*e1=(TextView)view.findViewById(R.id.e1);
        e2=(TextView)view.findViewById(R.id.e2);*/
        imgbtn = (ImageButton) view.findViewById(R.id.uploadimg);
        imgbtn.setOnClickListener(this);
        rButton = (Button) view.findViewById(R.id.reportbtn);
        rButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.uploadimg:
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                    startActivityForResult(intent,TAKE_IMAGE_CODE);
                }
                break;
            case R.id.reportbtn:

                textVehicle.setText(reportVehicle.getText().toString());
                String vehNo = textVehicle.getText().toString();
                if(bitmap!=null){
                handleUpload(bitmap);
                Intent intent1 = new Intent(getActivity().getBaseContext(),ReportMapsActivity.class);
                intent1.putExtra(VEHICLE_NO,vehNo);
                getActivity().startActivity(intent1);}
                else{
                    Toast.makeText(getContext(),"No Image Added",Toast.LENGTH_SHORT).show();
                }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_IMAGE_CODE){
            switch (resultCode){
                case  RESULT_OK:
                    bitmap=(Bitmap) data.getExtras().get("data");
                 //   handleUpload(bitmap);
            }
        }
    }
    private void handleUpload(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
       StorageReference reference= FirebaseStorage.getInstance().getReference()
                .child("Reports")
                .child(uid+".jpeg");
       reference.putBytes(baos.toByteArray())
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Toast.makeText(getContext(),"Image Upload Success",Toast.LENGTH_SHORT).show();
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.e(TAG,"onFailure: ",e.getCause());
                   }
               });
    }
}