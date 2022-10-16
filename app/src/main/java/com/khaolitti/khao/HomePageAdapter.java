package com.khaolitti.khao;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {
    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastPosition=-1;

    private Dialog imagesDialog;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool=new RecyclerView.RecycledViewPool();

    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;

            case 1:
                return HomePageModel.STRIP_ADD_BANNER;
            case 2:
                return HomePageModel.HORIZONTAL_PRODCT_VIEW;
            case 3:
                return HomePageModel.GRID_PRODCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannersliderview = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_add_layout, parent, false);
                return new BannerSliderViewHolder(bannersliderview);

            case HomePageModel.STRIP_ADD_BANNER:
                View stripadview = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAdBannerViewHolder(stripadview);

            case HomePageModel.HORIZONTAL_PRODCT_VIEW:
                View horizontalproductview = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scrool_layout, parent, false);
                return new HorizontalProductViewHolder(horizontalproductview);

            case HomePageModel.GRID_PRODCT_VIEW:
                View gridproductview = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridproductview);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();

                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;

            case HomePageModel.STRIP_ADD_BANNER:
                String resourse = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackGroundColor();
                ((StripAdBannerViewHolder) holder).setStripAd(resourse, color);
                break;

            case HomePageModel.HORIZONTAL_PRODCT_VIEW:
                String layoutcolour = homePageModelList.get(position).getBackGroundColor();
                String horizontallayouttitle = homePageModelList.get(position).getTitle();
                List<WishlistModel> viewAllProducyList = homePageModelList.get(position).getViewAllProductList();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModelList, horizontallayouttitle, layoutcolour, viewAllProducyList);
                break;

            case HomePageModel.GRID_PRODCT_VIEW:
                String gridLayoutcolour = homePageModelList.get(position).getBackGroundColor();
                String gridlayouttitletitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductScrollModelList, gridlayouttitletitle, gridLayoutcolour);
                break;

            default:
                return;
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition=position;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager bannerSliderViewPager;
        private int currentPage ;
        private Timer timer;
        final private long DELAYTIME = 3000;
        final private long PERIODTIME = 3000;
        private List<SliderModel> arrangedList;

        public BannerSliderViewHolder(@NonNull View itemView) {

            super(itemView);
            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);
        }

        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {
            currentPage=2;
            if (timer!=null){
               timer.cancel();
            }
            arrangedList=new ArrayList<>();
            for (int x=0;x<sliderModelList.size();x++){
                arrangedList.add(x,sliderModelList.get(x));
            }
            arrangedList.add(0,sliderModelList.get(sliderModelList.size()-2));
            arrangedList.add(1,sliderModelList.get(sliderModelList.size()-1));
            arrangedList.add(sliderModelList.get(0));
            arrangedList.add(sliderModelList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangedList);
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);
            bannerSliderViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedList);
                    }

                }
            };
            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);
            startbannerSlideShow(arrangedList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedList);
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startbannerSlideShow(arrangedList);
                    }
                    return false;
                }
            });

        }

        private void pageLooper(List<SliderModel> sliderModelList) {
            if (currentPage == sliderModelList.size() - 2) {
                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = sliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }

        }

        private void startbannerSlideShow(final List<SliderModel> sliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAYTIME, PERIODTIME);
        }

        private void stopBannerSlideShow() {
            timer.cancel();

        }
    }

    public class StripAdBannerViewHolder extends RecyclerView.ViewHolder {
        private ImageView stripaddImage;
        private ConstraintLayout stripadContainer;

        public StripAdBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            stripaddImage = itemView.findViewById(R.id.strip_ad_image);
            stripadContainer = itemView.findViewById(R.id.strip_ad_container);


        }

        private void setStripAd(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.loading)).into(stripaddImage);

            stripadContainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder {
        private TextView horizontalLayoutTitle;
        private Button horizontalviewAllButton;
        private RecyclerView horizontalRecyclerView;
        private ConstraintLayout container;


        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);

            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_layout_title);
            horizontalviewAllButton = itemView.findViewById(R.id.horizontal_scroll_viewall_button);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontal_scroll_layout_scroll_view);
            horizontalRecyclerView.setRecycledViewPool(recycledViewPool);
            container=itemView.findViewById(R.id.container);
        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String colour, final List<WishlistModel> viewAllProductList) {

            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colour)));
            horizontalLayoutTitle.setText(title);

            if (horizontalProductScrollModelList.size() > 8) {
                horizontalviewAllButton.setVisibility(View.VISIBLE);
                horizontalviewAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList=viewAllProductList;
                        Intent viewAllIntent=new Intent(itemView.getContext(),ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code",0);
                        viewAllIntent.putExtra("title",title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });

            } else {
                horizontalviewAllButton.setVisibility(View.INVISIBLE);
            }
            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerView.setLayoutManager(linearLayoutManager);

            horizontalRecyclerView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();

        }

    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder{

       private TextView gridLayoutTitle;
        private Button gridLayoutViewAllButton;
        private GridLayout gridProductLayout;
        private ConstraintLayout container;


        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container=itemView.findViewById(R.id.container);
             gridLayoutTitle=itemView.findViewById(R.id.grid_product_layout_title);
           gridLayoutViewAllButton=itemView.findViewById(R.id.grid_product_layout_viewall_button);
           gridProductLayout=itemView.findViewById(R.id.grid_layout);

        }
        private void setGridProductLayout(final List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String colour) {

            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colour)));
            gridLayoutTitle.setText("Coupon Applied "+title);

            for (int x = 0; x < 1; x++) {
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_image);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_title);
                TextView productDiscriotion = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_description);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_price);
                TextView cuttedPrice = gridProductLayout.getChildAt(x).findViewById(R.id.tvCuttedPrice);


                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.loading)).into(productImage);

                productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                productDiscriotion.setText(horizontalProductScrollModelList.get(x).getProductDiscription()+"% OFF");
                productPrice.setText("₹" + horizontalProductScrollModelList.get(x).getProductPrice() );
                cuttedPrice.setText("₹" + horizontalProductScrollModelList.get(x).getCuttedPrice() );
                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#ffffff"));

