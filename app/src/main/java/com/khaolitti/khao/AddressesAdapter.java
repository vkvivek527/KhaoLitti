package com.khaolitti.khao;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.khaolitti.khao.DeliveryActivity.SELECT_ADDRESS;
import static com.khaolitti.khao.MyAccountFragment.MANAGE_ADDRESS;
import static com.khaolitti.khao.MyAddressActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.Viewholder> {

    private List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectePosition;
    private boolean refresh=false;
    private Dialog loadingDialog;

    public AddressesAdapter(List<AddressesModel> addressesModelList, int MODE,  Dialog loadingDialog) {
        this.addressesModelList = addressesModelList;
        this.MODE=MODE;
        preSelectePosition=DBquaries.selectedAddress;
            this.loadingDialog=loadingDialog;
    }

    @NonNull
    @Override
    public AddressesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.Viewholder holder, int position) {

         String city=addressesModelList.get(position).getCity();
        String locality=addressesModelList.get(position).getLocality();
        String flatno=addressesModelList.get(position).getFlatno();
        String pincode=addressesModelList.get(position).getPincode();
        String landMark=addressesModelList.get(position).getLandMark();
        String name=addressesModelList.get(position).getName();
        String mobileNo=addressesModelList.get(position).getMobileNo();
        String alternateMoNo=addressesModelList.get(position).getAlternateMoNo();
        String state=addressesModelList.get(position).getState();
        Boolean selected=addressesModelList.get(position).getSelected();

        holder.setData(name,city,pincode,selected,position,mobileNo,alternateMoNo,flatno,locality,state,landMark);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        private TextView fullName;
        private TextView address;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout optionContainer;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            fullName=itemView.findViewById(R.id.name);
            address=itemView.findViewById(R.id.address);
            pincode=itemView.findViewById(R.id.pincode);
            icon=itemView.findViewById(R.id.icon_view);
            optionContainer=itemView.findViewById(R.id.option_container);

        }
        private void setData(String userName, String city, String userPincode,  Boolean selected, final int position,String mobileno,String alternateMoNo,String flatNo,String locality,String state,String landMark){

            if (alternateMoNo.equals("")){
                fullName.setText(userName+" - "+mobileno);
            }
            else {
                fullName.setText(userName+" - "+mobileno+"/"+alternateMoNo);
            }

                       if (landMark.equals("")){
                address.setText(flatNo+" "+ locality+","+city+","+state);

            }
            else {
                address.setText(flatNo+" "+ locality+" "+landMark+","+city+","+state);

            }
            pincode.setText(userPincode);


            if (MODE==SELECT_ADDRESS){
              icon.setImageResource(R.drawable.check);
              if (selected)
              {
                 icon.setVisibility(View.VISIBLE);
                 preSelectePosition=position;
              }
              else {
                  icon.setVisibility(View.GONE);
              }
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if (preSelectePosition != position) {

                          addressesModelList.get(position).setSelected(true);
                          addressesModelList.get(preSelectePosition).setSelected(false);
                          refreshItem(preSelectePosition, position);
                          preSelectePosition = position;
                          DBquaries.selectedAddress=position;
                      }
                  }
              });
            }
            else if (MODE==MANAGE_ADDRESS){
                optionContainer.setVisibility(View.GONE);

                ////edit address
                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addAddressIntent=new Intent(itemView.getContext(),AddAddressActivity.class);
                        addAddressIntent.putExtra("INTENT","update_address");
                        addAddressIntent.putExtra("index",position);
                        itemView.getContext().startActivity(addAddressIntent);
                        refresh=false;

                    }
                });
                ////edit address


                ////remove address
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        Map<String,Object> address=new HashMap<>();
                        int x=0;
                        int selected=-1;
                        for (int i=0;i<addressesModelList.size();i++){
                            if (i!=position){
                                x++;
                                address.put("city_"+x,addressesModelList.get(i).getCity());
                                address.put("locality_"+x,addressesModelList.get(i).getLocality());
                                address.put("flatno_"+x,addressesModelList.get(i).getFlatno());
                                address.put("pincode_"+x,addressesModelList.get(i).getPincode());
                                address.put("landMark_"+x,addressesModelList.get(i).getLandMark());
                                address.put("name_"+x,addressesModelList.get(i).getName());
                                address.put("mobileNo_"+x,addressesModelList.get(i).getMobileNo());
                                address.put("alternateMoNo_"+x,addressesModelList.get(i).getAlternateMoNo());
                                address.put("state_"+x,addressesModelList.get(i).getState());

                              if (addressesModelList.get(position).getSelected()){
                                  if (position-1>=0){
                                    if (x==position){
                                        address.put("selected_"+x,true);
                                        selected=x;
                                    }
                                    else {
                                        address.put("selected_"+x,addressesModelList.get(i).getSelected());

                                    }
                                  }
                                  else {
                                      if (x==1){
                                          address.put("selected_"+x,true);
                                          selected=x;
                                      }else {
                                          address.put("selected_"+x,addressesModelList.get(i).getSelected());

                                      }
                                  }
                              }else {
                                  address.put("selected_"+x,addressesModelList.get(i).getSelected());
                                    if (addressesModelList.get(i).getSelected()){
                                        selected=x;
                                    }
                              }
                            }
                        }
                        address.put("list_size",x);

                        final int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                .set(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    DBquaries.addressesModelList.remove(position);
                                    if (finalSelected != -1){
                                        DBquaries.selectedAddress= finalSelected -1;
                                        DBquaries.addressesModelList.get(finalSelected-1).setSelected(true);
                                    }else if (DBquaries.addressesModelList.size()==0){
                                        DBquaries.selectedAddress=-1;
                                    }

                                    notifyDataSetChanged();
                                }else {

                                    String error= task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });

                        refresh=false;
                    }
                });
                ////remove address

                icon.setImageResource(R.drawable.vertical);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refreshItem(preSelectePosition, preSelectePosition);
                        }
                        else {
                            refresh=true;
                        }
                        preSelectePosition=position;
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(preSelectePosition,preSelectePosition);
                        preSelectePosition=-1;
                    }
                });
            }
        }
    }
}
