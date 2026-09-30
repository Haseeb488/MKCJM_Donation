package com.example.donation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.Callback;
import com.stripe.stripeterminal.external.callable.Cancelable;
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback;
import com.stripe.stripeterminal.external.models.CaptureMethod;
import com.stripe.stripeterminal.external.models.Charge;
import com.stripe.stripeterminal.external.models.CollectConfiguration;
import com.stripe.stripeterminal.external.models.PaymentIntent;
import com.stripe.stripeterminal.external.models.PaymentIntentParameters;
import com.stripe.stripeterminal.external.models.TerminalException;

import java.util.HashMap;
import java.util.Map;

public class DiscoverReaderActivity extends AppCompatActivity {


    private static final String TAG = "DiscoverReaderActivity";
    private Cancelable discoveryCancelable;

    ImageView tapCardImageView;
    private int selectedAmount;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_reader);

        // Hide the title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide both the navigation bar and the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        tapCardImageView = findViewById(R.id.tapCardGif);

        TextView textView = findViewById(R.id.textView2);


        // Retrieve the intent that started this activity
        Intent intent = getIntent();

        // Extract the value using the key "selectedAmount"
        selectedAmount = intent.getIntExtra("selectedAmount", 700);
        //   donationPurpose = intent.getStringExtra("donationPurpose");

        //   Toast.makeText(this, "last "+donationPurpose, Toast.LENGTH_SHORT).show();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Create an ObjectAnimator to animate the alpha property
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        animator.setDuration(1000); // Duration of one blink
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE); // Repeat indefinitely

        // Start the animation
        animator.start();
        runOnUiThread(() -> showAnimation());

        createPaymentIntent(selectedAmount);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Hide both the navigation bar and the status bar
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void showAnimation() {
        // Load the GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.tapcard)
                .into(tapCardImageView);
    }


    private void createPaymentIntent(int selectedAmount) {
        PaymentIntentParameters params = new PaymentIntentParameters.Builder()
                .setAmount(selectedAmount)
                .setCurrency("gbp")
                .setCaptureMethod(CaptureMethod.Automatic)
                .setDescription("donationPurpose")
                .build();

        Terminal.getInstance().createPaymentIntent(params, new PaymentIntentCallback() {
            @Override
            public void onSuccess(PaymentIntent paymentIntent) {


                collectPaymentMethod(paymentIntent);
            }

            @Override
            public void onFailure(TerminalException exception) {
                Log.e("StripeTerminal", "Failed to create PaymentIntent: " + exception.getMessage());
                // Handle the error
            }
        });
    }

    private void collectPaymentMethod(PaymentIntent paymentIntent) {
        CollectConfiguration collectConfiguration = new CollectConfiguration.Builder().build();

        Terminal.getInstance().collectPaymentMethod(paymentIntent, new PaymentIntentCallback() {
            @Override
            public void onSuccess(PaymentIntent paymentIntent) {

                // Now confirm
                confirmPaymentIntent(paymentIntent);
            }

            @Override
            public void onFailure(TerminalException exception) {
                Log.e("StripeTerminal", "Failed to collect payment method: " + exception.getMessage());

                runOnUiThread(new Runnable() {
                    public void run() {

                        Intent intent = new Intent(DiscoverReaderActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();


//                        Toast.makeText(DiscoverReaderActivity.this, "Error: "+paymentConfirmationError, Toast.LENGTH_LONG).show();
                    }
                });
                // Handle the error
            }
        }, collectConfiguration);
    }
/*
    private void confirmPaymentIntent(PaymentIntent paymentIntent) {

        Terminal.getInstance().confirmPaymentIntent(paymentIntent, new PaymentIntentCallback() {
            @Override
            public void onSuccess(PaymentIntent paymentIntent) {
                Log.d("StripeTerminal", "Payment successful! PaymentIntent ID: " + paymentIntent.getId());

                runOnUiThread(new Runnable() {
                    public void run() {
                        showSuccessPaymentStatusActivity();
//                        Toast.makeText(DiscoverReaderActivity.this, "Error: "+paymentConfirmationError, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(TerminalException exception) {
                Log.e("StripeTerminal", "Failed to confirm payment intent: " + exception.getMessage());

                runOnUiThread(new Runnable() {
                    public void run() {
                        showFailedPaymentStatusActivity(exception.getErrorMessage());
//                        Toast.makeText(DiscoverReaderActivity.this, "Error: "+paymentConfirmationError, Toast.LENGTH_LONG).show();
                    }
                });
                // Handle the error
            }
        });
    }
*/


    private void confirmPaymentIntent(PaymentIntent paymentIntent) {
        Terminal.getInstance().confirmPaymentIntent(paymentIntent, new PaymentIntentCallback() {
            @Override
            public void onSuccess(@NonNull PaymentIntent confirmedIntent) {
                String capturedBrand = "Unknown";

                // The most reliable way to get the brand after confirmation is via the Charges list
                if (confirmedIntent.getCharges() != null && !confirmedIntent.getCharges().isEmpty()) {
                    Charge charge = confirmedIntent.getCharges().get(0);
                    if (charge.getPaymentMethodDetails() != null &&
                            charge.getPaymentMethodDetails().getCardPresentDetails() != null) {
                        capturedBrand = charge.getPaymentMethodDetails().getCardPresentDetails().getBrand();
                    }
                }
                // Fallback: Check the direct PaymentMethod
                else if (confirmedIntent.getPaymentMethod() != null &&
                        confirmedIntent.getPaymentMethod().getCardPresentDetails() != null) {
                    capturedBrand = confirmedIntent.getPaymentMethod().getCardPresentDetails().getBrand();
                }

                final String finalBrand = capturedBrand;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("StripeTerminal", "Payment Success! Brand: " + finalBrand);
//                        Toast.makeText(DiscoverReaderActivity.this, "Card: " + finalBrand, Toast.LENGTH_SHORT).show();

                        insertDonationRecord("success", finalBrand,"ok");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull TerminalException exception) {

                // This method MUST be exactly like this to satisfy the ErrorCallback interface
                Log.e("StripeTerminal", "Failed to confirm: " + exception.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(DiscoverReaderActivity.this, "Exception "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                        String cleanError = exception.getMessage() != null ? exception.getMessage() : "Unknown Error";
// Remove single quotes as a quick test to see if it fixes the 500 error
                        cleanError = cleanError.replace("'", "");
                        insertDonationRecord("failed","unknown",cleanError);
                    }
                });
            }
        });
    }

    private void showSuccessPaymentStatusActivity() {
        Intent intent = new Intent(DiscoverReaderActivity.this, SuccessPaymentStatusActivity.class);
        startActivity(intent);
        finish();
    }

    private void showFailedPaymentStatusActivity(String message) {
        Intent intent = new Intent(DiscoverReaderActivity.this, FailedPaymentStatusActivity.class);
        intent.putExtra("message", message);
        startActivity(intent);
        finish();
    }


    private void insertDonationRecord(String TransactionStatus, String CardType, String StripeResponse) {


        StringRequest request = new StringRequest(Request.Method.POST, "https://securenet.justyes.co.uk/Prod/DonationApis/insertTransaction.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(TransactionStatus.equals("success"))
                {
                    showSuccessPaymentStatusActivity();
                }
                else
                {
                    showFailedPaymentStatusActivity(StripeResponse);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(DiscoverReaderActivity.this, "error" + error, Toast.LENGTH_SHORT).show();

                Log.d("Volley Error","Error in inserting data"+error.getMessage());

                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                Toast.makeText(DiscoverReaderActivity.this, "status code " + statusCode, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                SharedPreferences sharedPreferencesValue =
                        getSharedPreferences("MyPrefs", MODE_PRIVATE);

                String deviceID = sharedPreferencesValue.getString("DEVICE_ID", "DEFAULT");
                String deviceName = sharedPreferencesValue.getString("DEVICE_NAME", "DEFAULT");


                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("DeviceID", deviceID);
                parameters.put("DeviceName", deviceName);
                parameters.put("Amount", String.valueOf(selectedAmount/100));
                parameters.put("CardType", CardType);
                parameters.put("Status", TransactionStatus);
                parameters.put("StripeResponse", StripeResponse);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();

        if (discoveryCancelable != null) {
            discoveryCancelable.cancel(new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Discovery cancelled");
                }

                @Override
                public void onFailure(@NonNull TerminalException e) {
                    Log.e(TAG, "Failed to cancel discovery", e);
                }
            });
        }
    }

     */

    @Override
    protected void onStop() {
        super.onStop();

        if (discoveryCancelable != null) {
            discoveryCancelable.cancel(new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Discovery cancelled");
                }

                @Override
                public void onFailure(@NonNull TerminalException e) {
                    if (e.getMessage().contains("Operation completed before it could be canceled")) {
                        Log.d(TAG, "Discovery already completed, no need to cancel");
                    } else {
                        Log.e(TAG, "Failed to cancel discovery", e);
                    }
                }
            });
        }
    }


}