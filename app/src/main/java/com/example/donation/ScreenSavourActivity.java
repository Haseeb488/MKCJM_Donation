package com.example.donation;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenSavourActivity extends AppCompatActivity {

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screensavour);

        // Hide system UI for a true "screensaver" feel
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        text = findViewById(R.id.centerText);

        // Load the animation
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_animation);

        // Start the animation
        text.startAnimation(blinkAnimation);


        // Close the activity when the screen is touched
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 finish(); // Close the black screen
                return true;
            }
        });

    }
}