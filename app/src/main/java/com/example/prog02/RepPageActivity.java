package com.example.prog02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.prog02.R.drawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RepPageActivity extends AppCompatActivity {
    ImageButton officialWebsiteButton, officialPhoneButton, officialTwitterButton, officialFacebookButton;
    TextView officialNameTextView, officialPartyAndPositionTextView;
    View officialNameBorderOne, officialNameBorderTwo;
    ArrayList<String> officialInfoArray;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rep_page);

        //Pre-Defined Values
        int colorRed = getColor(R.color.colorPrimaryRed);
        Drawable redButton = ContextCompat.getDrawable(this, R.drawable.red_circle_button);
        int colorBlue = getColor(R.color.colorPrimaryBlue);
        Drawable blueButton = ContextCompat.getDrawable(this, R.drawable.blue_circle_button);

        // Get Intent Values
        officialInfoArray = new ArrayList<>(Objects.requireNonNull(getIntent().getStringArrayListExtra("officialArrayList")));

        // Instantiate Objects
        ImageView officialPhoto = findViewById(R.id.imageViewRepresentativePhoto);
        officialNameTextView = findViewById(R.id.textViewRepresentativeHeaderName);
        officialPartyAndPositionTextView = findViewById(R.id.textViewRepresentativePartyAndPosition);
        officialNameBorderOne = findViewById(R.id.borderOneRepresentativeHeaderName);
        officialNameBorderTwo = findViewById(R.id.borderTwoRepresentativeHeaderName);

        officialWebsiteButton = findViewById(R.id.buttonRepresentativeWebsite);
        officialPhoneButton = findViewById(R.id.buttonRepresentativePhone);
        officialTwitterButton = findViewById(R.id.buttonRepresentativeTwitter);
        officialFacebookButton = findViewById(R.id.buttonRepresentativeFacebook);

        // Set Values Based On Intent Values
        if (officialInfoArray.get(8).length() > 0){
            Picasso.with(this)
                    .load(officialInfoArray.get(8))
                    .resize(250, 250)
                    .transform(new CropCircleTransformation())
                    .into(officialPhoto);
        }
        officialNameTextView.setText(officialInfoArray.get(0));
        officialPartyAndPositionTextView.setText(officialInfoArray.get(5));

        // Display Party Colors
        if (officialInfoArray.get(3).equals("Republican Party")) {
            officialNameTextView.setTextColor(colorRed);
            officialNameBorderOne.setBackgroundColor(colorRed);
            officialNameBorderTwo.setBackgroundColor(colorRed);

            officialWebsiteButton.setBackground(redButton);
            officialPhoneButton.setBackground(redButton);
            officialTwitterButton.setBackground(redButton);
            officialFacebookButton.setBackground(redButton);

        } if (officialInfoArray.get(3).equals("Democratic Party")) {
            officialNameTextView.setTextColor(colorBlue);
            officialNameBorderOne.setBackgroundColor(colorBlue);
            officialNameBorderTwo.setBackgroundColor(colorBlue);

            officialWebsiteButton.setBackground(blueButton);
            officialPhoneButton.setBackground(blueButton);
            officialTwitterButton.setBackground(blueButton);
            officialFacebookButton.setBackground(blueButton);
        }

        // CLICK THROUGH ACTIONS

            //// Website Button ////
            officialWebsiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openURL(officialInfoArray.get(7));
                }
            });

            //// Phone Button ////
            officialPhoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    officialNameTextView.setText(officialInfoArray.get(6));
                }
            });

            //// Twitter Button ////
            officialTwitterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openURL(String.format("%s%s", "https://twitter.com/", officialInfoArray.get(10)));
                }
            });

            //// Facebook Button ////
            officialFacebookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openURL(String.format("%s%s", "https://www.facebook.com/", officialInfoArray.get(9)));
                }
            });

    }

    public void openURL(String URL) {
        Intent goToURL = new Intent(Intent.ACTION_VIEW , Uri.parse(URL));
        startActivity(goToURL);
    }
}
