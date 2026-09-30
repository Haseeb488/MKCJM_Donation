package com.example.donation;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback;
import com.stripe.stripeterminal.external.models.CaptureMethod;
import com.stripe.stripeterminal.external.models.CollectConfiguration;
import com.stripe.stripeterminal.external.models.PaymentIntent;
import com.stripe.stripeterminal.external.models.PaymentIntentParameters;
import com.stripe.stripeterminal.external.models.TerminalException;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Ensure Terminal is initialized
        if (Terminal.getInstance() == null) {
            Log.e("StripeTerminal", "Terminal not initialized!");
            return;
        }
        createPaymentIntent();
    }

    private void createPaymentIntent() {
        PaymentIntentParameters params = new PaymentIntentParameters.Builder()
                .setAmount(1500)
                .setCurrency("gbp")
                .setCaptureMethod(CaptureMethod.Automatic)
                .setDescription("For Mosque Donation")
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
                confirmPaymentIntent(paymentIntent);
            }

            @Override
            public void onFailure(TerminalException exception) {
                Log.e("StripeTerminal", "Failed to collect payment method: " + exception.getMessage());
                // Handle the error
            }
        }, collectConfiguration);
    }

    private void confirmPaymentIntent(PaymentIntent paymentIntent) {
        Terminal.getInstance().confirmPaymentIntent(paymentIntent, new PaymentIntentCallback() {
            @Override
            public void onSuccess(PaymentIntent paymentIntent) {
                Log.d("StripeTerminal", "Payment successful! PaymentIntent ID: " + paymentIntent.getId());
                // Notify your backend to capture paymentIntent.id
            }

            @Override
            public void onFailure(TerminalException exception) {
                Log.e("StripeTerminal", "Failed to confirm payment intent: " + exception.getMessage());
                // Handle the error
            }
        });
    }
}
