package com.oyeafrica.devotions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oyeafrica.devotions.views.AuthActivity;
import com.oyeafrica.devotions.views.Onboarding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseUser firebaseUser;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.VISIBLE);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(this, AuthActivity.class));
        }

        BottomNavigationView bottomAppBar = findViewById(R.id.bottom_nav);
        bottomAppBar.setVisibility(View.VISIBLE);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        assert navHostFragment != null;
        NavigationUI.setupWithNavController(bottomAppBar,navHostFragment.getNavController());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(findViewById(R.id.nav_host)).navigate(R.id.addFragment);
            }
        });
    }


}