package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.parseColor;


public class ProductDetailsActivity extends AppCompatActivity {


    public static boolean runningwishlist_query = false;
//    public static boolean runningrating_query = false;
    public static boolean runningcart_query = false;
    public static Activity productDetailsActivity;

    public static boolean fromSearch=false;

    private TextView productTitle;
//    private TextView averageRatingMiniview;
//    private TextView totalRatingMiniview;
    private TextView productPrice;
    private TextView cuttedPrice;
//    private ImageView codIndicator;
//    private TextView tvCodIndicator;
//    private TextView rewardTitle;
//    private TextView rewardBody;

   // private String productOriginalPrice;

    /////product description

    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;
    private TextView productOnlyDescriptionBody;

    private String productDiscription;
    private String productOtherDetails;
    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();


    /////product description

    private TextView percentOff,perOffCoupanBody;

    public static FloatingActionButton addToWishListButton;
    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART=false;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTablayout;


    ////coupandialog
//    private TextView coupanTitle;
//    private TextView coupanExpirayDate;
//    private TextView coupanBody;
//    private  RecyclerView coupansRecylerView;
//    private  LinearLayout selectedCoupans;
//    private TextView discountedPrice;
//    private TextView originalPrice;
    ////coupandialog

    private boolean inStock=false;
    // private Dialog signInDialog;
    private Dialog loadingDialog;

    ///////rating layout
//    public static LinearLayout ratenowContainer;
//    private TextView totalRatings;
//    private LinearLayout ratingsNoContainer;
//    private TextView totalRatingFigure;
//    private LinearLayout ratingProgressBarContainer;
//    private TextView averageRating;
//    public static int initialRating;
    //////rating layout
   // private Button coupanReedemBtn;

    private Button buyNowBtn;
    private LinearLayout addTocartBtn;
    public static  MenuItem cartItem;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView badgeCount;
    public static String productID;
    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        percentOff=findViewById(R.id.percent_off);
        perOffCoupanBody=findViewById(R.id.apply_coupan_code);

        productImagesViewPager = findViewById(R.id.products_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTablayout = findViewById(R.id.product_details_tablayout);
        buyNowBtn = findViewById(R.id.buy_now_btn);
        //coupanReedemBtn = findViewById(R.id.coupan_reedemption_button);
        addToWishListButton = findViewById(R.id.add_to_wishlist_button);
        productTitle = findViewById(R.id.product_titletv);
//        totalRatingMiniview = findViewById(R.id.total_rating_minview);
//        averageRatingMiniview = findViewById(R.id.tv_product_rating_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
//        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
//        codIndicator = findViewById(R.id.cod_indicator_imageview);
//        rewardTitle = findViewById(R.id.reward_title);
//        rewardBody = findViewById(R.id.reward_body);
        productDetailsTabsContainer = findViewById(R.id.product_details_tab_container);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDescriptionBody = findViewById(R.id.product_detail_body);

//        ///rating
//        totalRatings = findViewById(R.id.total_ratings);
//        ratingsNoContainer = findViewById(R.id.ratins_number_container);
//        totalRatingFigure = findViewById(R.id.total_ratings_figure);
//        ratingProgressBarContainer = findViewById(R.id.ratings_progressbar_container);
//        averageRating = findViewById(R.id.average_rating);
//        initialRating = -1;
//        ///rating

        addTocartBtn = findViewById(R.id.add_to_cart_btn);
        firebaseAuth = FirebaseAuth.getInstance();


        //////loading dialog

        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();
        //////loading dialog


        ///////coupan dialog

//        final Dialog checkCoupanPriceDialog = new Dialog(ProductDetailsActivity.this);
//        checkCoupanPriceDialog.setContentView(R.layout.coupan_reedem_dialog);
//        checkCoupanPriceDialog.setCancelable(true);
//        checkCoupanPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        ImageView toggleRecyclerView = checkCoupanPriceDialog.findViewById(R.id.toggle_recyclerview);
//
//        coupansRecylerView = checkCoupanPriceDialog.findViewById(R.id.coupans_recyclerview);
//        selectedCoupans = checkCoupanPriceDialog.findViewById(R.id.selected_coupan);
//
//        coupanTitle = checkCoupanPriceDialog.findViewById(R.id.coupan_title);
//        coupanExpirayDate = checkCoupanPriceDialog.findViewById(R.id.coupan_validity);
//        coupanBody = checkCoupanPriceDialog.findViewById(R.id.coupan_body);
//
//        originalPrice = checkCoupanPriceDialog.findViewById(R.id.original_price);
//        discountedPrice = checkCoupanPriceDialog.findViewById(R.id.discounted_price);
//
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
//        layoutManager.setOrientation(RecyclerView.VERTICAL);
//        coupansRecylerView.setLayoutManager(layoutManager);
//
//        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialogRecyclerView();
//
//            }
//        });

        //////coupan dialog

        productID = getIntent().getStringExtra("PRODUCT_ID");
        firebaseFirestore = FirebaseFirestore.getInstance();
        final List<String> productImages = new ArrayList<>();
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful()){
                                        productTitle.setText(documentSnapshot.get("product_title").toString());
//                                        averageRatingMiniview.setText(documentSnapshot.get("average_rating").toString());
//                                        totalRatingMiniview.setText("(" + (long) documentSnapshot.get("total_ratings") + ")" + " ratings");
                                        productPrice.setText("₹" + documentSnapshot.get("product_price").toString());

                                        for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                            productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                        }
                                        ProducImagesAdapter producImagesAdapter = new ProducImagesAdapter(productImages);
                                        productImagesViewPager.setAdapter(producImagesAdapter);

