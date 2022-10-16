package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {
    private int totaltab;
    private String productDiscription;
    private  String productOtherDetails;
    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totaltab, String discription, String productOtherDetails, List<ProductSpecificationModel> productSpecificationModelList) {
        super(fm);
        this.totaltab=totaltab;
        productDiscription = discription;

        this.productOtherDetails = productOtherDetails;
        this.productSpecificationModelList = productSpecificationModelList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
              ProductDescriptionFragment productDescriptionFragment1=new ProductDescriptionFragment();
             productDescriptionFragment1.body=productDiscription;
            return productDescriptionFragment1;
              case 1:
                  ProductSpecificationFragment productSpecificationFragment=new ProductSpecificationFragment();
                  productSpecificationFragment.productSpecificationModelList=productSpecificationModelList;
                  return productSpecificationFragment;

            case 2:
                ProductDescriptionFragment productDescriptionFragment2=new ProductDescriptionFragment();
                productDescriptionFragment2.body=productOtherDetails;
                return productDescriptionFragment2;
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return totaltab;
    }
}
