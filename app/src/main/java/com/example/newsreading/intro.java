package com.example.newsreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().hide();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Handler h=new Handler();
        Runnable r=new Runnable() {
            @Override
            public void run() {
                DocumentReference dr;
                if(mAuth.getCurrentUser()!=null){
                    dr=db.collection("user").document(FirebaseAuth.getInstance().getUid());
                    dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Intent i;
                            DocumentSnapshot res=task.getResult();
                            if(res.exists()) {
                                i = new Intent(getApplicationContext(), news.class);
                            }else{
                                i = new Intent(getApplicationContext(), chooseInterest.class);
                            }
                            startActivity(i);
                            finish();
                        }
                    });
                }else{
                    Intent i=new Intent(getApplicationContext(),login.class);
                    startActivity(i);
                }
            }

        };
        h.postDelayed(r,1000);
    }
}