                                        ///for coupan dialog
                                        //productOriginalPrice=documentSnapshot.get("product_price").toString();
                                        //originalPrice.setText(productPrice.getText());
                                        //MyRewardAdapter myRewardAdapter = new MyRewardAdapter(DBquaries.rewardModelList, true,coupansRecylerView,selectedCoupans,productOriginalPrice,coupanTitle,coupanExpirayDate,coupanBody,discountedPrice);
                                        //coupansRecylerView.setAdapter(myRewardAdapter);
                                       // myRewardAdapter.notifyDataSetChanged();
                                        ///for coupan dialog

                                        cuttedPrice.setText("₹" + documentSnapshot.get("cutted_price").toString() );
                                        ////new coupan code
                                        percentOff.setText(documentSnapshot.get("percent_off").toString()+"% OFF");
                                        perOffCoupanBody.setText("Coupan Applied "+documentSnapshot.get("off_coupan_body").toString());
                                        ////new coupan code

//                                        if ((boolean) documentSnapshot.get("COD")) {
//                                            codIndicator.setVisibility(View.INVISIBLE);
//                                            tvCodIndicator.setVisibility(View.INVISIBLE);
//                                        } else {
//                                            codIndicator.setVisibility(View.INVISIBLE);
//                                            tvCodIndicator.setVisibility(View.INVISIBLE);
//
//                                        }
//                                        rewardTitle.setText(documentSnapshot.get("free_coupans") + documentSnapshot.get("free_coupan_title").toString());
//                                        rewardBody.setText(documentSnapshot.get("free_coupan_body").toString());
//
                                        if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                            productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                            productDetailsOnlyContainer.setVisibility(View.GONE);
                                            productDiscription = documentSnapshot.get("product_description").toString();


                                            for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                                productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                                for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                                    productSpecificationModelList.add(new ProductSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));
                                                    productOtherDetails = documentSnapshot.get("product_other_details").toString();

                                                }
                                            }
                                        } else {
                                            productDetailsTabsContainer.setVisibility(View.GONE);
                                            productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                            productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                        }

                                     //   totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");

//                                        for (int x = 0; x < 5; x++) {
//                                            TextView rating = (TextView) ratingsNoContainer.getChildAt(x);
//                                            rating.setText(String.valueOf((long) documentSnapshot.get(5 - x + "_star")));
//
//                                            ProgressBar progressBar = (ProgressBar) ratingProgressBarContainer.getChildAt(x);
//                                            int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
//                                            progressBar.setMax(maxProgress);
//                                            progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get(5 - x + "_star"))));
//
//                                        }
//                                        totalRatingFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
//                                        averageRating.setText(documentSnapshot.get("average_rating").toString());
                                        productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTablayout.getTabCount(), productDiscription, productOtherDetails, productSpecificationModelList));

