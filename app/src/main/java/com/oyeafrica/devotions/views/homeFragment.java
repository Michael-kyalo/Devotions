package com.oyeafrica.devotions.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oyeafrica.devotions.R;


public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public homeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.fab);
        if(bottomNavigationView.getVisibility() == View.GONE){
            Animation inRight = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_right);
            bottomNavigationView.setAnimation(inRight);
            bottomNavigationView.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_left);
            floatingActionButton.setAnimation(animation);
            floatingActionButton.setVisibility(View.VISIBLE);
        }

    }
}