package com.khaolitti.khao;

import java.util.List;

public class HomePageModel {
    public static final int BANNER_SLIDER=0;
    public static final int STRIP_ADD_BANNER=1;
    public static final int HORIZONTAL_PRODCT_VIEW=2;
    public static final int GRID_PRODCT_VIEW=3;
  private int type;
    private String backGroundColor;

  /////banner slider
    private List<SliderModel> sliderModelList;
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }
    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }
    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;

    }
    /////banner slider


    ////////strip ad
    private String resource;


    public HomePageModel(int type, String resource, String backGroundColor) {
        this.type = type;
        this.resource = resource;
        this.backGroundColor = backGroundColor;
    }

    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getBackGroundColor() {
        return backGroundColor;
    }
    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }
////////strip ad




    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    ////horizontal product layout
    private List<WishlistModel> viewAllProductList;

    public HomePageModel(int type, String title,String backGroundColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList,List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backGroundColor=backGroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList=viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    ///horizontal product layout


    ////grid product layout
    public HomePageModel(int type, String title, String backGroundColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backGroundColor=backGroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;

    }
    ////grid product layout
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }


    ///
}
