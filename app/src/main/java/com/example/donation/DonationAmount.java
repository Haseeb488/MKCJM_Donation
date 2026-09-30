package com.example.donation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.Cancelable;
import com.stripe.stripeterminal.external.models.TerminalException;
import com.stripe.stripeterminal.log.LogLevel;

public class DonationAmount extends AppCompatActivity {

    ScrollView scrollView;
    LinearLayout fivePounds, tenPounds, fifteenPounds, twentyPounds, customAmount;

    TextView response;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    CheckBox giftAid;
    int selectedAmount = 0;
    int donatedAmount = 0;

    RequestQueue requestQueue;
    private String firstName;
    private String donationPurpose;

    private static final String TAG = "ReaderActivity";
    private Cancelable discoveryCancelable;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_amount);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.center_title_donationpage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        scrollView = findViewById(R.id.donationScrollView);
        fivePounds = findViewById(R.id.fivePounds);
        tenPounds = findViewById(R.id.tenPounds);
        fifteenPounds = findViewById(R.id.fifteenPounds);
        twentyPounds = findViewById(R.id.twentyPounds);
        customAmount = findViewById(R.id.customAmount);

        giftAid = findViewById(R.id.giftAidcheckBox);

        response = findViewById(R.id.responsed);

        hideNavBtn(true);

        Intent it = getIntent();
        firstName = it.getStringExtra("firstName");
        donationPurpose = it.getStringExtra("donationPurpose");


        saveDonationValue(500);
        getDonationValue();

        initialize();

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideNavBtn(true);
                return false;
            }
        });
        fivePounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkBox = giftAid.isChecked();

                if (checkBox == true) {
                    Intent it = new Intent(DonationAmount.this, GiftAidActivity.class);
                    it.putExtra("donationAmount", 500);
                    it.putExtra("donationPurpose", donationPurpose);
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    return;
                } else {
                    saveDonationValue(selectedAmount);
//                    checkPermissionsAndDiscoverReaders();

                    Intent it = new Intent(DonationAmount.this, DiscoverReaderActivity.class);
                    it.putExtra("selectedAmount", 500);
                    it.putExtra("donationPurpose", donationPurpose);
                    startActivity(it);

//                    checkPermissionsAndDiscoverReaders();

                }
            }
        });

        tenPounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkBox = giftAid.isChecked();

                if (checkBox == true) {
                    Intent it = new Intent(DonationAmount.this, GiftAidActivity.class);
                    it.putExtra("donationAmount", "1000");
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    return;
                } else {
                    saveDonationValue(selectedAmount);
//                    checkPermissionsAndDiscoverReaders();

                    Intent it = new Intent(DonationAmount.this, DiscoverReaderActivity.class);
                    it.putExtra("selectedAmount",1000);
                    it.putExtra("donationPurpose", donationPurpose);
                    startActivity(it);
                }
            }
        });

        fifteenPounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkBox = giftAid.isChecked();

                if (checkBox == true) {
                    Intent it = new Intent(DonationAmount.this, GiftAidActivity.class);
                    it.putExtra("donationAmount", "1500");
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                } else {
                    saveDonationValue(selectedAmount);
//                    checkPermissionsAndDiscoverReaders();

                    Intent it = new Intent(DonationAmount.this, DiscoverReaderActivity.class);
                    it.putExtra("selectedAmount",1500);
                    it.putExtra("donationPurpose", donationPurpose);
                    startActivity(it);
                }
            }
        });

        twentyPounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkBox = giftAid.isChecked();

                if (checkBox == true) {
                    Intent it = new Intent(DonationAmount.this, GiftAidActivity.class);
                    it.putExtra("donationAmount", "2000");
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    return;
                } else {
                    Intent it = new Intent(DonationAmount.this, DiscoverReaderActivity.class);
                    it.putExtra("selectedAmount",2000);
                    it.putExtra("donationPurpose", donationPurpose);
                    startActivity(it);
                }
            }
        });

        customAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkBox = giftAid.isChecked();

                if (checkBox == true) {
                    Intent it = new Intent(DonationAmount.this, GiftAidActivity.class);
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    return;
                } else {
                    showAlertDialog();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        hideNavBtn(true);
        return super.onTouchEvent(event);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return super.onWindowStartingActionMode(callback, type);
    }

    public void hideNavBtn(boolean hide) {
//        if (hide || !hide) return; //for testing other way to lock
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_DEFAULT);

        if (hide) {
            // Hide the system bars and nav bars.
//           windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        } else {
            // Show the system bars and nav bars.
//            windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.show(WindowInsetsCompat.Type.navigationBars());
        }
    }


    /**
     * Initialize the [Terminal] and go to the [TerminalFragment]
     */
    private void initialize() {
        // Initialize the Terminal as soon as possible
        try {
            if (!Terminal.isInitialized()) {
                Terminal.initTerminal(getApplicationContext(), LogLevel.VERBOSE, new TokenProvider(DonationAmount.this),
                        TerminalEventListener.instance, TerminalOfflineListener.instance);
            }
        } catch (TerminalException e) {
            throw new RuntimeException(e);
        }

//        Toast.makeText(this, "SDK Initialized Successfully", Toast.LENGTH_SHORT).show();

    }

    public void saveDonationValue(int value) {
        // save data into share SharePreference
        SharedPreferences settings = getSharedPreferences("DonationInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("firstName", firstName);
        editor.putInt("DonationAmount", value / 100);
        editor.commit();
    }

    public void getDonationValue() {
        SharedPreferences sharedPreferences = getSharedPreferences("DonationInfo", MODE_PRIVATE);
        selectedAmount = sharedPreferences.getInt("DonationAmount", -1);
        firstName = sharedPreferences.getString("firstName", "UnKnown");

        //  Toast.makeText(this, "first Name "+firstName, Toast.LENGTH_SHORT).show();
    }



    public void showAlertDialog() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(DonationAmount.this);
        myDialog.setTitle(Html.fromHtml("<font color='#06568B'>Donation Amount</font>"));
        final EditText donationInput = new EditText(DonationAmount.this);
        donationInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        donationInput.setTextSize(17);
        donationInput.setTypeface(null, Typeface.BOLD);
        donationInput.setGravity(Gravity.CENTER);
        donationInput.setHint("Please Enter Donation Amount");
        donationInput.setTextColor(ContextCompat.getColor(DonationAmount.this, R.color.darkblue));

        myDialog.setView(donationInput);

        myDialog.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                donatedAmount = Integer.valueOf((int) (Double.valueOf(donationInput.getText().toString()) * 100.0));
                if (donatedAmount == 0) {
                    Toast.makeText(DonationAmount.this, "Please Enter Amount to Donate", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectedAmount = donatedAmount ;
                saveDonationValue(donatedAmount);

                Intent it = new Intent(DonationAmount.this, DiscoverReaderActivity.class);
                it.putExtra("selectedAmount",selectedAmount);
                startActivity(it);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_eft, R.anim.slide_to_right);
    }

    @Override
    protected void onRestart() {
      //  Toast.makeText(this, "restarted", Toast.LENGTH_SHORT).show();
        onBackPressed();
        super.onRestart();
    }
}