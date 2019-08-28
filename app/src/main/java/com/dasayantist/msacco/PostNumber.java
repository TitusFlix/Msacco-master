package com.dasayantist.msacco;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PostNumber extends AppCompatActivity {
    EditText username;
    String str_name, str_surname, str_id, str_phone;
    private ProgressDialog progress;
    Spinner spinner;
    Button btn_reg;

    static String  msg="";
    public static String getMessage(){
        return msg;
    }
    public static void setMessage(String ms){
        msg=ms;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.et_name);
        spinner =(Spinner) findViewById(R.id.spinner1);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(PostNumber.this,
        android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.transaction));
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    public void OnReg(View view) {
        if (TextUtils.isEmpty(username.getText())) {
            username.setError("Number is Required");
        }else if(spinner.getSelectedItem().equals("Select")){
            Toast.makeText(PostNumber.this,"Please Select transaction type !!", Toast.LENGTH_LONG) .show();
            return;
        }

        else {
            username = (EditText) findViewById(R.id.et_name);
            String str_name = username.getText().toString().trim();
            msg=username.getText().toString().trim();
            String type = "register";

            GetNumber backgroungWorker = new GetNumber(this);
            backgroungWorker.execute(type, str_name);
            Toast.makeText(this, "Process Initiated", Toast.LENGTH_SHORT).show();
            //username.getText().clear();
            //startActivity(new Intent(PostNumber.this, PrincipalActivity.class));
            Intent myIntent = new Intent( this, PrincipalActivity.class);
            myIntent.putExtra("myExtra", str_name);
            startActivity(myIntent);

        }
    }
}

