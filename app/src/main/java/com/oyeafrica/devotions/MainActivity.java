package com.oyeafrica.devotions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.oyeafrica.devotions.views.Onboarding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gotoOnboarding();
    }

    private void gotoOnboarding() {
        Intent intent = new Intent(this, Onboarding.class);
        startActivity(intent);
    }
}