package com.example.donation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class DonationPurposeActivity extends AppCompatActivity {

    LinearLayout mosque, zakat, sadaqah, education;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_purpose);
        getSupportActionBar().setTitle("Select Purpose of Donation");


        mosque = findViewById(R.id.mosque);
        zakat = findViewById(R.id.zakat);
        sadaqah = findViewById(R.id.sadaqah);
        education = findViewById(R.id.education);


        mosque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DonationPurposeActivity.this, DonationAmount.class);
                it.putExtra("donationPurpose","Mosque Donation");
                startActivity(it);
            }
        });


        zakat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DonationPurposeActivity.this, DonationAmount.class);
                it.putExtra("donationPurpose","Zakat Donation");
                startActivity(it);
            }
        });

        sadaqah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DonationPurposeActivity.this, DonationAmount.class);
                it.putExtra("donationPurpose","Sadaqah Donation");
                startActivity(it);
            }
        });

        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DonationPurposeActivity.this, DonationAmount.class);
                it.putExtra("donationPurpose","Education Donation");
                startActivity(it);
            }
        });
    }
}