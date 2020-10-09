package com.navya.rtautoambulance;


import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class HomeFragmentActivity extends Fragment {
    View view;
    Button imageButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.activity_home_fragment,container,false);
       onClickButton();
       return view;
    }
    private void onClickButton(){

        imageButton=(Button) view.findViewById(R.id.redb);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Fetching your location!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}