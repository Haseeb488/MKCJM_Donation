package com.example.donation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class AlertDialogueActivity extends AppCompatActivity {

    private Button mybutton;

    private String myText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialogue);
        mybutton = findViewById(R.id.button);

        mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showAlertDialog();
    }
    public void showAlertDialog()
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(AlertDialogueActivity.this);
        myDialog.setTitle( Html.fromHtml("<font color='#06568B'>Donation Amount</font>"));
        final EditText donationInput = new EditText(AlertDialogueActivity.this);
        donationInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        donationInput.setTextSize(17);
        donationInput.setTypeface(null, Typeface.BOLD);
        donationInput.setGravity(Gravity.CENTER);
        donationInput.setHint("Please Enter Donation Amount");
        donationInput.setTextColor(ContextCompat.getColor(AlertDialogueActivity.this,R.color.darkblue));

        myDialog.setView(donationInput);

        myDialog.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myText = donationInput.getText().toString();
                Toast.makeText(AlertDialogueActivity.this, "Donation amount is "+myText, Toast.LENGTH_SHORT).show();
            }
        });

        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        myDialog.show();
    }
        });
    }
}