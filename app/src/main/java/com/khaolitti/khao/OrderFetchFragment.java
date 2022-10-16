package com.khaolitti.khao;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OrderFetchFragment extends Fragment {

    private RecyclerView myOrderRecyclerView;
    public static  OrderFetchAdapter orderFetchAdapter;
    private Dialog loadingDialog;
    private SwipeRefreshLayout refreshLayout;

    public OrderFetchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_order_fetch, container, false);

        refreshLayout=view.findViewById(R.id.order_refresh);
        ////dialog
        loadingDialog=new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();
        ////dialog

        myOrderRecyclerView=view.findViewById(R.id.order_recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);

        orderFetchAdapter=new OrderFetchAdapter(DBquaries.fetchModelItemList);
        myOrderRecyclerView.setAdapter(orderFetchAdapter);
        DBquaries.fetchOrder(getContext(),orderFetchAdapter,loadingDialog);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                DBquaries.fetchOrder(getContext(),orderFetchAdapter,loadingDialog);
                refreshLayout.setRefreshing(false);
            }
        });

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        orderFetchAdapter.notifyDataSetChanged();
    }
}