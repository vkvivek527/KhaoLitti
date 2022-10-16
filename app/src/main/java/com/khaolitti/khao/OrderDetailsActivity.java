package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private int position;


    private ImageView productImage,orderedIndicator,packedIndicator,shippedIndicator,deliveredIndicator;
    private ProgressBar O_P_progress,P_S_progress,S_D_progress;
    private TextView orderedTitle,packedTitle,shippedTitle,deliveredTitle;
    private TextView orderedDate,packedDate,shippedDate,deliveredDate;
    private TextView orderedBody,packedBody,shippedBody,deliveredBody;
    private TextView tv_total_discount_amount;
    private TextView cutted_price_text;
    private TextView oDetail;


    private List<ImageTitleModel> imageTitleModels;
    private RecyclerView recyclerView;
    private ImageTitleAdapter  titleAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String orderIdForRefresh;

//    ////rating
//    private LinearLayout rateNowContainer;
//    private int rating;

    private TextView fullName,address,pincode;
    private TextView totalItems, totalItemPrice,deliveryPrice,totalAmount,savedAmount;
    private Dialog loadingDialog,cancelDialog;
    private SimpleDateFormat simpleDateFormat;
   // private Button cancelOrderBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        oDetail=findViewById(R.id.dtorderId);

        ////dialog
        loadingDialog=new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ////dialog

//        ////cancel dialog
//        cancelDialog=new Dialog(OrderDetailsActivity.this);
//        cancelDialog.setContentView(R.layout.order_cancel_dialog);
//        cancelDialog.setCancelable(true);
//       // cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
//        ////cancel dialog

        swipeRefreshLayout=findViewById(R.id.swipe_refresh_order_detail);

        imageTitleModels=new ArrayList<>();

        position=getIntent().getIntExtra("Position",-1);

       final List<MyOrderItemModel> model=DBquaries.fetchModelItemList.get(position).getFetchedOrderList();
       orderIdForRefresh=getIntent().getStringExtra("Oid");

        oDetail.setText("Order Id : "+orderIdForRefresh);

       int totalquantity=0;
       int totalAmounts=0;
       int paidAmount=0;
        for (int i=0;i<model.size();i++){
            imageTitleModels.add(new ImageTitleModel(model.get(i).getProductTitle(),model.get(i).getProductImage(),Integer.parseInt(model.get(i).getProductQuantity().toString()),model.get(i).getProductPrice(),model.get(i).getCuttedPrice()));
            totalquantity =totalquantity+ Integer.parseInt(model.get(i).getProductQuantity().toString());
            totalAmounts=totalAmounts+ Integer.parseInt(model.get(i).getProductQuantity().toString())*Integer.parseInt(model.get(i).getCuttedPrice().toString());
            paidAmount=paidAmount+Integer.parseInt(model.get(i).getProductQuantity().toString())*Integer.parseInt(model.get(i).getProductPrice().toString());
        }
        recyclerView=findViewById(R.id.quantity_image_title);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        titleAdapter=new ImageTitleAdapter(imageTitleModels);
        recyclerView.setAdapter(titleAdapter);
        titleAdapter.notifyDataSetChanged();

        totalItemPrice=findViewById(R.id.total_items_price);
        deliveryPrice=findViewById(R.id.delivery_price);
        totalAmount=findViewById(R.id.total_price);
        totalItems=findViewById(R.id.total_items);
        savedAmount=findViewById(R.id.saved_amount);
        cutted_price_text=findViewById(R.id.cutted_price_text);
        tv_total_discount_amount=findViewById(R.id.tv_total_discount_amount);

        totalItems.setText(""+totalquantity);

        totalItemPrice.setText("₹"+totalAmounts);
        if (totalquantity>1){
            totalItems.setText("Total("+totalquantity+")"+"items");
        }else {
            totalItems.setText("Total("+totalquantity+")"+"item");
        }


        int saved;
        int ttamt;

        if ((totalAmounts-paidAmount)>Integer.parseInt(MainActivity.maximumAmountTosetDelivery)){
            saved =totalAmounts-paidAmount;
            ttamt=paidAmount;
            deliveryPrice.setText("FREE");
        }
        else {
            saved =totalAmounts-paidAmount;
            ttamt=paidAmount+Integer.parseInt(MainActivity.setDelivertPrice);
            deliveryPrice.setText("₹"+MainActivity.setDelivertPrice);
        }

        totalAmount.setText("₹"+ttamt);
          savedAmount.setText("You Saved ₹"+saved+" on this order");
        int svd=totalAmounts-paidAmount;
        tv_total_discount_amount.setText("₹"+svd);

       // cancelOrderBtn=findViewById(R.id.cancel_btn);
        orderedIndicator=findViewById(R.id.ordered_indicator);
        packedIndicator=findViewById(R.id.packed_indicator);
        shippedIndicator=findViewById(R.id.shipping_indicator);
        deliveredIndicator=findViewById(R.id.delivered_indicator);
        O_P_progress=findViewById(R.id.ordered_packed_progress);
        P_S_progress=findViewById(R.id.packed_shipining_progress);
        S_D_progress=findViewById(R.id.shipping_delivered_progress);

        orderedTitle=findViewById(R.id.ordered_title);
        packedTitle=findViewById(R.id.packed_title);
        shippedTitle=findViewById(R.id.shipping_title);
        deliveredTitle=findViewById(R.id.delivered_title);

        orderedDate=findViewById(R.id.ordered_date);
        packedDate=findViewById(R.id.packed_date);
        shippedDate=findViewById(R.id.shipping_date);
        deliveredDate=findViewById(R.id.delevered_date);

        orderedBody=findViewById(R.id.ordered_body);
        packedBody=findViewById(R.id.packed_body);
        shippedBody=findViewById(R.id.shipping_body);
        deliveredBody=findViewById(R.id.delevered_body);

         //rateNowContainer=findViewById(R.id.rate_now_container);
          fullName=findViewById(R.id.fullName);
          address=findViewById(R.id.address);
          pincode=findViewById(R.id.pincode);

          fullName.setText(model.get(0).getFullName());
          address.setText(model.get(0).getAddress());
          pincode.setText(model.get(0).getPincode());

