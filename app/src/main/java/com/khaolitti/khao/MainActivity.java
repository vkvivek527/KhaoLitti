package com.khaolitti.khao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static String maximumAmountTosetDelivery,setDelivertPrice;

    public static DrawerLayout mNavDrawer;
    private FrameLayout frameLayout;
    private static final int HOME_FRAGMENT=0;
    private static final int CART_FRAGMENT=1;
    private static final int ORDER_FRAGMENT=2;
    private static final int WISHLIST_FRAGMENT=3;
    private static final int REWARD_FRAGMENT=4;
    private static final int ACCOUNT_FRAGMENT=5;
    private static final int ABOUT_FRAGMENT=6;
    private static final int CONTACT_FRAGMENT=7;
    public static Boolean showCart=false;
    private  int currentFragment=-1;
    public static boolean reserMainActivity=false;

    private NavigationView navigationView;
     private ImageView actionbarlogo;
     private TextView badgeCount;
     private int scrollFlags;
     private AppBarLayout.LayoutParams params;
     public static Activity mainActivity;
    public static Activity toast;
     private CircleImageView profileView;
     private TextView name,phone;
    private CircleImageView addprofileIcon;
    // private Dialog signInDialoog;
    private String link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        actionbarlogo = findViewById(R.id.actionbarlogo);
        setSupportActionBar(toolbar);

        params=(AppBarLayout.LayoutParams)toolbar.getLayoutParams();
        scrollFlags=params.getScrollFlags();
        params.setScrollFlags(scrollFlags);

        toast=MainActivity.this;

        mNavDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            MenuItem menu;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // if (currentUser!=null) than all the code will run
                //else run this mNavDrawer.closeDrawer(GravityCompat.START);
                //sinDialog.show return false
                DrawerLayout mNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                mNavDrawer.closeDrawer(GravityCompat.START);
                menu = menuItem;
                mNavDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        int idd = menu.getItemId();
                        if (idd == R.id.nav_my_mall) {
                            actionbarlogo.setVisibility(View.VISIBLE);
                            invalidateOptionsMenu();
                            setFragment(new HomeFragment(), HOME_FRAGMENT);
                        } else if (idd == R.id.nav_my_account) {
                            gotoFragment("Address Book", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                        } else if (idd == R.id.nav_my_rewards) {
                            gotoFragment("My Offers", new MyRewardsFragment(), REWARD_FRAGMENT);
                        } else if (idd == R.id.nav_my_order) {
                            gotoFragment("My Orders", new OrderFetchFragment(), ORDER_FRAGMENT);
                        } else if (idd == R.id.nav_my_wishlist) {
                            gotoFragment("Favourites", new MyWishlistFragment(), WISHLIST_FRAGMENT);
                        } else if (idd == R.id.nav_my_cart) {
                            gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                        }else if (idd==R.id.nav_about_us){
                            gotoFragment("About Us",new AboutUs(),ABOUT_FRAGMENT);
                        }else if (idd==R.id.nav_contact_us){
                            gotoFragment("Contact Us",new ContactUs(),CONTACT_FRAGMENT);
                        }else if (idd==R.id.nav_share){
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBody = "Download Khaolitti App \n"+link+" \nAnd Get Exciting Offers ";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }
                    }
                });
                return true;
            }
        });

        frameLayout = findViewById(R.id.fragment_container);
        profileView=navigationView.getHeaderView(0).findViewById(R.id.header_profileimage);
        name=navigationView.getHeaderView(0).findViewById(R.id.header_profilename);
        phone=navigationView.getHeaderView(0).findViewById(R.id.header_phone);
        addprofileIcon=navigationView.getHeaderView(0).findViewById(R.id.header_setting_button);

        if (showCart) {
            mainActivity=this;
            mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart", new MyCartFragment(), -2);
        } else {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mNavDrawer, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
            );
            mNavDrawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(), HOME_FRAGMENT);
        }

