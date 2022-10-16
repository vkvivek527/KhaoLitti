package com.khaolitti.khao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AboutUs extends Fragment {
    private Button aboutUs,privacyPolicy,termsCondition;

    public AboutUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_about_us, container, false);
        aboutUs=view.findViewById(R.id.about_us);
        privacyPolicy=view.findViewById(R.id.privacy_policy);
        termsCondition=view.findViewById(R.id.terms_condition);

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseAboutUs=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.khaolitti.com/about-us"));
                startActivity(browseAboutUs);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browsePrivacy=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.khaolitti.com/privacy-policy"));
                startActivity(browsePrivacy);
            }
        });
        termsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseTerms=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.khaolitti.com/terms-and-conditions"));
                startActivity(browseTerms);
            }
        });

        return view;
    }
}
