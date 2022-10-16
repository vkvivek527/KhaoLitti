package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddAddressActivity extends AppCompatActivity {
    private Button saveBtn;
    private EditText city;
    private EditText locality;
    private EditText flatno;
    private EditText pincode;
    private EditText landMark;
    private EditText name;
    private EditText mobileNo;
    private EditText alternateMoNo;
    private Spinner stateSpinner;
    private String selectedState;
    private String [] stateList;
    private Dialog loadingDialog;
    private boolean updateAddress=false;
    private AddressesModel addressesModel;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add Your Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ////dialog

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ////dialog
        stateList=getResources().getStringArray(R.array.india_states);

        city=findViewById(R.id.city);
        locality=findViewById(R.id.locality);
        flatno=findViewById(R.id.flat_no);
        pincode=findViewById(R.id.pincode);
        landMark=findViewById(R.id.landmark);
        name=findViewById(R.id.name);
        mobileNo=findViewById(R.id.mobile_no);
        alternateMoNo=findViewById(R.id.alternate_mobile_no);
        saveBtn=findViewById(R.id.save_btn);
        stateSpinner=findViewById(R.id.state_spinner);

        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(spinnerAdapter);

       stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               selectedState=stateList[position];
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

       if (getIntent().getStringExtra("INTENT").equals("update_address")){
           updateAddress=true;
           position=getIntent().getIntExtra("index",-1);
           addressesModel=DBquaries.addressesModelList.get(position);

           city.setText(addressesModel.getCity());
           locality.setText(addressesModel.getLocality());
           flatno.setText(addressesModel.getFlatno());
           landMark.setText(addressesModel.getLandMark());
           name.setText(addressesModel.getName());
           mobileNo.setText(addressesModel.getMobileNo());
           alternateMoNo.setText(addressesModel.getAlternateMoNo());
           pincode.setText(addressesModel.getPincode());

           for (int i=0;i<stateList.length;i++){
               if (stateList[i].equals(addressesModel.getState())){
                   stateSpinner.setSelection(i);
               }
           }
           saveBtn.setText("Update");
       }
       else {
           position=(int) DBquaries.addressesModelList.size() ;
       }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(city.getText())){
                    if (!TextUtils.isEmpty(locality.getText())){
                        if (!TextUtils.isEmpty(flatno.getText())){
                            if (!TextUtils.isEmpty(pincode.getText()) && pincode.getText().length()==6 ){
                                if (!TextUtils.isEmpty(name.getText())){
                                    if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.getText().length()==10){

                                        loadingDialog.show();

                                        Map<String,Object> addAddresses=new HashMap();
                                        addAddresses.put("city_" + String.valueOf(position+1), city.getText().toString());
                                        addAddresses.put("locality_" + String.valueOf(position+1), locality.getText().toString());
                                        addAddresses.put("flatno_"+ String.valueOf(position+1),flatno.getText().toString());
                                        addAddresses.put("pincode_"+ String.valueOf(position+1),pincode.getText().toString());
                                        addAddresses.put("landMark_"+ String.valueOf(position+1),landMark.getText().toString());
                                        addAddresses.put("name_"+ String.valueOf(position+1),name.getText().toString());
                                        addAddresses.put("mobileNo_"+ String.valueOf(position+1),mobileNo.getText().toString());
                                        addAddresses.put("alternateMoNo_"+ String.valueOf(position+1),alternateMoNo.getText().toString());
                                        addAddresses.put("state_"+ String.valueOf(position+1),selectedState);

                                        if (!updateAddress) {
                                            addAddresses.put("list_size", String.valueOf(DBquaries.addressesModelList.size() + 1));
                                            if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                if (DBquaries.addressesModelList.size()==0){
                                                    addAddresses.put("selected_"+ String.valueOf(position+1),true);
                                                }else {
                                                    addAddresses.put("selected_"+ String.valueOf(position+1),false);
                                                }
                                            }else {
                                                addAddresses.put("selected_"+ String.valueOf(position+1),true);
                                            }
                                            if (DBquaries.addressesModelList.size()>0) {
                                                addAddresses.put("selected_" + ( DBquaries.addressesModelList.size() + 1), false);
                                            }
                                        }
                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                                .document("MY_ADDRESSES")
                                                .update(addAddresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                   if (!updateAddress) {
                                                       if (DBquaries.addressesModelList.size() > 0) {
                                                           DBquaries.addressesModelList.get(DBquaries.selectedAddress).setSelected(false);
                                                       }
                                                       DBquaries.addressesModelList.add(new AddressesModel(true,city.getText().toString(),locality.getText().toString(),flatno.getText().toString(),pincode.getText().toString(),landMark.getText().toString(),name.getText().toString(),mobileNo.getText().toString(),alternateMoNo.getText().toString(),selectedState));
                                                       if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                           if (DBquaries.addressesModelList.size()==0){
                                                               DBquaries.selectedAddress = DBquaries.addressesModelList.size() - 1;
                                                           }
                                                       }else {
                                                           DBquaries.selectedAddress = DBquaries.addressesModelList.size() - 1;
                                                       }
                                                   }else {
                                                       DBquaries.addressesModelList.set(position,new AddressesModel(true,city.getText().toString(),locality.getText().toString(),flatno.getText().toString(),pincode.getText().toString(),landMark.getText().toString(),name.getText().toString(),mobileNo.getText().toString(),alternateMoNo.getText().toString(),selectedState));
                                                   }
                                                    if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                        startActivity(deliveryIntent);
                                                    }
                                                    else {
                                                        MyAddressActivity.refreshItem(DBquaries.selectedAddress,DBquaries.addressesModelList.size()-1);
                                                    }
                                                        finish();
                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                              loadingDialog.dismiss();
                                            }
                                        });
                                    }
                                    else {
                                        mobileNo.requestFocus();
                                        Toast.makeText(AddAddressActivity.this, "enter valid mobile number", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    name.requestFocus();
                                }
                            }
                            else {
                                pincode.requestFocus();
                                Toast.makeText(AddAddressActivity.this, "Enter Valid Pincode", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            flatno.requestFocus();
                        }
                    }
                    else {
                        locality.requestFocus();
                    }
                }
                else {
                    city.requestFocus();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
