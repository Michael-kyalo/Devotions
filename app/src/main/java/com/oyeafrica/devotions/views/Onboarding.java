package com.oyeafrica.devotions.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.oyeafrica.devotions.R;

public class Onboarding extends AppCompatActivity {
TextView get_start_text;
ImageView get_start_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        get_start_text = findViewById(R.id.get_started_text);
        get_start_image = findViewById(R.id.get_started_image);


        get_start_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAuth();
            }
        });
        get_start_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAuth();
            }
        });

    }

    private void goToAuth() {
        Intent auth = new Intent(this, AuthActivity.class);
        startActivity(auth);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }
}