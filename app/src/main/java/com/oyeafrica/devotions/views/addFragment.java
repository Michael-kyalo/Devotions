package com.oyeafrica.devotions.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oyeafrica.devotions.R;
import com.oyeafrica.devotions.models.Devotion;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.grpc.Context;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class addFragment extends Fragment {

    private static final String TAG = "addFragment";
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_AUDIO = 2;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 3;
    LinearLayout send;
    TextInputEditText body;
    ImageView image, audio, video, add_image, cancel, back;
    Uri imageUri, videoUri, finaluri;
    Animation inRight, inLeft, outRight, outLeft;
    VideoView videoView;
    String type, uid;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    StorageReference storageReference;
    CollectionReference devotionRef;
    Devotion devotion;


    public addFragment() {
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
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uid = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        devotionRef = FirebaseFirestore.getInstance().collection("devotions").document(uid).collection("user_devotions");

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        outRight = AnimationUtils.loadAnimation(getContext(),R.anim.slide_out_right);
        bottomNavigationView.setAnimation(outRight);
        bottomNavigationView.setVisibility(View.GONE);
        FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.fab);
        //floatingActionButton.setVisibility(View.GONE);
        body = view.findViewById(R.id.body_text);

        outLeft = AnimationUtils.loadAnimation(getContext(),R.anim.slide_out_left);
        floatingActionButton.setAnimation(outLeft);
        floatingActionButton.setVisibility(View.GONE);

        send = view.findViewById(R.id.add_layout);

        Animation slideup = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_in_delayed);
        send.setAnimation(slideup);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendtoDb();
            }
        });



        image = view.findViewById(R.id.add_photo_image);
        video = view.findViewById(R.id.add_video_image);
        audio = view.findViewById(R.id.add_audio_icon);
        add_image = view.findViewById(R.id.place_holder_image);
        videoView = view.findViewById(R.id.videoView);
        cancel = view.findViewById(R.id.cancel_image);
        cancel.setVisibility(View.GONE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelMedia();
            }
        });
        back = view.findViewById(R.id.back_image_veiw);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        type = "text";


        //imageview Animations
        inRight = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_right);
        inLeft = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_left);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryImage();
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryVideo();
            }
        });
    }

    private void sendtoDb() {
        String devotion_text = body.getText().toString();
        if(TextUtils.isEmpty(devotion_text)){
            Toast.makeText(getContext(), "Devotion cannot be empty", Toast.LENGTH_LONG).show();
        }
        else{
            if(TextUtils.equals(type, "text")){
                sendTextDevotion(devotion_text);
            }else if(TextUtils.equals(type, "image")){
                sendAssetDevotion(devotion_text, imageUri);
            }
            else if(TextUtils.equals(type,"video")){
                sendAssetDevotion(devotion_text, videoUri);

            }
        }
    }

    private void sendAssetDevotion(final String devotion_text, Uri videoUri) {
        if(videoUri != null){
         final StorageReference videoRef = storageReference.child("videos/" + videoUri.getLastPathSegment());
         videoRef.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                 if(task.isSuccessful()){
                     videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             String asset = uri.toString();
                             final String ref = devotionRef.document().getId();

                             devotion = new Devotion();
                             devotion.setAsset(asset);
                             devotion.setText(devotion_text);
                             devotion.setType(type);
                             devotion.setCreater_id(uid);
                             devotion.setPoster_id(uid);
                             devotion.setDevotion_id(ref);

                             devotionRef.document(ref).set(devotion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         Map<String, Object> updateTime = new HashMap<>();
                                         updateTime.put("date", FieldValue.serverTimestamp());
                                         devotionRef.document(ref).update(updateTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if(task.isSuccessful()){
                                                     Navigation.findNavController(getActivity().findViewById(R.id.nav_host)).popBackStack();
                                                 }
                                             }
                                         });
                                     }else {
                                         failedui();
                                     }
                                 }
                             });







                         }
                     });
                 }else {
                     failedui();
                 }
             }
         });
        }

    }

    private void failedui() {
        Toast.makeText(getContext(), "task failed",Toast.LENGTH_LONG).show();
    }



    private void sendTextDevotion(String devotion_text) {
        final String ref = devotionRef.document().getId();

        devotion = new Devotion();
        devotion.setAsset(type);
        devotion.setText(devotion_text);
        devotion.setType(type);
        devotion.setCreater_id(uid);
        devotion.setPoster_id(uid);
        devotion.setDevotion_id(ref);

        devotionRef.document(ref).set(devotion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Map<String, Object> updateTime = new HashMap<>();
                    updateTime.put("date", FieldValue.serverTimestamp());
                    devotionRef.document(ref).update(updateTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Navigation.findNavController(getActivity().findViewById(R.id.nav_host)).popBackStack();

                            }
                        }
                    });
                }else {
                    failedui();
                }
            }
        });

    }



    private void openGalleryVideo() {
        disableChoice();
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
    }

    private void openGalleryImage() {
        disableChoice();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    private void disableChoice() {
        video.setEnabled(false);
        audio.setEnabled(false);
        image.setEnabled(false);
        video.setBackground(getResources().getDrawable(R.drawable.icon_stroke_grey_background));
        audio.setBackground(getResources().getDrawable(R.drawable.icon_stroke_grey_background));
        image.setBackground(getResources().getDrawable(R.drawable.icon_stroke_grey_background));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: here");
        if(resultCode== RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                type = "image";

                assert data != null;
                imageUri = data.getData();
                Log.d(TAG, "onActivityResult: " + imageUri);
                Picasso.get().load(imageUri).fit().into(add_image);
                add_image.setAnimation(inLeft);
                add_image.setVisibility(View.VISIBLE);
                cancel.setAnimation(inRight);
                cancel.setVisibility(View.VISIBLE);
            }
            if(requestCode== REQUEST_TAKE_GALLERY_VIDEO){
                assert data != null;
                type = "video";
                videoUri = data.getData();
                videoView.setAnimation(inLeft);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(finaluri);
               // videoView.setZOrderOnTop(true);
                videoView.start();
                cancel.setAnimation(inRight);
                cancel.setVisibility(View.VISIBLE);

//                TrimVideo.activity(String.valueOf(videoUri))
//                        .setTrimType(TrimType.MIN_MAX_DURATION)
//                        .setMinToMax(30,300)
//                        .setCompressOption(new CompressOption(30,"1M",460,320))
//                        .start(requireActivity());


            }
        }
        else if (resultCode == RESULT_CANCELED){
            enableChoice();
        }

//        if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
//            finaluri = Uri.parse(TrimVideo.getTrimmedVideoPath(data));
//            Log.d(TAG,"Trimmed path:: "+finaluri);
//            videoView.setAnimation(inLeft);
//            videoView.setVisibility(View.VISIBLE);
//            videoView.setVideoURI(finaluri);
//            videoView.start();
//            cancel.setAnimation(inRight);
//            cancel.setVisibility(View.VISIBLE);
//        }


    }

    private void cancelMedia() {
        if(add_image.getVisibility() == View.VISIBLE){
            add_image.setAnimation(outLeft);
            add_image.setVisibility(View.GONE);
        }
        if(videoView.getVisibility() == View.VISIBLE){
            videoView.setAnimation(outLeft);
            videoView.setVisibility(View.GONE);
        }
        cancel.setAnimation(outRight);
        cancel.setVisibility(View.GONE);
        enableChoice();
    }

    private void enableChoice() {
        type = "text";
        video.setEnabled(true);
        audio.setEnabled(true);
        image.setEnabled(true);
        video.setBackground(getResources().getDrawable(R.drawable.icon_stroke_background));
        audio.setBackground(getResources().getDrawable(R.drawable.icon_stroke_background));
        image.setBackground(getResources().getDrawable(R.drawable.icon_stroke_background));
    }
}