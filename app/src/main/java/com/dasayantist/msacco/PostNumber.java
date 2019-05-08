package com.dasayantist.msacco;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PostNumber extends AppCompatActivity {
    EditText username;
    String str_name, str_surname, str_id, str_phone;
    private ProgressDialog progress;
    Button btn_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.et_name);
        btn_reg = (Button) findViewById(R.id.btn_reg);

    }

    public void OnReg(View view) {
        if (TextUtils.isEmpty(username.getText())) {
            username.setError("Phone is Required");
        } else {
            username = (EditText) findViewById(R.id.et_name);
            String str_name = username.getText().toString().trim();
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

