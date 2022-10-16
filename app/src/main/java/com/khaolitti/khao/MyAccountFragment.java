package com.khaolitti.khao;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }
    private Button viewAllAddressButton;
    public static final int MANAGE_ADDRESS=1;
    private Button signOutBtn;
    private FirebaseAuth firebaseAuth;
    private CircleImageView profileView;
    private TextView name;
    private TextView phone;
    public  Dialog loadingDialog;
    private FloatingActionButton settingButton;

   ////address
    private TextView addressname,address,pincode;
    ////address

   ///order status
    private LinearLayout layoutContainer;
    private CircleImageView currentOrderImage;
    private TextView tvCurrentOrderStatus;
    private ImageView orderIndicator,packedIndicator,shippedindicator,deliveredIndicator;
    private ProgressBar O_P_progress,P_S_progress,S_D_progress;
    ///order status

    ////recent order
    private TextView yourRecentOrderTitle;
    private LinearLayout yourRecentOrderContainer;

    ////recent orer

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_my_account, container, false);

        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();

        settingButton=view.findViewById(R.id.setting_button);

        viewAllAddressButton=view.findViewById(R.id.view_all_addresses_btn);
       firebaseAuth=FirebaseAuth.getInstance();
       signOutBtn=view.findViewById(R.id.sign_out_btn);

      profileView=view.findViewById(R.id.profile_image);
      name=view.findViewById(R.id.user_name);
      phone=view.findViewById(R.id.user_phone);

      ////address
        addressname=view.findViewById(R.id.address_fullname);
        address=view.findViewById(R.id.address);
        pincode=view.findViewById(R.id.address_pincode);
        ////address


        ///order status
       layoutContainer=view.findViewById(R.id.layout_container);
       currentOrderImage=view.findViewById(R.id.current_order_image);
       tvCurrentOrderStatus=view.findViewById(R.id.tv_current_order_status);
        orderIndicator=view.findViewById(R.id.ordered_indicator);
        packedIndicator=view.findViewById(R.id.packed_indicator);
        shippedindicator=view.findViewById(R.id.shipped_indicator);
        deliveredIndicator=view.findViewById(R.id.delivered_indicator);
        O_P_progress=view.findViewById(R.id.order_packed_progress);
        P_S_progress=view.findViewById(R.id.packed_shipped_progress);
        S_D_progress=view.findViewById(R.id.shipped_delivered_progress);
        ///order status

        ////recent order
        yourRecentOrderTitle=view.findViewById(R.id.your_recent_orders_title);
        yourRecentOrderContainer=view.findViewById(R.id.recent_orders_container);
        ////recent order

      if (!DBquaries.name.equals("")){
      name.setText(DBquaries.name);
      }
      if (!DBquaries.phone.equals("")){
          phone.setText(DBquaries.phone);
      }

      if (!DBquaries.profile.equals("")){

          Glide.with(getContext()).load(DBquaries.profile).apply(new RequestOptions().placeholder(R.drawable.profile)).into(profileView);
      }

        ///order status
        layoutContainer.getChildAt(1).setVisibility(View.GONE);

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (MyOrderItemModel orderItemModel : DBquaries.myOrderItemModelList){
                    if (!orderItemModel.isCancellationRequested() ){
                        if (!orderItemModel.getOrderStatus().equals("Delivered") && !orderItemModel.getOrderStatus().equals("Cancelled")){
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);

                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera)).into(currentOrderImage);
                            tvCurrentOrderStatus.setText(orderItemModel.getOrderStatus());
                            switch (orderItemModel.getOrderStatus()){

                                case "Ordered":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    break;
                                case "Packed":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    O_P_progress.setProgress(100);
                                    break;
                                case "Shipped":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    shippedindicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    O_P_progress.setProgress(100);
                                    P_S_progress.setProgress(100);
                                    break;
                                case "Out for Delivery":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    shippedindicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                    shippedindicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));

                                    O_P_progress.setProgress(100);
                                    P_S_progress.setProgress(100);
                                    S_D_progress.setProgress(100);

                                    break;
                            }
                        }
                    }
                }

                int i=0;
                for (MyOrderItemModel myOrderItemModel:DBquaries.myOrderItemModelList) {

                    if (i<4) {
                        if (myOrderItemModel.getOrderStatus().equals("Delivered")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera)).into((CircleImageView) yourRecentOrderContainer.getChildAt(i));
                            i++;
                        }
                    }
                    else {
                        break;
                    }
                }
                if (i==0){
                    yourRecentOrderTitle.setText("No recent orders");
                }
                if (i<3){
                    for (int x=i;x<4;x++){
                        yourRecentOrderContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        loadingDialog.setOnDismissListener(null);
                        if (DBquaries.addressesModelList.size()==0){
                            addressname.setText("no address");
                            address.setText("_");
                            pincode.setText("_");
                        }
                        else {
                          setAddress();
                        }
                    }
                });
                DBquaries.loadAddresses(getContext(),loadingDialog,false);
            }
        });

         DBquaries.fetchOrder(getContext(),null,loadingDialog);
        ///order status

      viewAllAddressButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent myAddressesIntent=new Intent(getContext(),MyAddressActivity.class);
              myAddressesIntent.putExtra("Mode",MANAGE_ADDRESS);
              startActivity(myAddressesIntent);
          }
      });

      signOutBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              firebaseAuth.signOut();
              Intent phoneIntent=new Intent(getActivity(),PhoneActivity.class);
              DBquaries.clearData();
              startActivity(phoneIntent);
              getActivity().finish();
          }
      });

      settingButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent updateUserInfo=new Intent(getContext(),UpdateUserInfoActivity.class);
              startActivity(updateUserInfo);
          }
      });

    return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!loadingDialog.isShowing()){
            if (DBquaries.addressesModelList.size()==0){
                addressname.setText("No Address");
                address.setText("");
                pincode.setText("");
            }
            else {
                setAddress();
            }
        }
    }

    private void setAddress() {

        String nameText,mobileno;
        nameText=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getName();
        mobileno=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getMobileNo();
        if (DBquaries.addressesModelList.get(DBquaries.selectedAddress).getAlternateMoNo().equals("")){
            addressname.setText(nameText+" - "+mobileno);
        }
        else {
            addressname.setText(nameText+" - "+mobileno+"/"+DBquaries.addressesModelList.get(DBquaries.selectedAddress).getAlternateMoNo());
        }
        String  flatNo=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getFlatno();
        String  locality=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getLocality();
        String  landMark=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getLandMark();
        String  city=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getCity();
        String  state=DBquaries.addressesModelList.get(DBquaries.selectedAddress).getState();
        if (landMark.equals("")){
            address.setText(flatNo+" "+ locality+","+city+","+state);

        }
        else {
            address.setText(flatNo+" "+ locality+" "+landMark+","+city+","+state);

        }
        pincode.setText(DBquaries.addressesModelList.get(DBquaries.selectedAddress).getPincode());

    }

}
