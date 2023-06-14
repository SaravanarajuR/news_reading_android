package com.example.newsreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class register extends AppCompatActivity {
    TextInputEditText t1;
    TextInputEditText t2;
    TextInputEditText t3;
    Button b;
    Button b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        t1=findViewById(R.id.inp1);
        t2=findViewById(R.id.inp2);
        t3=findViewById(R.id.inp3);
        b2=findViewById(R.id.red);
        b=findViewById(R.id.sub);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=String.valueOf(t1.getText());
                String pass =String.valueOf(t2.getText());
                String cpass =String.valueOf(t3.getText());
                Toast.makeText(register.this, phone, Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(register.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(pass) && TextUtils.isEmpty(cpass)){
                    Toast.makeText(register.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.equals(pass,cpass)) {
                    Intent j=new Intent(getApplicationContext(),otp.class);
                    j.putExtra("phone",phone);
                    j.putExtra("pass",pass);
                    startActivity(j);
                }else{
                    Toast.makeText(register.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),login.class);
                startActivity(i);
            }
        });
    }
}