//        title.setText(model.getProductTitle());
//        if (!model.getDiscountedPrice().equals("")){
//            price.setText("₹"+model.getDiscountedPrice());
//        }else {
//            price.setText("₹"+model.getProductQuantity()*Long.parseLong(model.getProductPrice()));
//        }
//        quantity.setText("Qty "+String.valueOf(model.getProductQuantity()));
//        Glide.with(this).load(model.getProductImage()).into(productImage);

        simpleDateFormat=new SimpleDateFormat("EEE,dd MMM YYYY hh:mm aa");

        refreshStatus(model.get(0).getOrderStatus(),model.get(0).getOrderedDate(),model.get(0).getPackedDate(),model.get(0).getShippedDate(),model.get(0).getDeliveredDate(),model.get(0).getCancelledDate());


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                firebaseFirestore.collection("ORDERS").document(orderIdForRefresh).collection("OrderItems")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String state="";
                        Date orddat=new Date(),pkddat=new Date(),shpddat=new Date(),dlvrdate=new Date(),cnlddate=new Date();

                        for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){

                            state = documentSnapshot.get("Order Status").toString();
                            orddat = documentSnapshot.getDate("Order date");
                            pkddat = documentSnapshot.getDate("packed date");
                            shpddat = documentSnapshot.getDate("Shipped date");
                            dlvrdate = documentSnapshot.getDate("Delivered date");
                            cnlddate = documentSnapshot.getDate("Cancelled date");
                            break;
                        }
                        refreshStatus(state,orddat,pkddat,shpddat,dlvrdate,cnlddate);
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });


