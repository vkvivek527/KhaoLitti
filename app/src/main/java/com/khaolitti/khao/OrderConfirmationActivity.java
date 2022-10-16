package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderConfirmationActivity extends AppCompatActivity {

    private List<Integer> indexList;

    private TextView continueShoppingbtn;
    private TextView orderId;
    private String id;
    public static boolean fromcart;
    private Dialog loadingDialog;
    private String mobile;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        continueShoppingbtn=findViewById(R.id.textView37);
        orderId=findViewById(R.id.order_id);
        firebaseFirestore=FirebaseFirestore.getInstance();

        id=getIntent().getExtras().getString("orderId");
        mobile=getIntent().getExtras().getString("mobile");
        String SMS_API="https://www.fast2sms.com/dev/bulk";

        ////dialog
        loadingDialog=new Dialog(OrderConfirmationActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ////dialog

         DeliveryActivity.getQtyIds=false;

        for (int x=0;x<DeliveryActivity.cartItemModelList.size()-1;x++){
            for (String qtyId:DeliveryActivity.cartItemModelList.get(x).getQtyId()){
                firebaseFirestore.collection("PRODUCTS").document(DeliveryActivity.cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyId).update("user_ID",FirebaseAuth.getInstance().getUid());

            }
        }

        //////setting order status after payment

        //////setting order status after payment

        if (MainActivity.mainActivity!=null){
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity=null;
            MainActivity.showCart=false;
        }
        else {
             MainActivity.reserMainActivity=true;
        }
        if (ProductDetailsActivity.productDetailsActivity!=null){
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity=null;
        }
        if (DeliveryActivity.deliveryActivity!=null){
            DeliveryActivity.deliveryActivity.finish();
            DeliveryActivity.deliveryActivity=null;
        }




        StringRequest stringRequest=new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                body.put("numbers",mobile);
                body.put("message","23765");
                body.put("variables","{FF}");
                body.put("variables_values", id);


                return body;            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue= Volley.newRequestQueue(OrderConfirmationActivity.this);
        requestQueue.add(stringRequest);

        if (fromcart){
            loadingDialog.show();
            Map<String,Object> updateCartList=new HashMap<>();

            long cartListSize=0;

              indexList=new ArrayList<>();

            for(int x=0;x<DBquaries.cartList.size();x++){

                if (!DeliveryActivity.cartItemModelList.get(x).isInStock()){
                    updateCartList.put("product_ID_"+cartListSize,DeliveryActivity.cartItemModelList.get(x).getProductID());
                    cartListSize++;
                }
                else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size",cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        for (int x=0;x<indexList.size();x++){
                            DBquaries.cartList.remove(indexList.get(x));
                            DBquaries.cartItemModelList.remove(indexList.get(x));
                            DBquaries.cartItemModelList.remove(DBquaries.cartItemModelList.size()-1);
                        }
                    }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(OrderConfirmationActivity.this, error, Toast.LENGTH_SHORT).show();

                    }
                    loadingDialog.dismiss();


                }
            });
        }


        orderId.setText("Your Order Id : "+id);

        continueShoppingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
         finish();

    }
}
