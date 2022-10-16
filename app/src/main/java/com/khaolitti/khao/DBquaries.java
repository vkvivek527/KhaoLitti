package com.khaolitti.khao;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.parseColor;

    public class DBquaries {

    public static FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    public static  String profile,name,phone;
    public static String orderid,status;

    public static List<CategoryModel> categoryModelList=new ArrayList<>();

    public static List<List<HomePageModel>> lists=new ArrayList<>();
    public static List<String> loadedCategoriesNames=new ArrayList<>();

    public static List<String> wishlist=new ArrayList<>();
    public static List<WishlistModel> wishlistModelList= new ArrayList<>();

    public static List<String> cartList=new ArrayList<>();
    public static List<CartItemModel> cartItemModelList= new ArrayList<>();

    public static List<String> myRatedIds=new ArrayList<>();
    public static List<Long> myRating=new ArrayList<>();

        public static List<FetchModel> fetchModelItemList=new ArrayList<>();

        public static List<AddressesModel> addressesModelList=new ArrayList<>();
    public static int selectedAddress=-1;

    public static List<RewardModel> rewardModelList=new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList=new ArrayList<>();

    public  static List<NotificationModel> notificationModelList=new ArrayList<>();
    private static ListenerRegistration registration;
    public static void loadCategories(final RecyclerView categoryRecyclerView, final Context context){
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot:task.getResult()){

                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(),documentSnapshot.get("categoryName").toString()));

                            }
                            CategoryAdapter categoryAdapter=new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();

                        }else {
                            String error=task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName){
        firebaseFirestore.collection("CATEGORIES").document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot:task.getResult()){

                                if ((long)documentSnapshot.get("view_type")==0)
                                {
                                    List<SliderModel> sliderModelList=new ArrayList<>();
                                    long no_of_banners=(long)documentSnapshot.get("no_of_banner");
                                    for (long x=1;x<no_of_banners+1;x++)
                                    {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_"+x).toString(),
                                                documentSnapshot.get("banner_"+x+"_background").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(0,sliderModelList));

                                }
                                else if ((long)documentSnapshot.get("view_type")==1)
                                {
                                    lists.get(index).add(new HomePageModel(1,documentSnapshot.get("strip_ad_banner").toString(),documentSnapshot.get("background").toString()));
                                }
                                else if ((long)documentSnapshot.get("view_type")==2)
                                {

                                    List<WishlistModel> viewAllProductList=new ArrayList<>();

                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList=new ArrayList<>();

                                    long no_of_products=(long)documentSnapshot.get("no_of_products");
                                    for (long x=1;x<no_of_products+1;x++)
                                    {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_"+x).toString()
                                                ,documentSnapshot.get("product_image_"+x).toString()
                                                ,documentSnapshot.get("product_title_"+x).toString()
                                                ,documentSnapshot.get("product_subtitle_"+x).toString()
                                                ,documentSnapshot.get("product_price_"+x).toString()
                                                ,documentSnapshot.get("cutted_price_"+x).toString()));

                                        viewAllProductList.add(new WishlistModel(documentSnapshot.get("product_ID_"+x).toString(),documentSnapshot.get("product_image_"+x).toString()
                                        ,documentSnapshot.get("product_full_title_"+x).toString()
                                        ,Long.parseLong(documentSnapshot.get("free_coupans_"+x).toString())
                                        ,documentSnapshot.get("average_rating_"+x).toString()
                                        ,Long.parseLong(documentSnapshot.get("total_ratings_"+x).toString())
                                        ,documentSnapshot.get("product_price_"+x).toString()
                                        ,documentSnapshot.get("cutted_price_"+x).toString()
                                        ,(boolean)documentSnapshot.get("COD_"+x)
                                         ,(boolean)documentSnapshot.get("in_stock_"+x)));

                                    }
                                    lists.get(index).add(new HomePageModel(2,documentSnapshot.get("layout_title").toString(),documentSnapshot.get("layout_background").toString(),horizontalProductScrollModelList,viewAllProductList));

                                }
                                else if ((long)documentSnapshot.get("view_type")==3)
                                {

                                    List<HorizontalProductScrollModel> gridLayoutModelList=new ArrayList<>();

                                    long no_of_products=(long)documentSnapshot.get("no_of_products");
                                    for (long x=1;x<no_of_products+1;x++)
                                    {
                                        gridLayoutModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_"+x).toString()
                                                ,documentSnapshot.get("product_image_"+x).toString()
                                                ,documentSnapshot.get("product_title_"+x).toString()
                                                ,documentSnapshot.get("product_subtitle_"+x).toString()
                                                ,documentSnapshot.get("product_price_"+x).toString()
                                                ,documentSnapshot.get("cutted_price_"+x).toString()
                                        ));
                                    }
                                    lists.get(index).add(new HomePageModel(3,documentSnapshot.get("layout_title").toString(),documentSnapshot.get("layout_background").toString(),gridLayoutModelList));
                                }

                            }
                            HomePageAdapter homePageAdapter=new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);

                        }else {
                            String error=task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    public static void loadWishlist(final Context context, final Dialog dialog,final boolean loadProductData){
        wishlist.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
          if (task.isSuccessful()){

                  for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                      wishlist.add(task.getResult().get("product_ID_" + x).toString());

                      if (DBquaries.wishlist.contains(ProductDetailsActivity.productID)){
                          ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=true;
                          if (ProductDetailsActivity.addToWishListButton!=null){
                         ProductDetailsActivity.addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#FF0000")));
                      }
                      }
                      else {
                          if (ProductDetailsActivity.addToWishListButton!=null) {
                              ProductDetailsActivity.addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#9e9e9e")));
                          }
                           ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=false;
                      }

                      if (loadProductData) {
                          wishlistModelList.clear();
                          final String productId=task.getResult().get("product_ID_" + x).toString();
                      firebaseFirestore.collection("PRODUCTS").document(productId)
                              .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                              if (task.isSuccessful()) {
                                  final DocumentSnapshot documentSnapshot=task.getResult();

                                  firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                          .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                              @Override
                                              public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                  if (task.isSuccessful()){

                                                      if (task.getResult().getDocuments().size()< (long)documentSnapshot.get("stock_quantity")){

                                                          wishlistModelList.add(new WishlistModel(productId,documentSnapshot.get("product_image_1").toString()
                                                                  , documentSnapshot.get("product_title").toString()
                                                                  , Long.parseLong(documentSnapshot.get("free_coupans").toString())
                                                                  , documentSnapshot.get("average_rating").toString()
                                                                  , Long.parseLong(documentSnapshot.get("total_ratings").toString())
                                                                  , documentSnapshot.get("product_price").toString()
                                                                  , documentSnapshot.get("cutted_price").toString()
                                                                  , (boolean) documentSnapshot.get("COD")
                                                                  , true));

                                                      }
                                                      else {

                                                          wishlistModelList.add(new WishlistModel(productId,documentSnapshot.get("product_image_1").toString()
                                                                  , documentSnapshot.get("product_title").toString()
                                                                  , Long.parseLong(documentSnapshot.get("free_coupans").toString())
                                                                  , documentSnapshot.get("average_rating").toString()
                                                                  , Long.parseLong(documentSnapshot.get("total_ratings").toString())
                                                                  , documentSnapshot.get("product_price").toString()
                                                                  , documentSnapshot.get("cutted_price").toString()
                                                                  , (boolean) documentSnapshot.get("COD")
                                                                  , false));
                                                      }
                                                      MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();

                                                  }
                                                  else {
                                                      //error
                                                      String error=task.getException().getMessage();
                                                      Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          });

                              } else {
                                  String error = task.getException().getMessage();
                                  Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                              }
                          }
                      });
                  }
              }

          }
          else {
              String error=task.getException().getMessage();
              Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
          }
          dialog.dismiss();

            }
        });

    }

    public static void removeFromWishlist(final int index, final Context context){
       final String removedProductId=wishlist.get(index);
        wishlist.remove(index);
        Map<String,Object> updateWishlist=new HashMap<>();

        for(int x=0;x<wishlist.size();x++){
            updateWishlist.put("product_ID_"+x,wishlist.get(x));

        }
        updateWishlist.put("list_size",(long)wishlist.size());
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    if (wishlistModelList.size()!=0){
                        wishlistModelList.remove(index);
                        MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                    }

                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=false;
                    Toast.makeText(context, "Removed Sucessfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (ProductDetailsActivity.addToWishListButton!=null) {
                        ProductDetailsActivity.addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#FF0000")));
                    }
                    wishlist.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                }

                ProductDetailsActivity.runningwishlist_query=false;
            }
        });
    }

