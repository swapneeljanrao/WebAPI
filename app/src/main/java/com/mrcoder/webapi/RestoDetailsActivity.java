package com.mrcoder.webapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RestoDetailsActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_details);
        City city = (City) getIntent().getExtras().getSerializable("city");
        mTextView = findViewById(R.id.RestoDetails);
        mTextView.setText(city.getRestoName() + "\n" + city.getCity() + "\n" + city.getAddress() + "\n" + city.getLatitude() + "\n" + city.getLongitude());

    }
}


