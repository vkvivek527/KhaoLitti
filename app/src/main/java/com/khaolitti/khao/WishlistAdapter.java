package com.khaolitti.khao;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private boolean fromSearch;
    private List<WishlistModel> wishlistModelList;
    private Boolean wishList;
    private int lastPosition=-1;

    public WishlistAdapter(List<WishlistModel> wishlistModelList,Boolean wishList) {
        this.wishlistModelList = wishlistModelList;
        this.wishList=wishList;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

   View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String productId=wishlistModelList.get(position).getProductId();
        String resourse=wishlistModelList.get(position).getProductImage();
        String title=wishlistModelList.get(position).getTitle();
        long freecoupans=wishlistModelList.get(position).getFreecoupan();
        String rating=wishlistModelList.get(position).getRating();
        long totalrating=wishlistModelList.get(position).getTotalrating();
        String productPrice=wishlistModelList.get(position).getProductPrice();
        String cuttedPrice=wishlistModelList.get(position).getCuttedPrice();
        Boolean paymentMethod=wishlistModelList.get(position).getCOD();
        boolean inStock=wishlistModelList.get(position).isInStock();

        holder.setData(productId,resourse,title,freecoupans,rating,totalrating,productPrice,cuttedPrice,paymentMethod,position,inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition=position;
        }

    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView productImage;
        private TextView produTitle;
        private TextView freecoupan;
        private ImageView coupanIcon;
//        private TextView rating;
//        private TextView totalRating;
//        private View priceCut;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethod;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.product_image);
            produTitle=itemView.findViewById(R.id.product_titletv);
            freecoupan=itemView.findViewById(R.id.free_coupan);
            coupanIcon=itemView.findViewById(R.id.coupan_icon);
//            rating=itemView.findViewById(R.id.tv_product_rating_miniview);
//            totalRating=itemView.findViewById(R.id.total_ratings);
//            priceCut=itemView.findViewById(R.id.price_cut);
            productPrice=itemView.findViewById(R.id.product_price);
            cuttedPrice=itemView.findViewById(R.id.cutted_price);
            paymentMethod=itemView.findViewById(R.id.payment_method);
            deleteBtn=itemView.findViewById(R.id.delete_button);
        }

        private void setData(final String productId, String resource, String title, long freecoupanNo, String averageRate, long totalRatingsNo, String price, String cutprice, boolean COD, final int index,boolean inStock)
        {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.loading)).into(productImage);
          produTitle.setText(title);
          if (freecoupanNo!=0 && inStock){
              coupanIcon.setVisibility(View.VISIBLE);
              if ( freecoupanNo==1){
                  freecoupan.setText("free "+freecoupanNo+" coupan");
              }
              else {
                  freecoupan.setText("free "+freecoupanNo+" coupans");
              }
          }else {
              coupanIcon.setVisibility(View.INVISIBLE);
              freecoupan.setVisibility(View.INVISIBLE);
          }

           // LinearLayout linearLayout=(LinearLayout) rating.getParent();

            if (inStock) {

//              rating.setVisibility(View.VISIBLE);
//              totalRating.setVisibility(View.VISIBLE);
              productPrice.setTextColor(Color.parseColor("#000000"));
              cuttedPrice.setVisibility(View.VISIBLE);
                //linearLayout.setVisibility(View.VISIBLE);

//              rating.setText(averageRate);
//              totalRating.setText("("+totalRatingsNo+")"+"ratings");
              productPrice.setText("₹"+price);
              cuttedPrice.setText("₹"+cutprice);
              if (COD){
                  paymentMethod.setVisibility(View.VISIBLE);
              }
              else {
                  paymentMethod.setVisibility(View.INVISIBLE);
              }
          }
          else {
           //  linearLayout.setVisibility(View.INVISIBLE);
//              rating.setVisibility(View.INVISIBLE);
//              totalRating.setVisibility(View.INVISIBLE);
              productPrice.setText("Out Of tock");
              productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
              cuttedPrice.setVisibility(View.INVISIBLE);
              paymentMethod.setVisibility(View.INVISIBLE);
          }


          if (wishList){
              deleteBtn.setVisibility(View.VISIBLE);
          }else {
              deleteBtn.setVisibility(View.GONE);
          }

          deleteBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (!ProductDetailsActivity.runningwishlist_query) {
                      ProductDetailsActivity.runningwishlist_query = true;
                      DBquaries.removeFromWishlist(index,itemView.getContext());
                  }

              }
          });

          itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (fromSearch){
                   ProductDetailsActivity.fromSearch=true;
                  }
                  Intent productDetailsIntent=new Intent(itemView.getContext(),ProductDetailsActivity.class);
                  productDetailsIntent.putExtra("PRODUCT_ID",productId);
                  itemView.getContext().startActivity(productDetailsIntent);
              }
          });
        }
    }
}