//    public static void loadRatingList(final Context context){
//        if (!ProductDetailsActivity.runningrating_query) {
//           ProductDetailsActivity.runningrating_query=true;
//            myRatedIds.clear();
//            myRating.clear();
//            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
//                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//
//                        List<String> orderProductIds=new ArrayList<>();
//                        for (int x=0;x<myOrderItemModelList.size();x++){
//                            orderProductIds.add(myOrderItemModelList.get(x).getProductId());
//
//                        }
//
//                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
//                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
//                            myRating.add(Long.valueOf(task.getResult().get("rating_" + x).toString()));
//
//                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID) ) {
//                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
//
//                                if (ProductDetailsActivity.ratenowContainer != null){
////                                    ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
//                            }
//                            }
//                            if (orderProductIds.contains(task.getResult().get("product_ID_" + x).toString())){
//                                myOrderItemModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
//                            }
//                        }
//                        if (MyOrdersFragment.myOrderAdapter!=null){
//                            MyOrdersFragment.myOrderAdapter.notifyDataSetChanged();
//                        }
//
//                    } else {
//                        String error = task.getException().getMessage();
//                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//
//                    }
//                    ProductDetailsActivity.runningrating_query=false;
//                }
//            });
//        }
//    }

    public static void  loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount, final TextView cartTotalAmount){
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    dialog.show();
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        cartList.add(task.getResult().get("product_ID_" + x).toString());

                        final long y=(long) task.getResult().get("list_size");

                        if (DBquaries.cartList.contains(ProductDetailsActivity.productID)){
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART=true;
                        }
                        else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART=false;
                        }
                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productId=task.getResult().get("product_ID_" + x).toString();
                            final long finalX = x;
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot documentSnapshot=task.getResult();

                                        firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            int index=0;
