package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneActivity extends AppCompatActivity {

    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private TextView t1,t2;
    private ImageView i1;
    private EditText e1,e2;
    private Button b1,b2;
    private FirebaseFirestore firebaseFirestore;
    private String phone;
    private TextView showNumber;
    private TextView resendOTP;
    private String phoneNumber;
    private TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        e1 = (EditText) findViewById(R.id.Phonenoedittext);
        b1 = (Button) findViewById(R.id.PhoneVerify);
        t1 = (TextView)findViewById(R.id.textView2Phone);
        i1 = (ImageView)findViewById(R.id.imageView2Phone);
        e2 = (EditText) findViewById(R.id.OTPeditText);
        b2 = (Button)findViewById(R.id.OTPVERIFY);
        t2 = (TextView)findViewById(R.id.textViewVerified);
        resendOTP=findViewById(R.id.resend_otp);
        loadingBar=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        showNumber=findViewById(R.id.showmobilenumber);
        firebaseFirestore=FirebaseFirestore.getInstance();
        timer=findViewById(R.id.timer);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                Toast.makeText(PhoneActivity.this,"Verification Complete",Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // Log.w(TAG, "onVerificationFailed", e);
                loadingBar.dismiss();
                Toast.makeText(PhoneActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(PhoneActivity.this,"InValid Phone Number",Toast.LENGTH_SHORT).show();
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Log.d(TAG, "onCodeSent:" + verificationId);
                loadingBar.dismiss();
                Toast.makeText(PhoneActivity.this,"Verification code has been send on your number",Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                e1.setVisibility(View.GONE);
                b1.setVisibility(View.GONE);
                t1.setVisibility(View.GONE);
                i1.setVisibility(View.GONE);
                t2.setVisibility(View.VISIBLE);
                e2.setVisibility(View.VISIBLE);
                b2.setVisibility(View.VISIBLE);
                showNumber.setVisibility(View.VISIBLE);
                showNumber.setText(phone);
                 timer.setVisibility(View.VISIBLE);

                CountDownTimer countDownTimer=new CountDownTimer(30000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                       timer.setText("00:"+ (millisUntilFinished/1000));
                    }
                    @Override
                    public void onFinish() {
                        timer.setVisibility(View.INVISIBLE);
                     resendOTP.setVisibility(View.VISIBLE);
                    }
                };
                countDownTimer.start();
            }
        };

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 phone = e1.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(PhoneActivity.this, "Please Enter Number", Toast.LENGTH_SHORT).show();
                } else {
                    phoneNumber="+91"+phone;
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Authenticating Please Wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            30,
                            java.util.concurrent.TimeUnit.SECONDS,
                            PhoneActivity.this,
                            mCallbacks);
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = e2.getText().toString();
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(PhoneActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle(" Verification Code");
                    loadingBar.setMessage("Verifying Code Please Wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, e2.getText().toString());
                    // [END verify_with_code]
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Resending OTP");
                loadingBar.setMessage("Authenticating Please Wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        java.util.concurrent.TimeUnit.SECONDS,
                        PhoneActivity.this,
                        mCallbacks);
            }
        });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Log.d(TAG, "signInWithCredential:success");

                            firebaseFirestore.collection("USERS").document(mAuth.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.getResult().exists()){

                                                loadingBar.dismiss();
                                                Intent intent=new Intent(PhoneActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                finish();
                                            }
                                            else {
                                           /////////wishlist
                                                Map<String,Object> userData=new HashMap<>();
                                                userData.put("mobileno",phone);
                                                userData.put("email","");
                                                userData.put("name","");
                                                userData.put("profile","");
                                                userData.put("age","");
                                                userData.put("gender","");
                                                firebaseFirestore.collection("USERS").document(mAuth.getUid())
                                                        .set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){

                                                            CollectionReference userDataReference=firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("USER_DATA");

                                                            ////maps
                                                            Map<String,Object> wishlistmap=new HashMap<>();
                                                            wishlistmap.put("list_size", (long) 0);

                                                            Map<String,Object> ratingsmap=new HashMap<>();
                                                            ratingsmap.put("list_size", (long) 0);

                                                            Map<String,Object> cartmap=new HashMap<>();
                                                            cartmap.put("list_size", (long) 0);

                                                            Map<String,Object> myAddressesMap=new HashMap<>();
                                                            myAddressesMap.put("list_size", (long) 0);

                                                            Map<String,Object> notificationsMap=new HashMap<>();
                                                            notificationsMap.put("list_size", (long) 0);


                                                            ////maps
                                                            final List<String> documentsNames=new ArrayList<>();
                                                            documentsNames.add("MY_WISHLIST");
                                                            documentsNames.add("MY_RATINGS");
                                                            documentsNames.add("MY_CART");
                                                            documentsNames.add("MY_ADDRESSES");
                                                            documentsNames.add("MY_NOTIFICATIONS");


                                                            List<Map<String,Object>> documentField=new ArrayList<>();
                                                            documentField.add(wishlistmap);
                                                            documentField.add(ratingsmap);
                                                            documentField.add(cartmap);
                                                            documentField.add(myAddressesMap);
                                                            documentField.add(notificationsMap);

                                                            for (int x=0;x<documentsNames.size();x++)
                                                            {
                                                                final int finalX = x;
                                                                userDataReference.document(documentsNames.get(x))
                                                                        .set(documentField.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()){
                                                                            if (finalX ==documentsNames.size()-1) {
                                                                                Intent intent = new Intent(PhoneActivity.this, MainActivity.class);
                                                                                startActivity(intent);
                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                finish();
                                                                            }
                                                                        }
                                                                        else {
                                                                            loadingBar.dismiss();
                                                                            String error=task.getException().getMessage();
                                                                            Toast.makeText(PhoneActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }
                                                                });
                                                            }

                                                        }
                                                        else {
                                                            loadingBar.dismiss();
                                                            String error=task.getException().getMessage().toString();

                                                            Toast.makeText(PhoneActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                setAppLaunchTime();

                                            }
                                        }
                                    });


                            Toast.makeText(PhoneActivity.this,"Verification Done",Toast.LENGTH_SHORT).show();
                            // ...
                        } else {
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneActivity.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }});

        /////setting time
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null)
        {
            Intent it=new Intent(PhoneActivity.this,MainActivity.class);
            startActivity(it);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();

        }
    }

    private void setAppLaunchTime(){

        if (mAuth.getCurrentUser()!=null) {
            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).update("Last seen", FieldValue.serverTimestamp())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(PhoneActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
