package com.navya.soonapp;


import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.skyfishjy.library.RippleBackground;

public class HomeFragmentActivity extends Fragment {
    Boolean clicked;
    View view;
    Button imageButton;
    RippleBackground ripple;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.activity_home_fragment,container,false);
       ripple=(RippleBackground)view.findViewById(R.id.ripple);
       ripple.startRippleAnimation();
       onClickButton();
       return view;
    }
    private void onClickButton(){

        imageButton=(Button) view.findViewById(R.id.redb);
        ripple.startRippleAnimation();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getActivity(),CancelRequestActivity.class);
                getActivity().startActivity(intent);


            }
        });
    }
}