//                                                            if (cartList.size()>=2){
//                                                                index=cartList.size()-2;
//                                                           }
                                                           if (task.getResult().getDocuments().size()< (long)documentSnapshot.get("stock_quantity")){
                                                                cartItemModelList.add(index,new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM,productId,documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_title").toString()
                                                                        , Long.parseLong(documentSnapshot.get("free_coupans").toString())
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (long)1
                                                                        , (long)documentSnapshot.get("offers_applied")
                                                                        , (long)0
                                                                        ,true
                                                                        ,(long)documentSnapshot.get("max-quantity")
                                                                        ,(long)documentSnapshot.get("stock_quantity")));
                                                            }
                                                            else {
                                                                cartItemModelList.add(index,new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM,productId,documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_title").toString()
                                                                        , Long.parseLong(documentSnapshot.get("free_coupans").toString())
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (long)1
                                                                        , (long)documentSnapshot.get("offers_applied")
                                                                        , (long)0
                                                                        ,false
                                                                        ,(long)documentSnapshot.get("max-quantity")
                                                                        ,(long)documentSnapshot.get("stock_quantity")));
                                                            }
                                                            if (finalX == y-1){
                                                                cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                                LinearLayout parent=(LinearLayout) cartTotalAmount.getParent().getParent();
                                                                parent.setVisibility(View.VISIBLE);
                                                            }
                                                            if (cartList.size()==0){
                                                                cartItemModelList.clear();
                                                                LinearLayout parent=(LinearLayout) cartTotalAmount.getParent().getParent();
                                                                parent.setVisibility(View.INVISIBLE);
                                                            }
                                                          MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                        }
                                                        else {
                                                            //error
                                                            String error=task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                        }
                    }

                    if (cartList.size()!=0){
                        badgeCount.setVisibility(View.VISIBLE);
                    }
                    else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (DBquaries.cartList.size()<99) {
                        badgeCount.setText(String.valueOf(DBquaries.cartList.size()));
                    }else {
                        badgeCount.setText("99");
                    }
                    dialog.dismiss();
                }
                else {
                    dialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public static void removeFromCart(final int index, final Context context,final TextView cartTotalAmount ){
        final String removedProductId=cartList.get(index);
        cartList.remove(index);
        Map<String,Object> updateCartList=new HashMap<>();

        for(int x=0;x<cartList.size();x++){
            updateCartList.put("product_ID_"+x,cartList.get(x));

        }
        updateCartList.put("list_size",(long)cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (cartItemModelList.size()!=0){
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size()==0){
                        LinearLayout parent=(LinearLayout) cartTotalAmount.getParent().getParent();
                         parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }

                    Toast.makeText(context, "Removed Sucessfully", Toast.LENGTH_SHORT).show();
                    ProductDetailsActivity.runningcart_query=false;
                }
                else {
                    cartList.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    ProductDetailsActivity.runningcart_query=false;
                }
                ProductDetailsActivity.runningcart_query=false;
            }
        });
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog, final boolean gotoDeliveryActivity){

        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                     Intent deliveryIntent=null;
                    if (Long.parseLong(task.getResult().get("list_size").toString())==0){
                        deliveryIntent=new Intent(context,AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT","deliveryIntent");
                    }
                 else {
                     for (long x=1;x<Long.parseLong(task.getResult().get("list_size").toString())+1;x++){
                        addressesModelList.add(new AddressesModel(task.getResult().getBoolean("selected_"+x)
                                ,task.getResult().getString("city_"+x)
                                ,task.getResult().getString("locality_"+x)
                                ,task.getResult().getString("flatno_"+x)
                                ,task.getResult().getString("pincode_"+x)
                                ,task.getResult().getString("landMark_"+x)
                                ,task.getResult().getString("name_"+x)
                                ,task.getResult().getString("mobileNo_"+x)
                                ,task.getResult().getString("alternateMoNo_"+x)
                                ,task.getResult().getString("state_"+x)));

                         if ((boolean)task.getResult().get("selected_"+x)){
                             selectedAddress= Integer.parseInt(String.valueOf(x-1));
                         }
                     }
                     if (gotoDeliveryActivity) {
                         deliveryIntent = new Intent(context, DeliveryActivity.class);
                     }
                      }
                     if (gotoDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();

            }
        });
    }

    public static void loadRewads(final Context context, final Dialog loadingDialog, final Boolean onRewardFragment){
        rewardModelList.clear();

       firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (task.isSuccessful()){
                          final Date lastseenDate=task.getResult().getDate("Last seen");

                           firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_REWARDS").get()
                                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                           if (task.isSuccessful()){
                                               for (QueryDocumentSnapshot documentSnapshot:task.getResult()) {
                                                   if (documentSnapshot.get("type").toString().equals("Discount") &&  lastseenDate.before(documentSnapshot.getDate("val"))) {
                                                       rewardModelList.add(new RewardModel(documentSnapshot.getId(),documentSnapshot.get("type").toString()
                                                               , documentSnapshot.get("lower_limit").toString()
                                                               , documentSnapshot.get("upper_limit").toString()
                                                               , documentSnapshot.get("percentage").toString()
                                                               , documentSnapshot.get("body").toString()
                                                               ,documentSnapshot.getDate("validity")
                                                                ,(boolean)documentSnapshot.get("already_used")));
                                                   } else if (documentSnapshot.get("type").toString().equals("Flat Rs.*Off") &&  lastseenDate.before(documentSnapshot.getDate("val")))
                                                   {
                                                       rewardModelList.add(new RewardModel(documentSnapshot.getId(),documentSnapshot.get("type").toString()
                                                               , documentSnapshot.get("lower_limit").toString()
                                                               , documentSnapshot.get("upper_limit").toString()
                                                               , documentSnapshot.get("amount").toString()
                                                               , documentSnapshot.get("body").toString()
                                                               ,documentSnapshot.getDate("validity")
                                                                ,(boolean)documentSnapshot.get("already_used")));
                                                   }
                                               }
                                               if (onRewardFragment) {
                                                   MyRewardsFragment.myRewardAdapter.notifyDataSetChanged();
                                               }
                                           }
                                           else {
                                               String error = task.getException().getMessage();
                                               Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                           }
                                           loadingDialog.dismiss();
                                       }
                                   });
                       }
                       else {
                           loadingDialog.dismiss();
                           String error = task.getException().getMessage();
                           Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                       }
                   }
               });
   }

