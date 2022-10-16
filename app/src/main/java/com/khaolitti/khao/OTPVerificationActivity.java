package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPVerificationActivity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo;
    private  String orderId;
    private Dialog loadingDialog;
    private TextView timer,resendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        phoneNo=findViewById(R.id.phone_number);
        otp=findViewById(R.id.otp);
        verifyBtn=findViewById(R.id.verify_btn);
        resendOTP=findViewById(R.id.resend);
        timer=findViewById(R.id.resendbtn);

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        orderId=getIntent().getStringExtra("orderId");
        userNo=getIntent().getStringExtra("mobileno");
        phoneNo.setText("To Confirm Your Order \n Verification Code Has Been Sent To \n"+userNo);


        Random random=new Random();
        final int OTPnumber=random.nextInt(999999-111111)+111111;
        String SMS_API="https://www.fast2sms.com/dev/bulk";

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (otp.getText().toString().equals(String.valueOf(OTPnumber))) {
                            loadingDialog.show();
                            //////updating trxn status

                            Map<String,Object> updateStatus=new HashMap<>();
                            updateStatus.put("payment Status","COD");
                            updateStatus.put("Order Status","Ordered");
                            FirebaseFirestore.getInstance().collection("ORDERS").document(orderId).update(updateStatus)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Map<String,Object> userOrder=new HashMap<>();
                                                userOrder.put("order_id",orderId);
                                                userOrder.put("time", FieldValue.serverTimestamp());
                                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(orderId).set(userOrder)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                    Intent orderconfirmation = new Intent(OTPVerificationActivity.this, OrderConfirmationActivity.class);
                                                                    orderconfirmation.putExtra("orderId", orderId);
                                                                    orderconfirmation.putExtra("mobile",userNo);
                                                                    startActivity(orderconfirmation);
                                                                    finish();

                                                                }else {

                                                                    Toast.makeText(OTPVerificationActivity.this, " sorry!!! something went wrong please try again", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });

                                            }
                                            else {
                                                Toast.makeText(OTPVerificationActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                            /////updating txn status

                        } else {
                            Toast.makeText(OTPVerificationActivity.this, "incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                finish();
                Toast.makeText(OTPVerificationActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
                headers.put("authorization","i2cWyd4UXzLVkMSKNox85mGjsDfIHqR3CnwvrO9uahe0ApBtYgIEk7gKHAyMcNusUTDxzpYilGm6Ohvq");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> body=new HashMap<>();
                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers",userNo);
                body.put("message","23716");
                body.put("variables","{BB}");
                body.put("variables_values", String.valueOf(OTPnumber));

                return body;            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
       5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue= Volley.newRequestQueue(OTPVerificationActivity.this);
        requestQueue.add(stringRequest);

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

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

    }
}
