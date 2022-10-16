package com.khaolitti.khao;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyWishlistFragment extends Fragment {

    private RecyclerView wishListrecyclerView;
    private Dialog loadingDialog;
    public static WishlistAdapter wishlistAdapter;

    public MyWishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_wishlist, container, false);
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();

       wishListrecyclerView =view.findViewById(R.id.m_wishlist_recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wishListrecyclerView.setLayoutManager(linearLayoutManager);

       if (DBquaries.wishlistModelList.size()==0){
           DBquaries.wishlist.clear();
           DBquaries.loadWishlist(getContext(),loadingDialog,true);
      }
      else {
          loadingDialog.dismiss();
       }

        wishlistAdapter=new WishlistAdapter(DBquaries.wishlistModelList,true);
        wishListrecyclerView.setAdapter(wishlistAdapter);
        wishlistAdapter.notifyDataSetChanged();

        return view;
    }

}