//                                        if (DBquaries.myRating.size() == 0) {
//                                            DBquaries.loadRatingList(ProductDetailsActivity.this);
//                                        }
                                       // if (DBquaries.cartList.size() == 0) {
                                         //   DBquaries.loadCartList(ProductDetailsActivity.this, loadingDialog, false,badgeCount,new TextView(ProductDetailsActivity.this));
                                      //  }
//
//                                        if (DBquaries.wishlist.size() == 0) {
//                                            DBquaries.loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
//                                        }
//                                        if (DBquaries.rewardModelList.size()==0){
//                                            DBquaries.loadRewads(ProductDetailsActivity.this,loadingDialog,false);
//                                        }
//                                        if (DBquaries.cartList.size() != 0 && DBquaries.wishlist.size() != 0 && DBquaries.rewardModelList.size()!=0){
//                                            loadingDialog.dismiss();
//                                        }

//                                        if (DBquaries.myRatedIds.contains(productID)) {
//                                            int index = DBquaries.myRatedIds.indexOf(productID);
//                                            initialRating = Integer.parseInt(String.valueOf(DBquaries.myRating.get(index))) - 1;
//                                            setRating(initialRating);
//                                        }

                                        if (DBquaries.cartList.contains(productID)) {
                                            ALREADY_ADDED_TO_CART = true;

                                        } else {
                                            ALREADY_ADDED_TO_CART = false;
                                        }

                                        if (DBquaries.wishlist.contains(productID)) {
                                            ALREADY_ADDED_TO_WISHLIST = true;
                                            addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#FF0000")));
                                        } else {
                                            addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#9e9e9e")));
                                            ALREADY_ADDED_TO_WISHLIST = false;
                                        }

                                        if (task.getResult().getDocuments().size()<(long)documentSnapshot.get("stock_quantity")){
                                                 inStock=true;
                                         buyNowBtn.setVisibility(View.VISIBLE);
                                            addTocartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ///  if (currentUser==null) than show dialog else start intent else
                                                    /// to do cart
                                                    if (!runningcart_query) {
                                                        runningcart_query = true;

                                                        if (ALREADY_ADDED_TO_CART) {
                                                            runningcart_query = false;
                                                            Toast.makeText(ProductDetailsActivity.this, "Already added to cart", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Map<String, Object> addProduct = new HashMap<>();
                                                            addProduct.put("product_ID_" + String.valueOf(DBquaries.cartList.size()), productID);
                                                            addProduct.put("list_size", (long) DBquaries.cartList.size() + 1);

                                                            firebaseFirestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_CART")
                                                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                        if (DBquaries.cartItemModelList.size() != 0) {
                                                                            DBquaries.cartItemModelList.add(0,new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                                                                                    , documentSnapshot.get("product_title").toString()
                                                                                    , Long.parseLong(documentSnapshot.get("free_coupans").toString())
                                                                                    , documentSnapshot.get("product_price").toString()
                                                                                    , documentSnapshot.get("cutted_price").toString()
                                                                                    , (long) 1
                                                                                    , (long) documentSnapshot.get("offers_applied")
                                                                                    , (long) 0
                                                                                    ,inStock
                                                                                    ,(long)documentSnapshot.get("max-quantity")
                                                                                    ,(long)documentSnapshot.get("stock_quantity")));
                                                                        }

                                                                        ALREADY_ADDED_TO_CART = true;
                                                                        DBquaries.cartList.add(productID);
                                                                        Toast.makeText(ProductDetailsActivity.this, "added to cart sucessfully", Toast.LENGTH_SHORT).show();
                                                                        runningcart_query = false;
                                                                        invalidateOptionsMenu();
                                                                    } else {

                                                                        runningcart_query = false;
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }

                                                }

                                            });


                                        }
                                        else {
                                            inStock=false;
                                            buyNowBtn.setVisibility(View.GONE);
                                            TextView outofStock=(TextView) addTocartBtn.getChildAt(0);
                                            outofStock.setText("Out of stock");
                                            outofStock.setTextColor(getResources().getColor(R.color.red));
                                            outofStock.setCompoundDrawables(null,null,null,null);

                                        }
                                    }
                                    else {
                                        //error

                                        String error=task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);

        addToWishListButton.setOnClickListener(new View.OnClickListener() {
            @Override

            //if(currentUser==null) than show the dialog else run the below code
            public void onClick(View v) {
                //  addToWishListButton.setEnabled(false);
                if (!runningwishlist_query) {
                    runningwishlist_query = true;
                    if (ALREADY_ADDED_TO_WISHLIST) {
                        int index = DBquaries.wishlist.indexOf(productID);
                        DBquaries.removeFromWishlist(index, ProductDetailsActivity.this);
                        addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#9e9e9e")));

                    } else {
                        addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#FF0000")));
                        Map<String, Object> addProduct = new HashMap<>();
                        addProduct.put("product_ID_" + String.valueOf(DBquaries.wishlist.size()), productID);
                        addProduct.put("list_size", (long) DBquaries.wishlist.size() + 1);
                        firebaseFirestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (DBquaries.wishlistModelList.size() != 0) {
                                        DBquaries.wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_1").toString()
                                                , documentSnapshot.get("product_title").toString()
                                                , Long.parseLong(documentSnapshot.get("free_coupans").toString())
                                                , documentSnapshot.get("average_rating").toString()
                                                , Long.parseLong(documentSnapshot.get("total_ratings").toString())
                                                , documentSnapshot.get("product_price").toString()
                                                , documentSnapshot.get("cutted_price").toString()
                                                , (boolean) documentSnapshot.get("COD")
                                                 ,inStock));
                                    }
                                    addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#FF0000")));
                                    ALREADY_ADDED_TO_WISHLIST = true;
                                    DBquaries.wishlist.add(productID);
                                    Toast.makeText(ProductDetailsActivity.this, "added to wishlist", Toast.LENGTH_SHORT).show();
                                } else {
                                    addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#FF0000")));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                                runningwishlist_query = false;
                            }
                        });

                    }
                }
            }
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTablayout));
        productDetailsTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        /////rating layout
