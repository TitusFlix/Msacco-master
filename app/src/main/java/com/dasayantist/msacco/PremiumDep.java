package com.dasayantist.msacco;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dasayantist.msacco.interfaces.IMainActivityListener;
import com.toe.chowder.Chowder;
import com.toe.chowder.interfaces.PaymentListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PremiumDep extends BaseActivity implements PaymentListener, IMainActivityListener {

    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {}

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 1;

    //Test parameters you can replace these with your own PayBill details
    String PAYBILL_NUMBER = "877433";
    String PASSKEY = "be9dc35907bd98ee471bbfe9ddd87e724cdef18ac3eabfecdfd08f2cc4a5c3e0";

    EditText etLAmount, etPhoneNumber;
    TextView num,share ;
    Button bPay, bConfirm, bAccess;

    String mem_num = "";
    String sharetype="";
    String status="";
    String transactionstatus="";
    String paybill="";
    String date_time="";
    String amt="";
    String phone="";
    String comment="";

    int attempt_counter = 3;
    private static TextView attempts;
    // private PrefManager prefManager;
    private SharedPreference sharedPreference;
    //progress dialog
    private ProgressDialog pDialog;
    Chowder chowder;
    public String number,share_type;
    public String transaction_id="";
    private int success = 0;
    private String path = "http://192.168.100.65/sacco/deposit.php";

    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                return netInfos.isConnected();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_shares);
//        WebView webView =  findViewById(R.id.webViewMusic);
//        webView.getSettings().setJavaScriptEnabled(true);
//        WebSettings webSettings=webView.getSettings();
//        webSettings.setDefaultTextEncodingName("utf-8");
//        webView.loadUrl("file:///android_asset/MusicContent.html");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait....");
        pDialog.setCancelable(false);

        num = findViewById(R.id.num);
        share = findViewById(R.id.share);
        //etPhoneNumber = findViewById(R.id.phone);
        //txt_o_charges = findViewById(R.id.txt_O_charges);
        //txt_principal1 = findViewById(R.id.txt_principal1);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            mem_num = extras.getString("number");
            if (extras.getString("transaction").equals("SHARES1")){
                sharetype="1";
            }else {
                sharetype="2";
            }
            //sharetype = extras.getString("transaction");
        }
        /*prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            accessMainApp();
            finish();
        } */


        if (checkForConnection()){
// do something here
        }
        else{
        }


        setUp();

    }
    @Override
    public void onDestroy() {
        // You must call this or the ad adapter may cause a memory leak
        super.onDestroy();
    }

    private Boolean checkForConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void confirmLastPayment() {
        SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //We saved the last transaction id to Shared Preferences
        String transactionId = sp.getString("chowderTransactionId", null);

        //Call chowder.checkTransactionStatus to check a transaction
        //Check last transaction
        if (transactionId != null) {
            chowder.checkTransactionStatus(PAYBILL_NUMBER, transactionId);


        } else {
            Toast.makeText(getApplicationContext(), "No previous transaction available", Toast.LENGTH_SHORT).show();
        }
    }

    private void accessApp() {
        //SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //We saved the last transaction id to Shared Preferences
        //String transactionStatus = sp.getString("transactionStatus", null);

        //Call chowder.checkTransactionStatus to check a transaction
        //Check last transaction
        sharedPreference = new SharedPreference();
        String transactionStatus;


        //Retrieve a value from SharedPreference
        Activity context = this;
        transactionStatus = sharedPreference.getValue(context);


        if (transactionStatus != null) {
            //chowder.checkTransactionStatus(PAYBILL_NUMBER, transactionStatus);
            Intent n = new Intent(PremiumDep.this, MainActivity.class);
            startActivity(n);

        } else {
            Toast.makeText(getApplicationContext(), "You haven't made payment yet.No transaction data available", Toast.LENGTH_LONG).show();
            attempt_counter--;
            attempts.setText(Integer.toString(attempt_counter));
            if (attempt_counter == 0) {
                bAccess.setEnabled(false);
                android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(PremiumDep.this);
                a_builder.setMessage("Have you made your payment?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(PremiumDep.this);
                                a_builder.setMessage("Ensure your internet connection is on,then click on the confirm payment button\n" +
                                        "If your last transaction is shown on the screen ,restart app and login again\n" +
                                        "if you dont get any message call:0722000000 for further assistance").setCancelable(false)

                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(PremiumDep.this);
                                a_builder.setMessage("Ensure your internet connection is on,then  enter your phone number on the space provided\n" +
                                        "click on the pay button..it is a mpesa payment method\n" +
                                        "if your payment is successful ,restart app and press the login button").setCancelable(false)

                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        });

                android.app.AlertDialog alert = a_builder.create();
                alert.setTitle("Login Assistant");
                alert.show();
            }
        }
    }
    ////
    private void setUp() {
        chowder = new Chowder(PremiumDep.this, PAYBILL_NUMBER, PASSKEY, this);
        etLAmount = findViewById(R.id.etLAmount);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        bPay = findViewById(R.id.bPay);
        bConfirm = findViewById(R.id.bConfirm);
        bAccess = findViewById(R.id.bAccess);
        attempts = findViewById(R.id.textView_attemp_Count);
        attempts.setText(Integer.toString(attempt_counter));


        etLAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bPay.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Log.i("myTag", "This is onTextChanged");
                try {
                    double   myNum1 = Double.parseDouble(etLAmount.getText().toString());
                    double myNum20 = Double.parseDouble(num.getText().toString());
                    double myNum30 = Double.parseDouble(share.getText().toString());
                    double  tot = myNum20 + myNum30;
                    if (1==1) {
                        bPay.setEnabled(true);
                    }
                    else{
                        bPay.setEnabled(false);
                    }

                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                // Log.i("myTag", "This is afterTextChanged");
                try {
                    double   myNum1 = Double.parseDouble(etLAmount.getText().toString());
                    double myNum20 = Double.parseDouble(num.getText().toString());
                    double myNum30 = Double.parseDouble(share.getText().toString());
                    double  tot = myNum20 + myNum30;
                    if (1==1) {
                        bPay.setEnabled(true);
                    }
                    else{
                        bPay.setEnabled(false);
                    }

                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }




            }
            //String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary", "photo_uri", "mimetype"};
//        Object object = getContentResolver().query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
//                main_data, "mimetype=?",
//                new String[]{"vnd.android.cursor.item/phone_v2"},
//                "is_primary DESC");
//        if (object != null) {
//            while (((Cursor) (object)).moveToNext()) {
//                // This is the phoneNumber
//                String phoneNum = ((Cursor) (object)).getString(4);
//                etPhoneNumber.setText(phoneNum);
//            }
//            ((Cursor) (object)).close();
//        }
        });
