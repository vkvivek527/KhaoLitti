package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateUserInfoActivity extends AppCompatActivity {

    private Spinner genderSpinner;
    private String [] genderList;
    private String selectedGender;
    private CircleImageView profileImage;
    private Button changePhotoBtn,removePhotoBtn,updateBtn;
    private EditText name,age,email;
    private Dialog loadingDialog;
    private FirebaseFirestore firebaseFirestore;
    private Uri imageuri;
    private boolean updatePhoto=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Profile");

        genderSpinner=findViewById(R.id.gender);
        genderList=getResources().getStringArray(R.array.gender);

        name =findViewById(R.id.name);
        age =findViewById(R.id.age);
        email =findViewById(R.id.email);
        profileImage=findViewById(R.id.profile_image);
        changePhotoBtn=findViewById(R.id.change_photo_btn);
        removePhotoBtn=findViewById(R.id.remove_photo_btn);
        updateBtn=findViewById(R.id.updateBtn);
        firebaseFirestore=FirebaseFirestore.getInstance();

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();

        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,genderList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender=genderList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    loadingDialog.show();
                    Map<String,Object> data=new HashMap<>();

                    if (!TextUtils.isEmpty(name.getText())){
                        data.put("name",name.getText().toString());
                    }
                    if (!TextUtils.isEmpty(age.getText())){
                        data.put("age",age.getText().toString());
                    }
                    if (!TextUtils.isEmpty(email.getText())){
                        data.put("email",email.getText().toString());
                    }
                    data.put("gender",selectedGender);

                    firebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                            .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(UpdateUserInfoActivity.this, "Profile Updated Sucessfully", Toast.LENGTH_SHORT).show();

                            }else {
                                String error=task.getException().getMessage().toString();
                                Toast.makeText(UpdateUserInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });


            }
        });
        changePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);
                    }else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
                    }
                }else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });

        removePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    loadingDialog.show();

                    Map<String,Object> data=new HashMap<>();
                    data.put("profile","");
                    firebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                            .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loadingDialog.dismiss();
                                Toast.makeText(UpdateUserInfoActivity.this, "removed sucessfully", Toast.LENGTH_SHORT).show();
                            }else {
                                loadingDialog.dismiss();
                                String error=task.getException().getMessage().toString();
                                Toast.makeText(UpdateUserInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    imageuri=null;
                    updatePhoto=true;
                    Glide.with(UpdateUserInfoActivity.this).load(R.drawable.profile).into(profileImage);


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                if (data!=null){
                    loadingDialog.show();
                     imageuri=data.getData();
                     updatePhoto=true;
                    Glide.with(this).load(imageuri).into(profileImage);

                    final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("profile/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
                    Glide.with(UpdateUserInfoActivity.this).asBitmap().circleCrop().load(imageuri).into(new ImageViewTarget<Bitmap>(profileImage) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG,100,baos);
                            byte[] data=baos.toByteArray();
                            UploadTask uploadTask=storageReference.putBytes(data);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()){
                                                    String updatedUri=task.getResult().toString();
                                                    Glide.with(UpdateUserInfoActivity.this).load(updatedUri).into(profileImage);
                                                    Map<String,Object> data=new HashMap<>();
                                                    data.put("profile",updatedUri);
                                                    firebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                                            .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                loadingDialog.dismiss();
                                                                Toast.makeText(UpdateUserInfoActivity.this, "updated sucessfully", Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                loadingDialog.dismiss();
                                                                String error=task.getException().getMessage().toString();
                                                                Toast.makeText(UpdateUserInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }else {
                                                    loadingDialog.dismiss();                                                        DBquaries.profile="";
                                                    String error=task.getException().getMessage().toString();
                                                    Toast.makeText(UpdateUserInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        loadingDialog.dismiss();
                                        String error=task.getException().getMessage().toString();
                                        Toast.makeText(UpdateUserInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return;
                        }

                        @Override
                        protected void setResource(@Nullable Bitmap resource) {
                            profileImage.setImageResource(R.drawable.profile);
                        }
                    });

                }
                else {
                    Toast.makeText(this, "Image Not Found!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==2){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){

                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!updatePhoto){
            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> userData = new HashMap<>();

                        userData.put("email", task.getResult().getString("email"));
                        userData.put("name", task.getResult().getString("name"));
                        userData.put("profile", task.getResult().getString("profile"));
                        userData.put("age", task.getResult().getString("age"));
                        userData.put("gender", task.getResult().getString("gender"));
                        userData.put("mobileno", task.getResult().getString("mobileno"));

                        name.setText(userData.get("name").toString());
                        age.setText(userData.get("age").toString());
                        email.setText(userData.get("email").toString());

                        if (!userData.get("gender").equals("")) {
                            for (int i = 0; i < genderList.length; i++) {
                                if (genderList[i].equals(userData.get("gender"))) {
                                    genderSpinner.setSelection(i);
                                }
                            }
                        }
                        if (!DBquaries.profile.equals("")) {
                            Glide.with(UpdateUserInfoActivity.this).load(DBquaries.profile).apply(new RequestOptions().placeholder(R.drawable.profile)).into(profileImage);
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(UpdateUserInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
    }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
