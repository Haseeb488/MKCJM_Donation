package com.example.donation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class SuccessPaymentStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_payment_status);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        hideNavBtn(true);


        String message = getIntent().getStringExtra("message");
        if (message != null) {
            Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent it = new Intent(SuccessPaymentStatusActivity.this, MainActivity.class);
            startActivity(it);
        }, 4000);
    }


    public void hideNavBtn(boolean hide) {
//        if (hide || !hide) return; //for testing other way to lock
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

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
}