//



        bPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegSer();
                if (etPhoneNumber.getText().toString().trim().length() <= 0 || !isNetworkStatusAvialable(getApplicationContext()) ) {

                    android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(PremiumDep .this);
                    a_builder.setMessage("Ensure your internet is turned on\n" +
                            " And  Enter Your Phone Number to make payment").setCancelable(false)

                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();





                }else {





                    String amount = etLAmount.getText().toString().trim();
                    String phoneNumber = etPhoneNumber.getText().toString().trim();
                    //Your product's ID must have 13 digits
                    String productId = "SACCO Loan";

                    chowder.processPayment(amount, phoneNumber.replaceAll("\\+", ""), productId);
                    //      That's it! You can now process payments using the M-Pesa API
                    //      IMPORTANT: Any cash you send to the test PayBill number is non-refundable, so use small amounts to test


                    //   ##What's happening:
                    //      The Merchant captures the payment details and prepares call to the SAG’s endpoint
                    //      The Merchant invokes SAG’s processCheckOut interface
                    //      The SAG validates the request sent and returns a response
                    //      Merchant receives the processCheckoutResponse parameters namely
                    //      TRX_ID, ENC_PARAMS, RETURN_CODE, DESCRIPTION and CUST_MSG (Customer message)
                    //      The merchant is supposed to display the CUST_MSG to the customer after which the merchant should invoke SAG’s confirmPaymentRequest interface to confirm the transaction
                    //      The system will push a USSD menu to the customer and prompt the customer to enter their BONGA PIN and any other validation information.
                    //      The transaction is processed on M-PESA and a callback is executed after completion of the transaction
                }     }
        });
        etLAmount.getText().clear();
        etPhoneNumber.getText().clear();





        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLastPayment();
            }
        });
        bAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessApp();
            }
        });
    }

    @Override
    public void setMode(int mode) {

    }

    @Override
    public void setTabs(List<String> tabList, TabLayout.OnTabSelectedListener onTabSelectedListener) {

    }

    @Override
    public void setFAB(int drawableId, View.OnClickListener onClickListener) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setPager(ViewPager vp, TabLayout.ViewPagerOnTabSelectedListener viewPagerOnTabSelectedListener) {

    }

    @Override
    public void setExpensesSummary(int dateMode) {

    }

    @Override
    public ActionMode setActionMode(ActionMode.Callback actionModeCallback) {
        return null;
    }

    public void RegSer() {

        share_type = sharetype;
        number = mem_num;
        phone = etPhoneNumber.getText().toString();
        date_time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        amt = etLAmount.getText().toString();
        paybill = PAYBILL_NUMBER;
        comment= "Mpesa Deposit";
        status="";
        transaction_id="";

        new PostDataTOServer().execute();
    }




    @Override
    public void onPaymentReady(String returnCode, String processDescription, String merchantTransactionId, String transactionId) {
        //The user is now waiting to enter their PIN
        //You can use the transaction id to confirm payment to make sure you store the ids somewhere if you want the user to be able to check later
        //Save the transaction ID
        SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sp.edit().putString("chowderTransactionId", transactionId).apply();
        //String transactionId1 = transactionId;
        //status=returnCode;

        new AlertDialog.Builder(PremiumDep.this)

                .setTitle("Payment in progress")
                .setMessage("Please wait for a pop up from Safaricom and enter your Bonga PIN")
                .setIcon(android.R.drawable.sym_contact_card)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Well you can skip the dialog if you want, but it will make the user feel safer, they'll know what's going on instead of sitting there
                        dialog.dismiss();
                    }
                }).show();
        //titus
        //new PostDataTOServer().execute();

    }

    @Override
    public void onPaymentSuccess(String merchantId, String msisdn, String amount, String mpesaTransactionDate, String mpesaTransactionId, String transactionStatus, String returnCode, String processDescription, String merchantTransactionId, String encParams, String transactionId) {
        //The payment was successful.
        RegSer();
        sharedPreference = new SharedPreference();
        Activity context = this;
        sharedPreference.save(context, transactionStatus);
        //sharedPreference.save(context, mpesaTransactionId);
        transaction_id=mpesaTransactionId;
        status = transactionStatus;
        new AlertDialog.Builder(PremiumDep.this)
                .setTitle("Payment confirmed")
                .setMessage(transactionStatus + ". Your amount of Ksh." + amount + " has been successfully paid from " + msisdn + " to PayBill number " + merchantId + " with the M-Pesa transaction code " + mpesaTransactionId + " on " + mpesaTransactionDate + ".\n\n iSing App\n\n Thank you for using the service\n iSing getting you Entertain")
                .setIcon(android.R.drawable.sym_contact_card)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Well you can skip the dialog if you want, but it might make the user feel safer
                        //The user has successfully paid so give them their goodies
                        dialog.dismiss();
                    }
                }).show();
        //new PostDataTOServer().execute();
    }

    @Override
    public void onPaymentFailure(String merchantId, String msisdn, String amount, String transactionStatus, String processDescription) {
        //The payment failed.
        //String transactionStatus1=transactionStatus;
        //transactionstatus=transactionStatus1;
        new AlertDialog.Builder(PremiumDep.this)
                .setTitle("Payment failed")
                .setMessage(transactionStatus + ". Your amount of Ksh." + amount + " was not paid from " + msisdn + " to PayBill number " + merchantId + ". Please try again.")
                .setIcon(android.R.drawable.sym_contact_card)
                .setCancelable(false)
                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = etLAmount.getText().toString().trim();
                        String phoneNumber = etPhoneNumber.getText().toString().trim();
                        //Your product's ID must have 13 digits
                        String productId = "sacco loan";

                        chowder.processPayment(amount, phoneNumber.replaceAll("\\+", ""), productId);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Well you can skip the dialog if you want, but it might make the user feel safer
                //The user has successfully paid so give them their goodies
                dialog.dismiss();
            }
        }).show();
        //new PostDataTOServer().execute();
    }
   /* private void accessMainApp() {
        prefManager.setFirstTimeLaunch(false);
        SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //We saved the last transaction id to Shared Preferences
        String transactionId = sp.getString("chowderTransactionId", null);

        //Call chowder.checkTransactionStatus to check a transaction
        //Check last transaction
        if (transactionId != null) {
            chowder.checkTransactionStatus(PAYBILL_NUMBER, transactionId);
            Intent i = new Intent(Premium.this, MainActivity.class);

            startActivity(i);

        } else {

            Intent j= new Intent("com.safariappreneurs.kikuyuapp.PAYMENT");
            startActivity(j);

        }
    }*/

    private class PostDataTOServer extends AsyncTask<Void, Void, Void> {

        String response = "";
        //Create hashmap Object to send parameters to web service
        HashMap<String, String> postDataParams;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                String link = path;
                String data = URLEncoder.encode("documentno", "UTF-8") + "=" +
                        URLEncoder.encode(number, "UTF-8");
                data += "&" + URLEncoder.encode("sharetype", "UTF-8") + "=" +
                        URLEncoder.encode(share_type, "UTF-8");
                data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" +
                        URLEncoder.encode(phone, "UTF-8");
                data += "&" + URLEncoder.encode("remitted", "UTF-8") + "=" +
                        URLEncoder.encode(amt, "UTF-8");
                data += "&" + URLEncoder.encode("transaction_id", "UTF-8") + "=" +
                        URLEncoder.encode(transaction_id, "UTF-8");
                data += "&" + URLEncoder.encode("transaction_status", "UTF-8") + "=" +
                        URLEncoder.encode(status, "UTF-8");
                data += "&" + URLEncoder.encode("paybill", "UTF-8") + "=" +
                        URLEncoder.encode(paybill, "UTF-8");
                data += "&" + URLEncoder.encode("comment", "UTF-8") + "=" +
                        URLEncoder.encode(comment, "UTF-8");
                data += "&" + URLEncoder.encode("date", "UTF-8") + "=" +
                        URLEncoder.encode(date_time, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                return null;
            } catch (Exception e) {
                String s = "Exception: " + e.getMessage();
                System.out.print(s);
                return null;

            }
        }


        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (success == 1) {
                Toast.makeText(getApplicationContext(), "Payment  successfully..!", Toast.LENGTH_LONG).show();
            }
        }

    }


}

