package com.khaolitti.khao;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter{

    private List<CartItemModel> cartItemModelList;
    private int lastPosition=-1;
    private TextView cartTotalAmount;
    private boolean showDeletebtn;
    public static int toPayAmount=0;

    public CartAdapter(List<CartItemModel> cartItemModelList,TextView cartTotalAmount,boolean showDeletebtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount=cartTotalAmount;
        this.showDeletebtn=showDeletebtn;
    }

    @Override
    public int getItemViewType(int position) {
       switch (cartItemModelList.get(position).getType()){

           case 0:
               return CartItemModel.CART_ITEM;
           case 1:
               return CartItemModel.TOTAL_AMOUNT;
               default:
                   return -1;
       }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case CartItemModel.CART_ITEM:

              View cartItemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
              return new CartItemViewHolder(cartItemView);
                case CartItemModel.TOTAL_AMOUNT:

                    View cartTotalView= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout,parent,false);
                    return new CartTotalAmountViewHolder(cartTotalView);
                    default:return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (cartItemModelList.size()>1) {
            int settingtotalItemsPrice=0;
            int ttamt;
            for (int x = 0; x < cartItemModelList.size(); x++) {
                if (cartItemModelList.get(x).getType()==CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){

                    int quantity=Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));

                    if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupanId())){
                        settingtotalItemsPrice=settingtotalItemsPrice+ Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                    }else {
                        settingtotalItemsPrice=settingtotalItemsPrice+ Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                    }
                }
            }
            if (settingtotalItemsPrice>150){
              ttamt=settingtotalItemsPrice;
            }
            else {
                ttamt=settingtotalItemsPrice+25;
            }
            cartTotalAmount.setText("₹"+ttamt);
        }
        switch (cartItemModelList.get(position).getType())
        {
            case CartItemModel.CART_ITEM:
                String productID=cartItemModelList.get(position).getProductID();
                String resource=cartItemModelList.get(position).getProductImage();
                String title=cartItemModelList.get(position).getProductTitle();
                Long freeCoupan=cartItemModelList.get(position).getFreeCoupans();
                String productPrice=cartItemModelList.get(position).getProductPrice();
                String cuttedPrice=cartItemModelList.get(position).getCuttedPrice();
                Long offerApplied=cartItemModelList.get(position).getOffersApplied();
                boolean inStock=cartItemModelList.get(position).isInStock();
               Long productQuantity=cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity=cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError=cartItemModelList.get(position).isQtyError();
               long stockQty=cartItemModelList.get(position).getStockQuantity();
                boolean cod=cartItemModelList.get(position).isCOD();
                List<String> qtyIds=cartItemModelList.get(position).getQtyId();
                ((CartItemViewHolder)holder).setItemDetails(productID,resource,title,freeCoupan,productPrice,cuttedPrice,offerApplied,position,inStock,String.valueOf(productQuantity),maxQuantity,qtyError,qtyIds,stockQty,cod);
                break;
            case CartItemModel.TOTAL_AMOUNT:

                int totalItems=0;
                int totalItemsPrice=0;
                String deliveryPrice="";
                int totalAmount;
                int savedAmount=0;
                for (int x=0;x<cartItemModelList.size();x++){
                    if (cartItemModelList.get(x).getType()==CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){

                        int quantity=Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                      totalItems=totalItems+quantity;

                      if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupanId())){
                          totalItemsPrice=totalItemsPrice+ Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                      }else {
                          totalItemsPrice=totalItemsPrice+ Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                      }
                      if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                          savedAmount=savedAmount+(Integer.parseInt(cartItemModelList.get(x).getCuttedPrice())-Integer.parseInt(cartItemModelList.get(x).getProductPrice()))*quantity;

                          if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupanId())){
                              savedAmount=savedAmount+(Integer.parseInt(cartItemModelList.get(x).getProductPrice())-Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))*quantity;
                          }
                      } else {
                          if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupanId())){
                              savedAmount=savedAmount+(Integer.parseInt(cartItemModelList.get(x).getProductPrice())-Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))*quantity;
                          }

                      }
                    }
                }

              if (totalItemsPrice>Integer.parseInt(MainActivity.maximumAmountTosetDelivery)){
                    deliveryPrice="FREE";
                    totalAmount=totalItemsPrice;
                }
                else {
                   deliveryPrice=MainActivity.setDelivertPrice;
                   totalAmount=totalItemsPrice+Integer.parseInt(MainActivity.setDelivertPrice);
                }
                cartItemModelList.get(0).setTotalItems(totalItems);
                cartItemModelList.get(0).setTotalItemsPrice( totalItemsPrice);
                cartItemModelList.get(0).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(0).setTotalAmount( totalAmount);
                cartItemModelList.get(0).setSavedAmoun(savedAmount);


                ((CartTotalAmountViewHolder)holder).setTotalAmount(totalItems,totalItemsPrice,deliveryPrice,totalAmount,savedAmount);
                break;
            default: return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition=position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView productImage;
        private ImageView freeCoupanIcon;
        private TextView productTitle;
        private TextView freeCoupan;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView offerApplied;
        private TextView coupanApplied;
        private TextView productQuantity;
        private LinearLayout coupsnRedeemptionLayout;

        private TextView coupanRedemptionBody;
        private LinearLayout deleteButton;
        private Button reedemBtn;

        ////coupandialog
        private TextView coupanTitle;
        private TextView coupanExpirayDate;
        private TextView coupanBody;
        private  RecyclerView coupansRecylerView;
        private  LinearLayout selectedCoupans;
        private TextView discountedPrice;
        private TextView originalPrice;
        private Button removeCoupanBtn;
        private Button applyCoupanBtn;
        private LinearLayout applyOrRemoveBtnContainer;
        private TextView footerText;
        private String productOriginalPrice;


        private ImageView codIndicator;
        ////coupandialog


        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage=itemView.findViewById(R.id.productt_image);
            productTitle=itemView.findViewById(R.id.product_titletv);
            freeCoupan=itemView.findViewById(R.id.tv_free_coupan);
            freeCoupanIcon=itemView.findViewById(R.id.free_coupan_icon);
            productPrice=itemView.findViewById(R.id.product_price);
            cuttedPrice=itemView.findViewById(R.id.cutted_price);
            offerApplied=itemView.findViewById(R.id.offers_applied);
            coupanApplied=itemView.findViewById(R.id.coupan_applied);
            productQuantity=itemView.findViewById(R.id.product_quantity);
            coupsnRedeemptionLayout=itemView.findViewById(R.id.coupan_reedemption_layout);
            coupanRedemptionBody=itemView.findViewById(R.id.tv_coupan_reedemption);
            reedemBtn=itemView.findViewById(R.id.coupan_reedemption_button);
            deleteButton=itemView.findViewById(R.id.remove_item_btn);
            codIndicator=itemView.findViewById(R.id.cod_indicator);

        }
        private void setItemDetails(final String productID, String resource, String title, Long freeCoupanNo, final String productPriceText, final String cuttedPriceText, Long offersAppliedNo, final int position, boolean inStock, final String quantity, final Long maxQuantity, boolean qtyError, final List<String> qtyIds, final Long stockQuantity, boolean cod) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.loading)).into(productImage);
            productTitle.setText(title);

            final Dialog checkCoupanPriceDialog = new Dialog(itemView.getContext());
            checkCoupanPriceDialog.setContentView(R.layout.coupan_reedem_dialog);
            checkCoupanPriceDialog.setCancelable(false);
            checkCoupanPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (cod){
                codIndicator.setVisibility(View.INVISIBLE);
            }else {
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if (inStock){
                if (freeCoupanNo > 0) {
                    freeCoupanIcon.setVisibility(View.VISIBLE);
                    freeCoupan.setVisibility(View.VISIBLE);
                    if (freeCoupanNo == 1)
                        freeCoupan.setText( freeCoupanNo + " Coupan Applied");
                    else freeCoupan.setText( freeCoupanNo + " Coupan Applied");
                } else {
                    freeCoupanIcon.setVisibility(View.INVISIBLE);
                    freeCoupan.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("₹"+Long.parseLong(productPriceText) * Long.parseLong(quantity));
                cuttedPrice.setText("₹"+Long.parseLong(cuttedPriceText) * Long.parseLong(quantity));
                productPrice.setTextColor(Color.parseColor("#000000"));
                coupsnRedeemptionLayout.setVisibility(View.GONE);

                ///////coupan dialog
                ImageView toggleRecyclerView = checkCoupanPriceDialog.findViewById(R.id.toggle_recyclerview);

                coupansRecylerView = checkCoupanPriceDialog.findViewById(R.id.coupans_recyclerview);
                selectedCoupans = checkCoupanPriceDialog.findViewById(R.id.selected_coupan);

                coupanTitle = checkCoupanPriceDialog.findViewById(R.id.coupan_title);
                coupanExpirayDate = checkCoupanPriceDialog.findViewById(R.id.coupan_validity);
                coupanBody = checkCoupanPriceDialog.findViewById(R.id.coupan_body);
                removeCoupanBtn=checkCoupanPriceDialog.findViewById(R.id.remove_btn);
                applyCoupanBtn=checkCoupanPriceDialog.findViewById(R.id.apply_btn);
                footerText=checkCoupanPriceDialog.findViewById(R.id.footer_text);
                applyOrRemoveBtnContainer=checkCoupanPriceDialog.findViewById(R.id.apply_or_remove_btn_container);

                footerText.setVisibility(View.GONE);
                applyOrRemoveBtnContainer.setVisibility(View.VISIBLE);

                originalPrice = checkCoupanPriceDialog.findViewById(R.id.original_price);
                discountedPrice = checkCoupanPriceDialog.findViewById(R.id.discounted_price);


                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                coupansRecylerView.setLayoutManager(layoutManager);

                productOriginalPrice=productPriceText;
                originalPrice.setText(productPrice.getText());
                MyRewardAdapter myRewardAdapter = new MyRewardAdapter(position,DBquaries.rewardModelList, true,coupansRecylerView,selectedCoupans,productOriginalPrice,coupanTitle,coupanExpirayDate,coupanBody,discountedPrice,cartItemModelList);
                coupansRecylerView.setAdapter(myRewardAdapter);
                myRewardAdapter.notifyDataSetChanged();

                applyCoupanBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupanId())) {

                            for (RewardModel rewardModel : DBquaries.rewardModelList) {
                                if (rewardModel.getCoupanId().equals(cartItemModelList.get(position).getSelectedCoupanId()));
                                {
                                    rewardModel.setAlreadyUsed(true);

                                    coupsnRedeemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                                    coupanRedemptionBody.setText(rewardModel.getCoupanBody());
                                    reedemBtn.setText("coupan");
                                }
                            }
                            coupanApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length()-2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountedAmount=String.valueOf(Long.valueOf(productPriceText)-Long.valueOf(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length()-2)));
                            coupanApplied.setText("coupan applied -Rs." + offerDiscountedAmount+"/-");
                            notifyItemChanged(cartItemModelList.size()-1);

                            checkCoupanPriceDialog.dismiss();
                        }
                    }
                });

                removeCoupanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel:DBquaries.rewardModelList){
                            if (rewardModel.getCoupanId().equals(cartItemModelList.get(position).getSelectedCoupanId()));{
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        coupanTitle.setText("Coupan");
                        coupanExpirayDate.setText("vaidity");
                        coupanBody.setText("please select a coupan...");
                        coupanApplied.setVisibility(View.INVISIBLE);
                        coupsnRedeemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupanred));
                        coupanRedemptionBody.setText("Apply Coupan");
                        reedemBtn.setText("Reedem");
                        productPrice.setText("₹"+productPriceText);
                        cartItemModelList.get(position).setSelectedCoupanId(null);
                        notifyItemChanged(cartItemModelList.size()-1);

                        checkCoupanPriceDialog.dismiss();
                    }
                });

                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogRecyclerView();

                    }
                });


                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupanId())) {

                    for (RewardModel rewardModel : DBquaries.rewardModelList) {
                        if (rewardModel.getCoupanId().equals(cartItemModelList.get(position).getSelectedCoupanId()));
                        {
                            coupsnRedeemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                            coupanRedemptionBody.setText(rewardModel.getCoupanBody());
                            reedemBtn.setText("coupan");

                            coupanBody.setText(rewardModel.getCoupanBody());
                            if (rewardModel.getType().equals("Discount")) {
                                coupanTitle.setText(rewardModel.getType());
                            } else {
                                coupanTitle.setText("FLAT ₹" + rewardModel.getDicsORamt() + " Off");
                            }
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
                            coupanExpirayDate.setText("Valid till " + simpleDateFormat.format(rewardModel.getTimestamp()));
                        }
                    }
                    discountedPrice.setText("₹"+cartItemModelList.get(position).getDiscountedPrice());

                    coupanApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("₹"+cartItemModelList.get(position).getDiscountedPrice());
                    String offerDiscountedAmount=String.valueOf(Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    coupanApplied.setText("coupan applied -Rs." + offerDiscountedAmount);
                }
                else {
                    coupanApplied.setVisibility(View.INVISIBLE);
                    coupsnRedeemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupanred));
                    coupanRedemptionBody.setText("Apply Coupan");
                    reedemBtn.setText("Reedem");
                }
                //////coupan dialog

                productQuantity.setText("Qty: "+quantity);

                if (!showDeletebtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));

                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    }
                }
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog=new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantityNo=quantityDialog.findViewById(R.id.quantity_number);
                        Button cancelButton=quantityDialog.findViewById(R.id.cancel_button);
                        Button okButton=quantityDialog.findViewById(R.id.ok_button);
                        quantityNo.setHint("Enter");

                         cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0) {

                                        if (itemView.getContext() instanceof MainActivity){

                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));

                                        }else {
                                            if (OrderConfirmationActivity.fromcart){

                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                            else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty: " + quantityNo.getText());

                                        productPrice.setText("₹"+Long.parseLong(productPriceText.toString())*Long.parseLong(quantityNo.getText().toString()));

                                        cuttedPrice.setText("₹"+Long.parseLong(cuttedPriceText.toString())*Long.parseLong(quantityNo.getText().toString()));

                                        notifyItemChanged(cartItemModelList.size()-1);
                                        notifyDataSetChanged();

                                        if (!showDeletebtn) {
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQuantity = Integer.parseInt(quantity);
                                            final int finalQuantity = Integer.parseInt(quantityNo.getText().toString());
                                            final FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

                                            if (finalQuantity > initialQuantity){
                                                for (int y = 0; y < finalQuantity-initialQuantity; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timeStamp = new HashMap<>();
                                                    timeStamp.put("time", FieldValue.serverTimestamp());

                                                    final int finalY = y;

                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(quantityDocumentName).set(timeStamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.add(quantityDocumentName);
                                                                    if (finalY + 1 ==finalQuantity-initialQuantity) {

                                                                        firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(stockQuantity).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {

                                                                                            List<String> serverQuantity = new ArrayList<>();

                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }
                                                                                            long availableQty = 0;

                                                                                            for (String qtyId : qtyIds) {

                                                                                                if (!serverQuantity.contains(qtyId)) {

                                                                                                        DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                        DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                        Toast.makeText(itemView.getContext(), " SORRY!!! All products may not be available in require quantity....", Toast.LENGTH_SHORT).show();


                                                                                                }else {
                                                                                                    availableQty++;
                                                                                                }
                                                                                            }
                                                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();

                                                                                        } else {

                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }

                                                                }
                                                            });

                                                }
                                        }
                                            else if (initialQuantity>finalQuantity){

                                                for (int x=0;x<initialQuantity-finalQuantity;x++) {
                                                  final String qtyId=  qtyIds.get(qtyIds.size() -1-x);

                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(qtyId).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                 qtyIds.remove(qtyId);
                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    if (finalX+1 ==initialQuantity-finalQuantity){
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });

                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max quantity" + maxQuantity, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                            quantityDialog.dismiss();


                            }
                        });
                        quantityDialog.show();

                    }
                });
                if (offersAppliedNo > 0) {
                    offerApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmount=String.valueOf(Long.valueOf(cuttedPriceText)-Long.valueOf(productPriceText));
                    offerApplied.setVisibility(View.INVISIBLE);
                 //   offerApplied.setText("offer applied - Rs."+offerDiscountedAmount+"/-");
                } else {
                    offerApplied.setVisibility(View.INVISIBLE);
                }
            }
            else {
                productPrice.setText("out of stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                cuttedPrice.setText("");
                coupsnRedeemptionLayout.setVisibility(View.GONE);
                productQuantity.setVisibility(View.INVISIBLE);
                freeCoupan.setVisibility(View.INVISIBLE);
                coupanApplied.setVisibility(View.GONE);
                offerApplied.setVisibility(View.GONE);
                freeCoupanIcon.setVisibility(View.INVISIBLE);
            }
            reedemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RewardModel rewardModel:DBquaries.rewardModelList){
                        if (rewardModel.getCoupanId().equals(cartItemModelList.get(position).getSelectedCoupanId()));{
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    checkCoupanPriceDialog.show();
                }
            });
            if (showDeletebtn){
                deleteButton.setVisibility(View.VISIBLE);
            }
            else {
                deleteButton.setVisibility(View.INVISIBLE);
            }
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupanId())){

                        for (RewardModel rewardModel:DBquaries.rewardModelList){
                            if (rewardModel.getCoupanId().equals(cartItemModelList.get(position).getSelectedCoupanId()));{
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }
                    if (!ProductDetailsActivity.runningcart_query){
                        ProductDetailsActivity.runningcart_query=true;
                        DBquaries.removeFromCart(position,itemView.getContext(),cartTotalAmount);
                    }
                }
            });
        }
        private void showDialogRecyclerView() {
            if (coupansRecylerView.getVisibility() == View.GONE) {
                coupansRecylerView.setVisibility(View.VISIBLE);
                selectedCoupans.setVisibility(View.GONE);
            } else {
                coupansRecylerView.setVisibility(View.GONE);
                selectedCoupans.setVisibility(View.VISIBLE);
            }
        }
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder{
        private TextView totalItems;
    private TextView totalItemPrice;
    private TextView deliveryPrice;
    private TextView totalAmount;
    private TextView savedAmount;
    private TextView totalDiscount,totalDiscountamount;

    public CartTotalAmountViewHolder(@NonNull View itemView) {
        super(itemView);

        totalItems=itemView.findViewById(R.id.total_items);
        totalItemPrice=itemView.findViewById(R.id.total_items_price);
        deliveryPrice=itemView.findViewById(R.id.delivery_price);
        totalAmount=itemView.findViewById(R.id.total_price);
        savedAmount=itemView.findViewById(R.id.saved_amount);

        totalDiscount=itemView.findViewById(R.id.tv_total_discount);
        totalDiscountamount=itemView.findViewById(R.id.tv_total_discount_amount);

    }
    private void setTotalAmount(int totalItemText,int totalItemPriceText,String deliveryPriceText,int totalAmountText,int savedAmountText){
        totalItems.setText("Total Price ("+totalItemText+" Items)");
        int total =totalItemPriceText+savedAmountText;
        totalItemPrice.setText("₹"+total);
         int saved=savedAmountText;
      totalDiscountamount.setText("-₹"+saved);
       totalDiscount.setText("Discount (Coupon KHAO 50 Applied)");
         if (deliveryPriceText.equals("FREE")){
        deliveryPrice.setText("FREE");
        }
        else {
            deliveryPrice.setText("₹"+deliveryPriceText);
        }
        totalAmount.setText("₹"+totalAmountText);
        cartTotalAmount.setText("₹"+totalAmountText);
        toPayAmount=totalAmountText;

        if (MyCartFragment.totalAmount1!=null) {
            MyCartFragment.totalAmount1.setText("₹" + totalAmountText);
        }
        savedAmount.setText("You saved ₹"+savedAmountText+" on this order");

        LinearLayout parent=(LinearLayout) cartTotalAmount.getParent().getParent();
        if (totalItemPriceText==0){
            if (OrderConfirmationActivity.fromcart) {
                DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                cartItemModelList.remove(cartItemModelList.size() - 1);
            }
            if (showDeletebtn){
               cartItemModelList.remove(cartItemModelList.size() - 1);
            }
            parent.setVisibility(View.GONE);
        }else {
            parent.setVisibility(View.VISIBLE);
        }
    }
}

}