//        /////rating layout
//        rating=model.getRating();
//        setRating(rating);
//
//        for (int x=0;x<rateNowContainer.getChildCount();x++){
//            final int starPosition=x;
//            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    loadingDialog.show();
//                    setRating(starPosition);
//                    final DocumentReference documentReference= FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductId());
//                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
//                        @Nullable
//                        @Override
//                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
//
//                            DocumentSnapshot documentSnapshot=transaction.get(documentReference);
//
//                            if (rating!=0){
//                                Long increase=documentSnapshot.getLong(starPosition+1+"_star")+1;
//                                Long decrease=documentSnapshot.getLong(starPosition+1+"_star")-1;
//                                transaction.update(documentReference,starPosition+1+"_star",increase);
//                                transaction.update(documentReference,rating+1+"_star",decrease);
//                            }
//                            else {
//                                Long increase=documentSnapshot.getLong(starPosition+1+"_star")+1;
//                                transaction.update(documentReference,starPosition+1+"_star",increase);
//
//                            }
//
//                            return null;
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
//                        @Override
//                        public void onSuccess(Object o) {
//                            Map<String, Object> myRating = new HashMap<>();
//                            if (DBquaries.myRatedIds.contains(model.getProductId())) {
//                                myRating.put("rating_" + DBquaries.myRatedIds.indexOf(model.getProductId()), (long) starPosition + 1);
//
//                            } else {
//
//                                myRating.put("list_size", (long) DBquaries.myRatedIds.size() + 1);
//                                myRating.put("product_ID_" + DBquaries.myRatedIds.size(), model.getProductId());
//                                myRating.put("rating_" + DBquaries.myRatedIds.size(), (long) starPosition + 1);
//                            }
//
//                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
//                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//
//                                        DBquaries.myOrderItemModelList.get(position).setRating(starPosition );
//                                        if (DBquaries.myRatedIds.contains(model.getProductId())) {
//                                            DBquaries.myRating.set(DBquaries.myRatedIds.indexOf(model.getProductId()), Long.parseLong(String.valueOf(starPosition + 1)));
//                                        } else {
//                                            DBquaries.myRatedIds.add(model.getProductId());
//                                            DBquaries.myRating.add(Long.parseLong(String.valueOf(starPosition + 1)));
//                                        }
//                                    } else {
//                                        String error = task.getException().getMessage();
//                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
//                                    }
//                                    loadingDialog.dismiss();
//
//                                }
//                            });
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            loadingDialog.dismiss();
//                        }
//                    });
//                }
//            });
//        }
//        //////rating layout


