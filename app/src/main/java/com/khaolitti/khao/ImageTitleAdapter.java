package com.khaolitti.khao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageTitleAdapter extends RecyclerView.Adapter<ImageTitleAdapter.ViewHolder> {

    private List<ImageTitleModel> imageTitleModelList ;

    public ImageTitleAdapter(List<ImageTitleModel> imageTitleModelList) {
        this.imageTitleModelList = imageTitleModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quantity_image_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String tittle=imageTitleModelList.get(position).getTitle();
        String image=imageTitleModelList.get(position).getImage();
        int quantity=imageTitleModelList.get(position).getQuantity();
        String price=imageTitleModelList.get(position).getPrice();
        String cuttedPrice=imageTitleModelList.get(position).getCuutedprice();
           holder.setData(tittle,image,quantity,cuttedPrice,price);
    }

    @Override
    public int getItemCount() {
        return imageTitleModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titletv;
        private ImageView imageView;
        private TextView quantitytv;
        private TextView pricetv;
        private TextView cuttedpricetv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titletv=itemView.findViewById(R.id.detailtitle);
            imageView=itemView.findViewById(R.id.detailimage);
            quantitytv=itemView.findViewById(R.id.detailquantity);
            pricetv=itemView.findViewById(R.id.detailprice);
            cuttedpricetv=itemView.findViewById(R.id.detailcuttedprice);
        }
        private void setData(String stitle,String simage,int squantity,String scuttedprice,String sprice){
            titletv.setText(stitle);
            Glide.with(itemView.getContext()).load(simage).into(imageView);
            quantitytv.setText(""+squantity);

            cuttedpricetv.setText(""+squantity* Integer.parseInt(scuttedprice));
            pricetv.setText(""+squantity*Integer.parseInt(sprice));
        }
    }
}
