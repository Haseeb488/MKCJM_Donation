package com.example.donation;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ValueAnimator;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.Callback;
import com.stripe.stripeterminal.external.callable.Cancelable;
import com.stripe.stripeterminal.external.callable.DiscoveryListener;
import com.stripe.stripeterminal.external.callable.ReaderCallback;
import com.stripe.stripeterminal.external.models.ConnectionConfiguration;
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration;
import com.stripe.stripeterminal.external.models.LocalMobileUxConfiguration;
import com.stripe.stripeterminal.external.models.Reader;
import com.stripe.stripeterminal.external.models.TerminalException;
import com.stripe.stripeterminal.log.LogLevel;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private Cancelable discoveryCancelable;
    // Replace with your actual location ID
    private static final String LOCATION_ID = "tml_FotVkAvUqTWiNp";
    private static final int PERMISSIONS_REQUEST_CODE = 1;


    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private static final int PERMISSIONS_REQUEST_CODES = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        hideNavBtn(true);

        // Initialize the ProgressBar
        progressBar = findViewById(R.id.progressBar);
        // Find the TextView
        TextView textView = findViewById(R.id.textView3);

        // Load the animation
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_animation);

        // Start the animation
        textView.startAnimation(blinkAnimation);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the current value in the text view
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 100 milliseconds to simulate work being done
                        Thread.sleep(190);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Hide ProgressBar when the task is complete
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        }).start();


        initialize();

    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
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

    private void initialize() {
        // Initialize the Terminal as soon as possible
        try {

            if (!Terminal.isInitialized()) {
                Terminal.initTerminal(getApplicationContext(), LogLevel.VERBOSE, new TokenProvider(SplashActivity.this),
                        TerminalEventListener.instance, TerminalOfflineListener.instance);

                checkPermissionsAndDiscoverReaders();

                LocalMobileUxConfiguration config = new LocalMobileUxConfiguration.Builder()
                        .tapZone(
                                new LocalMobileUxConfiguration.TapZone.Manual.Builder()
                                        .indicator(LocalMobileUxConfiguration.TapZoneIndicator.FRONT)
                                        .position(new LocalMobileUxConfiguration.TapZonePosition.Manual(0.5f, 0.5f))
                                        .build()
                        )
                        .colors(
                                new LocalMobileUxConfiguration.ColorScheme.Builder()
                                        .primary(new LocalMobileUxConfiguration.Color.Value(0xFF008686))
                                        .success(LocalMobileUxConfiguration.Color.Default.INSTANCE)
                                        .error(new LocalMobileUxConfiguration.Color.Resource(android.R.color.holo_red_dark))
                                        .build()
                        )
                        .darkMode(
                                LocalMobileUxConfiguration.DarkMode.LIGHT
                        )
                        .build();

                Terminal.getInstance().setLocalMobileUxConfiguration(config);

//                // Define red colors using hexadecimal values
//                LocalMobileUxConfiguration.Color primaryColor = new LocalMobileUxConfiguration.Color.Value(0xFF1566e0); //
//                LocalMobileUxConfiguration.Color successColor = new LocalMobileUxConfiguration.Color.Value(0xFF00FF00); // Green color for success (optional)
//                LocalMobileUxConfiguration.Color errorColor = new LocalMobileUxConfiguration.Color.Value(0xFFFF0000);
//
//                // Build the ColorScheme with specified colors
//                LocalMobileUxConfiguration.ColorScheme colorScheme = new LocalMobileUxConfiguration.ColorScheme.Builder()
//                        .primary(primaryColor)
//                        .success(successColor)
//                        .error(errorColor)
//                        .build();
//
//
//                // Define the TapZone (Default or Manual)
//                LocalMobileUxConfiguration.TapZone tapZone = LocalMobileUxConfiguration.TapZone.Default.INSTANCE; // Default tap zone
//
//
//// Example values for xBias and yBias, which represent the position within the screen as a percentage.
//                float xBias = 0.5f; // This would place the tap zone horizontally centered
//                float yBias = 0.5f; // This would place the tap zone vertically centered
//
//// Create a manual TapZonePosition
//                LocalMobileUxConfiguration.TapZonePosition manualPosition = new LocalMobileUxConfiguration.TapZonePosition.Manual(xBias, yBias);
//
//// Choose a TapZoneIndicator, e.g., DEFAULT
//                LocalMobileUxConfiguration.TapZoneIndicator tapZoneIndicator = LocalMobileUxConfiguration.TapZoneIndicator.DEFAULT;
//
//// Create a Manual TapZone using the indicator and position
//                LocalMobileUxConfiguration.TapZone manualTapZone = new LocalMobileUxConfiguration.TapZone.Manual(tapZoneIndicator, manualPosition);
//
//
//                // Define the DarkMode setting (SYSTEM, DARK, LIGHT)
//                LocalMobileUxConfiguration.DarkMode darkMode = LocalMobileUxConfiguration.DarkMode.LIGHT; // Use system default dark mode setting
//
//                // Build the LocalMobileUxConfiguration with the specified settings
//                LocalMobileUxConfiguration uxConfiguration = new LocalMobileUxConfiguration.Builder()
//                        .colors(colorScheme)
//                        .tapZone(tapZone)
//                        .darkMode(darkMode)
//                        .build();
//
//                // Apply this configuration to the Terminal
//                Terminal.getInstance().setLocalMobileUxConfiguration(uxConfiguration);

                checkPermissionsAndDiscoverReaders();

            }

        } catch (TerminalException e) {
            throw new RuntimeException(e);
        }

    }



    private void checkPermissionsAndDiscoverReaders() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//            Toast.makeText(this, "Requesting Permissions", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSIONS_REQUEST_CODE);
        } else {
            // Permissions are already granted, execute the function
            showProgressBar();
            disconnectReaderAndDiscoverReaders();
        }
    }

    private void disconnectReaderAndDiscoverReaders() {

        if (Terminal.getInstance().getConnectedReader() != null) {
            Terminal.getInstance().disconnectReader(new Callback() {
                @Override
                public void onSuccess() {
                    onDiscoverReaders();
                }

                @Override
                public void onFailure(@NonNull TerminalException e) {
                    Log.e(TAG, "Failed to disconnect reader", e);
                    // Handle disconnection failure if needed
                }
            });
        } else {
            onDiscoverReaders();
        }
    }


    public void onDiscoverReaders() {
        DiscoveryConfiguration config = new DiscoveryConfiguration.LocalMobileDiscoveryConfiguration(false);

//        Toast.makeText(this, "Starting Discovery", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Starting reader discovery");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        discoveryCancelable = Terminal.getInstance().discoverReaders(
                config,
                new DiscoveryListener() {
                    @Override
                    public void onUpdateDiscoveredReaders(@NonNull List<Reader> readers) {
                        Log.d(TAG, "Discovered " + readers.size() + " readers");
                        for (Reader reader : readers) {
                            Log.d(TAG, "Reader found: " + reader.getSerialNumber());

                            // Use the actual location ID here
                            ConnectionConfiguration.LocalMobileConnectionConfiguration config =
                                    new ConnectionConfiguration.LocalMobileConnectionConfiguration(LOCATION_ID);
                            Terminal.getInstance().connectLocalMobileReader(reader, config, new ReaderCallback() {
                                @Override
                                public void onSuccess(@NonNull Reader reader) {
                                    System.out.println("Connected to mobile device");
                                    Intent it = new Intent(SplashActivity.this,MainActivity.class);
                                    startActivity(it);
                                }

                                @Override
                                public void onFailure(@NonNull TerminalException e) {
                                    System.out.println("Not Connected to mobile device");
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                },
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Finished discovering readers");
                    }

                    @Override
                    public void onFailure(TerminalException e) {
                        Log.e(TAG, "Failed to discover readers", e);
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // All requested permissions are granted
               checkPermissionsAndDiscoverReaders();

            } else {
                // Handle the case where permissions are denied
            //    Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}