//    public static void loadOrders(final Context context, @Nullable final MyOrderAdapter myOrderAdapter, final Dialog loadingDialog){
//        myOrderItemModelList.clear();
//
//        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
//
//                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).collection("OrderItems").get()
//                                         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                if (task.isSuccessful()){
//
//                                                    for (DocumentSnapshot orderItem:task.getResult().getDocuments()){
//                                                         String itemId=orderItem.getId();
//                                                         MyOrderItemModel myOrderItemModel=new MyOrderItemModel(orderItem.getString("Product Id"),orderItem.getString("Order Status"),orderItem.getString("Address"),orderItem.getString("Coupan Id"),orderItem.getString("Cutted Price"),orderItem.getDate("Order date"),orderItem.getDate("packed date"),orderItem.getDate("shipped date"),orderItem.getDate("Delivered date"),orderItem.getDate("Cancelled date"),orderItem.getString("Discounted Price"),orderItem.getLong("Free Coupan"),orderItem.getString("Full Name"),orderItem.getString("ORDER ID"),orderItem.getString("Payment Mathod"),orderItem.getString("Pincode"),orderItem.getString("Product Price"),orderItem.getLong("Product Quantity"),orderItem.getString("User Id"),orderItem.getString("Product Image"),orderItem.getString("Product Title"),orderItem.getString("Delivery Price"),orderItem.getBoolean("Cancellation requested"));
//                                                         myOrderItemModelList.add(myOrderItemModel);
//                                                    }
//                                                  //  loadRatingList(context);
//                                                    if (myOrderAdapter !=null) {
//                                                        myOrderAdapter.notifyDataSetChanged();
//                                                    }
//
//                                                }else {
//                                                    String error = task.getException().getMessage();
//                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                                                }
//
//                                            }
//                                        });
//                            }
//
//                        }else {
//                            loadingDialog.dismiss();
//                            String error = task.getException().getMessage();
//                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                        }
//                        loadingDialog.dismiss();
//
//                    }
//
//                });
//   }

    public static void checkNotifications(boolean remove,@Nullable final TextView notifyCount){
        if (remove){
                 registration.remove();
        }else {
            registration= firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("USER_DATA").document("MY_NOTIFICATIONS")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot!= null && documentSnapshot.exists()){
                                notificationModelList.clear();
                                int unread=0;
                                for (long x = 0; x < (long) documentSnapshot.get("list_size"); x++){
                                    notificationModelList.add(0,new NotificationModel(documentSnapshot.get("Body_"+x).toString(),documentSnapshot.get("Image_"+x ).toString(),documentSnapshot.getBoolean("Readed_"+x)));
                                    if (!documentSnapshot.getBoolean("Readed_"+x)){
                                        unread++;
                                        if (notifyCount!=null){
                                            if (unread>0) {
                                                notifyCount.setVisibility(View.VISIBLE);
                                                if (unread < 99) {
                                                    notifyCount.setText(String.valueOf(unread));
                                                } else {
                                                    notifyCount.setText("99");
                                                }
                                            }else {
                                                notifyCount.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }

                                if (NotificationActivity.adapter!=null){
                                    NotificationActivity.adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }

        }

        public static void fetchOrder(final Context context, @Nullable final OrderFetchAdapter myOrderAdapter, @Nullable final Dialog loadingDialog){
            fetchModelItemList.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").orderBy("time", Query.Direction.ASCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){

                                    firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).collection("OrderItems").orderBy("Order date",Query.Direction.DESCENDING).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        List<MyOrderItemModel> orderlist=new ArrayList<>();
                                                        int count=0;
                                                        for (DocumentSnapshot orderItem:task.getResult().getDocuments()){

                                                            MyOrderItemModel myOrderItemModel=new MyOrderItemModel(orderItem.getString("Product Id"),orderItem.getString("Order Status"),orderItem.getString("Address"),orderItem.getString("Coupan Id"),orderItem.getString("Cutted Price"),orderItem.getDate("Order date"),orderItem.getDate("packed date"),orderItem.getDate("shipped date"),orderItem.getDate("Delivered date"),orderItem.getDate("Cancelled date"),orderItem.getString("Discounted Price"),orderItem.getLong("Free Coupan"),orderItem.getString("Full Name"),orderItem.getString("ORDER ID"),orderItem.getString("Payment Mathod"),orderItem.getString("Pincode"),orderItem.getString("Product Price"),orderItem.getLong("Product Quantity"),orderItem.getString("User Id"),orderItem.getString("Product Image"),orderItem.getString("Product Title"),orderItem.getString("Delivery Price"),orderItem.getBoolean("Cancellation requested"));
                                                            orderlist.add(myOrderItemModel);

                                                            if (count==task.getResult().getDocuments().size()-1){
                                                                fetchModelItemList.add(new FetchModel(orderItem.getString("ORDER ID"),orderItem.getString("Order Status"),orderlist));
                                                            }
                                                            count++;
                                                        }
                                                        //  loadRatingList(context);
                                                        if (myOrderAdapter !=null) {
                                                            myOrderAdapter.notifyDataSetChanged();
                                                        }

                                                    }else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                }

                            }else {

                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            if (loadingDialog!=null) {
                                loadingDialog.dismiss();
                            }

                        }

                    });

        }

    public static void clearData(){

        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishlist.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        addressesModelList.clear();
        rewardModelList.clear();
        myOrderItemModelList.clear();
        notificationModelList.clear();
        fetchModelItemList.clear();
    }



}
