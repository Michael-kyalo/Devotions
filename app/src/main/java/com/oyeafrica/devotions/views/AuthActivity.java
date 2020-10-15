package com.oyeafrica.devotions.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oyeafrica.devotions.MainActivity;
import com.oyeafrica.devotions.R;
import com.oyeafrica.devotions.User;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";
    private static int DEFAULT_SIGNIN = 1;
    private static int RC_SIGN_IN =2;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    LinearLayout signupLayout;
    LottieAnimationView loading_anim, failed_anim;
    TextView status;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //check first run to redirect to onboarding
        Boolean isfirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if(isfirstRun){
            Log.d(TAG, "First run:  " + true);
            gotoonboarding();
        }
        //update isfirstrun
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
        Log.d(TAG, "firstrun: " + isfirstRun);

        signupLayout = findViewById(R.id.login_linearlayout);
        loading_anim = findViewById(R.id.anim_loading);
        failed_anim = findViewById(R.id.anim_failed);
        status = findViewById(R.id.status_text);

        signupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinwithgoogle();
            }
        });

        /*
        GOOGLE SIGNIN
         */
        mAuth = FirebaseAuth.getInstance();



        GoogleSignInOptions google = new GoogleSignInOptions. Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_id))
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this,google);
    }

    private void signinwithgoogle() {
        updateUIloading();
        Intent intent  = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);


    }

    private void updateUIloading() {
        status.setText(R.string.loading);
        loading_anim.setVisibility(View.VISIBLE);
        failed_anim.setVisibility(View.GONE);
        signupLayout.setVisibility(View.INVISIBLE);
    }

    private void gotoonboarding() {
         startActivity(new Intent(this, Onboarding.class));
         finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                assert account != null;
                firebaseauth(account);

            }
            catch (ApiException e) {
              updateUIError();
                e.printStackTrace();
            }

        }
    }

    private void firebaseauth(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseauth: " + account.getId());
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                  firebaseUser  = mAuth.getCurrentUser();
                    final User user = new User();
                    user.setImage(Objects.requireNonNull(account.getPhotoUrl()).toString());
                    user.setUsername(account.getDisplayName());
                    assert firebaseUser != null;
                    user.setUid(firebaseUser.getUid());
                    user.setType("normal");

                    final DocumentReference documentReference = db.collection(getString(R.string.user_db)).document(user.getUid());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                assert documentSnapshot != null;
                                if(documentSnapshot.exists()){
                                    Snackbar.make(Objects.requireNonNull(getCurrentFocus()),"Hello," + user.getUsername(),Snackbar.LENGTH_SHORT).show();

                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                    finish();
                                }
                                else {
                                    //new User
                                    db.collection("Users").document(user.getUid()).set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                                        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                                                        finish();
                                                    }
                                                    else {
                                                        firebaseUser= null;
                                                        Log.d(TAG, "onComplete: task failed" );
                                                        updateUIError();
                                                    }

                                                }
                                            });

                                }


                            }else {
                                firebaseUser= null;
                                updateUIError();
                                Log.d(TAG, "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());

                            }
                        }
                    });

                }
                else {
                    firebaseUser= null;
                    updateUIError();
                    Log.d(TAG, "onComplete task 1: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    private void updateUIError() {
        status.setText(R.string.error);
        loading_anim.setVisibility(View.GONE);
        failed_anim.setVisibility(View.VISIBLE);
        signupLayout.setVisibility(View.VISIBLE);
    }
}
