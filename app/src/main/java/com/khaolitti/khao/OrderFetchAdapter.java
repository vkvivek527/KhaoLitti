package com.khaolitti.khao;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderFetchAdapter extends RecyclerView.Adapter<OrderFetchAdapter.ViewHolder>{

    private List<FetchModel> fetchedData;

    public OrderFetchAdapter(List<FetchModel> list) {
      this.fetchedData=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fetchitemlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String orderId=fetchedData.get(position).getOrderId();
        String orderStatus=fetchedData.get(position).getStatus();
        List<MyOrderItemModel> productList=fetchedData.get(position).getFetchedOrderList();

        Date date;
        switch (orderStatus) {
            case "Ordered":
                date = fetchedData.get(position).getFetchedOrderList().get(0).getOrderedDate();
                break;
            case "Packed":
                date = fetchedData.get(position).getFetchedOrderList().get(0).getPackedDate();
                break;
            case "Shipped":
                date = fetchedData.get(position).getFetchedOrderList().get(0).getShippedDate();
                break;
            case "Delivered":
                date = fetchedData.get(position).getFetchedOrderList().get(0).getDeliveredDate();
                break;
            case "Cancelled":
                date = fetchedData.get(position).getFetchedOrderList().get(0).getCancelledDate();
                break;
            default:
                date = fetchedData.get(position).getFetchedOrderList().get(0).getCancelledDate();
        }
        holder.setData(orderId,orderStatus,date,position,productList);
    }

    @Override
    public int getItemCount() {
        return fetchedData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       private TextView orderidTv;
       private TextView productTitleTv;
       private ImageView indicator;
       private TextView dateTv;
       private ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderidTv=itemView.findViewById(R.id.orderidtv);
             productTitleTv=itemView.findViewById(R.id.product_titletv);
             indicator=itemView.findViewById(R.id.order_indicatorim);
             dateTv=itemView.findViewById(R.id.order_deleverd_datetv);
             productImage=itemView.findViewById(R.id.product_image);

        }
        private void setData(final String orderid, String orderStatus, Date date, final int position, List<MyOrderItemModel> productList){

            String image=productList.get(0).getProductImage();
            Glide.with(itemView.getContext()).load(image).into(productImage);

            String title="";
            for (int i=0;i<productList.size();i++){
                title=title+productList.get(i).getProductTitle()+"\n";
            }
            productTitleTv.setText(title);

            orderidTv.setText("Order Id : "+orderid);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE,dd MMM YYYY hh:mm aa");

            dateTv.setText(orderStatus +" "+String.valueOf(simpleDateFormat.format(date)));

            if (orderStatus.equals("Cancelled")) {
                indicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.deleverycancel)));
            } else {
                indicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.delevered)));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailsIntent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    orderDetailsIntent.putExtra("Position", position);
                    orderDetailsIntent.putExtra("Oid",orderid);
                    itemView.getContext().startActivity(orderDetailsIntent);

                }
            });
        }
    }
}
