package com.khaolitti.khao;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCartFragment extends Fragment {


    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    private Button continuebtn;

    private Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    public static TextView totalAmount1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_cart, container, false);

        ////dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();
        ////dialog

        cartItemsRecyclerView=view.findViewById(R.id.cart_item_recyclerview);
        continuebtn=view.findViewById(R.id.cart_continue_btn);
        totalAmount1=view.findViewById(R.id.total_cart_amount_fragment);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(linearLayoutManager);

        cartAdapter=new CartAdapter(DBquaries.cartItemModelList,totalAmount1,true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.cartItemModelList=new ArrayList<>();

                OrderConfirmationActivity.fromcart=true;

                for (int x=0;x<DBquaries.cartItemModelList.size();x++){
                    CartItemModel cartItemModel=DBquaries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()){
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                loadingDialog.show();
                if (DBquaries.addressesModelList.size()==0) {
                    DBquaries.loadAddresses(getContext(), loadingDialog,true);
                }
                else {
                     loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        cartAdapter.notifyDataSetChanged();
//        if (DBquaries.rewardModelList.size()==0){
//            loadingDialog.show();
//            DBquaries.loadRewads(getContext(),loadingDialog,false);
//        }
        if (DBquaries.cartItemModelList.size()==0){
            DBquaries.cartList.clear();
            DBquaries.loadCartList(getContext(),loadingDialog,true,new TextView(getContext()),totalAmount1);

        }
        else {
            if (DBquaries.cartItemModelList.get(DBquaries.cartItemModelList.size()-1).getType()==CartItemModel.TOTAL_AMOUNT){
                LinearLayout parent=(LinearLayout) totalAmount1.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        for (CartItemModel cartItemModel:DBquaries.cartItemModelList){
            if (!TextUtils.isEmpty(cartItemModel.getSelectedCoupanId())){

                for (RewardModel rewardModel:DBquaries.rewardModelList){
                    if (rewardModel.getCoupanId().equals(cartItemModel.getSelectedCoupanId()));{
                        rewardModel.setAlreadyUsed(false);
                    }
                }
                cartItemModel.setSelectedCoupanId("null");
                if (MyRewardsFragment.myRewardAdapter != null) {
                    MyRewardsFragment.myRewardAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
