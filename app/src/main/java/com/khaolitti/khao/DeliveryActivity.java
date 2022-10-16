package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView deliveryRcyclierView;
    public static CartAdapter cartAdapter;
    private Button changeOrAddNewAddressbtn;
    public static final int SELECT_ADDRESS=0;
    public static TextView totalAmount;
    private TextView fullname;
    private String name,mobileno;
    private TextView fullAddress;
    private TextView pincode;
    public static List<CartItemModel> cartItemModelList;
    private Button continueButton;
    public static Dialog loadingDialog;
    private Dialog paymentMethoddialog;
    private LinearLayout paytm,cod,debits,credits,netbankings;
   // private TextView codTitle;
   // private View divider;
    public static Activity deliveryActivity;
    private String order_id;
    private String paymentMethod="PAYTM";
    private FirebaseFirestore firebaseFirestore;
    public static boolean getQtyIds=true;
    private boolean sucessResponse=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        deliveryRcyclierView=findViewById(R.id.delivery_recyclerview);
        changeOrAddNewAddressbtn=findViewById(R.id.change_add_address_btn);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRcyclierView.setLayoutManager(linearLayoutManager);

        totalAmount=findViewById(R.id.total_cart_amount);
        fullname=findViewById(R.id.fullName);
        fullAddress=findViewById(R.id.address);
        pincode=findViewById(R.id.pincode);
        continueButton=findViewById(R.id.cart_continue_btn);


        ////dialog
        loadingDialog=new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ////dialog
        firebaseFirestore=FirebaseFirestore.getInstance();

        //// payment dialog
        paymentMethoddialog=new Dialog(DeliveryActivity.this);
        paymentMethoddialog.setContentView(R.layout.payment_method);
        paymentMethoddialog.setCancelable(true);
        paymentMethoddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paymentMethoddialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        paytm=paymentMethoddialog.findViewById(R.id.paytm_payment);
        cod=paymentMethoddialog.findViewById(R.id.cash_on_delivery_payment);

        debits=paymentMethoddialog.findViewById(R.id.debit_card_payment);
        credits=paymentMethoddialog.findViewById(R.id.credit_card_payment);
        netbankings=paymentMethoddialog.findViewById(R.id.netbanking_payment);

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod="PAYTM";
                placeOederDetails();
                paytm();
 //               Toast.makeText(deliveryActivity, "This Payment method will be available soon ", Toast.LENGTH_SHORT).show();
            }
        });

        debits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(deliveryActivity, "This Payment method will be available soon ", Toast.LENGTH_SHORT).show();
            }
        });
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(deliveryActivity, "This Payment Method will be available soon ", Toast.LENGTH_SHORT).show();
            }
        });
        netbankings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(deliveryActivity, "This Payment Method will be available soon ", Toast.LENGTH_SHORT).show();
            }
        });
        //codTitle=paymentMethoddialog.findViewById(R.id.cod_btn_title);
        //divider=paymentMethoddialog.findViewById(R.id.dividerr);
        ////payment dialog

        getQtyIds=true;
        order_id= UUID.randomUUID().toString().substring(0,10);


        cartAdapter=new CartAdapter(cartItemModelList,totalAmount,false);
        deliveryRcyclierView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        changeOrAddNewAddressbtn.setVisibility(View.VISIBLE);

        changeOrAddNewAddressbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIds=false;
                Intent myAddressesIntent=new Intent(DeliveryActivity.this,MyAddressActivity.class);
                myAddressesIntent.putExtra("Mode",SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryActivity=DeliveryActivity.this;
                Boolean allProductsAvailable=true;
                for (CartItemModel cartItemModel:cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductsAvailable = false;
                        break;
                    }
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
//                            codTitle.setAlpha(0.5f);
//                            codTitle.setVisibility(View.GONE);
//                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
//                            codTitle.setAlpha(1f);
//                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (allProductsAvailable){
                    paymentMethoddialog.show();
                }
            }
        });


        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod="COD";
                placeOederDetails();

            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
      if (id==android.R.id.home){
          finish();
          return true;
      }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (sucessResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();
        if (getQtyIds) {
                for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                    if (!sucessResponse) {
                        for (final String qtyId : cartItemModelList.get(x).getQtyId()) {
                            final int finalX = x;
                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyId).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (qtyId.equals( cartItemModelList.get(finalX).getQtyId().get( cartItemModelList.get(finalX).getQtyId().size()-1))){
                                                cartItemModelList.get(finalX).getQtyId().clear();
                                            }
                                        }
                                    });
                        }
                    }else {
                        cartItemModelList.get(x).getQtyId().clear();
                    }
                }
            }
        }
    @Override
    protected void onStart() {
        super.onStart();
////acesssing quantity
        if (getQtyIds) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for (int y=0;y<cartItemModelList.get(x).getProductQuantity();y++){
                    final String quantityDocumentName=UUID.randomUUID().toString().substring(0,20);
                    Map<String,Object> timeStamp=new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName).set(timeStamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        cartItemModelList.get(finalX).getQtyId().add(quantityDocumentName);
                                        if (finalY +1==cartItemModelList.get(finalX).getProductQuantity()){

                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                List<String> serverQuantity=new ArrayList<>();
                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }
                                                                long availableQty=0;
                                                                boolean noLongerAvailable=true;
                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyId()){

                                                                    cartItemModelList.get(finalX).setQtyError(false);

                                                                    if (!serverQuantity.contains(qtyId)){

                                                                        if (noLongerAvailable){
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        }
                                                                        else {

                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, " SORRY!!! All products may not be available in require quantity....", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }
                                                                    else {
                                                                        availableQty++;
                                                                        noLongerAvailable=false;
                                                                    }

                                                                }
                                                                cartAdapter.notifyDataSetChanged();

                                                            }
                                                            else {

                                                                String error=task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this," 1-"+ error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        }

                                    }
                                    else {
                                        loadingDialog.dismiss();
                                        String error=task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, "2"+error, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                }
            }
        }
        else {
            getQtyIds=true;
        }
       ////acesssing quantity

        name=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getName();
        mobileno=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getMobileNo();
       if (DBquaries.addressesModelList.get(DBquaries.selectedAddress).getAlternateMoNo().equals("")){
           fullname.setText(name+" - "+mobileno);
       }
       else {
           fullname.setText(name+" - "+mobileno+"/"+DBquaries.addressesModelList.get(DBquaries.selectedAddress).getAlternateMoNo());
       }
      String  flatNo=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getFlatno();
        String  locality=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getLocality();
        String  landMark=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getLandMark();
        String  city=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getCity();
        String  state=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getState();
        if (landMark.equals("")){
            fullAddress.setText(flatNo+" "+ locality+","+city+","+state);

        }
        else {
            fullAddress.setText(flatNo+" "+ locality+" "+landMark+","+city+","+state);

        }
        pincode.setText(DBquaries.addressesModelList.get(DBquaries.selectedAddress).getPincode());
    }


        private void placeOederDetails(){
        String userId=FirebaseAuth.getInstance().getUid();
        loadingDialog.show();
        for (CartItemModel cartItemModel:cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID",order_id);
                orderDetails.put("Product Id",cartItemModel.getProductID());
               orderDetails.put("Product Image",cartItemModel.getProductImage());
               orderDetails.put("Product Title",cartItemModel.getProductTitle());

                orderDetails.put("User Id",userId);
                orderDetails.put("Product Quantity",cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                }
                else {
                    orderDetails.put("Cutted Price","");

                }
                orderDetails.put("Product Price",cartItemModel.getProductPrice());

                if (cartItemModel.getSelectedCoupanId() != null) {

                    orderDetails.put("Coupan Id", cartItemModel.getSelectedCoupanId());
                }
                else {
                    orderDetails.put("Coupan Id", "");

                }
                if (cartItemModel.getDiscountedPrice() != null) {

                    orderDetails.put("Discounted Price", cartItemModel.getDiscountedPrice());
                }
                else {
                    orderDetails.put("Discounted Price", "");
                }


                orderDetails.put("Order date",FieldValue.serverTimestamp());
                orderDetails.put("packed date",FieldValue.serverTimestamp());
                orderDetails.put("shipped date",FieldValue.serverTimestamp());
                orderDetails.put("Delivered date",FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date",FieldValue.serverTimestamp());
                orderDetails.put("Order Status","Ordered");
                orderDetails.put("Payment Mathod",paymentMethod);
                orderDetails.put("Address",fullAddress.getText());
                orderDetails.put("Full Name",fullname.getText());
                orderDetails.put("Pincode",pincode.getText());
                orderDetails.put("Free Coupan",cartItemModel.getFreeCoupans());
                orderDetails.put("Delivery Price",cartItemModelList.get(cartItemModelList.size()-1).getDeliveryPrice());
                orderDetails.put("Cancellation requested",false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                         if (!task.isSuccessful()){
                            String error =task.getException().getMessage();
                             Toast.makeText(DeliveryActivity.this,"3"+ error, Toast.LENGTH_SHORT).show();
                         }
                    }
                });

            }
            else {
                Map<String,Object> orederDtails=new HashMap<>();
                orederDtails.put("Total Items",cartItemModelList.get(0).getTotalItems());
                orederDtails.put("Total Items Price",cartItemModelList.get(0).getTotalItemsPrice());
                orederDtails.put("Delivery Price",cartItemModelList.get(0).getDeliveryPrice());
                orederDtails.put("Total Amount",cartItemModelList.get(0).getTotalAmount());
                orederDtails.put("Saved Amount",cartItemModelList.get(0).getSavedAmoun());
                orederDtails.put("payment Status","not Paid");
                orederDtails.put("Order Status","Cancelled");
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orederDtails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            if (paymentMethod.equals("PAYTM")){
                                paytm();
                            }
                            else {
                                cod();
                            }

                        }
                        else {
                            String error =task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this,"4"+ error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }
    }

    private void paytm(){


        getQtyIds=false;
        paymentMethoddialog.dismiss();
        loadingDialog.show();

        if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        final String m_id="mJtVrz26673033785286";
        final String customer_id= FirebaseAuth.getInstance().getUid();
        String url="https://klitti.000webhostapp.com/paytm/generateChecksum.php";
        final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        RequestQueue requestQueue= Volley.newRequestQueue(DeliveryActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.has("CHECKSUMHASH")){
                        String CHECKSUMHASH=jsonObject.getString("CHECKSUMHASH");

                        PaytmPGService paytmPGService=PaytmPGService.getStagingService(null);

                        HashMap<String, String> paramMap = new HashMap<String,String>();
                        paramMap.put( "MID" , m_id);
                        paramMap.put( "ORDER_ID" , order_id);
                        paramMap.put( "CUST_ID" , customer_id);
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2));
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        paramMap.put( "CALLBACK_URL",callBackUrl);
                        paramMap.put("CHECKSUMHASH",CHECKSUMHASH);

                        PaytmOrder order=new PaytmOrder(paramMap);
                        paytmPGService.initialize(order,null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {

                            Bundle iResponse;
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
                                // Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                 iResponse=inResponse;
                                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")){

                                    //////updating trxn status

                                    Map<String,Object> updateStatus=new HashMap<>();
                                    updateStatus.put("payment Status","Paid");
                                    updateStatus.put("Order Status","Ordered");
                                    firebaseFirestore.collection("ORDERS").document(order_id).update(updateStatus)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Map<String,Object> userOrder=new HashMap<>();
                                                        userOrder.put("order_id",order_id);
                                                        userOrder.put("time",FieldValue.serverTimestamp());
                                                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id).set(userOrder)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                       if (task.isSuccessful()){
                                                                           sucessResponse=true;
                                                                           Intent orderconfirmation=new Intent(DeliveryActivity.this,OrderConfirmationActivity.class);
                                                                           orderconfirmation.putExtra("orderId",iResponse.getString("ORDERID"));
                                                                           orderconfirmation.putExtra("mobile",mobileno);
                                                                           startActivity(orderconfirmation);
                                                                           finish();
                                                                       }else {
                                                                           Toast.makeText(DeliveryActivity.this, " sorry!!! something went wrong please try again", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                    }
                                                                });
                                                    }
                                                    else {
                                                        Toast.makeText(DeliveryActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                    /////updating txn status

                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                           @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onBackPressedCancelTransaction() {

                                Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled"+ inResponse.toString() , Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                //Toast.makeText(DeliveryActivity.this, "error-"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramMap = new HashMap<String,String>();
                paramMap.put( "MID" , m_id);
                paramMap.put( "ORDER_ID" , order_id);
                paramMap.put( "CUST_ID" , customer_id);
                paramMap.put( "CHANNEL_ID" , "WAP");
                paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString());
                paramMap.put( "WEBSITE" , "WEBSTAGING");
                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                paramMap.put( "CALLBACK_URL",callBackUrl);

                return paramMap;
            }
        };

        requestQueue.add(stringRequest);

    }

    private void cod(){

        getQtyIds=false;
        paymentMethoddialog.dismiss();
        Intent otpIntent=new Intent(DeliveryActivity.this,OTPVerificationActivity.class);
        otpIntent.putExtra("orderId",order_id);
        otpIntent.putExtra("mobileno",mobileno.substring(0,10));
        startActivity(otpIntent);

    }
}