//
//        ratenowContainer = findViewById(R.id.rate_now_container);
//
//        for (int x = 0; x < ratenowContainer.getChildCount(); x++) {
//            final int starPosition = x;
//            ratenowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ////////check for current user and than set rating
//
//                    if (starPosition != initialRating) {
//                        if (!runningrating_query) {
//                            runningrating_query = true;
//                            setRating(starPosition);
//
//                            Map<String, Object> updateRating = new HashMap<>();
//
//                            if (DBquaries.myRatedIds.contains(productID)) {
//
//                                TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
//                                TextView finalRrating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
//
//                                updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
//                                updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRrating.getText().toString()) + 1);
//                                updateRating.put("average_rating", calculateAverageRating((long) starPosition - initialRating, true));
//
//                            } else {
//
//                                updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
//                                updateRating.put("average_rating", calculateAverageRating(starPosition + 1, false));
//                                updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
//                            }
//
//                            firebaseFirestore.collection("PRODUCTS").document(productID)
//                                    .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//
//                                        Map<String, Object> myRating = new HashMap<>();
//                                        if (DBquaries.myRatedIds.contains(productID)) {
//                                            myRating.put("rating_" + DBquaries.myRatedIds.indexOf(productID), (long) starPosition + 1);
//
//                                        } else {
//
//                                            myRating.put("list_size", (long) DBquaries.myRatedIds.size() + 1);
//                                            myRating.put("product_ID_" + DBquaries.myRatedIds.size(), productID);
//                                            myRating.put("rating_" + DBquaries.myRatedIds.size(), (long) starPosition + 1);
//                                        }
//
//                                        firebaseFirestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_RATINGS")
//                                                .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    if (DBquaries.myRatedIds.contains(productID)) {
//
//                                                        DBquaries.myRating.set(DBquaries.myRatedIds.indexOf(productID), (long) starPosition + 1);
//
//                                                        TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
//                                                        TextView finalRrating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
//
//                                                        oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
//                                                        finalRrating.setText(String.valueOf(Integer.parseInt(finalRrating.getText().toString()) + 1));
//
//
//                                                    } else {
//
//                                                        DBquaries.myRatedIds.add(productID);
//                                                        DBquaries.myRating.add((long) starPosition + 1);
//
//                                                        TextView rating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
//                                                        rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));
//
//                                                        totalRatingMiniview.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")" + "ratings");
//                                                        totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
//                                                        totalRatingFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));
//
//                                                        Toast.makeText(ProductDetailsActivity.this, "Thank you for rating", Toast.LENGTH_SHORT).show();
//                                                    }
//
//                                                    for (int x = 0; x < 5; x++) {
//                                                        TextView ratingFigures = (TextView) ratingsNoContainer.getChildAt(x);
//                                                        ProgressBar progressBar = (ProgressBar) ratingProgressBarContainer.getChildAt(x);
//
//                                                        int maxProgress = Integer.parseInt(totalRatingFigure.getText().toString());
//                                                        progressBar.setMax(maxProgress);
//
//                                                        progressBar.setProgress(Integer.parseInt(ratingFigures.getText().toString()));
//
//                                                        initialRating = starPosition;
//                                                        averageRating.setText(calculateAverageRating(0, true));
//                                                        averageRatingMiniview.setText(calculateAverageRating(0, false));
//
//                                                        if (DBquaries.wishlist.contains(productID) && DBquaries.wishlistModelList.size() != 0) {
//                                                            int index = DBquaries.wishlist.indexOf(productID);
//                                                            DBquaries.wishlistModelList.get(index).setRating(averageRating.getText().toString());
//                                                            DBquaries.wishlistModelList.get(index).setTotalrating(Long.parseLong(totalRatingFigure.getText().toString()));
//                                                        }
//                                                    }
//                                                } else {
//                                                    setRating(initialRating);
//                                                    String error = task.getException().getMessage();
//                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
//                                                }
//                                                runningrating_query = false;
//                                            }
//                                        });
//
//                                    } else {
//                                        runningrating_query = false;
//                                        setRating(initialRating);
//                                        String error = task.getException().getMessage();
//                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            });
//
//                        }
//                    }
//                }
//            });
//
//        }
//        //////rating layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderConfirmationActivity.fromcart=false;
                loadingDialog.show();
                productDetailsActivity=ProductDetailsActivity.this;
                ///  if (currentUser==null) than show dialog else start intent else
                DeliveryActivity.cartItemModelList=new ArrayList<>();
                DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                        , documentSnapshot.get("product_title").toString()
                        , Long.parseLong(documentSnapshot.get("free_coupans").toString())
                        , documentSnapshot.get("product_price").toString()
                        , documentSnapshot.get("cutted_price").toString()
                        , (long) 1
                        , (long) documentSnapshot.get("offers_applied")
                        , (long) 0
                        , inStock
                        ,(long)documentSnapshot.get("max-quantity")
                        ,(long)documentSnapshot.get("stock_quantity")));

                  DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                if (DBquaries.addressesModelList.size()==0) {
                    DBquaries.loadAddresses(ProductDetailsActivity.this, loadingDialog,true);
                }
                else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                    startActivity( deliveryIntent);
                }
            }
        });
