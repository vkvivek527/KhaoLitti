package com.khaolitti.khao;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.khaolitti.khao.DBquaries.categoryModelList;
import static com.khaolitti.khao.DBquaries.lists;
import static com.khaolitti.khao.DBquaries.loadCategories;
import static com.khaolitti.khao.DBquaries.loadFragmentData;
import static com.khaolitti.khao.DBquaries.loadedCategoriesNames;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }
     private ConnectivityManager connectivityManager;
     private NetworkInfo networkInfo;
     public static SwipeRefreshLayout swipeRefreshLayout;
     private RecyclerView categoryRecyclerView;
     private List<CategoryModel> categoryModelFakeList=new ArrayList<>();
     private CategoryAdapter categoryAdapter;
     private RecyclerView homePageRecyclerView;
     private List<HomePageModel> homePageModelFakeList=new ArrayList<>();
     private HomePageAdapter adapter;
     private ImageView noInternetConnection;
     private Button retryBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_home, container, false);
     swipeRefreshLayout=view.findViewById(R.id.refresh_layout);
        noInternetConnection=view.findViewById(R.id.no_internet_connection);
     homePageRecyclerView=view.findViewById(R.id.home_page_recycler_view);
     retryBtn=view.findViewById(R.id.retry_button);

     categoryRecyclerView=view.findViewById(R.id.category_recyclerview);
     LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
     layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
     categoryRecyclerView.setLayoutManager(layoutManager);

     LinearLayoutManager testingLayoutManager=new LinearLayoutManager(getContext());
     testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
     homePageRecyclerView.setLayoutManager(testingLayoutManager);



///////category fake list
     categoryModelFakeList.add(new CategoryModel("null",""));
     categoryModelFakeList.add(new CategoryModel("",""));
     categoryModelFakeList.add(new CategoryModel("",""));
     categoryModelFakeList.add(new CategoryModel("",""));
     categoryModelFakeList.add(new CategoryModel("",""));
     categoryModelFakeList.add(new CategoryModel("",""));
     categoryModelFakeList.add(new CategoryModel("",""));
     categoryModelFakeList.add(new CategoryModel("",""));
     categoryModelFakeList.add(new CategoryModel("",""));

     ///////category fake list

     /////home page fake list
     List<SliderModel> sliderModelFakeList=new ArrayList<>();
     sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
     sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
     sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
     sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
     sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));

     List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList=new ArrayList<>();

     horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","","",""));
     horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","","",""));
     horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","","",""));
     horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","","",""));
     horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","","",""));
     horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","","",""));
     horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","","",""));


     homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
     homePageModelFakeList.add(new HomePageModel(1,"","#ffffff"));
     homePageModelFakeList.add(new HomePageModel(2,"","#ffffff",horizontalProductScrollModelFakeList,new ArrayList<WishlistModel>()));
     homePageModelFakeList.add(new HomePageModel(3,"","#ffffff",horizontalProductScrollModelFakeList));
     /////home page fake list
     categoryAdapter=new CategoryAdapter(categoryModelFakeList);


     adapter=new HomePageAdapter(homePageModelFakeList);

     connectivityManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
     networkInfo=connectivityManager.getActiveNetworkInfo();

     if (networkInfo!=null &&networkInfo.isConnected()==true ) {
      noInternetConnection.setVisibility(View.GONE);
      retryBtn.setVisibility(View.GONE);
      MainActivity.mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      categoryRecyclerView.setVisibility(View.VISIBLE);
      homePageRecyclerView.setVisibility(View.VISIBLE);

      if (categoryModelList.size()==0)
      {
       loadCategories(categoryRecyclerView,getContext());
      }
      else {
       categoryAdapter=new CategoryAdapter(categoryModelList);
       categoryRecyclerView.setAdapter(categoryAdapter);
       categoryAdapter.notifyDataSetChanged();
      }


      if (lists.size()==0)
      {
       loadedCategoriesNames.add("HOME");
       lists.add(new ArrayList<HomePageModel>());
       loadFragmentData(homePageRecyclerView,getContext(),0,"Home");
      }
      else {
       adapter=new HomePageAdapter(lists.get(0));
       adapter.notifyDataSetChanged();
      }
      homePageRecyclerView.setAdapter(adapter);

     }else {
      MainActivity.mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
      categoryRecyclerView.setVisibility(View.GONE);
      homePageRecyclerView.setVisibility(View.GONE);
      Glide.with(this).load(R.drawable.no_internet).into(noInternetConnection);
      noInternetConnection.setVisibility(View.VISIBLE);
      retryBtn.setVisibility(View.VISIBLE);
     }

       /////refresh layout
swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
 @Override
 public void onRefresh() {
  swipeRefreshLayout.setRefreshing(true);
  reloadPage();

 }
});

     /////refresh layout

     retryBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
       reloadPage();
      }
     });
        return view;
    }

private void reloadPage(){

 networkInfo=connectivityManager.getActiveNetworkInfo();

 /*categoryModelList.clear();
 lists.clear();
 loadedCategoriesNames.clear();*/

 DBquaries.clearData();
 if (networkInfo!=null &&networkInfo.isConnected()==true ) {
  MainActivity.mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
  noInternetConnection.setVisibility(View.GONE);
  retryBtn.setVisibility(View.GONE);

  categoryRecyclerView.setVisibility(View.VISIBLE);
  homePageRecyclerView.setVisibility(View.VISIBLE);
  categoryAdapter=new CategoryAdapter(categoryModelFakeList);
  adapter=new HomePageAdapter(homePageModelFakeList);
  categoryRecyclerView.setAdapter(categoryAdapter);
  homePageRecyclerView.setAdapter(adapter);

  loadCategories(categoryRecyclerView,getContext());
  loadedCategoriesNames.add("HOME");
  lists.add(new ArrayList<HomePageModel>());
  loadFragmentData(homePageRecyclerView,getContext(),0,"Home");
 }
 else
 {
  MainActivity.mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
  Toast.makeText(getContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
  retryBtn.setVisibility(View.VISIBLE);
  categoryRecyclerView.setVisibility(View.GONE);
  homePageRecyclerView.setVisibility(View.GONE);
  Glide.with(getContext()).load(R.drawable.no_internet).into(noInternetConnection);
  noInternetConnection.setVisibility(View.VISIBLE);
  swipeRefreshLayout.setRefreshing(false);
 }

}
}
