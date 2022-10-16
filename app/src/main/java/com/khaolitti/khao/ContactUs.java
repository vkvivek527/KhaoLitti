package com.khaolitti.khao;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ContactUs extends Fragment {
   private LinearLayout whatsapp,web,gmail;
   private ImageView facebook,instagram,twitter,youtube;

    public ContactUs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_contact_us, container, false);
         web=view.findViewById(R.id.website);
        whatsapp=view.findViewById(R.id.whatsApp);
         facebook=view.findViewById(R.id.facebook);
         instagram=view.findViewById(R.id.intagram);
         twitter=view.findViewById(R.id.twitter);
         youtube=view.findViewById(R.id.youtube);
         gmail=view.findViewById(R.id.gmail);

         web.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent websiteIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.khaolitti.com/"));
                 startActivity(websiteIntent);
             }
         });

         gmail.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(Intent.ACTION_SEND);
                 intent.setType("*/*");
                 intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"khaolitti@gmail.com"});
                 intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry");
                 intent.setPackage("com.google.android.gm");
                 startActivity(intent);
             }
         });

        whatsapp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
//                 if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//
//                     ActivityCompat.requestPermissions(getActivity(),new String[] {CALL_PHONE}, PackageManager.PERMISSION_GRANTED);                 }
//                 else
//                 {
//                     String s="tel:"+"7250999302";
//                     Intent call=new Intent(Intent.ACTION_DIAL);
//                     call.setData(Uri.parse(s));
//                     startActivity(call);
//                 }
                 Uri uri =Uri.parse("smsto:"+"+917250999302");
                 Intent whatsappIntent=new Intent(Intent.ACTION_SENDTO,uri);
                 whatsappIntent.setPackage("com.whatsapp");
                 startActivity(whatsappIntent);
             }
         });

         facebook.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String facebookId = "fb://page/439032513305406";
                 String urlPage = "https://www.facebook.com/khaolitti";
                 try {
                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId )));
                 } catch (Exception e) {
                     //Open url web page.
                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
                 }
             }
         });

         instagram.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
//                 String url = "https://www.instagram.com/khaolitti";
//                 Intent intent = new Intent(Intent.ACTION_VIEW);
//                 intent.setData(Uri.parse(url));
//                 startActivity(intent);
                 Uri uri = Uri.parse("https://www.instagram.com/khaolitti");
                 Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                 likeIng.setPackage("com.instagram.android");
                 try {
                     startActivity(likeIng);
                 } catch (ActivityNotFoundException e) {
                     startActivity(new Intent(Intent.ACTION_VIEW,
                             Uri.parse("https://www.instagram.com/khaolitti")));
                 }
             }
         });

         twitter.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     Intent intent = new Intent(Intent.ACTION_VIEW,
                             Uri.parse("twitter://user?screen_name=KhaoLitti"));
                     startActivity(intent);
                 } catch (Exception e) {
                     startActivity(new Intent(Intent.ACTION_VIEW,
                             Uri.parse("https://twitter.com/#!/KhaoLitti")));
                 }
             }
         });
//         youtube.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
//                 Intent webIntent = new Intent(Intent.ACTION_VIEW,
//                         Uri.parse("http://www.youtube.com/watch?v=" + id));
//                 try {
//                     startActivity(appIntent);
//                 } catch (ActivityNotFoundException ex) {
//                     startActivity(webIntent);
//                 }
//             }
//         });
        return view;
    }
}
