package com.example.prog02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class LocationOutputActivity extends AppCompatActivity {
    TextView cityTextView, stateTextView, senatorOneFirstName, senatorOneLastNameAndParty, senatorTwoFirstName, senatorTwoLastNameAndParty,
    congressRepFirstName, congressRepLastNameAndParty;
    ImageButton senatorOneButton, senatorTwoButton, congressRepButton;
    String city, state;
    ArrayList<String> senatorOne, senatorTwo, congressRep;
    JSONObject response;
    Intent officialInfoIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_output);

        //Pre-Defined Values
        String senatorString = "U.S. Senator";
        String congressString = "U.S. Representative";
        officialInfoIntent = new Intent(LocationOutputActivity.this, RepPageActivity.class);


        // Get Intent Values
        try {
            response = new JSONObject(Objects.requireNonNull(getIntent().getStringExtra("json")));
            JSONObject civicAddress = response.getJSONObject("normalizedInput");
            JSONArray civicOfficials = response.getJSONArray("officials");
            city = civicAddress.getString("city");
            state = civicAddress.getString("state");
            senatorOne = getOfficialInfoFromArray(senatorString, civicOfficials.getJSONObject(0));
            senatorTwo = getOfficialInfoFromArray(senatorString, civicOfficials.getJSONObject(1));
            congressRep = getOfficialInfoFromArray(congressString, civicOfficials.getJSONObject(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // INSTANTIATE OBJECTS
        cityTextView = findViewById(R.id.textViewCityOutput);
        stateTextView = findViewById(R.id.textViewStateOutput);

            //// Senator One ////
            senatorOneFirstName = findViewById(R.id.textViewSenatorOneFirstName);
            senatorOneLastNameAndParty = findViewById(R.id.textViewSenatorOneLastNameAndParty);
            senatorOneButton = findViewById(R.id.buttonSenatorOne);

            //// Senator Two ////
            senatorTwoFirstName = findViewById(R.id.textViewSenatorTwoFirstName);
            senatorTwoLastNameAndParty = findViewById(R.id.textViewSenatorTwoLastNameAndParty);
            senatorTwoButton = findViewById(R.id.buttonSenatorTwo);

            //// Congress Rep. ////
            congressRepFirstName = findViewById(R.id.textViewHouseFirstName);
            congressRepLastNameAndParty = findViewById(R.id.textViewHouseLastNameAndParty);
            congressRepButton = findViewById(R.id.buttonHouse);


        // SET VALUES BASED ON INTENT VALUES
        cityTextView.setText(city);
        stateTextView.setText(state);

            //// Senator One ////
            senatorOneFirstName.setText(senatorOne.get(1));
            senatorOneLastNameAndParty.setText(senatorOne.get(4));
            Log.i("Photo Handling", senatorOne.get(8));
            if (senatorOne.get(8).length() > 0){
                Picasso.with(this)
                        .load(senatorOne.get(8))
                        .resize(150, 150)
                        .transform(new CropCircleTransformation())
                        .into(senatorOneButton);
            }

            if (senatorOne.get(3).equals("Republican Party")) {
                senatorOneButton.setBackground(ContextCompat.getDrawable(this, R.drawable.red_circle_button));
            } if (senatorOne.get(3).equals("Democratic Party")) {
                senatorOneButton.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_circle_button));
            }

            //// Senator Two ////
            senatorTwoFirstName.setText(senatorTwo.get(1));
            senatorTwoLastNameAndParty.setText(senatorTwo.get(4));
            Log.i("Photo Handling", senatorTwo.get(8));
            if (senatorTwo.get(8).length() > 0){
                Picasso.with(this)
                        .load(senatorTwo.get(8))
                        .resize(150, 150)
                        .transform(new CropCircleTransformation())
                        .into(senatorTwoButton);
            }

            if (senatorTwo.get(3).equals("Republican Party")) {
                senatorTwoButton.setBackground(ContextCompat.getDrawable(this, R.drawable.red_circle_button));
            } if (senatorTwo.get(3).equals("Democratic Party")) {
                senatorTwoButton.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_circle_button));
            }

            //// Congress Rep. ////
            congressRepFirstName.setText(congressRep.get(1));
            congressRepLastNameAndParty.setText(congressRep.get(4));
            Log.i("Photo Handling", congressRep.get(8));
            if (congressRep.get(8).length() > 0){
                Picasso.with(this)
                        .load(congressRep.get(8))
                        .resize(150, 150)
                        .transform(new CropCircleTransformation())
                        .into(congressRepButton);
            }

            if (congressRep.get(3).equals("Republican Party")) {
                congressRepButton.setBackground(ContextCompat.getDrawable(this, R.drawable.red_circle_button));
            } if (congressRep.get(3).equals("Democratic Party")) {
                congressRepButton.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_circle_button));
            }


        // CLICK THROUGH TO OFFICIAL PAGE

            //// Senator One ////
            senatorOneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent senatorOneIntent = new Intent(LocationOutputActivity.this, RepPageActivity.class);
                    senatorOneIntent.putStringArrayListExtra("officialArrayList", senatorOne);
                    startActivity(senatorOneIntent);
                }
            });

            //// Senator Two ////
            senatorTwoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent senatorTwoIntent = new Intent(LocationOutputActivity.this, RepPageActivity.class);
                    senatorTwoIntent.putStringArrayListExtra("officialArrayList", senatorTwo);
                    startActivity(senatorTwoIntent);
                }
            });

            //// Congress Rep. ////
            congressRepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent congressRepIntent = new Intent(LocationOutputActivity.this, RepPageActivity.class);
                    congressRepIntent.putStringArrayListExtra("officialArrayList", congressRep);
                    startActivity(congressRepIntent);
                }
            });
    }

    // Function to Get Official Specific Information from JSON Returned By API
    private ArrayList<String> getOfficialInfoFromArray(String stringPosition, JSONObject jsonObject){
        ArrayList<String> officialInfoArrayList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            officialInfoArrayList.add(null);
        }
        try {
            // Name and Party
            String name = jsonObject.getString("name");
            String[] nameSplit = name.split(" ");
            String firstName = nameSplit[0];
            String lastName = nameSplit[nameSplit.length-1];
            String party = jsonObject.getString("party");

            officialInfoArrayList.set(0, name);
            officialInfoArrayList.set(1, firstName);
            officialInfoArrayList.set(2, lastName);
            officialInfoArrayList.set(3, party);
            officialInfoArrayList.set(4, String.format("%s (%s)", lastName, party.charAt(0)));
            officialInfoArrayList.set(5, String.format("%s (%s)", stringPosition, party));

            // Contact Information and Platforms
            officialInfoArrayList.set(6, String.valueOf(jsonObject.getJSONArray("phones").opt(0)));
            officialInfoArrayList.set(7, String.valueOf(jsonObject.getJSONArray("urls").opt(0)));
            officialInfoArrayList.set(8, jsonObject.optString("photoUrl"));
            JSONArray channels = jsonObject.getJSONArray("channels");
            officialInfoArrayList.set(9, channels.getJSONObject(0).getString("id")); // Facebook
            officialInfoArrayList.set(10, channels.getJSONObject(1).getString("id")); // Twitter

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return officialInfoArrayList;
    }
}