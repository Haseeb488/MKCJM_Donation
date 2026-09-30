package com.example.donation;

import static com.unipay.api.fields.Fields.TerminalReq;
import static com.unipay.api.fields.Fields.TerminalRes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.unipay.api.fields.FieldName;
import com.uniread.pos.TerminalAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GiftAidActivity extends AppCompatActivity {

    AutoCompleteTextView firstNameTextView;
    String apiResponse;
    int selectedAmount;
    String donationAmount;
    EditText lastNameEditText, contactNumberEditText, postCodeEditText, houseNumberEditText;
    private static final String base_URL = "https://securenet.justyes.co.uk/Prod/OnPointApis/";
    String[] str = {};
    private int i;

    String firstName, lastName, contactNumber, postCode, houseNumber;
    PayResult payResult;
    TextView register;
    RequestQueue requestQueue;
    private Object item;
    private String fourDigits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_aid);

        firstNameTextView = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        contactNumberEditText = findViewById(R.id.contactNumber);
        postCodeEditText = findViewById(R.id.postalCode);
        houseNumberEditText = findViewById(R.id.houseNumber);
        register = findViewById(R.id.registerButton);

        requestQueue = Volley.newRequestQueue(getApplicationContext());


        firstNameTextView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        lastNameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        contactNumberEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        postCodeEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        houseNumberEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);


        Intent it = getIntent();
        donationAmount = it.getStringExtra("donationAmount");

        Toast.makeText(this, "donation Amount "+donationAmount, Toast.LENGTH_SHORT).show();

        getDonorsName();

        firstNameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position);

                firstName = item.toString();

                getLastFourDigits();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstNameTextView.getText().toString().isEmpty()) {
                    Toast.makeText(GiftAidActivity.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lastNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(GiftAidActivity.this, "Please enter Last Name ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (contactNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(GiftAidActivity.this, "Please enter Contact Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (postCodeEditText.getText().toString().isEmpty()) {
                    Toast.makeText(GiftAidActivity.this, "Please enter Postal Code", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (houseNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(GiftAidActivity.this, "Please enter House Number", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    firstName = firstNameTextView.getText().toString();
                    lastName = lastNameEditText.getText().toString();
                    contactNumber = contactNumberEditText.getText().toString();
                    postCode = postCodeEditText.getText().toString();
                    houseNumber = houseNumberEditText.getText().toString();

                    insertDonorsRecord();
                }

            }
        });
    }


    private void getDonorsName() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://securenet.justyes.co.uk/Prod/DonationApis/DonorsJson.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray products = new JSONArray(response);

                            str = new String[products.length()];

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);
                                String name = productObject.getString("firstName");
                                str[i] = name;
                                //  Toast.makeText(GiftAidActivity.this, ""+str[i], Toast.LENGTH_SHORT).show();
                            }


                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(GiftAidActivity.this,
                                    android.R.layout.simple_list_item_1, str);

                            firstNameTextView.setAdapter(adapter);


                        } catch (JSONException e) {

                            Toast.makeText(GiftAidActivity.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(GiftAidActivity.this, "error occured " + error, Toast.LENGTH_SHORT).show();
            }
        });


        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void getLastFourDigits() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://securenet.justyes.co.uk/Prod/DonationApis/DonorsJson.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray products = new JSONArray(response);

                            str = new String[products.length()];

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);
                                String name = productObject.getString("firstName");
                                fourDigits = productObject.getString("contactNumber");

                                if (name.contentEquals(firstName)) {

                                    fourDigits = fourDigits.substring(fourDigits.length() - 4);
                                    showWarningDialog();
                                    return;
                                }
                            }


                        } catch (JSONException e) {

                            Toast.makeText(GiftAidActivity.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(GiftAidActivity.this, "error occured " + error, Toast.LENGTH_SHORT).show();
            }
        });


        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void showWarningDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (GiftAidActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(GiftAidActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Confirmation Required");
        ((TextView) view.findViewById(R.id.textMessage)).setText("The last Four Digits of your contact number is " + fourDigits + "?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        ((ImageView) view.findViewById(R.id.imageIcon))
                .setImageResource(R.drawable.question_mark);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                selectedAmount = Integer.parseInt(donationAmount);
                saveDonationValue(selectedAmount);
                processSaleRequest(selectedAmount);

            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    public void saveDonationValue(int value)
    {
        // save data into share SharePreference
        SharedPreferences settings = getSharedPreferences("DonationInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("firstName",firstName);
        editor.putInt("DonationAmount", value/100);
        editor.commit();
    }

    private void processSaleRequest(Integer amount) {
        //set credentials
        TerminalAPI.setCredentials("api-tidypay:AGjYUb5QEZuGjRunbFEH");

        //set defaults (optional)
        Map<FieldName, Object> defaults = new HashMap<>();
        defaults.put(TerminalReq.PartialAuthorizationPolicy, "N");
        defaults.put(TerminalReq.ReceiptMode, "P");
        TerminalAPI.setDefaults(defaults);

        //set parameters
        Map<FieldName, Object> params = new HashMap<>();

        //transaction type
        params.put(TerminalReq.RequestType, unipay.api.RequestType.Sale);
        params.put(TerminalReq.TransactionIndustryType, "RE");
        params.put(TerminalReq.HolderName, "John Smith");
        params.put(TerminalReq.TransactionInternalCode, "P");
        params.put(TerminalReq.TransactionOriginCode, "");
        params.put(TerminalReq.Memo, "xyz");
        params.put(TerminalReq.CustomerAccountCode, "2001");
        params.put(TerminalReq.CustomerAccountInternalCode, "abc");
        params.put(TerminalReq.UserCode, "P");
        params.put(TerminalReq.TaxAmount, "100");
        params.put(TerminalReq.TipRecipientCode, "2001");

        //transaction amount in cent
        params.put(TerminalReq.Amount, amount);

        //(mandatory) unique reference number from POS system to prevent duplicate transactions
        params.put(TerminalReq.TransactionCode, "0000000001");

        TerminalAPI.exec(GiftAidActivity.this, params);
    }



    private void insertDonorsRecord() {

        StringRequest request = new StringRequest(Request.Method.POST, "https://securenet.justyes.co.uk/Prod/DonationApis/InsertDonors.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent it = new Intent(GiftAidActivity.this, DonationAmount.class);
                it.putExtra("firstName", firstName);
                startActivity(it);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


                Toast.makeText(GiftAidActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(GiftAidActivity.this, "error" + error, Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("firstName", firstName);
                parameters.put("lastName", lastName);
                parameters.put("contactNumber", contactNumber);
                parameters.put("postCode", postCode);
                parameters.put("houseNumber", houseNumber);
                return parameters;
            }
        };
        requestQueue.add(request);
    }


    public void getDonationValue()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("DonationInfo",MODE_PRIVATE);
        selectedAmount = sharedPreferences.getInt("DonationAmount",-1);
        firstName = sharedPreferences.getString("firstName","UnKnown");

        //  Toast.makeText(this, "first Name "+firstName, Toast.LENGTH_SHORT).show();
    }

    private void insertDonationRecord() {

        getDonationValue();

        StringRequest request = new StringRequest(Request.Method.POST, "https://securenet.justyes.co.uk/Prod/DonationApis/PaymentInsert.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                onBackPressed();
                //  Toast.makeText(DonationAmount.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(GiftAidActivity.this, "error" + error, Toast.LENGTH_SHORT).show();

                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                Toast.makeText(GiftAidActivity.this, "status code "+statusCode, Toast.LENGTH_SHORT).show();
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    Toast.makeText(GiftAidActivity.this, "body "+body, Toast.LENGTH_SHORT).show();
                    Log.d("result",body);
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String donatedAmount = String.valueOf(selectedAmount);

                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("FirstName", firstName);
                parameters.put("amount", donatedAmount);
                parameters.put("result", "apiResponse" );
                parameters.put("deviceId", "MKCJM-01");
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    private PayResult processSaleResponse(Integer resultCode, Intent intent) {
        Double amount = 0.0;
        //check result code
        PayResult payResult = new PayResult();
        if (resultCode != RESULT_OK) {
            payResult.reciept = "Error detected!";
       //     response.setText(resultCode);
            return payResult;
        }

        //extract response parameters
        Map<FieldName, String> responseMap = TerminalAPI.getResponseParameters(intent);
        String emvData = "";
        //get response code and message
        String responseCode = responseMap.get(TerminalRes.ResponseCode);
        String returnStr = responseMap.get(TerminalRes.ResponseMessage);
        String responseMessage = "Unknown";
        if (returnStr != null) responseMessage = URLDecoder.decode(returnStr.replace('+', ' '));
        String receiptTagData = responseMap.get(TerminalRes.ReceiptTagData);
        String termAmount = responseMap.get(TerminalRes.Amount);
        if (termAmount != null)
            amount = Double.valueOf(termAmount) / 100.0;
        String amountStr = String.format(Locale.US, "%.2f", amount);
        emvData = responseMessage + " (" + responseCode + ")\n";

        emvData += "Total   : " + amountStr + responseMap.get(TerminalRes.CurrencyCode) + "\n";
        emvData += "Card    : " + responseMap.get(TerminalRes.AccountNumberMasked) + "\n";
        emvData += "Type    : " + responseMap.get(TerminalRes.EntryModeType) + "\n";
        emvData += "Approval: " + responseMap.get(TerminalRes.ApprovalCode) + "\n";
        emvData += "Trans id: " + responseMap.get(TerminalRes.TransactionId) + "\n";
        emvData += "Date    : " + responseMap.get(TerminalRes.TransactionDate) + "\n";
        emvData += "Mid     : " + responseMap.get(TerminalRes.AccountId) + "\n";
        emvData += "Terminal: " + responseMap.get(TerminalRes.TerminalId) + "\n";
        emvData += responseMap.get(TerminalRes.HolderVerificationModeType) + "\n";
        if (receiptTagData != null) {
            receiptTagData = receiptTagData.replace("%3A", ":").replace("%3B", "\n");
            emvData += receiptTagData;
        }
        //handle response data (e.g. show on the screen)
        //if(responseMessage.equals("Approved")){
        if (responseCode != null && (responseCode.equals("A01") || responseCode.equals("A02"))) {
            payResult.approved = true;
            payResult.reciept = emvData;
        } else {
            payResult.reciept = responseMessage;
        }
        return payResult;
    }

}