//       enabling the last drawer item signout
//        if (currentUser==null){
//          navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(false);
//        }
//        else {
//            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
//        }
        navigationView.getMenu().getItem(0).setChecked(true);

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
        addprofileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfo=new Intent(MainActivity.this,UpdateUserInfoActivity.class);
                startActivity(updateUserInfo);
            }
        });

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("Notification","Notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if(currentFragment==HOME_FRAGMENT){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.menu, menu);

            MenuItem cartItem=menu.findItem(R.id.carticonbar);
             cartItem.setActionView(R.layout.badge_layout);
                ImageView badgeIcon=cartItem.getActionView().findViewById(R.id.badge_icon);
                badgeIcon.setImageResource(R.drawable.cart);
                badgeCount=cartItem.getActionView().findViewById(R.id.badge_count);

            DBquaries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false,badgeCount,new TextView(MainActivity.this));
                if (DBquaries.cartList.size() == 0) {
                    badgeCount.setVisibility(View.INVISIBLE);
                }
                else {
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
                        gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);

                    }
                });

            MenuItem notifyItem=menu.findItem(R.id.notificationbar);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon=notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.notification);
            TextView notifyCount=notifyItem.getActionView().findViewById(R.id.badge_count);

            DBquaries.checkNotifications(false,notifyCount);

            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationIntent=new Intent(MainActivity.this,NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.carticonbar) {
            // setting dialog when clicked on cart
//            if (currentUser==null){
//                signInDialoog.show();
//            } else
          gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);

        }
       else if (id==R.id.notificationbar)
        {
            Intent notificationIntent=new Intent(this,NotificationActivity.class);
            startActivity(notificationIntent);
        }
       else if (id==R.id.searchiconbar)
        {
          Intent searchIntent=new Intent(this,SearchActivity.class);
          startActivity(searchIntent);
        }
       else if (id==android.R.id.home)
        {
           if (showCart){
               mainActivity=null;
                showCart=false;
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFragment(String title,Fragment fragment,int fragmentNo) {
        actionbarlogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment,fragmentNo);
        if (fragmentNo==CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(2).setChecked(true);
            params.setScrollFlags(0);
        }
        else {
            params.setScrollFlags(scrollFlags);
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavDrawer.isDrawerOpen(GravityCompat.START))
        {
            mNavDrawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (currentFragment==HOME_FRAGMENT) {
                currentFragment=-1;
                super.onBackPressed();
            }
            else {
                if (showCart) {
                    mainActivity=null;
                    showCart=false;
                    finish();
                } else {
                    actionbarlogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }

    private void setFragment(Fragment fragment,int fragmentNo)
    {
        if (fragmentNo!=currentFragment) {
            currentFragment=fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
        invalidateOptionsMenu();

        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
//                    DBquaries.email=task.getResult().getString("email");
                    DBquaries.name=task.getResult().getString("name");
                    DBquaries.profile=task.getResult().getString("profile");
//                    DBquaries.age=task.getResult().getString("age");
//                    DBquaries.gender=task.getResult().getString("gender");
                   DBquaries.phone=task.getResult().getString("mobileno");

                    phone.setText(DBquaries.phone);

                    if (!DBquaries.name.equals("")){
                        name.setText(DBquaries.name);
                    }
                    else {
                        name.setText("set your name");
                    }
                    if (!DBquaries.profile.equals(""))
                    {
                        Glide.with(MainActivity.this).load(DBquaries.profile).apply(new RequestOptions().placeholder(R.drawable.profile)).into(profileView);
                    }else {
                        Glide.with(MainActivity.this).load(R.drawable.profile).into(profileView);
                    }
                }
                else
                {
                  String error=task.getException().getMessage();
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (reserMainActivity){
            actionbarlogo.setVisibility(View.VISIBLE);
            reserMainActivity=false;
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setChecked(true);
        }

       FirebaseFirestore.getInstance().collection("DELIVERY").document("delivery").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            maximumAmountTosetDelivery=task.getResult().get("Amount").toString();
                            setDelivertPrice=task.getResult().get("Delivery").toString();
                            link=task.getResult().get("Link").toString();

                        }
                    }
                });
    }
    @Override
    protected void onPause() {
        super.onPause();
        DBquaries.checkNotifications(true,null);
    }
}
