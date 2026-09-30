package com.example.donation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback;
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider;
import com.stripe.stripeterminal.external.models.ConnectionTokenException;

public class TokenProvider implements ConnectionTokenProvider {

    private Context context;

    public TokenProvider(Context context) {
        this.context = context;
    }

    @Override
    public void fetchConnectionToken(ConnectionTokenCallback callback) {
        try {
            String token = ApiClient.createConnectionToken();
            Log.d("TokenProvider", "Original Token: " + token);
            // Store the token in SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("connection_token", token);
            editor.apply();

            callback.onSuccess(token);
        } catch (ConnectionTokenException e) {
            callback.onFailure(e);
        }
    }
}