//        coupanReedemBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                checkCoupanPriceDialog.show();
//            }
//        });

        ////sign in dialog

        //         enable sign in dialog
//        signInDialoog=new Dialog(MainActivity.this);
//        signInDialoog.setContentView(R.layout.sign_in_dialog);
//        signInDialoog.setCancelable(true);
//        signInDialoog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        Button dialogSignInButton=signInDialoog.findViewById(R.id.sign_in_button);
//        Button dialogSignUpButton=signInDialoog.findViewById(R.id.sign_up_button);
//
//
//        dialogSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signInDialoog.dismiss();
//                Intent registerintent=new Intent(MainActivity.this,PhoneActivity.class);
//                startActivity(registerintent);
//            }
//        });
//
//        dialogSignUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signInDialoog.dismiss();
//                Intent registerintent=new Intent(MainActivity.this,PhoneActivity.class);
//                startActivity(registerintent);
//
//            }
//        });

        ////sign in dialog

    }

//    private void showDialogRecyclerView() {
//        if (coupansRecylerView.getVisibility() == View.GONE) {
//            coupansRecylerView.setVisibility(View.VISIBLE);
//            selectedCoupans.setVisibility(View.GONE);
//        } else {
//            coupansRecylerView.setVisibility(View.GONE);
//            selectedCoupans.setVisibility(View.VISIBLE);
//        }
//
//    }
//    public static void setRating(int starPosition) {
//
//        for (int x = 0; x < ratenowContainer.getChildCount(); x++) {
//            ImageView starBtn = (ImageView) ratenowContainer.getChildAt(x);
//            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
//            if (x <= starPosition) {
//                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
//            }
//        }
//    }

