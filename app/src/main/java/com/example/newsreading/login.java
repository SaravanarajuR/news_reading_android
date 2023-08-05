package com.example.newsreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {
    TextInputEditText t1;
    TextInputEditText t2;
    Button sendOtp,submit;
    FirebaseAuth mAuth;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference dr;
    String enteredCode;
    TextView timer;
    private String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);


        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null) {
            dr=db.collection("user").document(FirebaseAuth.getInstance().getUid());
            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Intent i;
                    DocumentSnapshot res=task.getResult();
                    if(res!=null) {
                        i = new Intent(getApplicationContext(), intro.class);
                        startActivity(i);
                    }else{
                        i = new Intent(getApplicationContext(), chooseInterest.class);
                        startActivity(i);
                    }
                }
            });
        } else {
            sendOtp = findViewById(R.id.send);
            submit=findViewById(R.id.sub);
            t1 = findViewById(R.id.inp1);
            t2=findViewById(R.id.otp);
            timer=findViewById(R.id.timer);


            sendOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = String.valueOf(t1.getText());
                    if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(login.this, "Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        String PhoneNumber="+91"+phone;
                        sendOtp.setEnabled(false);
                        sendVerificationCode(PhoneNumber);
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            int i;
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(i<60) {
                                                i++;
                                                timer.setText("Resend OTP: " + String.valueOf(i)+"s");
                                            }
                                        }
                                    });
                                    if(i<60) {
                                        handler.postDelayed(this, 1000);
                                    }else{
                                        sendOtp.setEnabled(true);
                                    }
                                }
                        };
                        handler.postDelayed(runnable,0);
                    }
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enteredCode = t2.getText().toString();
                    if (enteredCode.length()<6) {
                        Toast.makeText(login.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        verifyCode(enteredCode);
                    }
                }
            });
        }
    }

    protected void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dr=db.collection("user").document(FirebaseAuth.getInstance().getUid());
                            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot res=task.getResult();
                                    Log.d("test",res.toString());
                                    if(res!=null){
                                        Intent i=new Intent(getApplicationContext(), intro.class);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        Intent i = new Intent(login.this, chooseInterest.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    protected void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    protected PhoneAuthProvider.OnVerificationStateChangedCallbacks

            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Toast.makeText(login.this, "code sent", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    protected void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }
}