//        if (model.isCancellationRequested()){
//            cancelOrderBtn.setVisibility(View.VISIBLE);
//            cancelOrderBtn.setEnabled(false);
//            cancelOrderBtn.setText("cancellation in progress");
//            cancelOrderBtn.setTextColor(getResources().getColor(R.color.red));
//            cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
//
//        }else {
//            if (model.getOrderStatus().equals("Ordered")){
//                cancelOrderBtn.setVisibility(View.INVISIBLE);
//                cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    cancelDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                         cancelDialog.dismiss();
//                        }
//                    });
//
//                    cancelDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            cancelDialog.dismiss();
//                            loadingDialog.show();
//                            Map<String,Object> map=new HashMap<>();
//                            map.put("Order Id",model.getOrderId());
//                            map.put("Product Id",model.getProductId());
//                            map.put("Order cancelled",false);
//                            FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document().set(map)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                            if (task.isSuccessful()){
//
//                                                FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderId()).collection("OrderItems").document(model.getProductId()).update("Cancellation requested",true)
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()){
//
//                                                                    model.setCancellationRequested(true);
//
//                                                                    cancelOrderBtn.setVisibility(View.VISIBLE);
//                                                                    cancelOrderBtn.setEnabled(false);
//                                                                    cancelOrderBtn.setText("cancellation in progress");
//                                                                    cancelOrderBtn.setTextColor(getResources().getColor(R.color.red));
//                                                                    cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
//
//                                                                }else {
//                                                                    String error=task.getException().getMessage();
//                                                                    Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
//
//                                                                }
//                                                                loadingDialog.dismiss();
//                                                            }
//                                                        });
//
//                                            }else {
//                                                loadingDialog.dismiss();
//                                                String error=task.getException().getMessage();
//                                                Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
//
//                                            }
//                                        }
//                                    });
//
//                        }
//                    });
//                        cancelDialog.show();
//
//                    }
//                });
//            }
//        }
//        fullName.setText(model.getFullName());
//        address.setText(model.getAddress());
//        pincode.setText(model.getPincode());
//
//        totalItems.setText("Price("+model.getProductQuantity()+" items)");
//         Long totalItemsPriceValue;
//
//        if (model.getDiscountedPrice().equals("")){
//            totalItemsPriceValue= model.getProductQuantity()*Long.valueOf(model.getCuttedPrice());
//            totalItemPrice.setText("₹"+totalItemsPriceValue);
//
//        }
//        else {
//            totalItemsPriceValue= model.getProductQuantity()*Long.valueOf(model.getDiscountedPrice());
//            totalItemPrice.setText("₹"+totalItemsPriceValue);
//        }
//
//        if (model.getDeliveryPrice().equals("FREE")){
//            deliveryPrice.setText("FREE");
//            totalAmount.setText(totalItemPrice.getText());
//        }
//        else {
//            totalAmount.setText("₹"+(totalItemsPriceValue+Long.valueOf(model.getDeliveryPrice())) );
//            deliveryPrice.setText("₹"+ Long.valueOf(model.getDeliveryPrice()));
//        }

//       if (!model.getCuttedPrice().equals("")){
//           if (!model.getDiscountedPrice().equals("")){
//               cutted_price_text.setText("₹"+ model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice())));
//               savedAmount.setText("You saved ₹"+(model.getProductQuantity()*(Long.valueOf( model.getCuttedPrice())-Long.valueOf(model.getDiscountedPrice())))+" on this order");
//               tv_total_discount_amount.setText("- ₹"+(model.getProductQuantity()*(Long.valueOf( model.getCuttedPrice())-Long.valueOf(model.getDiscountedPrice()))));
//           }else {
//              savedAmount.setText("You saved ₹"+(model.getProductQuantity()*(Long.valueOf( model.getCuttedPrice())-Long.valueOf(model.getProductPrice())))+" on this order");
//               tv_total_discount_amount.setText("- ₹"+(model.getProductQuantity()*(Long.valueOf( model.getCuttedPrice())-Long.valueOf(model.getProductPrice()))));
//               cutted_price_text.setText("₹"+ model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice())));
//
//           }
//       }else {
//           if (!model.getDiscountedPrice().equals("")){
//               savedAmount.setText("You saved Rs."+model.getProductQuantity()*(Long.valueOf( model.getProductPrice())-Long.valueOf(model.getDiscountedPrice()))+" on this order");
//           }else {
//               savedAmount.setText("You saved ₹0 on this order");
//           }
//       }
    }

    private void refreshStatus(String status, Date orderDates,Date packeDdates,Date shippedDates,Date deliveredDates,Date cancelledDates){

        switch (status){
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);


                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;

            case "Packed":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(packeDdates)));

                O_P_progress.setProgress(100);

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);


                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);


                break;

            case "Shipped":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(packeDdates)));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(shippedDates)));

                O_P_progress.setProgress(100);

                P_S_progress.setProgress(100);
                S_D_progress.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;

            case "Out for Delivery":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(packeDdates)));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(shippedDates)));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(deliveredDates)));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);
                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("your order is out for delivery");

                break;

            case "Delivered":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(packeDdates)));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(shippedDates)));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(deliveredDates)));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                break;

            case "Cancelled":

                if (packeDdates.after(orderDates)){

                    if (shippedDates.after(packeDdates)){

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(packeDdates)));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(shippedDates)));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(deliveredDates)));

                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been cancelled");

                    }else {

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(packeDdates)));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(cancelledDates)));

                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been cancelled");

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setVisibility(View.GONE);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);


                    }
                }
                else {

                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(orderDates)));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(cancelledDates)));

                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled");
                    O_P_progress.setProgress(100);

                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);

                    shippedIndicator.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);

                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void setRating(int starPosition) {
//        for (int x=0;x<rateNowContainer.getChildCount();x++){
//            ImageView starBtn=(ImageView)rateNowContainer.getChildAt(x);
//            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
//            if (x<=starPosition){
//                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
//            }
//        }
//    }
}