//    private String calculateAverageRating(long currentUserRating, boolean update) {
//        Double totalstar = Double.valueOf(0);
//        for (int x = 1; x < 6; x++) {
//            TextView ratingNo = (TextView) ratingsNoContainer.getChildAt(5 - x);
//            totalstar = totalstar + (Long.parseLong(ratingNo.getText().toString())) * x;
//        }
//        totalstar = totalstar + currentUserRating;
//        if (update) {
//            return String.valueOf(totalstar / Long.parseLong(totalRatingFigure.getText().toString())).substring(0, 3);
//        } else {
//            return String.valueOf(totalstar / (Long.parseLong(totalRatingFigure.getText().toString()) + 1)).substring(0, 3);
//        }
//
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem=menu.findItem(R.id.carticonbar);

            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon=cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.cart);
            badgeCount=cartItem.getActionView().findViewById(R.id.badge_count);

       // if (DBquaries.cartList.size() == 0) {
            DBquaries.loadCartList(ProductDetailsActivity.this, loadingDialog, false,badgeCount,new TextView(ProductDetailsActivity.this));

       // }
        if (DBquaries.cartList.size()!=0){
            badgeCount.setVisibility(View.VISIBLE);
            if (DBquaries.cartList.size()<99) {
                badgeCount.setText(String.valueOf(DBquaries.cartList.size()));
            }else {
                badgeCount.setText("99");
            }
        }
            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    MainActivity.showCart = true;
                    startActivity(cartIntent);
                }
            });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.searchiconbar) {
            if (fromSearch){
                finish();
            }else {
                Intent searchIntent=new Intent(this,SearchActivity.class);
                startActivity(searchIntent);
            }

            return true;
        } else if (id == android.R.id.home) {
            productDetailsActivity=null;
            finish();
            return true;
        }
        if (id == R.id.carticonbar) {
            productDetailsActivity=null;
            MainActivity.showCart = true;
            Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
            startActivity(cartIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (DBquaries.myRating.size() == 0) {
//            DBquaries.loadRatingList(ProductDetailsActivity.this);
//        }
        // if (DBquaries.wishlist.size() == 0) {
        invalidateOptionsMenu();

            DBquaries.loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
        //  }
//        if (DBquaries.rewardModelList.size()==0){
//            DBquaries.loadRewads(ProductDetailsActivity.this,loadingDialog,false);
//        }
        if (DBquaries.cartList.size() != 0 && DBquaries.wishlist.size() != 0 && DBquaries.rewardModelList.size()!=0){
            loadingDialog.dismiss();
        }
//        if (DBquaries.myRatedIds.contains(productID)) {
//            int index = DBquaries.myRatedIds.indexOf(productID);
//            initialRating = Integer.parseInt(String.valueOf(DBquaries.myRating.get(index))) - 1;
//            //setRating(initialRating);
//        }

//        if (DBquaries.cartList.contains(productID)) {
//            ALREADY_ADDED_TO_CART = true;
//
//        } else {
//            ALREADY_ADDED_TO_CART = false;
//        }
//        if (DBquaries.cartList.contains(productID)) {
//            ALREADY_ADDED_TO_CART = true;
//
//        } else {
//            ALREADY_ADDED_TO_CART = false;
//        }

        if (DBquaries.wishlist.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#FF0000")));
        } else {
            addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }

    }
    @Override
    public void onBackPressed() {
        productDetailsActivity=null;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch=false;
    }
}
