package com.khaolitti.khao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyRewardAdapter extends RecyclerView.Adapter<MyRewardAdapter.Viewholder> {

    private List<RewardModel> rewardModelList;
    private Boolean useMiniLayout=false;
    private RecyclerView coupansRecylerView;
    private  LinearLayout selectedCoupans;
    private String productOriginalPrice;
    private TextView selectedcoupanTitle;
    private TextView selectedcoupanExpirayDate;
    private TextView selectedcoupanBody;
    private TextView discountedPrice;
    private  int cartItemPosition=-1;
    private List<CartItemModel> cartItemModelList;

    public MyRewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout){
        this.useMiniLayout=useMiniLayout;
        this.rewardModelList=rewardModelList;
    }

    public MyRewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView coupansRecylerView, LinearLayout selectedCoupans, String productOriginalPrice, TextView coupanTitle, TextView coupanExpirayDate, TextView coupanBody,TextView discountedPrice) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout=useMiniLayout;
        this.coupansRecylerView=coupansRecylerView;
        this.selectedCoupans=selectedCoupans;
        this.productOriginalPrice=productOriginalPrice;
        this.selectedcoupanTitle=coupanTitle;
        this.selectedcoupanExpirayDate=coupanExpirayDate;
        this.selectedcoupanBody=coupanBody;
        this.discountedPrice=discountedPrice;
    }

    public MyRewardAdapter(int cartItemPosition,List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView coupansRecylerView, LinearLayout selectedCoupans, String productOriginalPrice, TextView coupanTitle, TextView coupanExpirayDate, TextView coupanBody,TextView discountedPrice,List<CartItemModel> cartItemModelList) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout=useMiniLayout;
        this.coupansRecylerView=coupansRecylerView;
        this.selectedCoupans=selectedCoupans;
        this.productOriginalPrice=productOriginalPrice;
        this.selectedcoupanTitle=coupanTitle;
        this.selectedcoupanExpirayDate=coupanExpirayDate;
        this.selectedcoupanBody=coupanBody;
        this.discountedPrice=discountedPrice;
        this.cartItemPosition=cartItemPosition;
        this.cartItemModelList=cartItemModelList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view;
        if (useMiniLayout){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_rewards_item_layout, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, false);
        }
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String coupanId=rewardModelList.get(position).getCoupanId();
        String type=rewardModelList.get(position).getType();
        Date validity=rewardModelList.get(position).getTimestamp();
        String body=rewardModelList.get(position).getCoupanBody();
        String lowerLimit=rewardModelList.get(position).getLowerLimit();
        String upperLimit=rewardModelList.get(position).getUpperLimit();
        String dicsORamt=rewardModelList.get(position).getDicsORamt();
        Boolean alreadyUsed=rewardModelList.get(position).getAlreadyUsed();

        holder.setData(coupanId,type,validity,body,upperLimit,lowerLimit,dicsORamt,alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView coupanTitle;
        private TextView coupanexpirydate;
        private TextView coupanBody;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            coupanTitle=itemView.findViewById(R.id.coupan_title);
            coupanexpirydate=itemView.findViewById(R.id.coupan_validity);
            coupanBody=itemView.findViewById(R.id.coupan_body);
        }
        private void setData(final String coupanId,final String type, final Date validity, final String body, final String upperLimit, final String lowerLimit, final String discORamt, final boolean alreadyUsed) {

            if (type.equals("Discount")) {
                coupanTitle.setText(type);
            } else {
                coupanTitle.setText("FLAT Rs." + discORamt + " Off");
            }

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");

            if (alreadyUsed){

                coupanexpirydate.setText("Already Used");
                coupanexpirydate.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                //coupanBody.setTextColor(); set coupan body background

            }
            else {
                coupanexpirydate.setTextColor(itemView.getContext().getResources().getColor(R.color.coupantext));
                coupanexpirydate.setText("Valid till " + simpleDateFormat.format(validity));
            }

            coupanBody.setText(body);


            if (useMiniLayout) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!alreadyUsed) {
                            ////notin
                            selectedcoupanTitle.setText(type);
                            selectedcoupanBody.setText(body);
                            selectedcoupanExpirayDate.setText(simpleDateFormat.format(validity));

                            if (Long.valueOf(productOriginalPrice) > Long.valueOf(lowerLimit) && (Long.valueOf(productOriginalPrice) < Long.valueOf(upperLimit))) {

                                if (type.equals("Discount")) {
                                    Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(discORamt) / 100;
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "/-");
                                } else {
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discORamt) + "/-"));
                                }
                                if (cartItemPosition!=-1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCoupanId(coupanId);

                                }
                            } else {
                                if (cartItemPosition!=-1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCoupanId(null);
                                }
                                discountedPrice.setText("Invalid Coupan");
                                Toast.makeText(itemView.getContext(), "Sorry! coupan is not available for this product", Toast.LENGTH_SHORT).show();

                            }

                            if (coupansRecylerView.getVisibility() == View.GONE) {
                                coupansRecylerView.setVisibility(View.VISIBLE);
                                selectedCoupans.setVisibility(View.GONE);
                            } else {
                                coupansRecylerView.setVisibility(View.GONE);
                                selectedCoupans.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        }
        }
    }
}
