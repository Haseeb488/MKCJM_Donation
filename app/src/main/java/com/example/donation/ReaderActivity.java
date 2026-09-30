package com.example.donation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.Callback;
import com.stripe.stripeterminal.external.callable.Cancelable;
import com.stripe.stripeterminal.external.callable.DiscoveryListener;
import com.stripe.stripeterminal.external.callable.ReaderCallback;
import com.stripe.stripeterminal.external.models.ConnectionConfiguration;
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration;
import com.stripe.stripeterminal.external.models.Reader;
import com.stripe.stripeterminal.external.models.TerminalException;

import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity";
    private Cancelable discoveryCancelable;
    private static final int PERMISSIONS_REQUEST_CODE = 1;

    TextView readerFound;

    // Replace with your actual location ID
    private static final String LOCATION_ID = "tml_FnCi6wRKCNrZ4h";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        readerFound = findViewById(R.id.foundReader);

        checkPermissionsAndDiscoverReaders();
    }

    private void checkPermissionsAndDiscoverReaders() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Requesting Permissions", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSIONS_REQUEST_CODE);
        } else {
            onDiscoverReaders();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                onDiscoverReaders();
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isApplicationDebuggable() {
        return 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
    }

    public void onDiscoverReaders() {
        DiscoveryConfiguration config = new DiscoveryConfiguration.LocalMobileDiscoveryConfiguration(
                false);

        Toast.makeText(this, "Starting Discovery", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Starting reader discovery");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
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


                                    Intent it = new Intent(ReaderActivity.this, PaymentActivity.class);
                                    startActivity(it);
                                }

                                @Override
                                public void onFailure(@NonNull TerminalException e) {
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
}