//                /////images viewPager
//                 imagesDialog=new Dialog(MainActivity.toast);
//                imagesDialog.setContentView(R.layout.images_dialog);
//                imagesDialog.setCancelable(true);
//                imagesDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                imagesDialog.getWindow().setBackgroundDrawable(MainActivity.toast.getDrawable(R.drawable.slider_background));
//               final ViewPager viewPager=imagesDialog.findViewById(R.id.products_images_viewpager);
//
//                final List<String> productImages = new ArrayList<>();
//                productImage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(horizontalProductScrollModelList.get(0).getProductId()).get()
//                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()){
//                                            DocumentSnapshot documentSnapshot=task.getResult();
//                                            for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
//                                                productImages.add(documentSnapshot.get("product_image_" + x).toString());
//
//                                            }
//                                            ProducImagesAdapter producImagesAdapter = new ProducImagesAdapter(productImages);
//                                            viewPager.setAdapter(producImagesAdapter);
//                                        }
//                                    }
//
//                                });
//                        imagesDialog.show();
//                    }
//                });
//
//                ////images vi
                if (!title.equals("")) {
//                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
//                            productDetailsIntent.putExtra("PRODUCT_ID",horizontalProductScrollModelList.get(finalX).getProductId());
//                            itemView.getContext().startActivity(productDetailsIntent);
//
//                        }
//                    });

                    final int finalX1 = x;
                    gridLayoutViewAllButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String productId=horizontalProductScrollModelList.get(finalX1).getProductId();
//                            ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
//                            Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
//                            viewAllIntent.putExtra("layout_code", 1);
//                            viewAllIntent.putExtra("title", title);
//                            itemView.getContext().startActivity(viewAllIntent);

                            Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            productDetailsIntent.putExtra("PRODUCT_ID",productId);
                            itemView.getContext().startActivity(productDetailsIntent);

                        }
                    });
                }
            }

        }
    }
}
