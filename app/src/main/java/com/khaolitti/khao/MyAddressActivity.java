package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.khaolitti.khao.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressActivity extends AppCompatActivity {
    private RecyclerView myAddressesRecyclerView;
    private static AddressesAdapter addressesAdapter;
    private Button deliverHereBtn;
    private LinearLayout addNewAddressBtn;
    private TextView addressesSaved;
    private int previousAddress;
    private Dialog loadingDialog;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ////dialog

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressesSaved.setText(String.valueOf(DBquaries.addressesModelList.size()+" saved address"));

            }
        });
        ////dialog

        previousAddress=DBquaries.selectedAddress;

        deliverHereBtn=findViewById(R.id.deliver_here_bth);
        addNewAddressBtn=findViewById(R.id.add_new_address_button);
        myAddressesRecyclerView=findViewById(R.id.addresses_recyclerview);

         LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(layoutManager);
        addressesSaved=findViewById(R.id.address_saved);

        mode=getIntent().getIntExtra("Mode",-1);
       if (mode==SELECT_ADDRESS){
           deliverHereBtn.setVisibility(View.VISIBLE);
       }
       else {
           deliverHereBtn.setVisibility(View.GONE);
       }


       deliverHereBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (DBquaries.selectedAddress != previousAddress) {
                   final int previousAddressIndex=previousAddress;

                   loadingDialog.show();

                   Map<String,Object> updateSelection=new HashMap<>();
                   updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                   updateSelection.put("selected_"+String.valueOf(DBquaries.selectedAddress+1),true);

                    previousAddress=DBquaries.selectedAddress;
                   FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                           .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           {
                               if (task.isSuccessful()) {
                                   finish();

                               } else {
                                   previousAddress=previousAddressIndex;
                                   String error = task.getException().getMessage();
                                   Toast.makeText(MyAddressActivity.this, error, Toast.LENGTH_SHORT).show();

                               }
                               loadingDialog.dismiss();

                           }
                       }
                   });
               }
               else {
                   finish();
               }
           }
       });


        addressesAdapter=new AddressesAdapter(DBquaries.addressesModelList,mode,loadingDialog);
        myAddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();


        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent=new Intent(MyAddressActivity.this,AddAddressActivity.class);
                if (mode !=SELECT_ADDRESS){
                    addAddressIntent.putExtra("INTENT","manage");
                    startActivity(addAddressIntent);
                }else {
                    addAddressIntent.putExtra("INTENT","null");
                    startActivity(addAddressIntent);
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBquaries.addressesModelList.size()+" saved address"));

    }

    public static void refreshItem(int deselect, int select){
        addressesAdapter.notifyItemChanged(deselect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home){
            if (mode==SELECT_ADDRESS) {
                if (DBquaries.selectedAddress != previousAddress) {
                    DBquaries.addressesModelList.get(DBquaries.selectedAddress).setSelected(false);
                    DBquaries.addressesModelList.get(previousAddress).setSelected(true);
                    DBquaries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
           if (mode==SELECT_ADDRESS) {
               if (DBquaries.selectedAddress != previousAddress) {
                   DBquaries.addressesModelList.get(DBquaries.selectedAddress).setSelected(false);
                   DBquaries.addressesModelList.get(previousAddress).setSelected(true);
                   DBquaries.selectedAddress = previousAddress;
               }
           }
        super.onBackPressed();
    }
}
