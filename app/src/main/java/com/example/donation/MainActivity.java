package com.example.donation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback;
import com.stripe.stripeterminal.external.models.CaptureMethod;
import com.stripe.stripeterminal.external.models.CollectConfiguration;
import com.stripe.stripeterminal.external.models.PaymentIntent;
import com.stripe.stripeterminal.external.models.PaymentIntentParameters;
import com.stripe.stripeterminal.external.models.Reader;
import com.stripe.stripeterminal.external.models.TerminalException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button giftAid;
    ImageView donateNow;
    private WebView webView;
    String fridayDefaultAmount;
    private String defaultAmount;
    private String mode;

    int s;

    private boolean isNavigatingAway = false;
    private String currentMode = "Default Mode";
    private String registeredDeviceName = "";
    private String deviceID = "";

    private static final long INACTIVITY_DELAY = 1800000; // 30 minutes

    private Handler handler;
    private Runnable inactivityRunnable;

    RequestQueue requestQueue;
    TextView deviceNameText;
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 100;
    String device_name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//            dpm.clearDeviceOwnerApp(getPackageName());


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            // 1. Inflate the XML layout into a View object
            View customView = getLayoutInflater().inflate(R.layout.center_title_webpage, null);

            // 2. Define the LayoutParams (Match Parent to ensure it fills the width)
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);

            // 3. Pass the View object and the params
            getSupportActionBar().setCustomView(customView, params);
        }

        enableKioskMode();
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.center_title_webpage);


        requestQueue = Volley.newRequestQueue(getApplicationContext());


        handler = new Handler();
        inactivityRunnable = new Runnable() {
            @Override
            public void run() {
                // Start BlackScreenActivity after inactivity
                startActivity(new Intent(MainActivity.this, ScreenSavourActivity.class));
            }
        };

        resetInactivityTimer();


        s = 0;

        hideNavBtn(true);

        donateNow = findViewById(R.id.donateNow);
        giftAid = findViewById(R.id.giftAidButton);
        deviceNameText = findViewById(R.id.leftTextView);

        showAnimation();

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://securenet.justyes.co.uk/UAT/iPay/MKCJM/index.html");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        SharedPreferences sharedPreferencesValue =
                getSharedPreferences("MyPrefs", MODE_PRIVATE);

        boolean isDeviceIdFound = sharedPreferencesValue.getBoolean("DEVICE_STATUS", false);
        deviceID = sharedPreferencesValue.getString("DEVICE_ID", "DEFAULT");
        device_name = sharedPreferencesValue.getString("DEVICE_NAME", "DEFAULT");

        if (!isDeviceIdFound) {
            getAvailableDevices();
        } else {
            deviceNameText.setText(device_name);
        }



        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideNavBtn(true);
                return false;
            }
        });

        giftAid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, GiftAidActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

        // AnimateButton();

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        mode = sharedPreferences.getString("Mode", "Normal Mode");
        defaultAmount = sharedPreferences.getString("defaultAmount", "Default Friday");

        if (mode.contentEquals("Friday Mode")) {
            createPaymentIntent(Integer.parseInt(defaultAmount) * 100);
        }

        donateNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                checkAndReconnectReader();

                if (AppStatus.getInstance(MainActivity.this).isOnline()) {

                    if (mode.contentEquals("Normal Mode")) {
                        isNavigatingAway = true; // Set the flag
                        //      Intent it = new Intent(MainActivity.this, DonationPurposeActivity.class);
                        Intent it = new Intent(MainActivity.this, DonationAmount.class);
                        startActivity(it);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    } else {

                        currentMode = "Normal";

                        SharedPreferences settings = getSharedPreferences("UserInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("Mode", "Normal Mode");
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Normal Mode set Successfully", Toast.LENGTH_SHORT).show();
                        recreate();
                    }
                } else {
                    InternetAlert();
                }
            }
        });

        OnTheTopRequest();

        startService(new Intent(MainActivity.this, MyService.class));
    }


    private void getAvailableDevices() {


//        String androidId = GlobalVariablesClass.getDeviceId();
        String androidId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://securenet.justyes.co.uk/Prod/DonationApis/devicesJson.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray products = new JSONArray(response);
                            boolean idFound = false;

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);
                                deviceID = productObject.getString("DeviceID");
                                registeredDeviceName = productObject.getString("DeviceName");

                                if (deviceID.equals(androidId)) {
                                    idFound = true;
                                    break;
                                }
                            }

                            if (idFound) {

                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("DEVICE_STATUS", true);
                                editor.putString("DEVICE_ID", deviceID);
                                editor.putString("DEVICE_NAME", registeredDeviceName);
                                editor.apply();   // or editor.commit();

                                deviceNameText.setText(registeredDeviceName);

                            } else {
                                //ask for Device Name
                                showDeviceNameAlertDialog(androidId);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "error occurred " + error, Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).

                add(stringRequest);

    }

    public void checkAndReconnectReader() {
        Reader connectedReader = Terminal.getInstance().getConnectedReader();
        if (connectedReader == null) {
            Log.d("ReaderStatus", "Reader is disconnected. Reconnecting...");
            restartApp(MainActivity.this);
        } else {
            Log.d("ReaderStatus", "Reader is still connected: " + connectedReader.getSerialNumber());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Reset timer on user interaction
        resetInactivityTimer();
        return super.dispatchTouchEvent(ev);
    }

    private void resetInactivityTimer() {
        // Remove previous callbacks and reset the timer
        handler.removeCallbacks(inactivityRunnable);
        handler.postDelayed(inactivityRunnable, INACTIVITY_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(inactivityRunnable);
    }


    public void OnTheTopRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                permissionAlert();
            }
        }
    }


    public static void restartApp(Activity activity) {
        Intent intent = activity.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
            Runtime.getRuntime().exit(0); // Ensures the app fully restarts
        }
    }

    /*
       public void permissionAlert() {

           android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);

           builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Permission Required</font>"))
                   .setMessage(Html.fromHtml("<font color='#FFFFFF'>Donation App required permission to draw on screen, on the next page scroll down to Donation app and allow permission.</font>"))

                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                           Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                           startActivityForResult(intent, 0);

                       }
                   })
                   .setNegativeButton("", null);
           android.app.AlertDialog alert = builder.create();
           alert.show();
           alert.setCancelable(false);
           alert.getWindow().setBackgroundDrawable(new ColorDrawable(parseColor("#1174d1")));
       }
   */
    public void permissionAlert() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);

        builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>Permission Required</font>"))
                .setMessage(Html.fromHtml("<font color='#FFFFFF'>Donation App requires permission to draw on the screen. On the next page, scroll down to the Donation app and allow permission.</font>"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
                    }
                })
                .setNegativeButton("", null);

        android.app.AlertDialog alert = builder.create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1174d1")));
    }

    // This method will be called when the user returns from the overlay permission screen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                // Permission granted, now start lock task (Kiosk mode)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

                    ComponentName admin =
                            new ComponentName(this, MyDeviceAdminReceiver.class);

                    if (dpm.isDeviceOwnerApp(getPackageName())) {



                        // Allow ONLY this app
//                        dpm.setLockTaskPackages(admin, new String[]{getPackageName()});
                        dpm.setLockTaskPackages(admin, new String[]{
                                getPackageName(),
                                "com.android.settings"
                        });

                        // Disable status bar
                        dpm.setStatusBarDisabled(admin, true);

                        // Block system escapes
                        dpm.addUserRestriction(admin, UserManager.DISALLOW_SYSTEM_ERROR_DIALOGS);
                        dpm.addUserRestriction(admin, UserManager.DISALLOW_FACTORY_RESET);
                        dpm.addUserRestriction(admin, UserManager.DISALLOW_SAFE_BOOT);
                        dpm.addUserRestriction(admin, UserManager.DISALLOW_ADD_USER);
                        startLockTask();
                    }

                }
            } else {
                // Permission not granted, you can handle it accordingly, maybe show a message to the user.
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableKioskMode() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        DevicePolicyManager dpm =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName admin =
                new ComponentName(this, MyDeviceAdminReceiver.class);

        // Make sure this app is actually Device Owner
        if (!dpm.isDeviceOwnerApp(getPackageName())) {
            Toast.makeText(this,
                    "App is NOT Device Owner",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Allow this app to enter Lock Task Mode
        dpm.setLockTaskPackages(
                admin,
                new String[]{getPackageName(),
                        "com.android.settings" // Allows launch and interaction with Settings
                 }
        );

        // Android 9+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Allow System Info (Clock, Status Icons) and Quick Settings access
            dpm.setLockTaskFeatures(
                    admin,
                    DevicePolicyManager.LOCK_TASK_FEATURE_SYSTEM_INFO |
                            DevicePolicyManager.LOCK_TASK_FEATURE_GLOBAL_ACTIONS
            );
        }
        // Disable status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dpm.setStatusBarDisabled(admin, true);
        }

        // Disable lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dpm.setKeyguardDisabled(admin, true);
        }

        // Prevent various ways of escaping/configuring the device
        dpm.addUserRestriction(
                admin,
                UserManager.DISALLOW_SYSTEM_ERROR_DIALOGS
        );

        dpm.addUserRestriction(
                admin,
                UserManager.DISALLOW_FACTORY_RESET
        );

        dpm.addUserRestriction(
                admin,
                UserManager.DISALLOW_SAFE_BOOT
        );

        dpm.clearUserRestriction(admin, UserManager.DISALLOW_CONFIG_WIFI);
        dpm.clearUserRestriction(admin, UserManager.DISALLOW_CHANGE_WIFI_STATE);
        dpm.clearUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH);
        dpm.clearUserRestriction(admin, UserManager.DISALLOW_CONFIG_BLUETOOTH);
        dpm.clearUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH_SHARING);




        // Finally enter kiosk mode
        startLockTask();
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


    public void InternetAlert() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);

        // Set title with white color
        SpannableString title = new SpannableString("No Internet Connection");
        title.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), 0);
        builder.setTitle(title);

        // Set message with white color
        SpannableString message = new SpannableString("Check your Internet Connection or Try again");
        message.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message.length(), 0);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNegativeButton("", null);

        android.app.AlertDialog alert = builder.create();
        alert.show();

        // Set background color of the dialog
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1174d1")));
    }


    @Override
    protected void onPause() {
        if (!isNavigatingAway) {
            BackgroundPendingIntent();
        }
        super.onPause();
    }


    @Override
    protected void onPostResume() {
        //  Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
        s = 0;
        //  stopService(new Intent(MainActivity.this,MyService.class));
        super.onPostResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.normalMode:
                showNormalModeMessage();
                break;

            case R.id.fridayMode:
                showFridayModeMessage();
                break;

            case R.id.deviceID:
                showDeviceIDAlertDialog();
                break;

            case R.id.exitApp:
                showExitPasswordAlertDialog();
                break;
        }
        return true;
    }


    public void showDeviceIDAlertDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Device ID")
                .setMessage(deviceID)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                })
                .setNegativeButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ClipboardManager clipboard =
                                (ClipboardManager) MainActivity.this
                                        .getSystemService(Context.CLIPBOARD_SERVICE);

                        ClipData clip = ClipData.newPlainText("copied_text", deviceID);
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });

        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void BackgroundPendingIntent() {
        //    Toast.makeText(this, "background Main", Toast.LENGTH_SHORT).show();

        if (isNavigatingAway) {
            Intent intent = new Intent(this, MainActivity.class);
            // intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 100,
                    intent, PendingIntent.FLAG_IMMUTABLE);

            final long DELAY_IN_MILLIS = 2000 + System.currentTimeMillis();
            AlarmManager alarmManager = (AlarmManager)
                    getSystemService(Activity.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, DELAY_IN_MILLIS, pendingIntent);

        }
    }


    public void showNormalModeMessage() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Normal Mode")
                .setMessage("Normal Mode will allow Donors to select amount from the menu they want to donate." + "\n" + "\n" +
                        "Are you sure to Select Normal Mode?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showNormalModePasswordAlert();
//                        SharedPreferences settings = getSharedPreferences("UserInfo", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString("Mode", "Normal Mode");
//                        editor.commit();
//                        Toast.makeText(MainActivity.this, "Normal Mode set Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mode.contentEquals("Friday Mode")) {

                        }
                    }
                });

        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void showFridayModeMessage() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Friday Mode")
                .setMessage("Friday Mode will not allow Donors to select amount from the menu rather a default amount needs to be set in Friday Mode and each time set amount will be deducted from the donors account." + "\n" + "\n" +
                        "Are you sure to Select Friday Mode?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showFridayModePasswordAlert();
                    }
                })
                .setNegativeButton("Cancel", null);

        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void showFridayModeAlertDialog() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle(Html.fromHtml("<font color='#06568B'>Friday Mode</font>"));
        final EditText donationInput = new EditText(MainActivity.this);
        donationInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        donationInput.setTextSize(17);
        donationInput.setTypeface(null, Typeface.BOLD);
        donationInput.setGravity(Gravity.CENTER);
        donationInput.setHint("Enter Default Donation Amount");
        donationInput.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.darkblue));

        myDialog.setView(donationInput);

        myDialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fridayDefaultAmount = donationInput.getText().toString();

                if (fridayDefaultAmount.contentEquals("")) {
                    Toast.makeText(MainActivity.this, "Please Enter Friday Default Donation Amount", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences settings = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Mode", "Friday Mode");
                    editor.putString("defaultAmount", fridayDefaultAmount);
                    boolean isCommitted = editor.commit();

                    if (isCommitted) {
                        Toast.makeText(MainActivity.this, "Default Amount set to £" + fridayDefaultAmount, Toast.LENGTH_SHORT).show();
                        recreate();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to save default amount", Toast.LENGTH_SHORT).show();
                    }
                }

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

    public void showFridayModePasswordAlert() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle(Html.fromHtml("<font color='#06568B'>Friday Mode</font>"));
        final EditText passwordInput = new EditText(MainActivity.this);
        passwordInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        passwordInput.setTextSize(17);
        passwordInput.setTypeface(null, Typeface.BOLD);
        passwordInput.setGravity(Gravity.CENTER);
        passwordInput.setHint("Enter Password to switch to Friday Mode");
        passwordInput.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.darkblue));

        myDialog.setView(passwordInput);

        myDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString();

                if (password.contentEquals("")) {
                    Toast.makeText(MainActivity.this, "Please Enter Password to Exit App", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }
                if (password.contentEquals(".321.")) {
                    showFridayModeAlertDialog();

                } else {
                    Toast.makeText(MainActivity.this, "Incorrect Password. Try Again!", Toast.LENGTH_SHORT).show();
                }

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

    public void showNormalModePasswordAlert() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle(Html.fromHtml("<font color='#06568B'>Normal Mode</font>"));
        final EditText passwordInput = new EditText(MainActivity.this);
        passwordInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        passwordInput.setTextSize(17);
        passwordInput.setTypeface(null, Typeface.BOLD);
        passwordInput.setGravity(Gravity.CENTER);
        passwordInput.setHint("Enter Password to switch to Normal Mode");
        passwordInput.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.darkblue));

        myDialog.setView(passwordInput);

        myDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString();

                if (password.contentEquals("")) {
                    Toast.makeText(MainActivity.this, "Please Enter Password to Exit App", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }
                if (password.contentEquals(".321.")) {
                    SharedPreferences settings = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Mode", "Normal Mode");
                    editor.commit();
                    Toast.makeText(MainActivity.this, "Normal Mode set Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect Password. Try Again!", Toast.LENGTH_SHORT).show();
                }

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

    public void showDeviceNameAlertDialog(String androidID) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle(Html.fromHtml("<font color='#FFFFFF'>Device Name Not Registered</font>"));

        final EditText deviceNameInputField = new EditText(MainActivity.this);

        deviceNameInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String upper = s.toString().toUpperCase();
                if (!upper.equals(s.toString())) {
                    deviceNameInputField.setText(upper);
                    deviceNameInputField.setSelection(upper.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        deviceNameInputField.setTextColor(
                ContextCompat.getColor(this, R.color.white)
        );
        deviceNameInputField.setGravity(Gravity.CENTER);
        deviceNameInputField.getBackground().setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_IN
        );
        deviceNameInputField.setHint(Html.fromHtml("<font color='#FFFFFF'>Enter Unique Device Name</font>"));

        myDialog.setView(deviceNameInputField);

        myDialog.setPositiveButton("Register", (dialog, which) -> {
            String uniqueDeviceName = deviceNameInputField.getText().toString();

            if (uniqueDeviceName.isEmpty()) {
                Toast.makeText(this, "Please Enter unique device name", Toast.LENGTH_SHORT).show();
            } else {
                registerDeviceName(androidID, uniqueDeviceName);
            }
        });

        myDialog.setNegativeButton("EXIT", (dialog, which) -> {
            stopService(new Intent(this, MyService.class));
            finishAffinity();
            System.exit(0);
        });

        // 🔥 Create dialog (NOT show yet)
        AlertDialog dialog = myDialog.create();

        // 🔒 Make dialog NON-dismissable
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // 🔥 Show dialog
        dialog.show();

        // 🔥 SET BACKGROUND COLOR TO RED
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.darkblue))
            );
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(Color.WHITE);

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(Color.WHITE);
    }


    private void registerDeviceName(String deviceID, String deviceName) {

        String url = "https://securenet.justyes.co.uk/Prod/DonationApis/registerDevice.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    // Handle PHP response
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    deviceNameText.setText(deviceName);

                },
                error -> {

                    String message = "Unknown error";

                    if (error.networkResponse != null) {
                        message = "Status Code: " + error.networkResponse.statusCode;

                        if (error.networkResponse.data != null) {
                            message += "\n" + new String(error.networkResponse.data);
                        }
                    } else if (error.getMessage() != null) {
                        message = error.getMessage();
                    }

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("DeviceID", deviceID);
                params.put("DeviceName", deviceName);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public void showExitPasswordAlertDialog() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle(Html.fromHtml("<font color='#06568B'>Exit App</font>"));
        final EditText passwordInput = new EditText(MainActivity.this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT);
        passwordInput.setTextSize(17);
        passwordInput.setTypeface(null, Typeface.BOLD);
        passwordInput.setGravity(Gravity.CENTER);
        passwordInput.setHint("Enter Password to Exit App");
        passwordInput.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.darkblue));

        myDialog.setView(passwordInput);

        myDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString();

                if (password.contentEquals("")) {
                    Toast.makeText(MainActivity.this, "Please Enter Password to Exit App", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }
                if (password.contentEquals(".321.")) {
                    stopService(new Intent(MainActivity.this, MyService.class));
                    finishAffinity();
                    System.exit(0);
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect Password. Try Again!", Toast.LENGTH_SHORT).show();
                }

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
                Toast.makeText(MainActivity.this, "here we go ", Toast.LENGTH_SHORT).show();
            }
        }, collectConfiguration);
    }

    private void createPaymentIntent(int defaultAmount) {
        PaymentIntentParameters params = new PaymentIntentParameters.Builder()
                .setAmount(defaultAmount)
                .setCurrency("gbp")
                .setCaptureMethod(CaptureMethod.Automatic)
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

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve the mode from SharedPreferences
        SharedPreferences settings = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String mode = settings.getString("Mode", "Default Mode"); // Use "Default Mode" as a fallback if "Mode" is not found

        // Delay of 5000 milliseconds (5 seconds)
        int delayMillis = 5000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentMode == "Normal") {
                    return;
                }

                if (mode.contentEquals("Friday Mode")) {
                    createPaymentIntent(Integer.parseInt(defaultAmount) * 100);
                }
            }
        }, delayMillis);
    }

    public void showAnimation() {
        // Load the GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.donate_button)
                .into(donateNow);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }
}

