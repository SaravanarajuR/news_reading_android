package com.example.newsreading;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText edtPhone, edtOTP;
    private Button verifyOTPBtn, generateOTPBtn;
    private String verificationId;
    String phone,pass;
    String enteredCode;
    TextInputEditText n1;
    TextInputEditText n2;
    TextInputEditText n3;
    TextInputEditText n4;
    TextInputEditText n5;
    TextInputEditText n6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle i=getIntent().getExtras();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        phone=i.getString("phone");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();
        n1=findViewById(R.id.inp1);
        n2=findViewById(R.id.inp2);
        n3=findViewById(R.id.inp3);
        n4=findViewById(R.id.inp4);
        n5=findViewById(R.id.inp5);
        n6=findViewById(R.id.inp6);
        verifyOTPBtn=findViewById(R.id.verify);

                if (phone=="") {
                    Toast.makeText(otp.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    String PhoneNumber="+91"+phone;
                    sendVerificationCode(PhoneNumber);
                }


        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredCode =n1.getText().toString()+n2.getText().toString()+n3.getText().toString()+n4.getText().toString()+n5.getText().toString()+n6.getText().toString();
                if (enteredCode.length()<6) {
                    Toast.makeText(otp.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                            verifyCode(enteredCode);
                }
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(otp.this, chooseInterest.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(otp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Toast.makeText(otp.this, "code sent", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e)
        {
            Toast.makeText(otp.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }
}